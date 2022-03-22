package com.newcoder.community;

import com.newcoder.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class testEmailTest {
    @Autowired
    MailClient mailClient;
    @Test
    public void testSendMail(){
        mailClient.sendMail("441102036@qq.com","TEST","Test");
    }
}
