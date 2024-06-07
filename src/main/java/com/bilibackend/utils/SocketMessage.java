package com.bilibackend.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/31 18:46
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SocketMessage {
    private String id;
    /**
     * 信息类型  'system' | 'user' | 'notice' | 'room'
     */
    private String type;
    /**
     * 接收者
     */
    private String to;
    /**
     * 发送者name
     */
    private String senderName;
    /**
     * 发送者id
     */
    private String senderId;

    /**
     * 信息
     */
    private String data;
}
