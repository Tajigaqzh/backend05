package com.bilibackend.repository;

import com.bilibackend.document.VideoDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @Author 20126
 * @Description
 * @Date 2024/5/18 13:08
 * @Version 1.0
 */
public interface VideoRepository extends ElasticsearchRepository<VideoDocument, Integer> {

    /**
     * find BY desc
     *
     * @param keyword 关键字
     * @return
     */
    @Query("{\n" +
            "    \"match\": {\n" +
            "      \"description\": \"?0\"\n" +
            "    }\n" +
            "  }")
    List<VideoDocument> findByVideoDescMatch(String keyword);


    //    @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"title\", \"keyWord\"], \"type\": \"best_fields\"}}")
    @Query("{\n" +
            "    \"match\": {\n" +
            "      \"description\": \"?0\"\n" +
            "    }\n" +
            "  }")
    Page<VideoDocument> findByMultiMatch(String searchText, PageRequest pageRequest);


    /**
     * 首先根据标题查，然后根据关键字，再根据描述查询
     *
     * @param keyword
     * @return
     */
    @Query(" {\n" +
            "    \"multi_match\": {\n" +
            "     \"query\": \"?0\",\n" +
            "     \"fields\": [\"title\",\"keyWord\"]\n" +
            "    }\n" +
            "  }")
    List<VideoDocument> findByRelation(String keyword, Pageable pageable);


    //todo 限制日期
    @Query("{\n" +
            "    \"match\": {\n" +
            "      \"authorName\": \"?0\"\n" +
            "    }\n" +
            "  }")
    List<VideoDocument> findByVideoAuthor(String keyword, Pageable pageable);

    @Query("{\n" +
            "    \"simple_query_string\": {\n" +
            "      \"query\": \"?0\",\n" +
            "      \"fields\": [\"authorName\"]\n" +
            "    }\n" +
            "    }")
    List<VideoDocument> findByAuthorName(String keyword);
}
