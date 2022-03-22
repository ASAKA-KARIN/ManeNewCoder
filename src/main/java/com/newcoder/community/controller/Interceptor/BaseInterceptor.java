package com.newcoder.community.controller.Interceptor;

import com.newcoder.community.pojo.LoginTicket;
import com.newcoder.community.pojo.User;
import com.newcoder.community.service.UserService;
import com.newcoder.community.util.CommonUtil;
import com.newcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @author YOSHINO
 */
@Component
public class BaseInterceptor implements HandlerInterceptor {
    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return true;
        }
        Cookie cookie = CommonUtil.searchCookie(cookies, "ticket");
        if (cookie == null) {
            return true;
        }
        String value = cookie.getValue();
        if (!StringUtils.isBlank(value)) {
            LoginTicket loginTicket = userService.getTicketByTicket(value);
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
                User user = userService.getUserById(loginTicket.getUserId());
                hostHolder.setUser(user);
                //手动将目前用户权限放入SecurityContext，实现权限管理
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        user, user.getPassword(), userService.getAuthorities(user.getId()));
                SecurityContext context = new SecurityContextImpl(authentication);
                SecurityContextHolder.setContext(context);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            modelAndView.addObject("loginUser", hostHolder.getUser());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }

}
