package com.newcoder.community.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.newcoder.community.dao.CommentMapper;
import com.newcoder.community.event.EventProducer;
import com.newcoder.community.pojo.Comment;
import com.newcoder.community.pojo.DiscussPost;
import com.newcoder.community.pojo.Event;
import com.newcoder.community.pojo.User;
import com.newcoder.community.service.*;
import com.newcoder.community.util.CommonUtil;
import com.newcoder.community.util.HostHolder;
import com.newcoder.community.util.RedisUtil;
import com.newcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.relational.core.sql.Like;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.HtmlUtils;

import javax.jws.WebParam;
import java.util.*;

/**
 * @author Yoshino
 */
@Controller
public class PostController implements CommunityConst {
    @Autowired
    PostService postService;
    @Autowired
    UserService userService;
    @Autowired
    CommentService commentService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    SensitiveFilter sensitiveFilter;
    @Autowired
    MessageService messageService;
    @Autowired
    LikeService likeService;
    @Autowired
    EventProducer producer;
    @Autowired
    RedisTemplate redisTemplate;


    @RequestMapping(value = {"/","/index"},method = RequestMethod.GET)
    String toIndex(Model model, @RequestParam(name = "orderMode",defaultValue = "0") int orderMode){
        User user1 = hostHolder.getUser();
        List<Map<String, Object>> postMap = new ArrayList<>();
        PageHelper.startPage(0,5);
        List<DiscussPost> posts = postService.getPosts(orderMode,0);
        PageInfo<DiscussPost> postPage = new PageInfo<>(posts,5);
        int[] navigatepageNums = postPage.getNavigatepageNums();
        model.addAttribute("pageCount",navigatepageNums);
        model.addAttribute("MaxPage",postPage.getPages());
        model.addAttribute("currentPage",1);
        model.addAttribute("orderMode",orderMode);
        for (DiscussPost post:posts)
        {
            HashMap<String, Object> hashMap = new HashMap<>();
            User user = userService.getUserById(post.getUserId());
            hashMap.put("user",user);
            hashMap.put("p",post);
            long likeNum = likeService.getLikeNum(ENTITY_TYPE_POST, post.getId());
            hashMap.put("likeNum",likeNum);
            postMap.add(hashMap);
        }
        model.addAttribute("posts",postMap);
        if (user1!=null){
            int unreadMessageCount = messageService.getUnreadMessageCount(user1.getId(), null);
            int noticeCount = messageService.getUnreadNoticeCount(user1.getId(), null);
            model.addAttribute("unreadNoticeCount",noticeCount+unreadMessageCount);
        }
        return "/index";
    }
    @RequestMapping(value = {"/index/{pageNum}"},method = RequestMethod.GET)
    String toIndexByPage(Model model,@PathVariable("pageNum") int pageNum,  @RequestParam(name = "orderMode") int orderMode){
        User user1 = hostHolder.getUser();
        List<Map<String, Object>> postMap = new ArrayList<>();
        PageHelper.startPage(pageNum,5);
        List<DiscussPost> posts = postService.getPosts(orderMode,pageNum);
        PageInfo<DiscussPost> postPage = new PageInfo<>(posts,5);
        int[] navigatepageNums = postPage.getNavigatepageNums();
        model.addAttribute("pageCount",navigatepageNums);
        model.addAttribute("MaxPage",postPage.getPages());
        model.addAttribute("currentPage",pageNum);
        model.addAttribute("orderMode",orderMode);
        if (user1!=null){
            int unreadMessageCount = messageService.getUnreadMessageCount(user1.getId(), null);
            model.addAttribute("unreadMessageCount", unreadMessageCount);
        }
        if (posts != null)
        {
            for (DiscussPost post:posts)
            {
                HashMap<String, Object> hashMap = new HashMap<>();
                User user = userService.getUserById(post.getUserId());
                hashMap.put("user",user);
                hashMap.put("p",post);
                long likeNum = likeService.getLikeNum(ENTITY_TYPE_POST, post.getId());
                hashMap.put("likeNum",likeNum);
                postMap.add(hashMap);
            }
        }
        if (user1!=null){
            int unreadMessageCount = messageService.getUnreadMessageCount(user1.getId(), null);
            int noticeCount = messageService.getUnreadNoticeCount(user1.getId(), null);
            model.addAttribute("unreadNoticeCount",noticeCount+unreadMessageCount);
        }
        model.addAttribute("posts",postMap);
        return "/index";
    }
    @ResponseBody
    @RequestMapping(value = "/post",method = RequestMethod.POST)
    public String sendPost(String title,String content)
    {
        User user = hostHolder.getUser();
        if (user == null)
        {
            return CommonUtil.getJsonObj(403,"请登陆后再发帖！");
        }
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        content= HtmlUtils.htmlEscape(content);
        post.setContent(sensitiveFilter.filter(content));
        title = HtmlUtils.htmlEscape(title);
        post.setTitle(sensitiveFilter.filter(title));
        post.setCreateTime(new Date());
        postService.addPost(post);
        Event event = new Event().setTopic(TOPIC_PUBLISH)
                                .setEntityId(post.getId())
                                .setEntityType(ENTITY_TYPE_POST)
                                .setUserId(post.getUserId())
                                .setEntityAuthorId(post.getUserId());
        producer.produceEvent(event);
        //将新发的帖子放入待计算分数的集合中
        SetOperations opsForSet = redisTemplate.opsForSet();
        String scorePostKey = RedisUtil.getScorePostKey();
        opsForSet.add(scorePostKey,post.getId());
        return CommonUtil.getJsonObj(0,"发帖成功");
    }
    @RequestMapping(value = "/singlePost/{pid}",method = RequestMethod.GET)
    public String getSinglePost(@PathVariable("pid") int id, Model model){
        DiscussPost discussPost = postService.getDiscussPost(id);
        User userById = userService.getUserById(discussPost.getUserId());
        long postLikeNum = likeService.getLikeNum(ENTITY_TYPE_POST, discussPost.getId());
        model.addAttribute("postLikeNum",postLikeNum);
        model.addAttribute("singlePost",discussPost);
        model.addAttribute("author",userById);
        PageHelper.startPage(0,5);
        List<Comment> postComments = commentService.getComment(ENTITY_TYPE_POST, id);
        PageInfo<Comment> postPage = new PageInfo<>(postComments,5);
        int[] navigatepageNums = postPage.getNavigatepageNums();
        model.addAttribute("CommentMaxPage",postPage.getPages());
        model.addAttribute("CommentPageCount",navigatepageNums);
        model.addAttribute("CommentCurrentPage",1);
        if(postComments!=null)
        {
            List<Map<String,Object>> commentList = new ArrayList<>();
            for(Comment comment:postComments)
            {
                //封装帖子的评论
                Map<String,Object> infoMap = new HashMap<>();
                infoMap.put("comment",comment);
                infoMap.put("commentUser",userService.getUserById(comment.getUserId()));
                long num = likeService.getLikeNum(ENTITY_TYPE_COMMENT, comment.getId());
                infoMap.put("commentLikeNum",num);
                //封装评论的回复
                List<Comment> replyComments = commentService.getComment(ENTITY_TYPE_COMMENT, comment.getId());
                if(replyComments != null)
                {
                    List<Map<String,Object>> replyList = new ArrayList<>();
                    for(Comment reply:replyComments)
                    {
                        Map<String,Object> replyMap = new HashMap<>();
                        replyMap.put("reply",reply);
                        replyMap.put("sender",userService.getUserById(reply.getUserId()));
                        User receiver = reply.getTargetId()==0?null:userService.getUserById(reply.getTargetId());
                        replyMap.put("receiver",receiver);
                        long likeNum = likeService.getLikeNum(ENTITY_TYPE_COMMENT, reply.getId());
                        replyMap.put("likeNum",likeNum);
                        replyList.add(replyMap);
                    }
                    infoMap.put("replys",replyList);
                }
                int count = commentService.getCountOfComment(ENTITY_TYPE_COMMENT, comment.getId());
                infoMap.put("replyCount",count);
                commentList.add(infoMap);
            }
            User user1 = hostHolder.getUser();
            if (user1!=null){
                int unreadMessageCount = messageService.getUnreadMessageCount(user1.getId(), null);
                int noticeCount = messageService.getUnreadNoticeCount(user1.getId(), null);
                model.addAttribute("unreadNoticeCount",noticeCount+unreadMessageCount);
            }
            model.addAttribute("comments",commentList);
        }
        return "/site/discuss-detail";
    }
    @RequestMapping(value = "/singlePost/{pid}/{pageNum}",method = RequestMethod.GET)
    public String getSinglePost(@PathVariable("pid") int id, @PathVariable("pageNum")int pageNum,Model model){
        DiscussPost discussPost = postService.getDiscussPost(id);
        User userById = userService.getUserById(discussPost.getUserId());
        long postLikeNum = likeService.getLikeNum(ENTITY_TYPE_POST, discussPost.getId());
        model.addAttribute("postLikeNum",postLikeNum);
        model.addAttribute("singlePost",discussPost);
        model.addAttribute("author",userById);
        PageHelper.startPage(pageNum,5);
        List<Comment> postComments = commentService.getComment(ENTITY_TYPE_POST, id);
        PageInfo<Comment> postPage = new PageInfo<>(postComments,5);
        int[] navigatepageNums = postPage.getNavigatepageNums();
        model.addAttribute("CommentMaxPage",postPage.getPages());
        model.addAttribute("CommentCurrentPage",pageNum);
        model.addAttribute("CommentPageCount",navigatepageNums);
        List<Map<String,Object>> commentList = new ArrayList<>();
        for(Comment comment:postComments)
        {
            //封装帖子的评论
            Map<String,Object> infoMap = new HashMap<>();
            infoMap.put("comment",comment);
            long num = likeService.getLikeNum(ENTITY_TYPE_COMMENT, comment.getId());
            infoMap.put("commentLikeNum",num);
            infoMap.put("commentUser",userService.getUserById(comment.getUserId()));
            //封装评论的回复
            List<Comment> replyComments = commentService.getComment(ENTITY_TYPE_COMMENT, comment.getId());
            if(replyComments != null)
            {
                List<Map<String,Object>> replyList = new ArrayList<>();
                for(Comment reply:replyComments)
                {
                    Map<String,Object> replyMap = new HashMap<>();
                    replyMap.put("reply",reply);
                    replyMap.put("sender",userService.getUserById(reply.getUserId()));
                    User receiver = reply.getTargetId()==0?null:userService.getUserById(reply.getTargetId());
                    replyMap.put("receiver",receiver);
                    long likeNum = likeService.getLikeNum(ENTITY_TYPE_COMMENT, reply.getId());
                    replyMap.put("likeNum",likeNum);
                    replyList.add(replyMap);
                }
                infoMap.put("replys",replyList);
            }
            int count = commentService.getCountOfComment(ENTITY_TYPE_COMMENT, comment.getId());
            infoMap.put("replyCount",count);
            commentList.add(infoMap);
        }
        User user1 = hostHolder.getUser();
        if (user1!=null){
            int unreadMessageCount = messageService.getUnreadMessageCount(user1.getId(), null);
            int noticeCount = messageService.getUnreadNoticeCount(user1.getId(), null);
            model.addAttribute("unreadNoticeCount",noticeCount+unreadMessageCount);
        }
        model.addAttribute("comments",commentList);
        return "/site/discuss-detail";
    }
    @RequestMapping(value = "/wonder",method = RequestMethod.POST)
    @ResponseBody
    public String updatePostStatus(int pid){
        DiscussPost post = postService.getDiscussPost(pid);
        Event event = new Event().setTopic(TOPIC_PUBLISH)
                .setEntityId(post.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setUserId(post.getUserId())
                .setEntityAuthorId(post.getUserId());
        producer.produceEvent(event);
        postService.updatePostStatus(pid,STATUS_WONDER);
        String scorePostKey = RedisUtil.getScorePostKey();
        SetOperations opsForSet = redisTemplate.opsForSet();
        opsForSet.add(scorePostKey,pid);
        return CommonUtil.getJsonObj(0);
    }
    @RequestMapping(value = "/top",method = RequestMethod.POST)
    @ResponseBody
    public String topPost(int pid){
        DiscussPost post = postService.getDiscussPost(pid);
        Event event = new Event().setTopic(TOPIC_PUBLISH)
                .setEntityId(post.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setUserId(post.getUserId())
                .setEntityAuthorId(post.getUserId());
        producer.produceEvent(event);
        postService.updatePostType(pid,TYPE_TOP);
        return CommonUtil.getJsonObj(0);
    }
    @RequestMapping(value = "/del",method = RequestMethod.POST)
    @ResponseBody
    public String delPost(int pid)
    {
        DiscussPost post = postService.getDiscussPost(pid);
        Event event = new Event().setTopic(TOPIC_DELETE)
                .setEntityId(post.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setUserId(post.getUserId())
                .setEntityAuthorId(post.getUserId());
        producer.produceEvent(event);
        postService.deletePost(post.getId());
        return CommonUtil.getJsonObj(0);
    }
    @RequestMapping(value = "/myPost/{pageNum}",method = RequestMethod.GET)
    public String toMyPosts(@PathVariable("pageNum") int pageNum,Model model)
    {
        PageHelper.startPage(pageNum,5);
        List<DiscussPost> posts = postService.myPosts(hostHolder.getUser().getId());
        PageInfo<DiscussPost> postPage = new PageInfo<>(posts,5);
        int[] navigatepageNums = postPage.getNavigatepageNums();
        model.addAttribute("pageCount",navigatepageNums);
        model.addAttribute("MaxPage",postPage.getPages());
        model.addAttribute("currentPage",pageNum);
        model.addAttribute("user",hostHolder.getUser());
        List<Map<String,Object>> myPostList = new ArrayList<>();
        for(DiscussPost post:posts)
        {
            Map<String,Object> map = new HashMap<>();
            map.put("post",post);
            map.put("likeNum",likeService.getLikeNum(ENTITY_TYPE_POST,post.getId()));
            myPostList.add(map);
        }
        model.addAttribute("postNum",myPostList.size());
        model.addAttribute("myPosts",myPostList);
        model.addAttribute("myCount",postService.getPostCount(hostHolder.getUser().getId()));
        return "/site/my-post";
    }




}
