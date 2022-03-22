package com.newcoder.community.service;

import com.newcoder.community.dao.UserMapper;
import com.newcoder.community.pojo.LoginTicket;
import com.newcoder.community.pojo.User;
import com.newcoder.community.util.CommonUtil;
import com.newcoder.community.util.MailClient;
import com.newcoder.community.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author 86156
 */
@Service
public class UserService implements CommunityConst {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String path;
    @Autowired
    private RedisTemplate redisTemplate;

    public User getUserById(int id) {
        User user = getUserCache(id);
        if (user == null) {
            user = userMapper.getUserById(id);
            initUserCache(user);
        }
        return user;
    }
    public User getUserByMail(String mail){
        return userMapper.getUserByEmail(mail);
    }
    public LoginTicket getTicketByTicket(String ticket) {
        if (!StringUtils.isBlank(ticket)) {
            return (LoginTicket) redisTemplate.opsForValue().get(RedisUtil.getLoginTicketKey(ticket));
        }
        return null;
    }

    public Map<String, Object> register(User user) {
        Map<String, Object> errMap = new HashMap<>();
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            errMap.put("usernameErr", "用户名不能为空！");
            return errMap;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            errMap.put("pwdErr", "密码不能为空！");
            return errMap;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            errMap.put("emailErr", "邮箱不能为空！");
            return errMap;
        }
        User u = userMapper.getUserByName(user.getUsername());
        if (u != null) {
            errMap.put("usernameErr", "用户名已存在！");
            return errMap;
        }
        u = userMapper.getUserByEmail(user.getEmail());
        if (u != null) {
            errMap.put("emailErr", "邮箱已被占用！");
            return errMap;
        }
        user.setType(0);
        user.setStatus(0);
        user.setSalt(CommonUtil.createCode().substring(0, 5));
        user.setPassword(CommonUtil.MD5(user.getPassword() + user.getSalt()));
        user.setActivationCode(CommonUtil.createCode());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //发送邮箱
        user = userMapper.getUserByName(user.getUsername());
        Context context = new Context();
        context.setVariable("user", user.getUsername());
        String url = domain + path + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String process = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "欢迎使用NewCoder", process);
        return errMap;
    }

    public int Activate(int userId, String activationCode) {
        User user = userMapper.getUserById(userId);
        if (user == null) {
            return CommunityConst.ACTIVATION_FAILED;
        }
        if (user.getStatus() == 1) {
            return CommunityConst.ACTIVATION_REPEAT;
        }
        if (!user.getActivationCode().equals(activationCode)) {
            return CommunityConst.ACTIVATION_FAILED;
        }

        user.setStatus(1);
        userMapper.updateUser(user);
        return CommunityConst.ACTIVATION_SUCCESS;
    }

    public Map<String, Object> login(String username, String password, long expiredSecs) {
        Map<String, Object> infoMap = new HashMap<>();
        if (StringUtils.isBlank(username)) {
            infoMap.put("userErr", "用户名不能为空");
            return infoMap;
        }
        if (StringUtils.isBlank(password)) {
            infoMap.put("pwdErr", "密码不能为空");
            return infoMap;
        }
        User user = userMapper.getUserByName(username);
        if (user == null) {
            infoMap.put("userErr", "无该用户");
            return infoMap;
        }
        if (user.getStatus() == 0) {
            infoMap.put("userErr", "用户未激活！");
            return infoMap;
        }
        password = CommonUtil.MD5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            infoMap.put("pwdErr", "密码错误！");
            return infoMap;
        }

        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommonUtil.createCode());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSecs * 1000));
        ValueOperations value = redisTemplate.opsForValue();
        String loginTicketKey = RedisUtil.getLoginTicketKey(loginTicket.getTicket());
        value.set(loginTicketKey, loginTicket);
        infoMap.put("ticket", loginTicket.getTicket());
        return infoMap;
    }

    public void logout(String ticket) {
        ValueOperations opsForValue = redisTemplate.opsForValue();
        String loginTicketKey = RedisUtil.getLoginTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) opsForValue.get(loginTicketKey);
        if (loginTicket == null) {
            return;
        } else {
            loginTicket.setStatus(1);
            opsForValue.set(loginTicketKey, loginTicket);
        }
    }

    public void uploadHeaderUrl(User user) {

        userMapper.updateUser(user);
        delUserCache(user.getId());
    }

    public User getUserByName(String username) {
        return userMapper.getUserByName(username);
    }

    private void initUserCache(User user) {
        String uKey = RedisUtil.getUKey(user.getId());
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(uKey, user, 30 * 60 * 60, TimeUnit.SECONDS);
    }

    private User getUserCache(int userId) {
        String ukey = RedisUtil.getUKey(userId);
        ValueOperations opsForValue = redisTemplate.opsForValue();
        User user = (User) opsForValue.get(ukey);
        return user;
    }

    private void delUserCache(int userId) {
        String ukey = RedisUtil.getUKey(userId);
        redisTemplate.delete(ukey);
    }
    public Map<String,String> sendVerifyCode(String email)
    {
        Map<String,String> errMap = new HashMap<>();
        User userByEmail = userMapper.getUserByEmail(email);
        if (userByEmail == null)
        {
            errMap.put("mailErr","该邮箱未注册！！");
            return errMap;
        }
        Context context = new Context();
        context.setVariable("user",userByEmail.getUsername());
        String code = CommonUtil.createCode().substring(0, 6);
        context.setVariable("code",code);
        String forgetKey = RedisUtil.getForgetKey(email);
        redisTemplate.opsForValue().set(forgetKey,code,60*5,TimeUnit.SECONDS);
        String process = templateEngine.process("/mail/forget",context);
        mailClient.sendMail(email,"NewCoder找回密码",process);
        return null;
    }

    public void updateUserPwd(int userId,String password)
    {
        User userById = userMapper.getUserById(userId);
        userMapper.updateUserPwd(userId,CommonUtil.MD5(password+userById.getSalt()));
    }

    /**
     * 构造权限，来让SpringSecurity帮助我们来进行权限管理
     * @param userId
     * @return
     */
    public Collection<? extends GrantedAuthority> getAuthorities(int userId) {
        List<GrantedAuthority> authorityList = new ArrayList<>();
        User userById = getUserById(userId);
        GrantedAuthority authority = new GrantedAuthority() {
            @Override
            public String getAuthority() {
                switch (userById.getType())
                {
                    case 0:return AUTHORITY_USER;
                    case 1:return AUTHORITY_ADMIN;
                    default:return AUTHORITY_AUTHOR;
                }
            }
        };
        authorityList.add(authority);
        return authorityList;
    }
}
