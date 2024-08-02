package com.ssafy.globalcc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class RedisTest {
    @Autowired
    private RedisOperations<String, String> redisOperations;

    @Test
    public void test() {
        redisOperations.opsForValue().set("test","test1");
        Assertions.assertEquals(redisOperations.opsForValue().get("test"), "test1");
    }
}
