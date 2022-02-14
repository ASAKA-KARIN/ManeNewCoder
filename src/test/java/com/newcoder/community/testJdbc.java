package com.newcoder.community;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.newcoder.community.dao.DiscussPostMapper;
import com.newcoder.community.dao.UserMapper;
import com.newcoder.community.pojo.DiscussPost;
import com.newcoder.community.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class testJdbc {
    @Autowired
    DiscussPostMapper discussPostMapper;
    @Autowired
    UserMapper userMapper;

    @Test
    void test() {
        System.out.println("test");
    }
    @Test
    void getPosts()
    {
        //List<DiscussPost> posts = discussPostMapper.getPosts(0);
        PageHelper.startPage(1,5);
        List<DiscussPost> posts = discussPostMapper.getPosts(0);
        PageInfo<DiscussPost> postPage = new PageInfo<>(posts);
        System.out.println(Arrays.toString(postPage.getNavigatepageNums()));
        System.out.println(postPage.getSize());
        for(DiscussPost post:posts)
        {
            System.out.println(post);
        }

    }
    @Test
    void TestUser()
    {
        User userById = userMapper.getUserById(101);
        System.out.println(userById);
    }

    
}
