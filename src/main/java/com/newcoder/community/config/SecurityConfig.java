package com.newcoder.community.config;

import com.newcoder.community.service.CommunityConst;
import com.newcoder.community.util.CommonUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Yoshino
 * 0.设置放行方法
 * 1.设置需要进行权限检查的请求
 * 2.设置权限不足时的对应逻辑
 * 3.
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConst {
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().mvcMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //需要进行权限检查的请求
        http.authorizeRequests().antMatchers(
                "/comment/**",
                "/follow",
                "/unfollow",
                "/like",
                "/like/**",
                "/user/logout",
                "/message/**",
                "/letter/**",
                "/letter_detail/**",
                "/message",
                "/notice",
                "/notice_detail/**",
                "/post",
                "/user/setting",
                "/user/headerUrl"
        )
                .hasAnyAuthority(AUTHORITY_USER, AUTHORITY_ADMIN, AUTHORITY_AUTHOR)
                .antMatchers(
                        "/top",
                        "/wonder"
                ).
                hasAnyAuthority(AUTHORITY_AUTHOR).
                antMatchers(
                        "/del",
                        "/data"
                )
                .hasAnyAuthority(AUTHORITY_ADMIN)
                //其他请求全部放行
                .anyRequest().permitAll().and().csrf().disable();

        http.exceptionHandling()
                //未登录时
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                        String header = request.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equals(header)) {
                            response.setContentType("application/plain;charset=utf-8");
                            String jsonObj = CommonUtil.getJsonObj(403, "请先登录");
                            response.getWriter().write(jsonObj);
                        } else {
                            response.sendRedirect(request.getContextPath() + "/login");
                        }
                    }
                })
                //权限不足时
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                        String header = request.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equals(header)) {
                            response.setContentType("application/plain;charset=utf-8");
                            String jsonObj = CommonUtil.getJsonObj(403, "权限不足！");
                            response.getWriter().write(jsonObj);
                        } else {
                            response.sendRedirect(request.getContextPath() + "/denied");
                        }
                    }
                });
        //设置一个不存在的路径，来绕过Spring Security的登出处理
        http.logout().logoutUrl("/SecurityLogout");
    }
}
