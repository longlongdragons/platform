package org.example.platformspringboot.service;

import jakarta.annotation.PostConstruct;
import org.example.platformspringboot.common.BizException;
import org.example.platformspringboot.common.RedisKey;
import org.example.platformspringboot.entity.ImagePost;
import org.example.platformspringboot.entity.Music;
import org.example.platformspringboot.entity.User;
import org.example.platformspringboot.entity.Video;
import org.example.platformspringboot.mapper.ImagePostMapper;
import org.example.platformspringboot.mapper.MusicMapper;
import org.example.platformspringboot.mapper.UserMapper;
import org.example.platformspringboot.mapper.VideoMapper;
import org.example.platformspringboot.utils.FileUtil;
import org.example.platformspringboot.utils.SensitiveWordFilter;
import org.example.platformspringboot.utils.UserContext;
import org.redisson.api.RBloomFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VideoService {

    @Autowired private VideoMapper videoMapper;
    @Autowired private UserMapper userMapper;
    @Autowired private ImagePostMapper imageMapper;
    @Autowired private MusicMapper musicMapper;
    @Autowired private FileUtil fileUtil;
    @Autowired private SensitiveWordFilter sensitive;
    @Autowired private StringRedisTemplate redis;
    @Autowired private RBloomFilter<Long> videoBloom;
    @Autowired private LikeService likeService;
    @Autowired private FavoriteService favoriteService;
    @Autowired private FollowService followService;

    /** 启动时把已有视频id加载到布隆过滤器，防止缓存穿透 */
    @PostConstruct
    void warmUp() {
        try {
            for (Long id : videoMapper.loadAllIds()) videoBloom.add(id);
        } catch (Exception ignored) {}
    }

    public Video create(Video v, List<String> imageUrls) {
        if (sensitive.contains(v.getTitle()) || sensitive.contains(v.getContent())) {
            throw new BizException("内容含敏感词");
        }
        v.setTitle(sensitive.replace(v.getTitle()));
        v.setContent(sensitive.replace(v.getContent()));

        if (v.getType() != null && v.getType() == 2 && imageUrls != null && !imageUrls.isEmpty()) {
            v.setCoverUrl(imageUrls.get(0));
        }

        videoMapper.insert(v);
        videoBloom.add(v.getId());
        redis.delete(RedisKey.videoDetailKey(v.getId()));

        if (v.getType() != null && v.getType() == 2 && imageUrls != null) {
            int i = 0;
            for (String url : imageUrls) {
                imageMapper.insert(v.getId(), url, i++);
            }
        }
        // 累加用户兴趣标签
        if (v.getTags() != null && !v.getTags().isBlank()) {
            for (String t : v.getTags().split(",")) {
                if (!t.isBlank()) redis.opsForZSet().incrementScore(
                        RedisKey.userTagKey(v.getUserId()), t.trim(), 1);
            }
        }
        return v;
    }

    public Video getDetail(long id, Long currentUserId) {
        // 布隆过滤器挡住不存在的id -> 防止缓存穿透
        if (!videoBloom.contains(id)) throw new BizException(404, "视频不存在");

        String key = RedisKey.videoDetailKey(id);
        Video v = null;
        String cached = redis.opsForValue().get(key);
        if (cached == null) {
            v = videoMapper.findById(id);
            if (v == null) throw new BizException(404, "视频不存在");
            // 写缓存，5分钟
            redis.opsForValue().set(key, "1", Duration.ofMinutes(5));
        } else {
            v = videoMapper.findById(id);
        }
        if (v == null) throw new BizException(404, "视频不存在");
        // 写一个"占位" -> 简单实现：只缓存热度高的视频详情; 真实场景用json
        if (v.getType() != null && v.getType() == 2) {
            v.setImageList(imageMapper.listByVideo(v.getId()));
        }
        User author = userMapper.findById(v.getUserId());
        if (author != null) {
            author.setPassword(null);
            followService.fillStats(author);
        }
        v.setAuthor(author);
        if (currentUserId != null) {
            v.setLiked(likeService.isLiked(currentUserId, id));
            v.setFavorited(favoriteService.isFavorited(currentUserId, id));
            if (author != null) v.setFollowed(followService.isFollowed(currentUserId, author.getId()));
        }
        return v;
    }

    public void incView(long id) {
        videoMapper.incView(id);
    }

    public List<Video> feed(Long lastId, Integer type, int size) {
        List<Video> list = videoMapper.feed(lastId, type, size);
        fillAuthors(list, null);
        return list;
    }

    public List<Video> search(String keyword, int page, int size) {
        List<Video> list = videoMapper.search(keyword, (page - 1) * size, size);
        fillAuthors(list, null);
        return list;
    }

    public List<Video> recommend(long userId, int size) {
        // 取用户兴趣标签 Top3
        List<String> tags = redis.opsForZSet()
                .reverseRange(RedisKey.userTagKey(userId), 0, 2)
                .stream().collect(Collectors.toList());
        if (tags.isEmpty()) {
            // 无历史则推热榜
            return hot(size);
        }
        List<Video> list = videoMapper.recommendByTags(tags, (long) -1, 1, size);
        if (list.size() < size) {
            // 不足补热门
            List<Video> hot = hot(size - list.size());
            list.addAll(hot);
        }
        fillAuthors(list, userId);
        return list;
    }

    public List<Video> hot(int size) {
        // 简化：从DB按热度排序
        List<Long> ids = videoMapper.hotVideoIds();
        return ids.stream().limit(size).map(id -> videoMapper.findById(id))
                .filter(java.util.Objects::nonNull).collect(Collectors.toList());
    }

    public List<Video> listByUser(long userId, int page, int size) {
        List<Video> list = videoMapper.listByUser(userId, (page - 1) * size, size);
        fillAuthors(list, null);
        return list;
    }

    private void fillAuthors(List<Video> list, Long currentUserId) {
        for (Video v : list) {
            User u = userMapper.findById(v.getUserId());
            if (u != null) {
                u.setPassword(null);
                followService.fillStats(u);
            }
            v.setAuthor(u);
            if (v.getType() != null && v.getType() == 2) {
                v.setImageList(imageMapper.listByVideo(v.getId()));
            }
            if (v.getMusicId() != null) {
                v.setMusic(musicMapper.findById(v.getMusicId()));
            }
            if (currentUserId != null) {
                v.setLiked(likeService.isLiked(currentUserId, v.getId()));
                v.setFavorited(favoriteService.isFavorited(currentUserId, v.getId()));
                if (u != null) v.setFollowed(followService.isFollowed(currentUserId, u.getId()));
            }
        }
    }

    public void hide(Long id) {
        Video v = videoMapper.findById(id);
        if (v == null) throw new BizException("视频不存在");
        if (!v.getUserId().equals(UserContext.getUserId())) {
            throw new BizException("无权操作");
        }
        videoMapper.updateStatus(id, 0);
        redis.delete(RedisKey.videoDetailKey(id));
    }

    public void restore(Long id) {
        Video v = videoMapper.findById(id);
        if (v == null) throw new BizException("视频不存在");
        if (!v.getUserId().equals(UserContext.getUserId())) {
            throw new BizException("无权操作");
        }
        videoMapper.updateStatus(id, 1);
        videoBloom.add(id);
        redis.delete(RedisKey.videoDetailKey(id));
    }

    public void delete(Long id) {
        Video v = videoMapper.findById(id);
        if (v == null) throw new BizException("视频不存在");
        if (!v.getUserId().equals(UserContext.getUserId())) {
            throw new BizException("无权操作");
        }
        videoMapper.deleteById(id);
        redis.delete(RedisKey.videoDetailKey(id));
        imageMapper.deleteByVideo(id);
    }

    public List<Video> listPrivate(long userId, int page, int size) {
        List<Video> list = videoMapper.listByUserWithStatus(userId, 0, (page - 1) * size, size);
        fillAuthors(list, userId);
        return list;
    }
}
