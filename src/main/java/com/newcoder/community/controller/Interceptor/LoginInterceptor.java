package com.newcoder.community.controller.Interceptor;

import com.newcoder.community.annotation.LoginRequire;
import com.newcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author 86156
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    HostHolder hostHolder;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod)
        {
            HandlerMethod method = (HandlerMethod) handler;
            LoginRequire annotation = method.getMethodAnnotation(LoginRequire.class);
            if (annotation != null&&hostHolder.getUser()==null)
            {
                response.sendRedirect(request.getContextPath()+"/login");
                return false;
            }
        }
        return true;
    }
}
