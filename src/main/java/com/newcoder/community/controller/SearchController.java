package com.newcoder.community.controller;

import com.github.pagehelper.PageInfo;
import com.newcoder.community.dao.EsRepository;
import com.newcoder.community.pojo.DiscussPost;
import com.newcoder.community.pojo.User;
import com.newcoder.community.service.CommunityConst;
import com.newcoder.community.service.LikeService;
import com.newcoder.community.service.PostSearchService;
import com.newcoder.community.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yoshino
 */
@Controller
public class SearchController implements CommunityConst {
    @Autowired
    PostSearchService searchService;
    @Autowired
    LikeService likeService;
    @Autowired
    UserService userService;

    @RequestMapping(value = "/search",method = RequestMethod.GET)
    public String getSearchRes(String keyword, Model model){
        if (StringUtils.isBlank(keyword))
        {
            return "/index";
        }
        List<DiscussPost> postList = searchService.searchPost(keyword);
        if (postList != null&&postList.size() != 0) {
            List<Map<String, Object>> res = new ArrayList<>();
            for (DiscussPost post : postList) {
                Map<String, Object> postMap = new HashMap<>(6);
                postMap.put("post", post);
                long likeNum = likeService.getLikeNum(ENTITY_TYPE_POST, post.getId());
                postMap.put("likeNum", likeNum);
                User user = userService.getUserById(post.getUserId());
                postMap.put("author", user);
                res.add(postMap);
            }
            model.addAttribute("searchResult", res);
        }
        return "/site/search";
    }
}
