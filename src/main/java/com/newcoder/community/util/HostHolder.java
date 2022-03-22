package com.newcoder.community.util;

import com.newcoder.community.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 86156
 * 存储各个线程相互独立的数据
 * 用来代替Session
 */
@Component
public class HostHolder {
    private ThreadLocal<User> localUser = new ThreadLocal<>();
    public void setUser(User user)
    {
        localUser.set(user);
    }
    public User getUser()
    {
        return localUser.get();
    }
    public  void clear()
    {
        localUser.remove();
    }

 }
