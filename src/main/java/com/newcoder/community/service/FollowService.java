package com.newcoder.community.service;

import com.newcoder.community.dao.UserMapper;
import com.newcoder.community.pojo.User;
import com.newcoder.community.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author 86156
 */
@Service
public class FollowService {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    UserMapper userMapper;

    /**
     * 关注某个实体(用户，帖子，评论.....)
     * @param entityType
     * @param entityId
     * @param userId
     */
    public void followEntity(int entityType,int entityId,int userId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisUtil.getFollowerKey(entityId, entityType);
                operations.multi();
                ZSetOperations setOperations = operations.opsForZSet();
                setOperations.add(followeeKey,entityId,System.currentTimeMillis());
                setOperations.add(followerKey,userId,System.currentTimeMillis());
                return operations.exec();
            }
        });
    }

    /**
     * 取关某个实体
     * @param entityType
     * @param entityId
     * @param userId
     */
    public void unfollowEntity(int entityType,int entityId,int userId)
    {
            redisTemplate.execute(new SessionCallback() {
                @Override
                public Object execute(RedisOperations operations) throws DataAccessException {
                    String followeeKey = RedisUtil.getFolloweeKey(userId, entityType);
                    String followerKey = RedisUtil.getFollowerKey(entityId, entityType);
                    operations.multi();
                    ZSetOperations opsForZSet = operations.opsForZSet();
                    opsForZSet.remove(followeeKey,entityId);
                    opsForZSet.remove(followerKey,userId);
                    return operations.exec();
                }
            });
    }
    public Long getFansCount(int entityType, int entityId)
    {
        String followerKey = RedisUtil.getFollowerKey(entityId,entityType);
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        return zSetOperations.zCard(followerKey);
    }
    public Long getFollowCount(int entityType, int entityId)
    {
        String followeeKey = RedisUtil.getFolloweeKey(entityId,entityType);
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        return zSetOperations.zCard(followeeKey);
    }
    public boolean isFollowed(int entityType,int entityId,int userId)
    {
        String followerKey = RedisUtil.getFollowerKey(entityId,entityType);
        ZSetOperations opsForZSet = redisTemplate.opsForZSet();
        Double score = opsForZSet.score(followerKey, userId);
        return score==null;
    }

    public List<Map<String,Object>> getFansList(int entityType,int entityId,int localId)
    {
        String followerKey = RedisUtil.getFollowerKey(entityId, entityType);
        ZSetOperations zSet = redisTemplate.opsForZSet();
        Set<Integer> range = zSet.reverseRange(followerKey, 0, -1);
        List<Map<String,Object>> fansList = new ArrayList<>();
        if (range == null||range.size()==0)
        {
            return null;
        }
        for (Integer userId:range)
        {
            Map<String,Object> map = new HashMap<>(2);
            User userById = userMapper.getUserById(userId);
            double score = zSet.score(followerKey, userId);
            map.put("user",userById);
            map.put("score",new Date((long) score));
            map.put("isFollowed",isFollowed(entityType,userById.getId(),localId));
            fansList.add(map);
        }
        return fansList;
    }
    public List<Map<String,Object>> getFollowList(int entityType,int entityId,int localId)
    {
        String followerKey = RedisUtil.getFolloweeKey(entityId,entityType);
        ZSetOperations zSet = redisTemplate.opsForZSet();
        Set<Integer> range = zSet.reverseRange(followerKey, 0, -1);
        List<Map<String,Object>> followList = new ArrayList<>();
        if (range == null||range.size()==0)
        {
            return null;
        }
        for (int userId:range)
        {
            Map<String,Object> map = new HashMap<>(6);
            User userById = userMapper.getUserById(userId);
            double score = zSet.score(followerKey, userId);
            map.put("user",userById);
            map.put("score",new Date((long) score));
            map.put("isFollowed",isFollowed(entityType,userById.getId(),localId));
            followList.add(map);
        }
        return followList;
    }


}
