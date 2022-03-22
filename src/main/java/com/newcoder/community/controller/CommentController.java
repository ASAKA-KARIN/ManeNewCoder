package com.newcoder.community.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.newcoder.community.event.EventProducer;
import com.newcoder.community.pojo.Comment;
import com.newcoder.community.pojo.DiscussPost;
import com.newcoder.community.pojo.Event;
import com.newcoder.community.service.CommentService;
import com.newcoder.community.service.CommunityConst;
import com.newcoder.community.service.PostService;
import com.newcoder.community.util.HostHolder;
import com.newcoder.community.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.*;

/**
 * @author 86156
 */
@Controller
public class CommentController implements CommunityConst {

    @Autowired
    private CommentService commentService;
    @Autowired
    private PostService postService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private EventProducer eventProducer;
    @Autowired
    private RedisTemplate redisTemplate;


    @RequestMapping(value = "/comment/{pid}", method = RequestMethod.POST)
    public String addComment(Comment comment, @PathVariable("pid") int pid) {
        comment.setStatus(0);
        comment.setUserId(hostHolder.getUser().getId());
        comment.setCreateTime(new Date());
        commentService.addComment(comment);
        Event event = new Event().setUserId(hostHolder.getUser().getId())
                .setTopic(TOPIC_COMMENT)
                //设置目前该实体的ID
                .setEntityId(comment.getEntityId())
                .setEntityType(comment.getEntityType())
                .setData("targetId", pid);
        //设置需要通知的用户的ID
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            DiscussPost post = postService.getDiscussPost(comment.getEntityId());
            event.setEntityAuthorId(post.getUserId());
            Event publishEvent = new Event().setTopic(TOPIC_PUBLISH)
                    .setEntityId(comment.getEntityId())
                    .setEntityType(ENTITY_TYPE_COMMENT)
                    .setUserId(comment.getUserId());
            eventProducer.produceEvent(publishEvent);
        }
        if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {
            Comment commentById = commentService.getCommentById(comment.getEntityId());
            event.setEntityAuthorId(commentById.getUserId());
        }
        eventProducer.produceEvent(event);
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            String key = RedisUtil.getScorePostKey();
            redisTemplate.opsForSet().add(key, pid);
        }
        return "redirect:/singlePost/" + pid;
    }

    @RequestMapping(value = "/myComment/{pageNum}",method = RequestMethod.GET)
    public String toMyComment(@PathVariable("pageNum") int pageNum,Model model){
        PageHelper.startPage(pageNum,5);
        List<Comment> comments = commentService.myComments(hostHolder.getUser().getId());
        PageInfo<Comment> postPage = new PageInfo<>(comments,5);
        int[] navigatepageNums = postPage.getNavigatepageNums();
        model.addAttribute("pageCount",navigatepageNums);
        model.addAttribute("MaxPage",postPage.getPages());
        model.addAttribute("currentPage",pageNum);
        model.addAttribute("user",hostHolder.getUser());
        List<Map<String,Object>> myComments = new ArrayList<>();
        for(Comment comment:comments)
        {
            Map<String,Object> map = new HashMap<>(6);
            DiscussPost post = postService.getDiscussPost(comment.getEntityId());
            map.put("post",post);
            map.put("comment",comment);
            myComments.add(map);
        }
        model.addAttribute("myComments", myComments);
        model.addAttribute("commentCount",commentService.getMyCommentCount(hostHolder.getUser().getId()));
        return "/site/my-reply";
    }

}
