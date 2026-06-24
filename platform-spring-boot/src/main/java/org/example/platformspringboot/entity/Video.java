package org.example.platformspringboot.entity;

import java.time.LocalDateTime;
import java.util.List;

public class Video {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String coverUrl;
    private String videoUrl;
    private Integer type;
    private String tags;
    private Integer duration;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer favoriteCount;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long musicId;
    private String subtitle;
    private User author;
    private List<String> imageList;
    private Boolean liked;
    private Boolean favorited;
    private Boolean followed;
    private Music music;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
    public Integer getCommentCount() { return commentCount; }
    public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }
    public Integer getFavoriteCount() { return favoriteCount; }
    public void setFavoriteCount(Integer favoriteCount) { this.favoriteCount = favoriteCount; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }
    public List<String> getImageList() { return imageList; }
    public void setImageList(List<String> imageList) { this.imageList = imageList; }
    public Boolean getLiked() { return liked; }
    public void setLiked(Boolean liked) { this.liked = liked; }
    public Boolean getFavorited() { return favorited; }
    public void setFavorited(Boolean favorited) { this.favorited = favorited; }
    public Boolean getFollowed() { return followed; }
    public void setFollowed(Boolean followed) { this.followed = followed; }
    public Long getMusicId() { return musicId; }
    public void setMusicId(Long musicId) { this.musicId = musicId; }
    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
    public Music getMusic() { return music; }
    public void setMusic(Music music) { this.music = music; }
}