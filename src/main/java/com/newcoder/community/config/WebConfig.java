package com.newcoder.community.config;

import com.newcoder.community.controller.Interceptor.BaseInterceptor;
import com.newcoder.community.controller.Interceptor.StatisticsInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private BaseInterceptor baseInterceptor;
    //    @Autowired
//    private LoginInterceptor loginInterceptor;
    @Autowired
    private StatisticsInterceptor statisticsInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(baseInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png",
                        "/**/*.jpeg", "/**/*.jpg", "/kaptcha");
//        registry.addInterceptor(loginInterceptor).excludePathPatterns("/**/*.css","/**/*.js","/**/*.png",
//                "/**/*.jpeg","/**/*.jpg","/kaptcha");
        registry.addInterceptor(statisticsInterceptor).excludePathPatterns(
                "/**/*.css", "/**/*.js", "/**/*.png",
                "/**/*.jpeg", "/**/*.jpg", "/kaptcha"
        );
    }
}
