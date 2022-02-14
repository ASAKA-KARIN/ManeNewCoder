package com.newcoder.community.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.sql.Timestamp;

/**
 * @author Yoshino
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private int id;
    private String username;
    private String password;
    private String salt;
    private String email;
    /**
     * 0-普通用户
     * 1-超级管理员
     * 2-版主
     */
    private int type;
    /**
     * 0-未激活 1-已激活
     */
    private int status;
    private String activationCode;
    private String headerUrl;
    private Timestamp createTime;
}
