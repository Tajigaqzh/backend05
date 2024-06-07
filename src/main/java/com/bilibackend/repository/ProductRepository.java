package com.bilibackend.repository;


import com.bilibackend.document.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ProductRepository extends ElasticsearchRepository<Product, Integer> {
    //match是将查询条件分词后进行搜索，range可以对数字类型的字段进行范围搜索

    @Query("{\n" +
            "    \"match\": {\n" +
            "      \"productDesc\": \"?0\"\n" +
            "    }\n" +
            "  }")
    List<Product> findByProductDescMatch(String keyword);

    @Query("{\n" +
            "    \"match\": {\n" +
            "      \"productDesc\": \"?0\"\n" +
            "    }\n" +
            "  }")
    Page<Product> findByProductDescMatch(String keyword, Pageable pageable);

    @Query("{\n" +
            "    \"match\": {\n" +
            "      \"productDesc\": {\n" +
            "        \"query\": \"?0\",\n" +
            "        \"fuzziness\": 1\n" +
            "      }\n" +
            "    }\n" +
            "  }")
    List<Product> findByProductDescFuzzy(String keyword);


    List<Product> findByProductName(String productName);
    List<Product> findByProductNameOrProductDesc(String productName,String productDesc);
    List<Product> findByIdBetween(Integer startId,Integer endId);
}
