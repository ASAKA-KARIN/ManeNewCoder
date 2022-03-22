package com.newcoder.community.Quartz;

import com.newcoder.community.pojo.DiscussPost;
import com.newcoder.community.service.CommunityConst;
import com.newcoder.community.service.LikeService;
import com.newcoder.community.service.PostSearchService;
import com.newcoder.community.service.PostService;
import com.newcoder.community.util.RedisUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 86156
 */
public class ScoreJob implements Job {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private final static Logger logger = LoggerFactory.getLogger(ScoreJob.class);
    @Autowired
    private LikeService likeService;
    @Autowired
    private PostSearchService searchService;
    @Autowired
    private PostService postService;
    private final static Date NEW_CODER_EPOCH;
    static {
        try {
            NEW_CODER_EPOCH = new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2019-8-28 00:00:00");
        } catch (ParseException e) {
            logger.error("初始化纪元时间失败");
            throw  new RuntimeException("初始化纪元时间失败");
        }
    }
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String scorePostKey = RedisUtil.getScorePostKey();
        BoundSetOperations operations = redisTemplate.boundSetOps(scorePostKey);
        if (operations.size()==0)
        {
            logger.info("[任务取消] 无需要执行refresh方法的实体");
            return;
        }
        logger.info("[任务开始] 开始执行refresh方法");
        while (operations.size()>0)
        {
            refresh((Integer)operations.pop());
        }
        logger.info("[任务结束] 已刷新完所有需要刷新的实体");
    }

    public void refresh(int postId){
        DiscussPost discussPost = postService.getDiscussPost(postId);
        if (discussPost == null)
        {
            logger.error("帖子不存在或已被删除！！");
        }
        Date createTime = discussPost.getCreateTime();
        boolean isWonder = discussPost.getStatus()==1;
        int commentCount = discussPost.getCommentCount();
        long likeNum = likeService.getLikeNum(CommunityConst.ENTITY_TYPE_POST, postId);
        // 计算权重:精华分+点赞数*2+评论数*10
        double weight = isWonder?70:0+likeNum*2+commentCount*10;
        double score = Math.log10(Math.max(weight,1))+((createTime.getTime()-NEW_CODER_EPOCH.getTime())/1000*3600*24);
        discussPost.setScore(score);
        postService.refreshPostScore(postId,score);
        searchService.updatePost(discussPost);
    }
}
