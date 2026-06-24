package org.example.platformspringboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.platformspringboot.entity.VideoLike;

import java.util.List;

@Mapper
public interface VideoLikeMapper {
    int insert(VideoLike like);
    int delete(@Param("userId") Long userId, @Param("videoId") Long videoId);
    boolean exists(@Param("userId") Long userId, @Param("videoId") Long videoId);
    long countByVideo(@Param("videoId") Long videoId);
    List<Long> listVideoIdsByUser(@Param("userId") Long userId,
                                  @Param("offset") int offset,
                                  @Param("size") int size);
}
