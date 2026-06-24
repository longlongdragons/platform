package org.example.platformspringboot.common;

public class RedisKey {
    // 点赞集合: video:like:{videoId} -> set<userId>
    public static String likeKey(long videoId)    { return "video:like:" + videoId; }
    // 收藏集合
    public static String favoriteKey(long videoId){ return "video:favorite:" + videoId; }
    // 用户点赞列表(用于个人中心)
    public static String userLikeKey(long userId)  { return "user:like:" + userId; }
    public static String userFavoriteKey(long userId){ return "user:favorite:" + userId; }
    // 关注关系
    public static String followKey(long userId)    { return "user:follow:" + userId; }     // 关注集合
    public static String fansKey(long userId)      { return "user:fans:" + userId; }       // 粉丝集合
    // 视频热度(ZSet)
    public static String hotVideoZset()            { return "video:hot"; }
    // 视频详情缓存
    public static String videoDetailKey(long id)   { return "video:detail:" + id; }
    // 用户兴趣标签(ZSet  score=权重)
    public static String userTagKey(long userId)   { return "user:tag:" + userId; }
    // 黑名单token
    public static String tokenBlacklist(String jti){ return "auth:blacklist:" + jti; }
    // 分片上传的元数据
    public static String uploadMetaKey(String uid) { return "upload:meta:" + uid; }
}
