package org.example.platformspringboot.controller;

import org.example.platformspringboot.common.Result;
import org.example.platformspringboot.config.JwtProperties;
import org.example.platformspringboot.dto.LoginDTO;
import org.example.platformspringboot.dto.RegisterDTO;
import org.example.platformspringboot.entity.User;
import org.example.platformspringboot.service.UserService;
import org.example.platformspringboot.utils.JwtUtil;
import org.example.platformspringboot.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired private UserService userService;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private JwtProperties jwtProps;

    @PostMapping("/register")
    public Result<User> register(@RequestBody RegisterDTO dto) {
        User u = userService.register(dto.getUsername(), dto.getPassword(), dto.getNickname());
        return Result.success(u);
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginDTO dto) {
        User u = userService.login(dto.getUsername(), dto.getPassword());
        String token = jwtUtil.issue(u.getId(), u.getUsername());
        u.setPassword(null);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", u);
        return Result.success(data);
    }

    @GetMapping("/info")
    public Result<User> me() {
        User u = userService.getById(UserContext.getUserId());
        return Result.success(u);
    }

    @GetMapping("/info/{id}")
    public Result<User> info(@PathVariable Long id,
                             @RequestHeader(value = "Authorization", required = false) String token) {
        User u = userService.getById(id);
        if (u == null) return Result.error(404, "用户不存在");
        return Result.success(u);
    }

    @PostMapping("/update")
    public Result<Void> update(@RequestBody User u) {
        u.setId(UserContext.getUserId());
        userService.update(u);
        return Result.success();
    }

    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token != null) {
            try { jwtUtil.blacklist(jwtUtil.parse(token).getId()); } catch (Exception ignored) {}
        }
        return Result.success();
    }

    @GetMapping("/list")
    public Result<Object> list(@RequestParam(required = false) String keyword,
                               @RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "20") int size) {
        return Result.success(userService.list(keyword, page, size));
    }
}
