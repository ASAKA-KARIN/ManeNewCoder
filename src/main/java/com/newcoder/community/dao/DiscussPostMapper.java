package com.newcoder.community.dao;

import com.newcoder.community.pojo.DiscussPost;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Yoshino
 */
@Mapper
public interface DiscussPostMapper {
    /**
     *
     * @param userId
     * @param orderMode 0正常查询 1按热度查询
     * @return
     */
    List<DiscussPost> getPosts(int userId,int orderMode);
    int getPostsCount(int userId);
    void insertPost(DiscussPost post);
    DiscussPost getPost(int id);
    void updatePostCount(int id,int count);
    void updateType(int id,int type);
    void updateStatus(int id,int status);
    void deletePostById(int id);
    void updatePostScore(int id,double score);
    List<DiscussPost> getMyPost(int userId);
}
