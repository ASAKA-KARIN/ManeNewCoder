package com.newcoder.community.controller;

import com.newcoder.community.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author 86156
 */
@ControllerAdvice(annotations = Controller.class)
public class ExceptionController {
    private final static Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    @ExceptionHandler
    public void handleException(Exception e, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logger.error(e.getMessage());
        for(StackTraceElement s:e.getStackTrace())
        {
            logger.error(s.toString());
        }
        String header = req.getHeader("x-requested-with");
        if (header!=null&&header.equals("XMLHttpRequest"))
        {
            resp.setContentType("application/plain;charset=utf-8");
            resp.getWriter().write(CommonUtil.getJsonObj(0,"服务器内部错误"));
        }
        else
        {
            resp.sendRedirect(req.getContextPath()+"/err");
        }
    }
    @RequestMapping( value = "/denied",method = RequestMethod.GET)
    public String toDenied(){
        return "/error/404";
    }
}
