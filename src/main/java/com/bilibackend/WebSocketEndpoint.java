package com.bilibackend;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.bilibackend.entity.User;
import com.bilibackend.mapper.UserMapper;
import com.bilibackend.utils.SocketMessage;
import com.bilibackend.utils.WebsocketGroupUtil;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/31 18:22
 * @Version 1.0
 */
@Component
@ServerEndpoint("/chat/{roomId}/{userId}")
public class WebSocketEndpoint {

    private static UserMapper userMapper;

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketEndpoint.userMapper = userMapper;
    }

    @OnOpen
    public void onOpen(@PathParam(value = "roomId") String roomId, @PathParam(value = "userId") String userId,
                       Session session) {

        User user = userMapper.selectById(userId);
        if (!ObjectUtil.isNull(user)) {
            WebsocketGroupUtil.enterRoom(roomId, userId, user.getUsername(), session);
        } else {
            WebsocketGroupUtil.enterRoom(roomId, userId, null, session);
        }
    }

    @OnMessage
    public void onMessage(@PathParam(value = "roomId") String roomId, String strMessage, Session session) {
        SocketMessage socketMessage = JSONUtil.toBean(strMessage, SocketMessage.class);
        if (Objects.equals(socketMessage.getType(), "room") && Objects.equals(socketMessage.getTo(), "close")) {
            WebsocketGroupUtil.close(roomId);
        } else {
            WebsocketGroupUtil.broadCastText(roomId, strMessage);
        }
    }

//    https://video-1304757922.cos.ap-shanghai.myqcloud.com/

    @OnClose
    public void onClose(@PathParam(value = "roomId") String roomId, @PathParam(value = "userId") String userId,
                        Session session) {
        User user = userMapper.selectById(userId);
        if (!ObjectUtil.isNull(user)) {
            WebsocketGroupUtil.leaveRoom(roomId, userId, user.getUsername());
        } else {
            WebsocketGroupUtil.leaveRoom(roomId, userId, null);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        try {
            WebsocketGroupUtil.leaveRoom(session);
            session.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        throwable.printStackTrace();
    }

}
