package com.newcoder.community.service;

import com.newcoder.community.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;

/**
 * @author Yoshino
 */
@Service
public class LikeService {
    @Autowired
    RedisTemplate redisTemplate;

    public void like(int userId, int entityType, int entityId, int entityUserId) {
        redisTemplate.execute(new SessionCallback() {
                                  @Override
                                  public Object execute(RedisOperations operations) throws DataAccessException {
                                      String entityKey = RedisUtil.getEntityKey(entityType, entityId);
                                      String userKey = RedisUtil.getUserKey(entityUserId);
                                      SetOperations opsForSet = operations.opsForSet();
                                      ValueOperations opsForValue = operations.opsForValue();
                                      Boolean isLiked = opsForSet.isMember(entityKey, userId);
                                      Integer userLikeNum = opsForValue.get(userKey)==null?0:(Integer) opsForValue.get(userKey);
                                      operations.multi();
                                      if (isLiked) {
                                          opsForSet.remove(entityKey, userId);
                                          opsForValue.set(userKey, userLikeNum - 1);
                                      } else {
                                          operations.multi();
                                          opsForSet.add(entityKey, userId);
                                          opsForValue.set(userKey, userLikeNum + 1);
                                      }


                                      return operations.exec();
                                  }
                              }
        );

    }

    public long getLikeNum(int entityType, int entityId) {
        SetOperations setOperations = redisTemplate.opsForSet();
        String key = RedisUtil.getEntityKey(entityType, entityId);
        return setOperations.size(key);
    }

    public int getLikeStatus(int userId, int entityType, int entityId) {
        SetOperations setOperations = redisTemplate.opsForSet();
        String key = RedisUtil.getEntityKey(entityType, entityId);
        return setOperations.isMember(key, userId) ? 1 : 0;
    }
    public int getLikeNumOfUser(int userId)
    {
        String userKey = RedisUtil.getUserKey(userId);
        Integer o = (Integer) redisTemplate.opsForValue().get(userKey);
        return o==null?0:o;
    }

}
