package com.newcoder.community.service;

import com.newcoder.community.dao.DiscussPostMapper;
import com.newcoder.community.pojo.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Yoshino
 */
@Service
public class PostService {
    @Autowired
    private DiscussPostMapper postMapper;
    public List<DiscussPost> getPosts()
    {
        return  postMapper.getPosts(0);
    }

}
