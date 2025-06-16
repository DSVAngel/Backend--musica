package com.uv.backend.dto;

import com.uv.backend.entity.Track;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Set;

public class TrackDto {
    private Long id;
    private String title;
    private String description;
    private String audioUrl;
    private String waveformUrl;
    private String coverImageUrl; // CORREGIDO
    private Integer duration;
    private String genre;
    private Set<String> tags;
    private Integer playsCount;
    private Boolean isPublic;
    private UserDto user;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    private Integer likesCount;
    private Integer commentsCount;
    private Integer repostsCount;
    private Boolean isLiked;

    // Constructor vacío
    public TrackDto() {}

    // Constructor desde entidad
    public TrackDto(Track track) {
        this.id = track.getId();
        this.title = track.getTitle();
        this.description = track.getDescription();
        this.audioUrl = track.getAudioUrl();
        this.waveformUrl = track.getWaveformUrl();
        this.coverImageUrl = track.getCoverImageUrl(); // CORREGIDO
        this.duration = track.getDuration();
        this.genre = track.getGenre();
        this.tags = track.getTags();
        this.playsCount = track.getPlaysCount();
        this.isPublic = track.getIsPublic();
        this.createdAt = track.getCreatedAt();
        this.updatedAt = track.getUpdatedAt();
        this.likesCount = track.getLikesCount();
        this.commentsCount = track.getCommentsCount();
        this.repostsCount = track.getRepostsCount();
        this.user = UserDto.fromUserPublic(track.getUser()); // CORREGIDO
        this.isLiked = false;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }

    public String getWaveformUrl() { return waveformUrl; }
    public void setWaveformUrl(String waveformUrl) { this.waveformUrl = waveformUrl; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }

    public Integer getPlaysCount() { return playsCount; }
    public void setPlaysCount(Integer playsCount) { this.playsCount = playsCount; }

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }

    public UserDto getUser() { return user; }
    public void setUser(UserDto user) { this.user = user; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Integer getLikesCount() { return likesCount; }
    public void setLikesCount(Integer likesCount) { this.likesCount = likesCount; }

    public Integer getCommentsCount() { return commentsCount; }
    public void setCommentsCount(Integer commentsCount) { this.commentsCount = commentsCount; }

    public Integer getRepostsCount() { return repostsCount; }
    public void setRepostsCount(Integer repostsCount) { this.repostsCount = repostsCount; }

    public Boolean getIsLiked() { return isLiked; }
    public void setIsLiked(Boolean isLiked) { this.isLiked = isLiked; }
}
