package com.newcoder.community.controller.Interceptor;

import com.newcoder.community.pojo.User;
import com.newcoder.community.service.DataStatisticsService;
import com.newcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 86156
 */
@Component
public class StatisticsInterceptor implements HandlerInterceptor {
    @Autowired
    DataStatisticsService statisticsService;
    @Autowired
    HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = request.getRemoteHost();
        statisticsService.StatisticsUv(ip);
        User user = hostHolder.getUser();
        if (user != null) {
            statisticsService.staticDau(user.getId());
        }
        return true;
    }
}
