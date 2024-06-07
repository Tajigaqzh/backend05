package com.bilibackend.service;

import com.bilibackend.dto.StartRtcDto;
import com.bilibackend.dto.StopDto;
import com.bilibackend.entity.LiveRoom;
import com.bilibackend.vo.Client;
import com.bilibackend.vo.LiveRoomVO;
import com.bilibackend.vo.Stream;

import java.util.List;
import java.util.Map;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/12 17:01
 * @Version 1.0
 */

public interface SrsService {

    String retryToken(String token);

    List<Stream> getStreams();

    boolean stopPublish(StopDto stopDto);

    /**
     * 踢人的时候记得更新watcher表中的leaveTime
     *
     * @param clientId
     * @param roomId
     */
    void kickOutUser(String clientId, String roomId);

    /**
     * 发起直播
     * @param startRtcDto 参数
     * @return
     */
    LiveRoomVO startLive(StartRtcDto startRtcDto);

    /**
     * 获取当前观看直播的客户端
     * @param roomId 房间号
     * @return
     */
    List<Client> getClients(String roomId);

    /**
     * 按照分类获取推荐的直播
     * @param typeName
     * @param page
     * @param size
     * @return
     */
    Map<String, Object> getRecommendRoom(String typeName, Long page, Long size);



    List<LiveRoom> getSomeRoom();

    /**
     * 查看正在观看直播的人数
     * @param roomId
     * @return
     */
    long getWatching(String roomId);


    /**
     * 主动离开房间
     * @param roomId
     * @param userId
     */
    void leaveRoom(String roomId, String userId);



}
