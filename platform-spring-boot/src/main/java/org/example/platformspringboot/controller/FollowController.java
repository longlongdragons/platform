package org.example.platformspringboot.controller;

import org.example.platformspringboot.common.Result;
import org.example.platformspringboot.service.FollowService;
import org.example.platformspringboot.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/follow")
public class FollowController {

    @Autowired private FollowService followService;

    @PostMapping("/toggle")
    public Result<Map<String, Object>> toggle(@RequestParam Long followId) {
        boolean followed = followService.toggle(UserContext.getUserId(), followId);
        return Result.success(Map.of("followed", followed));
    }

    @GetMapping("/status")
    public Result<Map<String, Object>> status(@RequestParam Long followId) {
        boolean f = followService.isFollowed(UserContext.getUserId(), followId);
        return Result.success(Map.of(
                "followed", f,
                "followCount", followService.countFollow(followId),
                "fansCount", followService.countFans(followId)
        ));
    }
}
