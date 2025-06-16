package com.uv.backend.dto;

import com.uv.backend.entity.Comment;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class CommentDto {
    private Long id;
    private String content;
    private UserDto user; // CORREGIDO

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    private Integer likesCount;
    private Integer repliesCount;
    private Boolean isLiked;

    // Constructor vacío
    public CommentDto() {}

    // Constructor desde entidad
    public CommentDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.user = UserDto.fromUserPublic(comment.getUser()); // CORREGIDO
        this.createdAt = comment.getCreatedAt();
        this.likesCount = comment.getLikesCount();
        this.repliesCount = comment.getRepliesCount();
        this.isLiked = false;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public UserDto getUser() { return user; }
    public void setUser(UserDto user) { this.user = user; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getLikesCount() { return likesCount; }
    public void setLikesCount(Integer likesCount) { this.likesCount = likesCount; }

    public Integer getRepliesCount() { return repliesCount; }
    public void setRepliesCount(Integer repliesCount) { this.repliesCount = repliesCount; }

    public Boolean getIsLiked() { return isLiked; }
    public void setIsLiked(Boolean isLiked) { this.isLiked = isLiked; }
}