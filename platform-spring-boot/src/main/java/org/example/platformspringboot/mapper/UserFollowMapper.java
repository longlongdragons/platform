package org.example.platformspringboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.platformspringboot.entity.UserFollow;

import java.util.List;

@Mapper
public interface UserFollowMapper {
    int insert(UserFollow f);
    int delete(@Param("userId") Long userId, @Param("followId") Long followId);
    boolean exists(@Param("userId") Long userId, @Param("followId") Long followId);
    long countFollow(@Param("userId") Long userId);
    long countFans(@Param("userId") Long userId);
    List<Long> listFollowIds(@Param("userId") Long userId);
    List<Long> listFansIds(@Param("userId") Long userId);
}
