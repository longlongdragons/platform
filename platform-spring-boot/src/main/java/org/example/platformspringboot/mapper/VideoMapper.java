package org.example.platformspringboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.platformspringboot.entity.Video;

import java.util.List;

@Mapper
public interface VideoMapper {
    int insert(Video v);
    Video findById(@Param("id") Long id);

    List<Video> feed(@Param("lastId") Long lastId,
                     @Param("type") Integer type,
                     @Param("size") int size);

    List<Video> recommendByTags(@Param("tags") List<String> tags,
                                @Param("excludeId") Long excludeId,
                                @Param("type") Integer type,
                                @Param("size") int size);

    List<Video> listByUser(@Param("userId") Long userId,
                           @Param("offset") int offset,
                           @Param("size") int size);

    List<Video> search(@Param("keyword") String keyword,
                       @Param("offset") int offset,
                       @Param("size") int size);

    int incView(@Param("id") Long id);
    int incCommentCount(@Param("id") Long id, @Param("delta") int delta);
    int incLikeCount(@Param("id") Long id, @Param("delta") int delta);
    int incFavoriteCount(@Param("id") Long id, @Param("delta") int delta);

    List<Long> loadAllIds();
    List<Long> hotVideoIds();

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    int deleteById(@Param("id") Long id);
    List<Video> listByUserWithStatus(@Param("userId") Long userId,
                                     @Param("status") Integer status,
                                     @Param("offset") int offset,
                                     @Param("size") int size);
}
