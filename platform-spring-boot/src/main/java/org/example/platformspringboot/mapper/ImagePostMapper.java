package org.example.platformspringboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ImagePostMapper {
    int insert(@Param("videoId") Long videoId,
               @Param("imageUrl") String imageUrl,
               @Param("sort") int sort);
    List<String> listByVideo(@Param("videoId") Long videoId);
    int deleteByVideo(@Param("videoId") Long videoId);
}
