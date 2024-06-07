package com.bilibackend.utils;

import com.bilibackend.document.VideoDocument;
import com.bilibackend.entity.Video;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/18 23:52
 * @Version 1.0
 */
public class ConvertUtil {

    public static Video convertToVideo(VideoDocument video) {
        return Video.builder()
                .id(video.getId())
                .url(video.getUrl())
                .description(video.getDescription())
                .typeName(video.getTypeName())
                .keyWord(video.getKeyWord())
                .createTime(video.getCreateTime())
                .groupId(video.getGroupId())
                .cover(video.getCover())
                .typeName(video.getTypeName()).build();

    }

    public static VideoDocument convertToDocument(Video video) {
        VideoDocument videoDocument = new VideoDocument();
        videoDocument.setId(video.getId());
        videoDocument.setTitle(video.getTitle());
        videoDocument.setCover(video.getCover());
        videoDocument.setUrl(video.getUrl());
        videoDocument.setAuthorName(video.getAuthors());
        videoDocument.setTypeName(video.getTypeName());
        videoDocument.setCreateTime(video.getCreateTime());
        videoDocument.setDescription(video.getDescription());
        return videoDocument;
    }
}
