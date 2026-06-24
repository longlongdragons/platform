package org.example.platformspringboot.interceptor;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.platformspringboot.common.BizException;
import org.example.platformspringboot.config.JwtProperties;
import org.example.platformspringboot.utils.JwtUtil;
import org.example.platformspringboot.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private JwtProperties props;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        // 预检请求
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) return true;
        String token = req.getHeader(props.getHeader());
        if (token == null || token.isBlank()) {
            throw new BizException(401, "未登录");
        }
        try {
            Claims c = jwtUtil.parse(token);
            String jti = c.getId();
            if (jwtUtil.isBlacklisted(jti)) {
                throw new BizException(401, "token已失效");
            }
            UserContext.set(Long.parseLong(c.getSubject()), c.get("username", String.class));
            return true;
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            throw new BizException(401, "token无效: " + e.getMessage());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse res, Object handler, Exception ex) {
        UserContext.clear();
    }
}
