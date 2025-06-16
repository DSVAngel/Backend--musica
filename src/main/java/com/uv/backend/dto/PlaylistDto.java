package com.uv.backend.dto;

import com.uv.backend.entity.Playlist;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class PlaylistDto {
    private Long id;
    private String title;
    private String description;
    private String coverImageUrl; // CORREGIDO
    private Boolean isPublic;
    private UserDto user;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    private Integer likesCount;
    private Integer tracksCount;
    private Boolean isLiked;

    // Constructor vacío
    public PlaylistDto() {}

    // Constructor desde entidad
    public PlaylistDto(Playlist playlist) {
        this.id = playlist.getId();
        this.title = playlist.getTitle();
        this.description = playlist.getDescription();
        this.coverImageUrl = playlist.getCoverImageUrl(); // CORREGIDO
        this.isPublic = playlist.getIsPublic();
        this.createdAt = playlist.getCreatedAt();
        this.updatedAt = playlist.getUpdatedAt();
        this.likesCount = playlist.getLikesCount();
        this.tracksCount = playlist.getTracksCount();
        this.user = UserDto.fromUserPublic(playlist.getUser()); // CORREGIDO
        this.isLiked = false;
    }

    // Constructor público (sin datos privados)
    public PlaylistDto(Playlist playlist, boolean isPublic) {
        this.id = playlist.getId();
        this.title = playlist.getTitle();
        this.description = playlist.getDescription();
        this.coverImageUrl = playlist.getCoverImageUrl(); // CORREGIDO
        this.isPublic = playlist.getIsPublic();
        this.createdAt = playlist.getCreatedAt();
        this.updatedAt = playlist.getUpdatedAt();
        this.likesCount = playlist.getLikesCount();
        this.tracksCount = playlist.getTracksCount();
        this.user = UserDto.fromUserPublic(playlist.getUser()); // CORREGIDO
        this.isLiked = false;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

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

    public Integer getTracksCount() { return tracksCount; }
    public void setTracksCount(Integer tracksCount) { this.tracksCount = tracksCount; }

    public Boolean getIsLiked() { return isLiked; }
    public void setIsLiked(Boolean isLiked) { this.isLiked = isLiked; }
}
