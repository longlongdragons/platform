package org.example.platformspringboot.service;

import org.example.platformspringboot.common.BizException;
import org.example.platformspringboot.entity.Comment;
import org.example.platformspringboot.entity.User;
import org.example.platformspringboot.mapper.CommentMapper;
import org.example.platformspringboot.mapper.UserMapper;
import org.example.platformspringboot.mapper.VideoMapper;
import org.example.platformspringboot.utils.SensitiveWordFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired private CommentMapper mapper;
    @Autowired private VideoMapper videoMapper;
    @Autowired private UserMapper userMapper;
    @Autowired private SensitiveWordFilter sensitive;

    public Comment add(Comment c) {
        if (sensitive.contains(c.getContent())) {
            throw new BizException("评论含敏感词");
        }
        c.setContent(sensitive.replace(c.getContent()));
        mapper.insert(c);
        videoMapper.incCommentCount(c.getVideoId(), 1);
        return c;
    }

    public List<Comment> list(long videoId, int page, int size) {
        List<Comment> list = mapper.listByVideo(videoId, (page - 1) * size, size);
        for (Comment c : list) {
            User u = userMapper.findById(c.getUserId());
            if (u != null) u.setPassword(null);
            c.setAuthor(u);
        }
        return list;
    }

    public void delete(long id, long userId) {
        Comment c = mapper.listByVideo(id, 0, 1).stream()
                .filter(x -> x.getId().equals(id)).findFirst().orElse(null);
        if (c == null) throw new BizException("评论不存在");
        if (!c.getUserId().equals(userId)) throw new BizException("无权删除");
        mapper.deleteById(id, userId);
        videoMapper.incCommentCount(c.getVideoId(), -1);
    }
}
