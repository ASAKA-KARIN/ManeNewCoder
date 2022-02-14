package com.newcoder.community.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.newcoder.community.pojo.DiscussPost;
import com.newcoder.community.pojo.User;
import com.newcoder.community.service.PostService;
import com.newcoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yoshino
 */
@Controller
public class PostController {
    @Autowired
    PostService postService;
    @Autowired
    UserService userService;

    @RequestMapping(value = {"/","/index"},method = RequestMethod.GET)
    String toIndex(Model model){
        List<Map<String, Object>> postMap = new ArrayList<>();
        PageHelper.startPage(0,5);
        List<DiscussPost> posts = postService.getPosts();
        PageInfo<DiscussPost> postPage = new PageInfo<>(posts,5);
        int[] navigatepageNums = postPage.getNavigatepageNums();
        model.addAttribute("pageCount",navigatepageNums);
        if (posts != null)
        {
            for (DiscussPost post:posts)
            {
                HashMap<String, Object> hashMap = new HashMap<>();
                User user = userService.getUserById(post.getUserId());
                hashMap.put("user",user);
                hashMap.put("p",post);
                postMap.add(hashMap);
            }
        }
        model.addAttribute("posts",postMap);
        return "index";
    }
    @RequestMapping(value = {"/index/{pageNum}"},method = RequestMethod.GET)
    String toIndexByPage(Model model,@PathVariable("pageNum") int pageNum){
        List<Map<String, Object>> postMap = new ArrayList<>();
        PageHelper.startPage(pageNum,5);
        List<DiscussPost> posts = postService.getPosts();
        PageInfo<DiscussPost> postPage = new PageInfo<>(posts,5);
        int[] navigatepageNums = postPage.getNavigatepageNums();
        model.addAttribute("pageCount",navigatepageNums);
        model.addAttribute("MaxPage",postPage.getPages());
        model.addAttribute("currentPage",pageNum);
        if (posts != null)
        {
            for (DiscussPost post:posts)
            {
                HashMap<String, Object> hashMap = new HashMap<>();
                User user = userService.getUserById(post.getUserId());
                hashMap.put("user",user);
                hashMap.put("p",post);
                postMap.add(hashMap);
            }
        }
        model.addAttribute("posts",postMap);
        return "index";
    }


}
