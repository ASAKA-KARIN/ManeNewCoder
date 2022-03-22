package com.newcoder.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.digester.Digester;
import org.springframework.util.DigestUtils;

import javax.servlet.http.Cookie;
import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.UUID;

/**
 * @author 86156
 */
public class CommonUtil {
    /**
     * get random String
     *
     * @return
     */
    public static String createCode() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String MD5(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    public static Cookie searchCookie(Cookie[] cookies, String cookieName) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)) {
                return cookie;
            }
        }
        return null;
    }

    public static String getJsonObj(int code, String msg, Map<String, Object> map) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("msg", msg);
        if (map != null) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                jsonObject.put(entry.getKey(), entry.getValue());
            }
        }
        return jsonObject.toJSONString();
    }

    public static String getJsonObj(int code, String msg) {
        return getJsonObj(code, msg, null);
    }

    public static String getJsonObj(int code) {
        return getJsonObj(code, null, null);
    }


}
