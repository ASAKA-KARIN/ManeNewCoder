package com.newcoder.community.controller;

import com.newcoder.community.annotation.LoginRequire;
import com.newcoder.community.pojo.User;
import com.newcoder.community.service.CommunityConst;
import com.newcoder.community.service.FollowService;
import com.newcoder.community.service.LikeService;
import com.newcoder.community.service.UserService;
import com.newcoder.community.util.CommonUtil;
import com.newcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.jws.WebParam;
import javax.mail.Multipart;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author 86156
 */
@Controller
@RequestMapping("/user")
public class UserController implements CommunityConst {

    private final static Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    HostHolder hostHolder;
    @Autowired
    UserService userService;
    @Value("${comuunity.path.filePath}")
    String uploadPath;
    @Value("${community.path.domain}")
    String domain;
    @Value("${server.servlet.context-path}")
    String contextPath;
    @Autowired
    LikeService likeService;
    @Autowired
    FollowService followService;

    @LoginRequire
    @RequestMapping(value = "/setting", method = RequestMethod.GET)
    public String toSetting() {
        return "/site/setting";
    }

    @LoginRequire
    @RequestMapping(value = "/headerUrl", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile file, Model model) {
        String filename = file.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf('.'));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("fileErr", "传入的头像不合法！请重新选择");
            return "/site/setting";
        }
        String newFileName = CommonUtil.createCode().substring(0, 5) + suffix;
        File dest = new File(uploadPath + "/" + newFileName);
        try {
            file.transferTo(dest);
        } catch (IOException ioException) {
            logger.error("文件上传失败！" + ioException.getMessage());
            throw new RuntimeException("文件上传异常！！");
        }
        User user = hostHolder.getUser();
        String headUrl = domain + contextPath + "/user/headerUrl/" + newFileName;
        user.setHeaderUrl(headUrl);
        userService.uploadHeaderUrl(user);
        return "redirect:/index";
    }

    @RequestMapping(value = "/headerUrl/{fileName}", method = RequestMethod.GET)
    public void getHeader(HttpServletResponse resp, @PathVariable("fileName") String fileName) {
        fileName = uploadPath+"/"+fileName;
        try(
        FileInputStream fileInputStream  = new FileInputStream(fileName);
        ServletOutputStream outputStream = resp.getOutputStream();
        ){
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b=fileInputStream.read(buffer))!= -1)
            {
                outputStream.write(buffer,0,b);
            }
        }catch (IOException e)
        {
            logger.error("获取用户头像失败！！"+e.getMessage());
            throw new RuntimeException("获取头像失败"+e.getMessage());
        }
    }
    @RequestMapping(value = "/profile/{userId}",method = RequestMethod.GET)
    public String toProfile(Model model,@PathVariable("userId")int userId)
    {
        User user = userService.getUserById(userId);
        model.addAttribute("user",user);
        int likeNumOfUser = likeService.getLikeNumOfUser(userId);
        model.addAttribute("likeNumOfUser",likeNumOfUser);
        int fansCount = Math.toIntExact(followService.getFansCount(ENTITY_TYPE_USER, userId));
        model.addAttribute("fansCount",fansCount);
        int followCount = Math.toIntExact(followService.getFollowCount(ENTITY_TYPE_USER, userId));
        model.addAttribute("followCount",followCount);
        User localUser = hostHolder.getUser();
        if (localUser != null)
        {
            boolean followed = followService.isFollowed(ENTITY_TYPE_USER, userId, localUser.getId());
            model.addAttribute("isFollowed",followed);
        }else
        {
            model.addAttribute("isFollowed",false);
        }
        return "/site/profile";
    }
    @RequestMapping(value = "/follower/{userId}",method = RequestMethod.GET)
    public String toFollower(Model model,@PathVariable("userId") int userId)
    {
        User hostHolderUser = hostHolder.getUser();
        model.addAttribute("loginUser",hostHolderUser);
        User user = userService.getUserById(userId);
        model.addAttribute("user",user);
        List<Map<String, Object>> fansList = followService.getFansList(ENTITY_TYPE_USER, userId,hostHolderUser.getId());
        model.addAttribute("fans",fansList);
        return "/site/follower";
    }

    @RequestMapping(value = "/followee/{userId}",method = RequestMethod.GET)
    public String toFollowee(Model model,@PathVariable("userId") int userId)
    {
        User hostHolderUser = hostHolder.getUser();
        model.addAttribute("loginUser",hostHolderUser);
        User user = userService.getUserById(userId);
        model.addAttribute("user",user);
        List<Map<String, Object>> fansList = followService.getFollowList(ENTITY_TYPE_USER, userId,hostHolderUser.getId());
        model.addAttribute("follows",fansList);
        return "/site/followee";
    }
    @RequestMapping(value = "/password",method = RequestMethod.POST)
    public String changePassword(String oldPwd,String newPwd,String confirmPwd,Model model){
        if (!newPwd.equals(confirmPwd))
        {
            model.addAttribute("confirmErr","两次密码必须一致！！");
            return "/site/setting";
        }
        User user = hostHolder.getUser();
        if (!user.getPassword().equals(CommonUtil.MD5(oldPwd+user.getSalt())))
        {
            model.addAttribute("pwdErr","原密码错误！！");
            return "/site/setting";
        }
        userService.updateUserPwd(user.getId(),newPwd);
        return "/site/login";
    }
}

