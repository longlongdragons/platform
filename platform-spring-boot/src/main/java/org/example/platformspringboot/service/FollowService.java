package org.example.platformspringboot.service;

import org.example.platformspringboot.common.RedisKey;
import org.example.platformspringboot.entity.User;
import org.example.platformspringboot.entity.UserFollow;
import org.example.platformspringboot.mapper.UserFollowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class FollowService {

    @Autowired
    private UserFollowMapper mapper;
    @Autowired
    private StringRedisTemplate redis;

    /** 关注/取消关注，返回最终状态(true=已关注) */
    public boolean toggle(long userId, long followId) {
        if (userId == followId) throw new IllegalArgumentException("不能关注自己");
        String fk = RedisKey.followKey(userId);
        if (Boolean.TRUE.equals(redis.opsForSet().isMember(fk, String.valueOf(followId)))) {
            // 取消
            redis.opsForSet().remove(fk, String.valueOf(followId));
            redis.opsForSet().remove(RedisKey.fansKey(followId), String.valueOf(userId));
            mapper.delete(userId, followId);
            return false;
        } else {
            redis.opsForSet().add(fk, String.valueOf(followId));
            redis.opsForSet().add(RedisKey.fansKey(followId), String.valueOf(userId));
            UserFollow uf = new UserFollow();
            uf.setUserId(userId); uf.setFollowId(followId);
            mapper.insert(uf);
            return true;
        }
    }

    public boolean isFollowed(long userId, long followId) {
        return Boolean.TRUE.equals(
                redis.opsForSet().isMember(RedisKey.followKey(userId), String.valueOf(followId)));
    }

    public long countFollow(long userId) {
        Long c = redis.opsForSet().size(RedisKey.followKey(userId));
        if (c != null && c > 0) return c;
        long db = mapper.countFollow(userId);
        // 回写
        Set<String> ids = redis.opsForSet().members(RedisKey.followKey(userId));
        if (ids != null && !ids.isEmpty()) {
            // 已存在不动
        } else {
            for (Long fid : mapper.listFollowIds(userId)) {
                redis.opsForSet().add(RedisKey.followKey(userId), String.valueOf(fid));
            }
        }
        return db;
    }

    public long countFans(long userId) {
        Long c = redis.opsForSet().size(RedisKey.fansKey(userId));
        if (c != null && c > 0) return c;
        long db = mapper.countFans(userId);
        for (Long fid : mapper.listFansIds(userId)) {
            redis.opsForSet().add(RedisKey.fansKey(userId), String.valueOf(fid));
        }
        return db;
    }

    public void fillStats(User user) {
        user.setFollowCount(countFollow(user.getId()));
        user.setFollowerCount(countFans(user.getId()));
    }
}
