package org.example.platformspringboot.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.example.platformspringboot.config.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {

    @Autowired
    private JwtProperties props;
    @Autowired
    private StringRedisTemplate redis;

    private SecretKey key;

    @PostConstruct
    void init() {
        byte[] bytes = Base64.getDecoder().decode(props.getSecret());
        this.key = Keys.hmacShaKeyFor(bytes);
    }

    public String issue(long userId, String username) {
        long now = System.currentTimeMillis();
        String jti = UUID.randomUUID().toString();
        return Jwts.builder()
                .id(jti)
                .subject(String.valueOf(userId))
                .claim("username", username)
                .issuedAt(new Date(now))
                .expiration(new Date(now + props.getExpire() * 1000))
                .signWith(key)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    /** 把jti加入黑名单，使旧token失效 */
    public void blacklist(String jti) {
        if (jti == null) return;
        redis.opsForValue().set("auth:blacklist:" + jti, "1", props.getExpire(), TimeUnit.SECONDS);
    }

    public boolean isBlacklisted(String jti) {
        return Boolean.TRUE.equals(redis.hasKey("auth:blacklist:" + jti));
    }
}
