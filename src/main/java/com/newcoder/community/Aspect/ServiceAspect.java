package com.newcoder.community.Aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 86156
 */
@Component
@Aspect
public class ServiceAspect {

    private final static Logger logger = LoggerFactory.getLogger(ServiceAspect.class);

    @Pointcut("execution(* com.newcoder.community.service.*.*(..))")
    public void ServicePointcut(){
    }

    @Before(value = "ServicePointcut()")
    public void logUserBehavior(JoinPoint joinPoint){
        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        if (attributes==null)
        {
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        String host = request.getRemoteHost();
        String serviceName = joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName();
        String now = new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        logger.info("用户["+host+"]在"+now+"访问了"+serviceName+"服务");
    }
}
