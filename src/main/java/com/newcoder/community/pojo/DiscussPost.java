package com.newcoder.community.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @author Yoshino
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class DiscussPost {
    private int id;
    private int userId;
    private String title;
    private String content;
    /**
     * * 0-普通
     * * 1-置顶
     */
    private int type;
    /**
     * * 0-正常
     * * 1-精华
     * * 2-拉黑
     */
    private int status;
    private Date createTime;
    private int commentCount;
    private double score;
}
