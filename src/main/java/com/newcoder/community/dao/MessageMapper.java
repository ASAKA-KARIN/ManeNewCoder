package com.newcoder.community.dao;

import com.newcoder.community.pojo.Message;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.relational.core.sql.In;

import java.util.List;

/**
 * @author 86156
 */
@Mapper
public interface MessageMapper {

    /**
     * @param toId 当前用户id
     * @retur 显示消息列表 只显示最新消息
     */
    List<Message> getLastedMessage(int toId);

    /**
     * 获取当前用户的会话数量
     *
     * @param toId
     * @return
     */
    int getMessageCount(int toId);

    /**
     * 显示详细该对话的详细消息
     *
     * @param conversationId
     * @return
     */
    List<Message> getLetters(String conversationId);

    /**
     * 获取一条会话中所有消息的数量
     *
     * @param conversationId
     * @return
     */
    int getSingleLetterCount(String conversationId);

    /**
     * 1.conversationId 不为空时，查询该条会话中未读消息的数量
     * 2.conversationId 为空时，查询所有未读消息的数量
     *
     * @param toId
     * @param conversationId
     * @return
     */
    int getUnreadMessageCount(int toId, String conversationId);

    void insertMessage(Message message);

    void updateMessage(List<Integer> ids, int status);

    Message getLastedMsg(int userId, String topic);
    int getUnreadMsgCount(int userId,String topic);
    int getAllCount(int userId,String topic);
    List<Message> getMessageDetail(int userId,String topic);
}
