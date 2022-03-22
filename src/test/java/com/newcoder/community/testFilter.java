package com.newcoder.community;

import com.newcoder.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import javax.servlet.Servlet;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class testFilter {
    @Autowired
    public SensitiveFilter filter;

    @Test
    public void testSensitiveFilter() {
        String text = "红色绿色黄色蓝色，可以赌博，能过稀释&&毒&&品&&sdhsa";
        String filter = this.filter.filter(text);
        System.out.println(filter);
    }
    @Test
    public void testClassLoader(){
        ClassLoader classLoader = String.class.getClassLoader();
        ClassLoader classLoader1 = Servlet.class.getClassLoader();
        System.out.println(classLoader.toString());
    }

}
