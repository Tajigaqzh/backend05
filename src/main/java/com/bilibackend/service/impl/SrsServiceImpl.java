package com.bilibackend.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bilibackend.config.SrsConfig;
import com.bilibackend.dto.StartRtcDto;
import com.bilibackend.dto.StopDto;
import com.bilibackend.entity.LiveRoom;
import com.bilibackend.entity.Watcher;
import com.bilibackend.service.LiveRoomService;
import com.bilibackend.service.SrsService;
import com.bilibackend.service.WatcherService;
import com.bilibackend.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author 20126
 * @Description srsService, 直播的核心逻辑
 * @Date 2024/5/12 17:01
 * @Version 1.0
 */
@Service
@Slf4j
public class SrsServiceImpl implements SrsService {


    @Autowired
    private SrsConfig srsConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WatcherService watcherService;

    @Autowired
    private LiveRoomService liveRoomService;

    @Autowired
    @Qualifier("jsonRedisTemplate")
    private RedisTemplate<String, Serializable> redisTemplate;


    @Autowired
    private RedissonClient redissonClient;

    /**
     * 锁，锁定视频的正在观看次数
     */
    private static String liveCountLockPrefix = "live:lock:watching:";


    /**
     * data,某个视频的正在观看人数，过期时间3小时，如果有则继续新增
     * 请求视频详情的时候新增，用户点击返回或者退出，或者完成播放的时候减少
     */
    private static String liveCount = "live:count:";


    private final Map<String, String> tokenMep = new ConcurrentHashMap<>();


    public Stream getStreamByRoomId(String roomId) {
        long page = 0L;
        long count = 50L;
        boolean isEnd = false;
        String mapToken = this.tokenMep.get("token");

        while (!isEnd) {
            //重试三次
            String token = retryToken(mapToken);
            String url = srsConfig.getFullStreamsURL() + "?token=" + token + "&start=" + page + "&count=" + count;

            try {
                ResponseEntity<StreamResultVO> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, StreamResultVO.class);

                if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                    StreamResultVO body = responseEntity.getBody();
                    List<Stream> queryStream = body.getStreams();

                    List<Stream> streams = queryStream.stream().filter(stream -> Objects.equals(stream.getName(), roomId)).toList();
                    if (CollectionUtil.isNotEmpty(streams)) {
                        isEnd = true;
                        return streams.get(0);
                    }

                    if (queryStream.size() == 50) {
                        page++;
                    } else {
                        isEnd = true;
                    }

                } else {
                    isEnd = true;
                    return null;
                }
            } catch (Exception e) {
                isEnd = true;
                return null;
            }

        }
        return null;
    }

    /**
     * 分页查询在线stream
     *
     * @return
     */
    public List<Stream> getStreams() {
        long page = 0L;
        long count = 50L;
        boolean isEnd = false;

        List<Stream> streams = new ArrayList<>();
        String mapToken = this.tokenMep.get("token");

        while (!isEnd) {
            //重试三次
            String token = retryToken(mapToken);
            System.out.println(token);
            String url = srsConfig.getFullStreamsURL() + "?token=" + token + "&start=" + page + "&count=" + count;
            System.out.println(url);
            try {
                ResponseEntity<StreamResultVO> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, StreamResultVO.class);
                if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                    StreamResultVO body = responseEntity.getBody();
                    List<Stream> queryStream = body.getStreams();
                    //不为空
                    if (CollectionUtil.isNotEmpty(queryStream)) {
                        streams.addAll(queryStream);
                        //在线流超过50，继续查询
                        if (queryStream.size() == 50) {
                            page++;
                        } else {
                            //在线流未超过50，直接返回
                            isEnd = true;
                            return streams;
                        }
                    } else {
                        //查询出的为空
                        isEnd = true;
                        return streams;
                    }
                } else {
                    return streams;
                }
            } catch (Exception e) {
                this.tokenMep.remove("token");
                throw new RuntimeException(e);
            }
        }


        return streams;
    }

    /**
     * todo 记录历史记录
     *
     * @param stopDto
     * @return
     */
    @Override
    public boolean stopPublish(StopDto stopDto) {
        String type = stopDto.getRoomId().split("-")[0];
        LiveRoom stream = (LiveRoom) redisTemplate.opsForHash().get("live:" + type, stopDto.getRoomId());
        if (ObjectUtil.isNull(stream)) {
            return false;
        }

        String roomId = stream.getRoomId();
        Stream streamByRoomId = getStreamByRoomId(roomId);

        String mapToken = this.tokenMep.get("token");


        if (streamByRoomId != null) {
            //重试三次
            String token = retryToken(mapToken);

            String url = srsConfig.getClientsURL() + "/" + streamByRoomId.getPublish().getCid() + "?token=" + token;
            try {
                //发起http请求关闭直播
                restTemplate.delete(url);

                //删除redis推荐列表中的数据
                redisTemplate.opsForHash().delete("live:" + type, stopDto.getRoomId());

                //更新房间的结束时间
                liveRoomService.updateById(LiveRoom.builder().roomId(stopDto.getRoomId()).endTime(new Date()).build());
                QueryWrapper<Watcher> watcherQueryWrapper = new QueryWrapper<>();

                //更新还未退出的观看者的离开时间
                watcherQueryWrapper.eq("stream", roomId).eq("leaveTime", null);
                List<Watcher> list = watcherService.list(watcherQueryWrapper);
                List<Watcher> watchers = list.stream().map(watcher -> Watcher.builder().id(watcher.getId()).leaveTime(new Date()).build()).toList();
                watcherService.updateBatchById(watchers);


                //把直播间观看人数的记录删除
                redisTemplate.delete(liveCountLockPrefix + roomId);

                return true;
            } catch (Exception e) {
                System.out.println(e);
                return false;
            }
        }
        return false;
    }

    /**
     * todo 记录历史记录
     *
     * @param clientId
     * @param roomId
     */
    @Override
    public void kickOutUser(String clientId, String roomId) {
        String mapToken = this.tokenMep.get("token");

        //重试三次
        String token = retryToken(mapToken);
        String url = srsConfig.getClientsURL() + "/" + clientId + "?token=" + token;

        RLock lock = redissonClient.getLock(liveCountLockPrefix + roomId);

        QueryWrapper<Watcher> watcherQueryWrapper = new QueryWrapper<>();
        watcherQueryWrapper.eq("client_id", clientId).eq("stream", roomId);
        Watcher build = Watcher.builder().leaveTime(new Date()).build();
        watcherService.update(build, watcherQueryWrapper);

        try {
            //发起http请求关闭直播
            restTemplate.delete(url);

            //踢人的时候把在线观看人数减一
            lock.lock();

            Long count = (Long) redisTemplate.opsForValue().get(liveCount + roomId);
            if (count == null || count == 1) {
                redisTemplate.delete(liveCount + roomId);
            } else {
                redisTemplate.opsForValue().decrement(liveCount + roomId);
                redisTemplate.expire(liveCount + roomId, 3, TimeUnit.HOURS);
            }
        } catch (Exception e) {
            log.warn(e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    //todo 加锁，放到推荐列表
    @Override
    public LiveRoomVO startLive(StartRtcDto startRtcDto) {
        String roomId1 = startRtcDto.getRoomId();
        String roomId;
        String playUrl;
        if (roomId1 != null && roomId1.length() > 0) {
            roomId = roomId1;
            playUrl = srsConfig.getPlayPrefix() + roomId;

        } else {
            roomId = startRtcDto.getTypeTitle() + "-" + IdUtil.simpleUUID();
            playUrl = srsConfig.getPlayPrefix() + roomId;

            LiveRoom liveRoom = LiveRoom.builder()
                    .startId(startRtcDto.getStartId())
                    .roomId(roomId)
                    .roomDesc(startRtcDto.getRoomDesc())
                    .roomTitle(startRtcDto.getRoomTitle())
                    .deleteStatus(1)
                    .roomCover(startRtcDto.getRoomCover())
                    .notice(startRtcDto.getNotice())
                    .typeTitle(startRtcDto.getTypeTitle())
                    .createTime(new Date())
                    //这个url只是简单的拼接一下，还要传递token和参数
                    .playUrl(playUrl).build();

            liveRoomService.save(liveRoom);
//            String date = LocalDate.now().toString(); date + ":" +
            redisTemplate.opsForHash().put("live:" + startRtcDto.getTypeTitle(), roomId, liveRoom);
        }

        //发起人id，房间uuid，房间标题，房间简介，房间关键字，公告，sdp，弹幕？，此时还要创建一个room群私聊
        String fullPublishURL = srsConfig.getFullPublishURL(roomId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/sdp"));

        HttpEntity<String> requestEntity = new HttpEntity<>(startRtcDto.getSdp(), headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(fullPublishURL, HttpMethod.POST, requestEntity, String.class);
            // 获取响应结果
            HttpStatusCode statusCode = response.getStatusCode();
            //数据库保存记录
            if (statusCode.is2xxSuccessful()) {
                String sdp = response.getBody();

//                System.out.println();
                return LiveRoomVO.builder().roomId(roomId).sdp(sdp).playUrl(playUrl).build();
            } else {
                return null;
            }
        } catch (Exception e) {
            log.warn(e.getLocalizedMessage());
        }
        return null;
    }


    /**
     * 查询所有在线的用户（包括房主）
     * 可以根据这个下线用户，停止直播
     * 这个必须要解析了
     * 遇到500的时候前端重发请求，
     * 用户加入后在socket中发一个消息，房主发起请求更新观看，
     * 这个频率比较高，就不加入redis了
     *
     * @return result
     */
    @Override
    public List<Client> getClients(String roomId) {
        long page = 0L;
        long count = 50L;
        boolean isEnd = false;
        List<Client> clients = new ArrayList<>();
        String mapToken = this.tokenMep.get("token");

        String[] split = srsConfig.getPlayPrefix().split("/");

        String queryStr = "/" + split[split.length - 1] + "/" + roomId;
        while (!isEnd) {
            //重试三次
            String token = retryToken(mapToken);
            String url = srsConfig.getClientsURL() + "?token=" + token + "&start=" + page + "&count=" + count;

            try {
                ResponseEntity<ClientsResultVO> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, ClientsResultVO.class);
                if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                    ClientsResultVO body = responseEntity.getBody();
                    List<Client> queryClients = body.getClients();
                    //不为空
                    if (CollectionUtil.isNotEmpty(queryClients)) {
                        List<Client> matched = queryClients.stream().filter(client -> client.getUrl().equals(queryStr)).toList();
                        clients.addAll(matched);

                        //在线用户超过50，继续查询
                        if (queryClients.size() > 50) {
                            page++;
                        } else {
                            //在线流未超过50，直接返回
                            isEnd = true;
                            return clients;
                        }
                    } else {
                        //查询出的为空
                        isEnd = true;
                        return clients;
                    }
                } else {
                    return clients;
                }
            } catch (Exception e) {
                this.tokenMep.remove("token");
                throw new RuntimeException(e);
            }
        }
        return clients;
    }


    @Override
    public Map<String, Object> getRecommendRoom(String typeName, Long page, Long size) {
        HashMap<String, Object> hashMap = new HashMap<>();

        Map<Object, Object> entries = redisTemplate.opsForHash().entries("live:" + typeName);

        LiveRoom[] liveRoom = (LiveRoom[]) entries.values().toArray();
        List<LiveRoom> liveRoomList = Arrays.stream(liveRoom).toList();

        hashMap.put("total", liveRoom.length);

        if (liveRoom.length <= size) {
            hashMap.put("current", 0);
            List<LiveRoom> result = new ArrayList<>(liveRoomList);
            hashMap.put("liveRoom", result);
        } else {
            int endIndex = Math.min((int) (size * (page + 1)), liveRoom.length - 1);
            List<LiveRoom> pageResult = liveRoomList.subList((int) (size * page), endIndex);

            hashMap.put("current", page);
            hashMap.put("liveRoom", pageResult);
        }
        return hashMap;
    }

    /**
     * 查询正在观看直播的人数
     *
     * @param roomId 房间id
     * @return
     */
    @Override
    public long getWatching(String roomId) {
        RLock lock = redissonClient.getLock(liveCountLockPrefix + roomId);
        Long count;
        try {
            lock.lock();
            count = (Long) redisTemplate.opsForValue().get(liveCount + roomId);
        } finally {
            lock.unlock();
        }
        if (ObjectUtil.isNull(count)) {
            return 0;
        }
        return count;
    }

    /**
     * todo 记录历史记录
     *
     * @param roomId
     * @param userId
     */
    @Override
    public void leaveRoom(String roomId, String userId) {
        RLock lock = redissonClient.getLock(liveCountLockPrefix + roomId);


        QueryWrapper<Watcher> watcherQueryWrapper = new QueryWrapper<>();
        watcherQueryWrapper.eq("userId", userId).eq("stream", roomId);
        Watcher build = Watcher.builder().leaveTime(new Date()).build();
        watcherService.update(build, watcherQueryWrapper);

        try {
            lock.lock();
            Long count = (Long) redisTemplate.opsForValue().get(liveCount + roomId);

            if (count == null || count == 1) {
                redisTemplate.delete(liveCount + roomId);
            } else {
                redisTemplate.opsForValue().decrement(liveCount + roomId);
                redisTemplate.expire(liveCount + roomId, 3, TimeUnit.HOURS);
            }
        } finally {
            lock.unlock();
        }

    }


    public String retryToken(String token) {
        int count = 0;

        while (count < 3 && token == null) {
            token = getBackendToken();
            count++;
        }
        return token;
    }


    /**
     * 内部方法
     *
     * @return 登录后台获取token
     * srs-stack把所有的接口都代理了，需要认证，所以加了一个登录的方法 返回token
     */
    private String getBackendToken() {
        //设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 设置请求参数
        Map<String, String> params = new HashMap<>();
        params.put("password", srsConfig.getPassword());

        // 创建请求实体对象
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(params, headers);
        ResponseEntity<LoginResultVO> responseEntity = restTemplate.postForEntity(srsConfig.getFullLoginURL(), requestEntity, LoginResultVO.class);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            LoginResultVO.LoginData data = Objects.requireNonNull(responseEntity.getBody()).getData();
            Date expireAt = data.getExpireAt();
            long time = expireAt.getTime();
            String token = data.getToken();
            this.tokenMep.put("token", token);
            //过期时间先保留着
            this.tokenMep.put("expire", Long.toString(time));
            return token;
        }
        return null;
    }

}
