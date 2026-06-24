package org.example.platformspringboot.controller;

import org.example.platformspringboot.common.Result;
import org.example.platformspringboot.service.FavoriteService;
import org.example.platformspringboot.service.LikeService;
import org.example.platformspringboot.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/interact")
public class InteractController {

    @Autowired private LikeService likeService;
    @Autowired private FavoriteService favoriteService;

    @PostMapping("/like")
    public Result<Map<String, Object>> like(@RequestParam Long videoId) {
        boolean liked = likeService.toggle(UserContext.getUserId(), videoId);
        return Result.success(Map.of("liked", liked, "count", likeService.count(videoId)));
    }

    @PostMapping("/favorite")
    public Result<Map<String, Object>> favorite(@RequestParam Long videoId) {
        boolean fav = favoriteService.toggle(UserContext.getUserId(), videoId);
        return Result.success(Map.of("favorited", fav));
    }
}
