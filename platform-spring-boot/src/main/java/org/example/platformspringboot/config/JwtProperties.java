package org.example.platformspringboot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret;
    private long expire;
    private String header;

    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }
    public long getExpire() { return expire; }
    public void setExpire(long expire) { this.expire = expire; }
    public String getHeader() { return header; }
    public void setHeader(String header) { this.header = header; }
}