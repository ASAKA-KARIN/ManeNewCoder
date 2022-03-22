package com.newcoder.community.dao;

import com.newcoder.community.pojo.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Yoshino
 */
@Mapper
public interface UserMapper {
    User getUserById(int userId);
    User getUserByName(String username);
    User getUserByEmail(String email);
    void insertUser(User user);
    void updateUser(User user);
    void updateUserPwd(int userId,String password);

}
