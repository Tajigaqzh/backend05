package com.bilibackend.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/12 11:31
 * @Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LiveRoomVO {
    private String playUrl;
    private String sdp;
    private String roomId;
}
