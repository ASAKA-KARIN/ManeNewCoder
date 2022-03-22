package com.newcoder.community.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * @author 86156
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class Message {
    private int id;
    private int fromId;
    private int toId;
    private String conversationId ;
    private String content ;
    /**
     * 0-未读
     * 1-已读
     * 2-删除
     */
    private int  status;
    private Date createTime;

}
