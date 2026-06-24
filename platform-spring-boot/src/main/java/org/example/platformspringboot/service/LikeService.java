package org.example.platformspringboot.service;

import org.example.platformspringboot.common.RedisKey;
import org.example.platformspringboot.entity.VideoLike;
import org.example.platformspringboot.mapper.VideoLikeMapper;
import org.example.platformspringboot.mapper.VideoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired private StringRedisTemplate redis;
    @Autowired private VideoLikeMapper likeMapper;
    @Autowired private VideoMapper videoMapper;

    /** 点赞/取消点赞，返回最新状态 */
    public boolean toggle(long userId, long videoId) {
        String k = RedisKey.likeKey(videoId);
        String userSetKey = RedisKey.userLikeKey(userId);
        if (Boolean.TRUE.equals(redis.opsForSet().isMember(k, String.valueOf(userId)))) {
            redis.opsForSet().remove(k, String.valueOf(userId));
            redis.opsForSet().remove(userSetKey, String.valueOf(videoId));
            likeMapper.delete(userId, videoId);
            videoMapper.incLikeCount(videoId, -1);
            return false;
        } else {
            redis.opsForSet().add(k, String.valueOf(userId));
            redis.opsForSet().add(userSetKey, String.valueOf(videoId));
            VideoLike v = new VideoLike();
            v.setUserId(userId); v.setVideoId(videoId);
            likeMapper.insert(v);
            videoMapper.incLikeCount(videoId, 1);
            return true;
        }
    }

    public boolean isLiked(long userId, long videoId) {
        return Boolean.TRUE.equals(
                redis.opsForSet().isMember(RedisKey.likeKey(videoId), String.valueOf(userId)));
    }

    public long count(long videoId) {
        Long c = redis.opsForSet().size(RedisKey.likeKey(videoId));
        if (c != null) return c;
        long db = likeMapper.countByVideo(videoId);
        return db;
    }
}
