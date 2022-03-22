package com.newcoder.community;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class testRedis {
    @Autowired
    RedisTemplate redisTemplate;

    @Test
    public void testString()
    {
        String redisKey = "count:l";
        redisTemplate.opsForValue().set(redisKey,12);
      //  System.out.println(redisTemplate.opsForValue().increment(redisKey));

    }

}
