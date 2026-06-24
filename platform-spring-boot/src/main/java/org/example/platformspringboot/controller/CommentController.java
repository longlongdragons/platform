package org.example.platformspringboot.controller;

import org.example.platformspringboot.common.Result;
import org.example.platformspringboot.dto.CommentDTO;
import org.example.platformspringboot.entity.Comment;
import org.example.platformspringboot.service.CommentService;
import org.example.platformspringboot.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired private CommentService commentService;

    @PostMapping("/add")
    public Result<Comment> add(@RequestBody CommentDTO dto) {
        Comment c = new Comment();
        c.setVideoId(dto.getVideoId());
        c.setUserId(UserContext.getUserId());
        c.setParentId(dto.getParentId() == null ? 0L : dto.getParentId());
        c.setContent(dto.getContent());
        return Result.success(commentService.add(c));
    }

    @GetMapping("/list")
    public Result<Map<String, Object>> list(@RequestParam Long videoId,
                                            @RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "20") int size) {
        List<Comment> list = commentService.list(videoId, page, size);
        return Result.success(Map.of(
                "list", list,
                "page", page,
                "size", size
        ));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        commentService.delete(id, UserContext.getUserId());
        return Result.success();
    }
}
