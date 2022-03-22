package com.newcoder.community.service;

/**
 * @author 86156
 */
public interface CommunityConst {
    /**
     *激活失败
     */
    int ACTIVATION_FAILED = -1;
    /**
     * 已经激活
     */
    int ACTIVATION_REPEAT = 0;
    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 1;

    /**
     * 默认过期时间
     */
    int DEFAULT_EXPIRED_TIME = 3600*12;

    /**
     * “记住我“时的过期时间
     */
    int LONG_EXPIRED_TIME = 3600*24*30;
    /**
     * 回帖的评论
     */
    int ENTITY_TYPE_POST = 1;
    /**
     * 回复的评论
     */
    int ENTITY_TYPE_COMMENT = 2;
    /**
     * 用户的实体
     */
    int ENTITY_TYPE_USER = 3;
    /**
     *事件主题:点赞
     */
    String TOPIC_LIKE="LIKE";
    /**
     *事件主题:关注
     */
    String TOPIC_FOLLOW="FOLLOW";
    /**
     *事件主题:评论
     */
    String TOPIC_COMMENT="COMMENT";
    /**
     * 事件主题:发帖
     */
    String TOPIC_PUBLISH="PUBLISH";
    /**
     * 事件主题:删除
     */
    String TOPIC_DELETE="DELETE";
    /**
     * 事件主题:分分享
     */
    String TOPIC_SHARE="SHARE";
    /**
     * 系统的ID
     */
    int SYSTEM_USER_ID=1;
    /**
     * 普通用户
     */
    String AUTHORITY_USER="USER";
    /**
     * 版主
     */
    String AUTHORITY_AUTHOR="AUTHOR";
    /**
     * 管理员
     */
    String AUTHORITY_ADMIN="ADMIN";
    /**
     * 帖子类型：普通
     */
    int TYPE_COMMON = 0;
    /**
     * 帖子类型：置顶
     */
    int TYPE_TOP = 1;
    /**
     * 帖子状态：普通
     */
    int STATUS_COMMON = 0;
    /**
     * 帖子状态：精华
     */
    int STATUS_WONDER = 1;
    /**
     * 帖子状态：拉黑
     */
    int STATUS_BLOCKED = 2;

}
