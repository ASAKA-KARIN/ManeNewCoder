package com.newcoder.community.controller;

import com.newcoder.community.event.EventProducer;
import com.newcoder.community.pojo.Event;
import com.newcoder.community.service.CommunityConst;
import com.newcoder.community.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 86156
 */
@Controller
public class ShareController implements CommunityConst {

    private final static Logger logger = LoggerFactory.getLogger(ShareController.class);
    @Autowired
    EventProducer producer;
    @Value("${community.path.domain}")
    String domain;
    @Value("${server.servlet.context-path}")
    String path;
    @Value("${community.path.wk.filePath}")
    String filePath;


    @RequestMapping(value = "/share", method = RequestMethod.GET)
    @ResponseBody
    public String shareImage(String htmlUrl) {
        String fileName = CommonUtil.createCode() + ".png";
        Event event = new Event().setTopic(TOPIC_SHARE)
                .setData("fileName", fileName)
                .setData("htmlUrl", htmlUrl);
        producer.produceEvent(event);
        Map<String, Object> shareMap = new HashMap<>(6);
        shareMap.put("sharUrl", domain + path + "/share/image/" + fileName);
        return CommonUtil.getJsonObj(0, null, shareMap);
    }

    @RequestMapping(value = "/share/image/{fileName}", method = RequestMethod.GET)
    public void getShare(HttpServletRequest request, HttpServletResponse response,
                           @PathVariable(name = "fileName") String fileName) {
        if (StringUtils.isBlank(fileName))
        {
            try {
                response.getWriter().write("文件名不能为空！！！");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            return;
        }
        File file = new File(filePath+'/'+fileName);
        response.setContentType("image/png");
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int flag;
            while ((flag=fis.read(buffer))!=-1)
            {
                outputStream.write(buffer,0,flag);
            }
        } catch (IOException ioException) {
            logger.error("获取分享图片失败！！"+ioException.getMessage());
        }

    }

}
