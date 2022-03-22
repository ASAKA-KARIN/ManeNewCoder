package com.newcoder.community.pojo;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 86156
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Event implements Serializable {
    /**
     * 主题
     */
    private String topic;
    /**
     * 主题的类型 (例如 点赞评论/点赞帖子 回复评论/回复帖子)
     */
    private int entityType;
    /**
     * 主题的id
     */
    private int entityId;
    /**
     * 进行操作的用户的id
     */
    private int userId;
    /**
     * 操作的实体的作者id(帖子的作者，评论的发送者..)
     */
    private int entityAuthorId;
    private Map<String, Object> data = new HashMap<>();

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityAuthorId() {
        return entityAuthorId;
    }

    public Event setEntityAuthorId(int entityAuthorId) {
        this.entityAuthorId = entityAuthorId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(String key, Object msg) {
        this.data.put(key, msg);
        return this;
    }
}
