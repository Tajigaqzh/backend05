package com.bilibackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bilibackend.entity.LiveRoom;
import com.bilibackend.vo.RoomDetail;

/**
* @author 20126
* @description 针对表【bili_live_room】的数据库操作Service
* @createDate 2024-05-12 11:24:04
*/
public interface LiveRoomService extends IService<LiveRoom> {

    RoomDetail getDetailById(String id);

}
