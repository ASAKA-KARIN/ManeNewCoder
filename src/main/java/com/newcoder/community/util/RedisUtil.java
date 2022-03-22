package com.newcoder.community.util;

/**
 * @author Yoshino
 */
public class RedisUtil {
    private final static String SPLIT =":";
    private final static String LIKE_ENTITY = "like:entity";
    private final static String LIKE_USER = "like:user";
    private final static String FOLLOWER_ENTITY="follower";
    private final static String FOLLOWEE_ENTITY="followee";
    private final static String KAPTCHA="kaptcha";
    private final static String LOGIN_TICKET ="loginTicket";
    private final static String USER = "user";
    private final static String STATISTICS_UV="uv";
    private final static String STATISTICS_DAU="dau";
    private final static String POST_ENTITY="post";
    private final static String POST_CACHE="post:cache";
    private final static String FORGET_CODE="forget";
    public static String getEntityKey(int entityType,int entityId)
    {
        return LIKE_ENTITY+SPLIT+entityType+SPLIT+entityId;
    }
    public static String getUserKey(int userId)
    {
        return LIKE_USER+SPLIT+userId;
    }

    /**
     *获取实体的粉丝key follower:entityType:entityId ——>zset(粉丝的id，时间)
     */
    public static String getFollowerKey(int entityId,int entityType)
    {
        return FOLLOWER_ENTITY+SPLIT+entityType+SPLIT+entityId;
    }

    /**
     *获取自己的收藏/关注key followee:userId:entityType ——>zset(收藏/关注的实体id,时间)
     * @return
     */
    public  static String getFolloweeKey(int userId,int entityType){
        return FOLLOWEE_ENTITY+SPLIT+userId+SPLIT+entityType;
    }
    public static String getKaptchaKey(String owner)
    {
        return KAPTCHA+SPLIT+owner;
    }
    public static String getLoginTicketKey(String ticket){
        return LOGIN_TICKET+SPLIT+ticket;
    }
    public static String getUKey(int userId)
    {
        return USER+SPLIT+userId;
    }

    /**
     * value hyperloglog 用户Ip
     * @param date
     * @return
     */
    public static String getUvKey(String date)
    {
        return STATISTICS_UV+SPLIT+date;
    }
    public static String getDauKey(String date)
    {
        return STATISTICS_DAU+SPLIT+date;
    }
    public static String getUvUnionKey(String startDate,String endDate){
        return STATISTICS_UV+SPLIT+startDate+SPLIT+endDate;
    }
    public static String getDavUnionKey(String startDate,String endDate){
        return STATISTICS_DAU+SPLIT+startDate+SPLIT+endDate;
    }
    public static String getScorePostKey(){
        return POST_ENTITY+SPLIT+"score";
    }
    public static String getCachePostKey(int pageNum){
        return POST_CACHE+pageNum;
    }
    public static String getForgetKey(String email)
    {return FORGET_CODE+SPLIT+email;}






}
