package com.newcoder.community.dao;

import com.newcoder.community.pojo.LoginTicket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 86156
 */
@Mapper
@Deprecated
public interface LoginTicketMapper {
    void insertLoginTicket(LoginTicket loginTicket);
    LoginTicket getLoginTicket(String ticket);
    void updateStatus(int status,String ticket);

}
