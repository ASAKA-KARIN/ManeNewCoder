package com.newcoder.community.event;

import com.alibaba.fastjson.JSONObject;
import com.newcoder.community.pojo.DiscussPost;
import com.newcoder.community.pojo.Event;
import com.newcoder.community.pojo.Message;
import com.newcoder.community.service.CommunityConst;
import com.newcoder.community.service.MessageService;
import com.newcoder.community.service.PostSearchService;
import com.newcoder.community.service.PostService;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yoshino
 */
@Component
public class EventConsumer implements CommunityConst {
    @Autowired
    private MessageService messageService;
    @Autowired
    private PostService postService;
    @Autowired
    PostSearchService postSearchService;
    @Value("${community.wk.command}")
    String wkCommand;
    @Value("${community.path.wk.filePath}")
    String filePath;
    private final static Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @KafkaListener(topics = {TOPIC_LIKE,TOPIC_COMMENT,TOPIC_FOLLOW})
    public void receiveEvent(ConsumerRecord consumerRecord){
        if (consumerRecord == null||consumerRecord.value()==null)
        {
            logger.error("消息接收失败 : （");
            return;
        }
        Event event = JSONObject.parseObject(consumerRecord.value().toString(),Event.class);
        if (event == null)
        {
            logger.error("消息格式有误!");
            return;
        }
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityAuthorId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());
        message.setStatus(0);
        Map<String,Object> content = new HashMap<>();
        content.put("userId",event.getUserId());
        content.put("entityType",event.getEntityType());
        content.put("entityId",event.getEntityId());
        for(Map.Entry<String,Object> entry:event.getData().entrySet())
        {
            content.put(entry.getKey(),entry.getValue());
        }
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }
    @KafkaListener(topics = TOPIC_PUBLISH)
    public void handlePost(ConsumerRecord consumerRecord){
        if (consumerRecord == null||consumerRecord.value()==null)
        {
            logger.error("消息接收失败 : （");
            return;
        }
        Event event = JSONObject.parseObject(consumerRecord.value().toString(),Event.class);
        if (event == null)
        {
            logger.error("消息格式有误!");
            return;
        }
        DiscussPost post=postService.getDiscussPost(event.getEntityId());
        postSearchService.addPost(post);

    }
    @KafkaListener(topics = TOPIC_DELETE)
    public void handleDel(ConsumerRecord consumerRecord){
        if (consumerRecord == null||consumerRecord.value()==null)
        {
            logger.error("消息接收失败 : （");
            return;
        }
        Event event = JSONObject.parseObject(consumerRecord.value().toString(),Event.class);
        if (event == null)
        {
            logger.error("消息格式有误!");
            return;
        }
        DiscussPost discussPost = postService.getDiscussPost(event.getEntityId());
        postSearchService.delPost(discussPost);
    }
    @KafkaListener(topics = TOPIC_SHARE)
    public void handleShare(ConsumerRecord record)
    {
        if (record == null||record.value()==null)
        {
            logger.error("消息接收失败 : （");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(),Event.class);
        if (event == null)
        {
            logger.error("消息格式有误!");
            return;
        }
        String fileName = (String)event.getData().get("fileName");
        String htmlUrl = (String)event.getData().get("htmlUrl");
        String command = wkCommand+" --quality 75 "+htmlUrl +' '+filePath+'/' + fileName;
        try {
            Runtime.getRuntime().exec(command);
            logger.info("生成长图成功");
        } catch (IOException ioException) {
            ioException.printStackTrace();
            logger.error("生成长图失败"+ioException.getMessage());
        }
    }

}
