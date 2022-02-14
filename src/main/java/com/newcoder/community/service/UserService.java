package com.newcoder.community.service;

import com.newcoder.community.dao.UserMapper;
import com.newcoder.community.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 86156
 */
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User getUserById(int id) {
        return userMapper.getUserById(id);
    }

}
