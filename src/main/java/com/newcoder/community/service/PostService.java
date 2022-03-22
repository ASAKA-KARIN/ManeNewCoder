package com.newcoder.community.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.newcoder.community.dao.DiscussPostMapper;
import com.newcoder.community.pojo.DiscussPost;
import com.newcoder.community.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Yoshino
 */
@Service
public class PostService {
    private final static Logger logger = LoggerFactory.getLogger(PostService.class);
    @Autowired
    private DiscussPostMapper postMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Value("${caffeine.maximum}")
    private int maximum;
    @Value("${caffeine.expire}")
    private int expiredTime;
    LoadingCache<String,List<DiscussPost>> postCache;
    @PostConstruct
    void init(){
        postCache = Caffeine.newBuilder().maximumSize(maximum).expireAfterWrite(expiredTime, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
            @Override
            public @Nullable List<DiscussPost> load(String s) throws Exception {
                if (StringUtils.isBlank(s))
                {
                    throw new IllegalArgumentException("参数为空");
                }
                //首先从redis中获取数据
                SetOperations opsForSet = redisTemplate.opsForSet();
                List<DiscussPost> postList = (List<DiscussPost>) opsForSet.pop(s, 5).get(0);
                if (postList != null&&postList.size()!=0)
                {
                    logger.debug("initialize posts from redis....");
                    return  postList;
                }
                //redis中获取不到，从DB中获取，并存一份至redis
                logger.debug("initialize posts from db....");
                List<DiscussPost> posts = postMapper.getPosts(0, 1);
                opsForSet.add(s,posts);
                return posts;
            }
        });
    }
    public List<DiscussPost> getPosts(int orderMode,int pageNum)
    {
        if (orderMode == 1)
        {
            String cacheKey = RedisUtil.getCachePostKey(pageNum);
            List<DiscussPost> discussPosts = postCache.get(cacheKey);
            if (discussPosts != null&&discussPosts.size()!=0)
            {
                return  discussPosts;
            }
        }
        logger.debug("get posts from DB");
        return  postMapper.getPosts(0,orderMode);
    }
    public  void addPost(DiscussPost post)
    { postMapper.insertPost(post);}
    public DiscussPost getDiscussPost(int id)
    {
        return postMapper.getPost(id);
    }
    public void updatePostType(int id,int type)
    {
        postMapper.updateType(id,type);
    }
    public void updatePostStatus(int id,int status)
    {
        postMapper.updateStatus(id,status);
    }
    public void deletePost(int id)
    {
        postMapper.deletePostById(id);
    }
    public void refreshPostScore(int id,double score)
    {postMapper.updatePostScore(id,score);}
    public List<DiscussPost> myPosts(int userId){
        return postMapper.getMyPost(userId);
    }
    public int getPostCount(int userId)
    {return postMapper.getPostsCount(userId);}



}
