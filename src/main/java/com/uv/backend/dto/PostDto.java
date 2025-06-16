package com.uv.backend.dto;
import com.uv.backend.entity.Post;
import com.uv.backend.entity.PostType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class PostDto {
    private Long id;
    private PostType type;
    private String content;
    private TrackDto track;
    private PostDto originalPost;
    private UserDto user;
    private Integer likesCount;
    private Integer repostsCount;
    private Integer commentsCount;
    private Boolean isLiked;
    private Boolean isReposted;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    // Constructors
    public PostDto() {}

    public PostDto(Post post) {
        this.id = post.getId();
        this.type = post.getType();
        this.content = post.getContent();
        this.likesCount = post.getLikesCount();
        this.repostsCount = post.getRepostsCount();
        this.commentsCount = post.getCommentsCount();
        this.createdAt = post.getCreatedAt();
        this.user = UserDto.fromUserPublic(post.getUser());
        
        if (post.getTrack() != null) {
            this.track = new TrackDto(post.getTrack());
        }
        
        if (post.getOriginalPost() != null) {
            this.originalPost = new PostDto(post.getOriginalPost());
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PostType getType() { return type; }
    public void setType(PostType type) { this.type = type; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public TrackDto getTrack() { return track; }
    public void setTrack(TrackDto track) { this.track = track; }

    public PostDto getOriginalPost() { return originalPost; }
    public void setOriginalPost(PostDto originalPost) { this.originalPost = originalPost; }

    public UserDto getUser() { return user; }
    public void setUser(UserDto user) { this.user = user; }

    public Integer getLikesCount() { return likesCount; }
    public void setLikesCount(Integer likesCount) { this.likesCount = likesCount; }

    public Integer getRepostsCount() { return repostsCount; }
    public void setRepostsCount(Integer repostsCount) { this.repostsCount = repostsCount; }

    public Integer getCommentsCount() { return commentsCount; }
    public void setCommentsCount(Integer commentsCount) { this.commentsCount = commentsCount; }

    public Boolean getIsLiked() { return isLiked; }
    public void setIsLiked(Boolean isLiked) { this.isLiked = isLiked; }

    public Boolean getIsReposted() { return isReposted; }
    public void setIsReposted(Boolean isReposted) { this.isReposted = isReposted; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
