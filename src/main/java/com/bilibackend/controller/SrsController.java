package com.bilibackend.controller;

import cn.hutool.core.util.ObjectUtil;
import com.bilibackend.dto.StartRtcDto;
import com.bilibackend.dto.StopDto;
import com.bilibackend.entity.LiveRoom;
import com.bilibackend.service.LiveRoomService;
import com.bilibackend.service.SrsService;
import com.bilibackend.utils.Result;
import com.bilibackend.utils.ResultCode;
import com.bilibackend.vo.Client;
import com.bilibackend.vo.LiveRoomVO;
import com.bilibackend.vo.RoomDetail;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/10 18:47
 * @Version 1.0 直播的相关 没有考虑token过期
 * <p>
 * <p>
 * watcher表中要记录离开时间，记录时要区分主动离开，被踢，直播结束，离开
 * 离开时间为空表示一直看到直播结束，可以在live_room表中查询直播结束时间
 * <p>
 * 主动离开记录一下时间，以及在线观看人数
 */
@RestController
@RequestMapping("/srs")
@Slf4j
public class SrsController {

    @Autowired
    private SrsService srsService;


    @Autowired
    private LiveRoomService liveRoomService;


    /**
     * 开始直播
     * todo 前端限制带宽，当前发起一个webRTC流就占据1.5mb带宽，开多个个直播间带宽就没了
     * 发起房间时同步创建一个room
     *
     * @param startRtcDto 发起直播的参数：发起人id（必需），房间uuid（必需），直播间标题，房间关键字，房间公告，发起者sdp（必需）
     * @return Result
     */
    @PostMapping("/publish")
    public Result startLive(@RequestBody @Validated StartRtcDto startRtcDto) {
        LiveRoomVO liveRoomVO1 = srsService.startLive(startRtcDto);
        if (ObjectUtil.isNotNull(liveRoomVO1)) {
            return Result.success(liveRoomVO1);
        } else {
            return Result.error(ResultCode.ERROR);
        }
    }


    @GetMapping("/recommend")
    public Result getRecommendRoom(
            @RequestParam("type") String typeName,
            @RequestParam("page") Long page,
            @RequestParam("size") Long size
    ) {
        Map<String, Object> recommendRoom = srsService.getRecommendRoom(typeName, page, size);
        return Result.success(recommendRoom);
    }


    @GetMapping("/random")
    public Result getRecommendRandom() {
        List<LiveRoom> someRoom = srsService.getSomeRoom();
        return Result.success(someRoom);
    }

    /**
     * 查询正在观看直播的人数
     *
     * @param roomId
     * @return
     */
    @GetMapping("/watching")
    public Result getWatching(@RequestParam("roomId") @Validated @NotNull String roomId) {
        long watching = srsService.getWatching(roomId);
        return Result.success(watching);
    }

    /**
     * 停止直播
     * 遇到500的时候前端重发请求
     * todo 暂时先放行，测试用
     * 这个没法传cid ，接收房间id
     *
     * @return
     */
    @PostMapping("/stop")
    public Result stopPublish(@RequestBody @Validated StopDto stopDto) {
        System.out.println(stopDto);
        boolean b = srsService.stopPublish(stopDto);
        if (b) {
            return Result.ok();
        }
        return Result.error(ResultCode.DELETE_ERROR);
    }


    @GetMapping("/leave")
    public Result leaveRoom(@RequestParam("roomId") String roomId, @RequestParam("userId") String userId) {
        srsService.leaveRoom(roomId, userId);
        return Result.ok();
    }


    /**
     * 踢人
     *
     * @return Result
     */
    @GetMapping("/kickOut")
    public Result kickOut(@RequestParam("clientId") String clientId, @RequestParam("roomId") String roomId) {
        srsService.kickOutUser(clientId, roomId);
        return Result.ok();
    }


    /**
     * 这个变化比较多，没加入redis缓存
     *
     * @param roomId roomId
     * @return
     */
    @GetMapping("/clients")
    public Result getWatchClient(String roomId) {
        List<Client> clients = srsService.getClients(roomId);
        return Result.success(clients);
    }


    @GetMapping("/detail")
    public Result getById(String roomId) {
        RoomDetail detailById = liveRoomService.getDetailById(roomId);
        if (ObjectUtil.isNotNull(detailById)) {
            return Result.success(detailById);
        }
        return Result.error(ResultCode.QUERY_ERROR);
    }

}
