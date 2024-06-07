package com.bilibackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bilibackend.entity.LiveRoom;
import com.bilibackend.mapper.LiveRoomMapper;
import com.bilibackend.service.LiveRoomService;
import com.bilibackend.vo.RoomDetail;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 20126
 * @description 针对表【bili_live_room】的数据库操作Service实现
 * @createDate 2024-05-12 11:24:04
 */
@Service
public class LiveRoomServiceImpl extends ServiceImpl<LiveRoomMapper, LiveRoom> implements LiveRoomService {

    @Autowired
    private LiveRoomMapper liveRoomMapper;
    @Override
    public RoomDetail getDetailById(String id) {

        QueryWrapper<RoomDetail> roomDetailQueryWrapper = new QueryWrapper<>();
        roomDetailQueryWrapper.eq("r.roomId",id);
        return liveRoomMapper.getRoomDetailByRoomId(roomDetailQueryWrapper);
    }
}




