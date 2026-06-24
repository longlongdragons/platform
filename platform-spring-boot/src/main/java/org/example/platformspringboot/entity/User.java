package org.example.platformspringboot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

public class User {
    private Long id;
    private String username;
    @JsonIgnore
    private String password;
    private String nickname;
    private String avatar;
    private String signature;
    private Integer gender;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long followCount;
    private Long followerCount;
    private Long videoCount;
    private Boolean followed;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }
    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    public Long getFollowCount() { return followCount; }
    public void setFollowCount(Long followCount) { this.followCount = followCount; }
    public Long getFollowerCount() { return followerCount; }
    public void setFollowerCount(Long followerCount) { this.followerCount = followerCount; }
    public Long getVideoCount() { return videoCount; }
    public void setVideoCount(Long videoCount) { this.videoCount = videoCount; }
    public Boolean getFollowed() { return followed; }
    public void setFollowed(Boolean followed) { this.followed = followed; }
}