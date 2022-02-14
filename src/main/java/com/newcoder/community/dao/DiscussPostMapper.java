package com.newcoder.community.dao;

import com.newcoder.community.pojo.DiscussPost;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Yoshino
 */
@Mapper
public interface DiscussPostMapper {

    List<DiscussPost> getPosts(int userId);
    int getPostsCount(int userId);
}
