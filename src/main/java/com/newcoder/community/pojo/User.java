package com.newcoder.community.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Yoshino
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
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
    private Date createTime;
}
