package org.example.platformspringboot.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.platformspringboot.entity.Music;

import java.util.List;

@Mapper
public interface MusicMapper {
    List<Music> listAll();
    Music findById(Long id);
}