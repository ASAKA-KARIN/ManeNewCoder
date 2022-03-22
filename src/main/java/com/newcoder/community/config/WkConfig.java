package com.newcoder.community.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * @author 86156
 */
@Configuration
public class WkConfig {
    @Value("${community.path.wk.filePath}")
    private String sharePath;
    private static final Logger logger = LoggerFactory.getLogger(WkConfig.class);
    @PostConstruct
    public void init() {
        File file = new File(sharePath);
        if (!file.exists())
        {
            file.mkdir();
        }
    }
}
