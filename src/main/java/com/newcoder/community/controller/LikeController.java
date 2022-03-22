package com.newcoder.community.controller;

import com.newcoder.community.event.EventProducer;
import com.newcoder.community.pojo.Comment;
import com.newcoder.community.pojo.DiscussPost;
import com.newcoder.community.pojo.Event;
import com.newcoder.community.pojo.User;
import com.newcoder.community.service.CommentService;
import com.newcoder.community.service.CommunityConst;
import com.newcoder.community.service.LikeService;
import com.newcoder.community.service.PostService;
import com.newcoder.community.util.CommonUtil;
import com.newcoder.community.util.HostHolder;
import com.newcoder.community.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 86156
 */
@Controller
public class LikeController implements CommunityConst {
    @Autowired
    LikeService likeService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    EventProducer eventProducer;
    @Autowired
    PostService postService;
    @Autowired
    CommentService commentService;
    @Autowired
    RedisTemplate redisTemplate;

    @RequestMapping(value = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityId, int entityType, int entityUserId, int postId) {
        User user = hostHolder.getUser();
        likeService.like(user.getId(), entityType, entityId, entityUserId);
        long likeNum = likeService.getLikeNum(entityType, entityId);
        int likeStatus = likeService.getLikeStatus(user.getId(), entityType, entityId);
        Map<String, Object> likeMap = new HashMap<>();
        likeMap.put("likeNum", likeNum);
        likeMap.put("likeStatus", likeStatus);
        if (likeStatus ==1) {
            Event event = new Event().setTopic(TOPIC_LIKE)
                    .setUserId(user.getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityAuthorId(entityUserId)
                    .setData("pid", postId);
            eventProducer.produceEvent(event);
        }
        if (entityType==CommunityConst.ENTITY_TYPE_POST)
        {
            String scorePostKey = RedisUtil.getScorePostKey();
            redisTemplate.opsForSet().add(scorePostKey,entityId);
        }
        return CommonUtil.getJsonObj(0, null, likeMap);
    }

}
