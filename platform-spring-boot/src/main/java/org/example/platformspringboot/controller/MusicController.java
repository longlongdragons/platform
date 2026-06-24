package org.example.platformspringboot.controller;

import org.example.platformspringboot.common.Result;
import org.example.platformspringboot.entity.Music;
import org.example.platformspringboot.mapper.MusicMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/music")
public class MusicController {

    @Autowired private MusicMapper musicMapper;

    @GetMapping("/list")
    public Result<List<Music>> list() {
        return Result.success(musicMapper.listAll());
    }
}