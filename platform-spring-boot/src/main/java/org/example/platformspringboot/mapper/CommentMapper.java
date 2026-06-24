package org.example.platformspringboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.platformspringboot.entity.Comment;

import java.util.List;

@Mapper
public interface CommentMapper {
    int insert(Comment c);
    List<Comment> listByVideo(@Param("videoId") Long videoId,
                              @Param("offset") int offset,
                              @Param("size") int size);
    long countByVideo(@Param("videoId") Long videoId);
    int deleteById(@Param("id") Long id, @Param("userId") Long userId);
}
