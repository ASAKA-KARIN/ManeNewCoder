package com.newcoder.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class testKafka {
    @Autowired
    Producer producer;
    @Test
    void testKfk()  {
        producer.produce("test","hello");
        producer.produce("test","test111");
        producer.produce("test","你好");
        producer.produce("test","在吗");
        producer.produce("test","上班迟到就");
        try {
            Thread.sleep(1000*4);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}

@Component
class Producer {
    @Autowired
    private KafkaTemplate template;
    void produce(String topic, String content) {
        template.send(topic, content);
    }
}

@Component
class Consumer {

    @KafkaListener(topics = {"test"})
    void getMessage(ConsumerRecord consumerRecord) {
        System.out.println("+++++++++++++++++++++++"+consumerRecord.value());
    }
}
