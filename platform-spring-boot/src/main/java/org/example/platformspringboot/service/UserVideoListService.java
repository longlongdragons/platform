package org.example.platformspringboot.service;

import org.example.platformspringboot.entity.User;
import org.example.platformspringboot.entity.Video;
import org.example.platformspringboot.mapper.UserMapper;
import org.example.platformspringboot.mapper.VideoFavoriteMapper;
import org.example.platformspringboot.mapper.VideoLikeMapper;
import org.example.platformspringboot.mapper.VideoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserVideoListService {

    @Autowired private VideoLikeMapper likeMapper;
    @Autowired private VideoFavoriteMapper favMapper;
    @Autowired private VideoMapper videoMapper;
    @Autowired private UserMapper userMapper;
    @Autowired private FollowService followService;

    public List<Video> likedBy(long userId, int page, int size) {
        List<Long> ids = likeMapper.listVideoIdsByUser(userId, (page-1)*size, size);
        return toVideos(ids);
    }

    public List<Video> favoritedBy(long userId, int page, int size) {
        List<Long> ids = favMapper.listVideoIdsByUser(userId, (page-1)*size, size);
        return toVideos(ids);
    }

    private List<Video> toVideos(List<Long> ids) {
        return ids.stream()
                .map(id -> videoMapper.findById(id))
                .filter(Objects::nonNull)
                .peek(v -> {
                    User u = userMapper.findById(v.getUserId());
                    if (u != null) {
                        u.setPassword(null);
                        followService.fillStats(u);
                    }
                    v.setAuthor(u);
                })
                .collect(Collectors.toList());
    }
}
