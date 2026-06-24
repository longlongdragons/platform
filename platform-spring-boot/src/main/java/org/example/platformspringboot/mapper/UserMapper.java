package org.example.platformspringboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.platformspringboot.entity.User;

import java.util.List;

@Mapper
public interface UserMapper {
    int insert(User user);
    User findById(@Param("id") Long id);
    User findByUsername(@Param("username") String username);
    List<User> list(@Param("keyword") String keyword,
                    @Param("offset") int offset,
                    @Param("size") int size);
    int update(User user);
    long count();
}
