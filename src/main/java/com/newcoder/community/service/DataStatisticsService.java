package com.newcoder.community.service;

import com.newcoder.community.util.RedisUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.HyperLogLogOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Yoshino
 */
@Service
public class DataStatisticsService {
    @Autowired
    RedisTemplate redisTemplate;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


    public void StatisticsUv(String ip) {
        String s = dateFormat.format(new Date());
        String uvKey = RedisUtil.getUvKey(s);
        HyperLogLogOperations hyperLogLog = redisTemplate.opsForHyperLogLog();
        hyperLogLog.add(uvKey, ip);
    }

    public Long statisticsUvUnion( Date start,  Date end) {
        String startTime = dateFormat.format(start);
        String endTime = dateFormat.format(end);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        List<String> uvKeys = new ArrayList<>();
        while (!calendar.getTime().after(end)) {
            String uvKey = RedisUtil.getUvKey(dateFormat.format(calendar.getTime()));
            uvKeys.add(uvKey);
            calendar.add(Calendar.DATE, 1);
        }
        String uvUnionKey = RedisUtil.getUvUnionKey(startTime, endTime);
        HyperLogLogOperations hyperLogLog = redisTemplate.opsForHyperLogLog();
        Long union = hyperLogLog.union(uvUnionKey, uvKeys.toArray());
        return union;
    }

    public void staticDau(int userId) {
        String date = dateFormat.format(new Date());
        String dauKey = RedisUtil.getDauKey(date);
        ValueOperations value = redisTemplate.opsForValue();
        value.setBit(dauKey, userId, true);
    }

    public long staticDauUnion( Date start, Date end) {
        String startTime = dateFormat.format(start);
        String endTime = dateFormat.format(end);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        ValueOperations opsForValue = redisTemplate.opsForValue();
        List<byte[]> dauKeys = new ArrayList<>();
        while (!calendar.getTime().after(end)) {
            String dauKey = RedisUtil.getDauKey((dateFormat.format(calendar.getTime())));
            dauKeys.add(dauKey.getBytes());
            calendar.add(Calendar.DATE, 1);
            System.out.println(opsForValue.getBit(dauKey, 131));
        }
        byte[][] buffer = new byte[dauKeys.size()][dauKeys.get(0).length];

        return (long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String davUnionKey = RedisUtil.getDavUnionKey(startTime, endTime);
                connection.bitOp(RedisStringCommands.BitOperation.OR, davUnionKey.getBytes(), dauKeys.toArray(buffer));
                return connection.bitCount(davUnionKey.getBytes());
            }
        });

    }


}
