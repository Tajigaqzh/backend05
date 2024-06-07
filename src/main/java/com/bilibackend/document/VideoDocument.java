package com.bilibackend.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/18 13:06
 * @Version 1.0
 */
@Data
@Document(indexName = "video", createIndex = false)
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class VideoDocument implements Serializable {
    @Id
    @Field(type = FieldType.Integer, store = true, index = true)
    private Long id;

    /**
     * 存储，创建缩进，分词器，搜索时的分词器
     */
    @Field(type = FieldType.Text, store = true, index = true, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String title;

    @Field(type = FieldType.Text, store = true, index = false)
    private String url;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Field(type = FieldType.Date, store = true)
    private Date createTime;

    @Field(type = FieldType.Text, store = true, index = true, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String keyWord;

    @Field(type = FieldType.Text, store = true, index = true, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String description;

    @Field(type = FieldType.Text, store = true, index = true, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String typeName;

    /**
     * 合集id
     */
    @Field("groupId")
    private Long groupId;


    @Field(type = FieldType.Text, store = true, analyzer = "ik_smart",searchAnalyzer = "ik_max_word")
    private String authorName;

    /**
     * 视频封面
     */
    @Field("cover")
    private String cover;

}
