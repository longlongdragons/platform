package org.example.platformspringboot.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class PublishVideoDTO {
    private String title;
    private String content;
    private String coverUrl;
    private String videoUrl;
    private Integer type;
    private String tags;
    private Integer duration;
    private List<String> images;
    private Long musicId;
    private String subtitle;

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
    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
    public Long getMusicId() { return musicId; }
    public void setMusicId(Long musicId) { this.musicId = musicId; }
    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
}
