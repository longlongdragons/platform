package org.example.platformspringboot.config;

import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host:localhost}")
    private String host;
    @Value("${spring.data.redis.port:6379}")
    private int port;
    @Value("${spring.data.redis.password:}")
    private String password;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port)
                .setPassword(password.isBlank() ? null : password)
                .setDatabase(0);
        return Redisson.create(config);
    }

    /** 视频ID布隆过滤器 - 用于防止缓存穿透 */
    @Bean
    public RBloomFilter<Long> videoBloomFilter(RedissonClient redisson) {
        RBloomFilter<Long> bf = redisson.getBloomFilter("video:id");
        bf.tryInit(100_000L, 0.01);
        return bf;
    }
}
