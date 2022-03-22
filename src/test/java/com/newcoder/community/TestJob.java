package com.newcoder.community;

import com.newcoder.community.Quartz.DelShareJob;
import org.junit.jupiter.api.Test;
import org.quartz.impl.JobExecutionContextImpl;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class TestJob {

    public DelShareJob shareJob = new DelShareJob();

    @Test
    public void testDelJob(){}
}
