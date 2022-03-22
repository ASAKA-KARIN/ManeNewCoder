package com.newcoder.community.controller;

import com.alibaba.fastjson.JSONObject;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.newcoder.community.pojo.Message;
import com.newcoder.community.pojo.User;
import com.newcoder.community.service.CommunityConst;
import com.newcoder.community.service.MessageService;
import com.newcoder.community.service.UserService;
import com.newcoder.community.util.CommonUtil;
import com.newcoder.community.util.HostHolder;
import com.newcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

/**
 * @author Yoshino
 */
@Controller
public class MessageController implements CommunityConst {
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @RequestMapping(value = "/message", method = RequestMethod.GET)
    public String showAllMessage(Model model) {
        User user = hostHolder.getUser();
        PageHelper.startPage(1, 5);
        List<Message> allMessage = messageService.getAllMessage(user.getId());
        PageInfo<Message> postPage = new PageInfo<>(allMessage, 5);
        int[] navigatepageNums = postPage.getNavigatepageNums();
        model.addAttribute("MsgPageCount", navigatepageNums);
        model.addAttribute("MsgMaxPage", postPage.getPages());
        model.addAttribute("MsgCurrentPage", 1);
        List<Map<String, Object>> messageList = new ArrayList<>();
        for (Message message : allMessage) {
            Map<String, Object> msgMap = new HashMap<>();
            msgMap.put("msg", message);
            msgMap.put("msgTotalCount", messageService.getSingleLetterCount(message.getConversationId()));
            msgMap.put("unreadLetterCount", messageService.getUnreadMessageCount(user.getId(), message.getConversationId()));
            int targetId = user.getId() == message.getToId() ? message.getFromId() : message.getToId();
            msgMap.put("target", userService.getUserById(targetId));
            messageList.add(msgMap);
        }
        model.addAttribute("AllMessage", messageList);
        int unreadMessageCount = messageService.getUnreadMessageCount(user.getId(), null);
        model.addAttribute("unreadMessageCount", unreadMessageCount);
        int noticeCount = messageService.getUnreadNoticeCount(user.getId(), null);
        model.addAttribute("totalUnread", noticeCount);
        User user1 = hostHolder.getUser();
        if (user1!=null){
            model.addAttribute("unreadNoticeCount",noticeCount+unreadMessageCount);
        }
        return "/site/letter";

    }

    @RequestMapping(value = "/message/{pageNum}", method = RequestMethod.GET)
    public String showAllMessage(Model model, @PathVariable("pageNum") int pageNum) {
        User user = hostHolder.getUser();
        PageHelper.startPage(pageNum, 5);
        List<Message> allMessage = messageService.getAllMessage(user.getId());
        PageInfo<Message> postPage = new PageInfo<>(allMessage, 5);
        int[] navigatepageNums = postPage.getNavigatepageNums();
        model.addAttribute("MsgPageCount", navigatepageNums);
        model.addAttribute("MsgMaxPage", postPage.getPages());
        model.addAttribute("MsgCurrentPage", pageNum);
        List<Map<String, Object>> messageList = new ArrayList<>();
        for (Message message : allMessage) {
            Map<String, Object> msgMap = new HashMap<>();
            msgMap.put("msg", message);
            msgMap.put("msgTotalCount", messageService.getSingleLetterCount(message.getConversationId()));
            msgMap.put("unreadLetterCount", messageService.getUnreadMessageCount(user.getId(), message.getConversationId()));
            int targetId = user.getId() == message.getToId() ? message.getFromId() : message.getToId();
            msgMap.put("target", userService.getUserById(targetId));
            messageList.add(msgMap);
        }
        model.addAttribute("AllMessage", messageList);
        int unreadMessageCount = messageService.getUnreadMessageCount(user.getId(), null);
        model.addAttribute("unreadMessageCount", unreadMessageCount);
        int noticeCount = messageService.getUnreadNoticeCount(user.getId(), null);
        model.addAttribute("totalUnread", noticeCount);
        User user1 = hostHolder.getUser();
        if (user1!=null){
            model.addAttribute("unreadNoticeCount",noticeCount+unreadMessageCount);
        }
        return "/site/letter";
    }

    @RequestMapping(value = "/letter_detail/{conversationId}", method = RequestMethod.GET)
    public String showConversationDetail(@PathVariable("conversationId") String conversationId, Model model) {
        PageHelper.startPage(1, 5);
        List<Message> letters = messageService.getAllLetters(conversationId);
        List<Integer> idList = getIdList(letters);
        if (idList != null && idList.size() != 0) {
            messageService.updateMessage(1, idList);
        }
        PageInfo<Message> postPage = new PageInfo<>(letters, 5);
        int[] navigatepageNums = postPage.getNavigatepageNums();
        model.addAttribute("LetterPageCount", navigatepageNums);
        model.addAttribute("LetterMaxPage", postPage.getPages());
        model.addAttribute("LetterCurrentPage", 1);
        User user = hostHolder.getUser();
        List<Map<String, Object>> letterList = new ArrayList<>();
        int targetId;
        for (Message letter : letters) {
            Map<String, Object> letterMap = new HashMap<>();
            letterMap.put("letter", letter);
            letterMap.put("from", userService.getUserById(letter.getFromId()));
            letterList.add(letterMap);
        }
        targetId = user.getId() == letters.get(0).getToId() ? letters.get(0).getFromId() : letters.get(0).getToId();
        model.addAttribute("conversationObj", userService.getUserById(targetId).getUsername());
        model.addAttribute("letters", letterList);
        model.addAttribute("cid", conversationId);
        User user1 = hostHolder.getUser();
        if (user1!=null){
            int unreadMessageCount = messageService.getUnreadMessageCount(user1.getId(), null);
            int noticeCount = messageService.getUnreadNoticeCount(user1.getId(), null);
            model.addAttribute("unreadNoticeCount",noticeCount+unreadMessageCount);
        }
        return "/site/letter-detail";
    }

    @RequestMapping(value = "/letter_detail/{conversationId}/{pageNum}", method = RequestMethod.GET)
    public String showConversationDetail(@PathVariable("pageNum") int pageNum, @PathVariable("conversationId") String conversationId, Model model) {
        PageHelper.startPage(pageNum, 5);
        List<Message> letters = messageService.getAllLetters(conversationId);
        List<Integer> idList = getIdList(letters);
        if (idList != null && idList.size() != 0) {
            messageService.updateMessage(1, idList);
        }
        PageInfo<Message> postPage = new PageInfo<>(letters, 5);
        int[] navigatepageNums = postPage.getNavigatepageNums();
        model.addAttribute("LetterPageCount", navigatepageNums);
        model.addAttribute("LetterMaxPage", postPage.getPages());
        model.addAttribute("LetterCurrentPage", pageNum);
        User user = hostHolder.getUser();
        List<Map<String, Object>> letterList = new ArrayList<>();
        int targetId;
        for (Message letter : letters) {
            Map<String, Object> letterMap = new HashMap<>();
            letterMap.put("letter", letter);
            letterMap.put("from", userService.getUserById(letter.getFromId()));
            letterList.add(letterMap);
        }
        targetId = user.getId() == letters.get(0).getToId() ? letters.get(0).getFromId() : letters.get(0).getToId();
        model.addAttribute("conversationObj", userService.getUserById(targetId).getUsername());
        model.addAttribute("letters", letterList);
        model.addAttribute("cid", conversationId);
        User user1 = hostHolder.getUser();
        if (user1!=null){
            int unreadMessageCount = messageService.getUnreadMessageCount(user1.getId(), null);
            int noticeCount = messageService.getUnreadNoticeCount(user1.getId(), null);
            model.addAttribute("unreadNoticeCount",noticeCount+unreadMessageCount);
        }
        return "/site/letter-detail";
    }

    private List<Integer> getIdList(List<Message> letters) {
        List<Integer> list = new ArrayList<>();
        User user = hostHolder.getUser();
        for (Message message : letters) {
            if (user.getId() == message.getToId() && message.getStatus() == 0) {
                list.add(message.getId());
            }
        }
        return list;
    }

    @RequestMapping(value = "/message", method = RequestMethod.POST)
    @ResponseBody
    public String addMessage(String toName, String content) {
        User toUser = userService.getUserByName(toName);
        User fromUser = hostHolder.getUser();
        if (toUser == null) {
            String jsonObj = CommonUtil.getJsonObj(1, "不存在该用户！！");
            return jsonObj;
        }
        Message message = new Message();
        message.setToId(toUser.getId());
        message.setFromId(fromUser.getId());
        message.setStatus(0);
        message.setContent(sensitiveFilter.filter(content));
        String conversationId =
                fromUser.getId() >= toUser.getId() ? toUser.getId() + "_" + fromUser.getId() : fromUser.getId() + "_" + toUser.getId();
        message.setConversationId(conversationId);
        message.setCreateTime(new Date());
        messageService.addMessage(message);
        return CommonUtil.getJsonObj(0);
    }

    @RequestMapping(value = "/notice", method = RequestMethod.GET)
    public String toNotice(Model model) {
        User user = hostHolder.getUser();

        //封装评论
        Message lastedMsg = messageService.getLastedMsg(user.getId(), TOPIC_COMMENT);

        if (lastedMsg != null) {
            Map<String, Object> noticeMap = new HashMap<>(14);
            noticeMap.put("notice", lastedMsg);
            HashMap<String, Object> content = JSONObject.parseObject(HtmlUtils.htmlUnescape(lastedMsg.getContent()), HashMap.class);
            noticeMap.put("pid", content.get("targetId"));
            noticeMap.put("entity", content.get("entityId"));
            noticeMap.put("type", content.get("entityType"));
            noticeMap.put("user", userService.getUserById((Integer) content.get("userId")));
            int unread = messageService.getUnreadNoticeCount(user.getId(), TOPIC_COMMENT);
            noticeMap.put("unReadCount", unread);
            int noticeCount = messageService.getTotalNoticeCount((user.getId()), TOPIC_COMMENT);
            noticeMap.put("noticeCount", noticeCount);
            model.addAttribute("commentNotice", noticeMap);
        }
        //封装赞
        lastedMsg = messageService.getLastedMsg(user.getId(), TOPIC_LIKE);
        if (lastedMsg != null) {
            Map<String, Object> noticeMap = new HashMap<>(14);
            noticeMap.put("notice", lastedMsg);
            HashMap<String, Object> content = JSONObject.parseObject(HtmlUtils.htmlUnescape(lastedMsg.getContent()), HashMap.class);
            noticeMap.put("pid", content.get("pid"));
            noticeMap.put("entity", content.get("entityId"));
            noticeMap.put("type", content.get("entityType"));
            noticeMap.put("user", userService.getUserById((Integer) content.get("userId")));
            int unread = messageService.getUnreadNoticeCount(user.getId(), TOPIC_LIKE);
            noticeMap.put("unReadCount", unread);
            int noticeCount = messageService.getTotalNoticeCount((user.getId()), TOPIC_LIKE);
            noticeMap.put("noticeCount", noticeCount);
            model.addAttribute("likeNotice", noticeMap);
        }
        //封装关注
        lastedMsg = messageService.getLastedMsg(user.getId(), TOPIC_FOLLOW);
        if (lastedMsg != null) {
            Map<String, Object> noticeMap = new HashMap<>(14);
            noticeMap.put("notice", lastedMsg);
            HashMap<String, Object> content = JSONObject.parseObject(HtmlUtils.htmlUnescape(lastedMsg.getContent()), HashMap.class);
            noticeMap.put("entity", content.get("entityId"));
            noticeMap.put("type", content.get("entityType"));
            noticeMap.put("user", userService.getUserById((Integer) content.get("userId")));
            int unread = messageService.getUnreadNoticeCount(user.getId(), TOPIC_FOLLOW);
            noticeMap.put("unReadCount", unread);
            int noticeCount = messageService.getTotalNoticeCount((user.getId()), TOPIC_FOLLOW);
            noticeMap.put("noticeCount", noticeCount);
            model.addAttribute("followNotice", noticeMap);
        }
        int unreadMessageCount = messageService.getUnreadMessageCount(user.getId(), null);
        model.addAttribute("totalUnreadMsg", unreadMessageCount);
        int noticeCount = messageService.getUnreadNoticeCount(user.getId(), null);
        model.addAttribute("totalUnread", noticeCount);
        User user1 = hostHolder.getUser();
        if (user1!=null){
            model.addAttribute("unreadNoticeCount",noticeCount+unreadMessageCount);
        }

        return "site/notice";
    }

    @RequestMapping(value = "/notice_detail/{topic}/{pageNum}", method = RequestMethod.GET)
    public String toNoticeDetail(Model model, @PathVariable("topic") String topic, @PathVariable("pageNum") int pageNum) {
        User user = hostHolder.getUser();
         PageHelper.startPage(pageNum, 5);
        List<Message> messageList = messageService.getMessageDetail(user.getId(), topic);
        PageInfo<Message> postPage = new PageInfo<>(messageList, 5);
        int[] navigatepageNums = postPage.getNavigatepageNums();
        model.addAttribute("MsgPageCount", navigatepageNums);
        model.addAttribute("MsgMaxPage", postPage.getPages());
        model.addAttribute("MsgCurrentPage", pageNum);
        model.addAttribute("topic",topic);
        if (messageList == null||messageList.size()==0)
        {
            return "/site/notice-detail";
        }
        List<Map<String,Object>> messages = new ArrayList<>();
        for(Message message:messageList)
        {
            Map<String,Object> singleMap = new HashMap<>(14);
            Map<String,Object> content = JSONObject.parseObject(HtmlUtils.htmlUnescape(message.getContent()),HashMap.class);
            //不为空
            singleMap.put("type",content.get("entityType"));
            singleMap.put("entityId",content.get("entityId"));
            singleMap.put("user",userService.getUserById((Integer)content.get("userId")));
            //可能为空
            //标识点赞的帖子
            singleMap.put("pid",content.get("pid"));
            //标识评论的帖子
            singleMap.put("targetId",content.get("targetId"));
            singleMap.put("fromUser",userService.getUserById(message.getFromId()));
            singleMap.put("singleMsg",message);

            messages.add(singleMap);
        }
        List<Integer> ids = new ArrayList<>();
        for(Message message:messageList)
        {
            ids.add(message.getId());
        }
        messageService.updateMessage(1,ids);
        model.addAttribute("messages",messages);
        User user1 = hostHolder.getUser();
        if (user1!=null){
            int unreadMessageCount = messageService.getUnreadMessageCount(user1.getId(), null);
            int noticeCount = messageService.getUnreadNoticeCount(user1.getId(), null);
            model.addAttribute("unreadNoticeCount",noticeCount+unreadMessageCount);
        }
        return "/site/notice-detail";
    }


}
