package org.example.platformspringboot.service;

import org.example.platformspringboot.common.RedisKey;
import org.example.platformspringboot.entity.VideoFavorite;
import org.example.platformspringboot.mapper.VideoFavoriteMapper;
import org.example.platformspringboot.mapper.VideoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class FavoriteService {

    @Autowired private StringRedisTemplate redis;
    @Autowired private VideoFavoriteMapper favMapper;
    @Autowired private VideoMapper videoMapper;

    public boolean toggle(long userId, long videoId) {
        String k = RedisKey.favoriteKey(videoId);
        String userSet = RedisKey.userFavoriteKey(userId);
        if (Boolean.TRUE.equals(redis.opsForSet().isMember(k, String.valueOf(userId)))) {
            redis.opsForSet().remove(k, String.valueOf(userId));
            redis.opsForSet().remove(userSet, String.valueOf(videoId));
            favMapper.delete(userId, videoId);
            videoMapper.incFavoriteCount(videoId, -1);
            return false;
        } else {
            redis.opsForSet().add(k, String.valueOf(userId));
            redis.opsForSet().add(userSet, String.valueOf(videoId));
            VideoFavorite f = new VideoFavorite();
            f.setUserId(userId); f.setVideoId(videoId);
            favMapper.insert(f);
            videoMapper.incFavoriteCount(videoId, 1);
            return true;
        }
    }

    public boolean isFavorited(long userId, long videoId) {
        return Boolean.TRUE.equals(
                redis.opsForSet().isMember(RedisKey.favoriteKey(videoId), String.valueOf(userId)));
    }
}
