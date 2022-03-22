package com.newcoder.community.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * @author 86156
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private int id;
    private int userId;
    /**
     * 1-对评论的回复
     * 2-对帖子的评论
     */
    private int entityType;
    private int entityId;
    private int targetId;
    private String content ;
    /**
     * 0-普通
     * 1-置顶
     * 2-拉黑
     */
    private int status ;
    private Date createTime;
}
