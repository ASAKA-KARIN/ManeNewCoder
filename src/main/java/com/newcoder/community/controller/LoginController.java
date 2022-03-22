package com.newcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.newcoder.community.pojo.User;
import com.newcoder.community.service.CommunityConst;
import com.newcoder.community.service.UserService;
import com.newcoder.community.util.CommonUtil;
import com.newcoder.community.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author 86156
 */
@Controller
public class LoginController {
    @Autowired
    private UserService userService;
    @Autowired
    RedisTemplate redisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private Producer producer;

    @Value("${server.servlet.context-path}")
    private String path;

    @RequestMapping(value = "/register",method = RequestMethod.GET)
    public String toRegister(){
        return "/site/register";
    }
    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public String toLogin(){
        return "/site/login";
    }

    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public String registerUser(Model model, User user)
    {
        Map<String, Object> map = userService.register(user);
        if (map.isEmpty()||map == null)
        {
            model.addAttribute("msg","注册成功,我们已经成功发送了一封激活邮件至您的邮箱" +
                    "，请尽快激活");
            model.addAttribute("target","/community/index");
            return "/site/operate-result";
        }
        else
        {
            model.addAttribute("userErr",map.get("usernameErr"));
            model.addAttribute("pwdErr",map.get("pwdErr"));
            model.addAttribute("emailErr",map.get("emailErr"));
            model.addAttribute("user",user);
            return "/site/register";
        }
    }
    @RequestMapping( value = "/activation/{id}/{activation}",method = RequestMethod.GET)
    public String activateUser(Model model, @PathVariable("id") int id,@PathVariable("activation") String activationCode)
    {
        int res = userService.Activate(id, activationCode);
        if(res == CommunityConst.ACTIVATION_SUCCESS)
        {
            model.addAttribute("msg","激活成功，请登录");
            model.addAttribute("target","/community/login");
            return "/site/operate-result";
        }else if(res == CommunityConst.ACTIVATION_REPEAT)
        {
            model.addAttribute("msg","该账号已激活，请勿重复激活");
            model.addAttribute("target","/community/login");
            return "/site/operate-result";
        }else
        {
            model.addAttribute("msg","激活失败，请检查激活码是否正确");
            model.addAttribute("target","/community/login");
            return "/site/operate-result";
        }
    }
    @RequestMapping(value = "/kaptcha", method = RequestMethod.GET)
    void generateKaptcha(HttpServletResponse resp){
        String text = producer.createText();
        BufferedImage image = producer.createImage(text);
        String owner = CommonUtil.createCode();
        Cookie cookie = new Cookie("kapchaOwner", owner);
        cookie.setMaxAge(60);
        resp.addCookie(cookie);
        redisTemplate.opsForValue().set(RedisUtil.getKaptchaKey(owner),text,60, TimeUnit.SECONDS);
        resp.setContentType("image/png");
        try {
            ServletOutputStream outputStream = resp.getOutputStream();
            ImageIO.write(image,"png",outputStream);
        } catch (IOException ioException) {
            logger.error("生成验证码出错！"+ioException.getMessage());
        }
    }
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    String login(String username, String password, String code, boolean rememberMe,
                 @CookieValue("kapchaOwner") String owner, HttpServletResponse resp, Model model){
        if (StringUtils.isNotBlank(owner))
        {
            String kaptchaKey = RedisUtil.getKaptchaKey(owner);
            String kaptcha = (String)redisTemplate.opsForValue().get(kaptchaKey);
            if (StringUtils.isBlank(code)||StringUtils.isBlank(kaptcha)||!kaptcha.equalsIgnoreCase(code)){
                model.addAttribute("err","验证码错误！");
                return "/site/login";
            }
        }else
        {
            model.addAttribute("err","验证码已过期，请刷新后再试");
            return "/site/login";
        }

        int expiredSec = rememberMe? CommunityConst.LONG_EXPIRED_TIME: CommunityConst.DEFAULT_EXPIRED_TIME;
        Map<String, Object> login = userService.login(username, password, expiredSec);
        if (login.containsKey("ticket")){
            Cookie cookie = new Cookie("ticket",(String) login.get("ticket"));
            cookie.setMaxAge(expiredSec);
            cookie.setPath(path);
            resp.addCookie(cookie);
            return "redirect:/index";
        }else
        {
            model.addAttribute("userErr",login.get("userErr"));
            model.addAttribute("pwdErr", login.get("pwdErr"));
            return "/site/login";
        }
    }

    @RequestMapping(value = "/logout",method = {RequestMethod.GET,RequestMethod.POST})
    public String logout(HttpServletRequest request,HttpServletResponse response)
    {
        Cookie[] cookies = request.getCookies();
        Cookie ticket = CommonUtil.searchCookie(cookies, "ticket");
        userService.logout(ticket.getValue());
        ticket.setMaxAge(-1);
        response.addCookie(ticket);
        SecurityContextHolder.clearContext();
        return "/site/login";
    }
    @RequestMapping("/err")
    public String toErr(){
        return "/error/500";
    }
    @RequestMapping(value = "/forget",method = RequestMethod.GET)
    public String toForget(){
        return "/site/forget";
    }
    @RequestMapping(value = "/verifyCode",method = RequestMethod.POST)
    public String getVerifyCode(String email,Model model)
    {
        Map map = userService.sendVerifyCode(email);
        if (map != null)
        {
            return CommonUtil.getJsonObj(1, (String) map.get("mailErr"));
        }
        return CommonUtil.getJsonObj(0);
    }
    @RequestMapping(value = "/forget",method = RequestMethod.POST)
    public String findPwd(String newPassword,String email,String code,Model model){
        ValueOperations opsForValue = redisTemplate.opsForValue();
        String trueCode = (String) opsForValue.get(RedisUtil.getForgetKey(email));
        if (StringUtils.isBlank(trueCode) || !trueCode.equals(code))
        {
             model.addAttribute("codeErr","验证码有误或已过期！");
             return "/site/forget";
        }
        User user = userService.getUserByMail(email);
        userService.updateUserPwd(user.getId(),newPassword);
        return "/site/login";
    }


}
