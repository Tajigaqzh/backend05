package com.bilibackend.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/16 18:45
 * @Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CollectionVo {
    //id是视频id
    private Long id;
    private String title;
    private Integer deleteStatus;
    private Date createTime;
    private String url;
    private String groupId;
    private String typeName;
    private String cover;
    private Long coin;
    private Long likeNumber;
    private Long collect;
    private Long playTimes;
    private Long transmit;
    private String upId;
    private String username;
    private String nickname;
    private String avatar;
    private Date collectTime;
}
