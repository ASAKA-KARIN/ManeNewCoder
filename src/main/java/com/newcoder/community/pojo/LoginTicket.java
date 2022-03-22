package com.newcoder.community.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 86156
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoginTicket implements Serializable {
    private int id;
    private int userId;
    private String ticket;
    private int status;
    private Date expired;


}
