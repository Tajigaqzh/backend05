package com.bilibackend;

import com.bilibackend.entity.Video;
import com.bilibackend.mapper.VideoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Backend05ApplicationTests {

    @Autowired
    private VideoMapper videoMapper;
    @Test
    void contextLoads() {
        Video detail = videoMapper.getDetail(1L);
        System.out.println(detail);
    }

}
