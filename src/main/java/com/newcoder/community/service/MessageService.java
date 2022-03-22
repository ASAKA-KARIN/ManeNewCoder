package com.newcoder.community.service;

import com.newcoder.community.dao.MessageMapper;
import com.newcoder.community.pojo.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 86156
 */
@Service
public class MessageService {
    @Autowired
    private MessageMapper messageMapper;

    public List<Message> getAllMessage(int uid) {
        return messageMapper.getLastedMessage(uid);
    }

    public List<Message> getAllLetters(String conversationId) {
        return messageMapper.getLetters(conversationId);
    }

    public int getAllMessageCount(int uid) {
        return messageMapper.getMessageCount(uid);
    }

    public int getUnreadMessageCount(int uid, String conversationId) {
        return messageMapper.getUnreadMessageCount(uid, conversationId);
    }

    public int getSingleLetterCount(String conversationId) {
        return messageMapper.getSingleLetterCount(conversationId);
    }

    public void addMessage(Message message) {
        messageMapper.insertMessage(message);
    }

    public void updateMessage(int status, List<Integer> ids) {
        messageMapper.updateMessage(ids, status);
    }

    public Message getLastedMsg(int userId, String topic) {
        return messageMapper.getLastedMsg(userId, topic);
    }

    public int getUnreadNoticeCount(int userId, String topic) {
        return messageMapper.getUnreadMsgCount(userId, topic);
    }

    public int getTotalNoticeCount(int userId, String topic) {
        return messageMapper.getAllCount(userId, topic);
    }
    public List<Message> getMessageDetail(int userId,String topic)
    {
        return messageMapper.getMessageDetail(userId,topic);
    }

}
