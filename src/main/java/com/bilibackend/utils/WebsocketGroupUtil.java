package com.bilibackend.utils;


import cn.hutool.json.JSONUtil;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author 20126
 * @Description
 * @Date 2024/4/15 23:45
 * @Version 1.0
 */
@Slf4j
public class WebsocketGroupUtil {

    private static final Map<String, ArrayList<HashMap<String, Session>>> allRooms = new ConcurrentHashMap<>();
//    private static final Map<String, ArrayList<Session>> rooms = new ConcurrentHashMap<>();

    /**
     * enterRoom(roomId, userId, null, session);
     */
    /**
     * 加入房间
     *
     * @param session
     */
    public static void enterRoom(String roomId, String userId, String userName, Session session) {

        HashMap<String, Session> userMessage = new HashMap<>();
        userMessage.put(userId, session);

        ArrayList<HashMap<String, Session>> room = allRooms.get(roomId);

        if (room != null) {
            room.add(userMessage);
        } else {
            room = new ArrayList<>();
            room.add(userMessage);
        }
        allRooms.put(roomId, room);
        SocketMessage message;
        Random random = new Random();
        int i = random.nextInt(100000000);

        if (userName == null) {
            message = SocketMessage.builder().id(Integer.toString(i)).data("用户加入直播间").type("notice").build();
        } else {
            message = SocketMessage.builder().id(Integer.toString(i)).data(userName + "加入直播间").type("notice").build();
        }
        String resMessage = JSONUtil.toJsonStr(message);
        try {
            room.forEach(user -> {
                user.values().forEach(inRoomSession -> {
                    inRoomSession.getAsyncRemote().sendText(resMessage);
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 离开房间（发消息主动离开）
     */
    public static void leaveRoom(String roomId, String uid, String userName) {
        ArrayList<HashMap<String, Session>> room = allRooms.get(roomId);
        try {
            if (room != null) {
                room.removeIf(user -> user.containsKey(uid));
                if (room.size() == 0) {
                    allRooms.remove(roomId);
                } else {
                    SocketMessage message;
                    Random random = new Random();
                    int i = random.nextInt(100000000);
                    if (userName == null) {
                        message = SocketMessage.builder().id(Integer.toString(i)).data("某用户离开直播间").type("notice").build();
                    } else {
                        message = SocketMessage.builder().id(Integer.toString(i)).data(userName + "离开直播间").type("notice").build();
                    }
                    String resMessage = JSONUtil.toJsonStr(message);
                    room.forEach(user -> user.forEach((key, sess) -> sess.getAsyncRemote().sendText(resMessage)));
                }

            } else {
                allRooms.remove(roomId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void leaveRoom(Session session) {
        try {
            Collection<ArrayList<HashMap<String, Session>>> rooms = allRooms.values();

            rooms.forEach(room -> room.removeIf(user -> user.containsValue(session)));
            rooms.removeIf(r -> r.size() == 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void close(String roomId) {
        allRooms.remove(roomId);
    }

    /**
     * 广播消息
     *
     * @param roomId     房间iD
     * @param strMessage message
     */
    public static void broadCastText(String roomId, String strMessage) {
        try {
            if (strMessage != null) {
                System.out.println(roomId);
                ArrayList<HashMap<String, Session>> room = allRooms.get(roomId);
                room.forEach(user -> {
                    Collection<Session> values = user.values();
                    values.forEach(session -> session.getAsyncRemote().sendText(strMessage));
                });
            }
        } catch (Exception e) {
            System.out.println("解析失败");
        }
    }
}

