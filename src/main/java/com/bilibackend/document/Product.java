package com.bilibackend.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "product",createIndex = false)
@Data
@AllArgsConstructor
public class Product {
    @Id
    @Field(type = FieldType.Integer,store = true,index = true)
    private Integer id;
    @Field(type = FieldType.Text,store = true,index = true,analyzer = "ik_max_word",searchAnalyzer = "ik_max_word")
    private String productName;
    @Field(type = FieldType.Text,store = true,index = true,analyzer = "ik_max_word",searchAnalyzer = "ik_max_word")
    private String productDesc;
}
