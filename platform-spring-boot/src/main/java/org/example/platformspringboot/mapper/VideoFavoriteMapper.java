package org.example.platformspringboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.platformspringboot.entity.VideoFavorite;

import java.util.List;

@Mapper
public interface VideoFavoriteMapper {
    int insert(VideoFavorite f);
    int delete(@Param("userId") Long userId, @Param("videoId") Long videoId);
    boolean exists(@Param("userId") Long userId, @Param("videoId") Long videoId);
    List<Long> listVideoIdsByUser(@Param("userId") Long userId,
                                  @Param("offset") int offset,
                                  @Param("size") int size);
}
