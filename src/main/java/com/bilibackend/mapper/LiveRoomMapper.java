package com.bilibackend.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.bilibackend.entity.LiveRoom;
import com.bilibackend.vo.RoomDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
* @author 20126
* @description 针对表【bili_live_room】的数据库操作Mapper
* @createDate 2024-05-12 11:24:04
* @Entity generator.domain.LiveRoom
*/
@Mapper
public interface LiveRoomMapper extends BaseMapper<LiveRoom> {


//        IPage<History> listPage(Page<History> page, @Param(Constants.WRAPPER) Wrapper<History> ew);
    RoomDetail getRoomDetailByRoomId(@Param(Constants.WRAPPER) Wrapper<RoomDetail> ew);

}




