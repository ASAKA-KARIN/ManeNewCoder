package com.newcoder.community.dao;

import com.newcoder.community.pojo.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Yoshino
 */
@Mapper
public interface UserMapper {
    User getUserById(int userId);

}
