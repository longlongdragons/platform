package org.example.platformspringboot.controller;

import org.example.platformspringboot.common.Result;
import org.example.platformspringboot.dto.PublishVideoDTO;
import org.example.platformspringboot.entity.Video;
import org.example.platformspringboot.service.UserVideoListService;
import org.example.platformspringboot.service.VideoService;
import org.example.platformspringboot.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/video")
public class VideoController {

    @Autowired private VideoService videoService;
    @Autowired private UserVideoListService userVideoListService;

    @GetMapping("/feed")
    public Result<List<Video>> feed(@RequestParam(required = false) Long lastId,
                                    @RequestParam(required = false) Integer type,
                                    @RequestParam(defaultValue = "10") int size) {
        return Result.success(videoService.feed(lastId, type, Math.min(size, 30)));
    }

    @GetMapping("/recommend")
    public Result<List<Video>> recommend(@RequestParam(defaultValue = "10") int size,
                                         @RequestHeader(value = "Authorization", required = false) String token) {
        Long uid = UserContext.getUserId();
        return Result.success(videoService.recommend(uid == null ? -1L : uid, Math.min(size, 30)));
    }

    @GetMapping("/hot")
    public Result<List<Video>> hot(@RequestParam(defaultValue = "10") int size) {
        return Result.success(videoService.hot(Math.min(size, 50)));
    }

    @GetMapping("/search")
    public Result<List<Video>> search(@RequestParam String keyword,
                                      @RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "20") int size) {
        return Result.success(videoService.search(keyword, page, size));
    }

    @GetMapping("/detail/{id}")
    public Result<Video> detail(@PathVariable Long id,
                                @RequestHeader(value = "Authorization", required = false) String token) {
        Long uid = UserContext.getUserId();
        // 这里如果用了拦截器免登录路径，则通过threadlocal取
        return Result.success(videoService.getDetail(id, uid));
    }

    @GetMapping("/view/{id}")
    public Result<Void> view(@PathVariable Long id) {
        videoService.incView(id);
        return Result.success();
    }

    @PostMapping("/publish")
    public Result<Video> publish(@RequestBody PublishVideoDTO dto) {
        Video v = new Video();
        v.setUserId(UserContext.getUserId());
        v.setTitle(dto.getTitle());
        v.setContent(dto.getContent());
        v.setCoverUrl(dto.getCoverUrl());
        v.setVideoUrl(dto.getVideoUrl());
        v.setType(dto.getType() == null ? 1 : dto.getType());
        v.setTags(dto.getTags());
        v.setDuration(dto.getDuration() == null ? 0 : dto.getDuration());
        v.setMusicId(dto.getMusicId());
        v.setSubtitle(dto.getSubtitle());
        return Result.success(videoService.create(v, dto.getImages()));
    }

    @GetMapping("/byUser/{userId}")
    public Result<List<Video>> byUser(@PathVariable Long userId,
                                      @RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "20") int size) {
        return Result.success(videoService.listByUser(userId, page, size));
    }

    @GetMapping("/liked")
    public Result<List<Video>> liked(@RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "20") int size) {
        return Result.success(userVideoListService.likedBy(UserContext.getUserId(), page, size));
    }

    @GetMapping("/favorited")
    public Result<List<Video>> favorited(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "20") int size) {
        return Result.success(userVideoListService.favoritedBy(UserContext.getUserId(), page, size));
    }

    @PostMapping("/hide/{id}")
    public Result<Void> hide(@PathVariable Long id) {
        videoService.hide(id);
        return Result.success();
    }

    @PostMapping("/restore/{id}")
    public Result<Void> restore(@PathVariable Long id) {
        videoService.restore(id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        videoService.delete(id);
        return Result.success();
    }

    @GetMapping("/private")
    public Result<List<Video>> privateList(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "20") int size) {
        return Result.success(videoService.listPrivate(UserContext.getUserId(), page, size));
    }
}
