package com.uv.backend.dto.request;

import com.uv.backend.entity.PostType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PostRequest {
    @NotNull(message = "Post type is required")
    private PostType type;

    @Size(max = 1000, message = "Content cannot exceed 1000 characters")
    private String content;

    private Long trackId;
    private Long originalPostId;

    // Constructors
    public PostRequest() {}

    // Getters and Setters
    public PostType getType() { return type; }
    public void setType(PostType type) { this.type = type; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getTrackId() { return trackId; }
    public void setTrackId(Long trackId) { this.trackId = trackId; }

    public Long getOriginalPostId() { return originalPostId; }
    public void setOriginalPostId(Long originalPostId) { this.originalPostId = originalPostId; }
}
