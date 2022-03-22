package com.newcoder.community.dao;

import com.newcoder.community.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author 86156
 */
@Mapper
public interface CommentMapper {
    List<Comment> getComment(int entityType,int entityId);
    int getCommentCount(int entityType,int entityId);
    void addComment(Comment comment);
    Comment getCommentById(int id);
    List<Comment> getMyComment(int userId);
    int getMyCommentCount(int userId);
}
