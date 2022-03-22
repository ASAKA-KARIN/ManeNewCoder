package com.newcoder.community.event;

import com.alibaba.fastjson.JSONObject;
import com.newcoder.community.pojo.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

/**
 * @author 86156
 */
@Component
public class EventProducer {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void produceEvent(Event event)
    {
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }



}
