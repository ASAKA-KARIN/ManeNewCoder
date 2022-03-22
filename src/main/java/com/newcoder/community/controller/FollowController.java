package com.newcoder.community.controller;

import com.newcoder.community.event.EventProducer;
import com.newcoder.community.pojo.Event;
import com.newcoder.community.pojo.User;
import com.newcoder.community.service.CommunityConst;
import com.newcoder.community.service.FollowService;
import com.newcoder.community.util.CommonUtil;
import com.newcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Yoshino
 */
@Controller
public class FollowController implements CommunityConst {
    @Autowired
    FollowService followService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    EventProducer eventProducer;
    @RequestMapping( value = "/follow",method = RequestMethod.POST)
    @ResponseBody
    public String followSomething(int entityId,int entityType){
        User user = hostHolder.getUser();
        if(user.getId() == entityId)
        {
            return CommonUtil.getJsonObj(1,"不能关注自己");
        }
        followService.followEntity(entityType,entityId,user.getId());
        Event event = new Event().setEntityType(entityType)
                                 .setEntityId(entityId)
                                 .setTopic(TOPIC_FOLLOW)
                                 .setUserId(user.getId())
                                 .setData("followee",entityId)
                                .setEntityAuthorId(entityId);
        eventProducer.produceEvent(event);
        return CommonUtil.getJsonObj(0,"关注成功");
    }
    @RequestMapping( value = "/unfollow",method = RequestMethod.POST)
    @ResponseBody
    public String unfollowSomething(int entityId,int entityType){
        User user = hostHolder.getUser();
        followService.unfollowEntity(entityType,entityId,user.getId());
        return CommonUtil.getJsonObj(0,"已取消关注");
    }

}
