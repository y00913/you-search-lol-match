package com.example.lol;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LolApplicationTests {

    @Test
    void contextLoads() {
        long timestamp = (System.currentTimeMillis() - Long.parseLong("1661521220421")) / 1000;

        System.out.println(timestamp / 60 + " 분 ");
        System.out.println(timestamp / 60 / 60 + " 시 ");
        System.out.println(timestamp / 60 / 60 / 24 + " 일 ");
    }

}
