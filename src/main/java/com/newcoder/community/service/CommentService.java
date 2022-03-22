package com.newcoder.community.service;

import com.newcoder.community.dao.CommentMapper;
import com.newcoder.community.dao.DiscussPostMapper;
import com.newcoder.community.pojo.Comment;
import com.newcoder.community.pojo.DiscussPost;
import com.newcoder.community.util.SensitiveFilter;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author Yoshino
 */
@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private DiscussPostMapper postMapper;
    @Autowired
    private SensitiveFilter filter;

    public List<Comment> getComment(int entityType,int entityId)
    {
        return  commentMapper.getComment(entityType,entityId);
    }
    public int getCountOfComment(int entityType,int entityId)
    {
        return commentMapper.getCommentCount(entityType,entityId);
    }
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public void addComment(Comment comment)
    {
        comment.setContent(filter.filter(comment.getContent()));
        commentMapper.addComment(comment);
        if (comment.getEntityType() == CommunityConst.ENTITY_TYPE_POST) {
            DiscussPost post = postMapper.getPost(comment.getEntityId());
            postMapper.updatePostCount(post.getId(), post.getCommentCount() + 1);

        }
    }
    public Comment getCommentById(int commentId){
        return commentMapper.getCommentById(commentId);
    }
    public List<Comment> myComments(int userId){return  commentMapper.getMyComment((userId));}
    public int getMyCommentCount(int userId){return  commentMapper.getMyCommentCount(userId);}


}
