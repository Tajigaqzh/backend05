package com.bilibackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
public class Backend05Application {
    public static void main(String[] args) {
        SpringApplication.run(Backend05Application.class, args);
    }

}
