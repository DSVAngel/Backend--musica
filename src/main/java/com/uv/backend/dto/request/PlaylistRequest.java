package com.uv.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public class PlaylistRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;
    private Boolean isPublic = true;

    // Constructors
    public PlaylistRequest() {}

    public PlaylistRequest(String title, String description, Boolean isPublic) {
        this.title = title;
        this.description = description;
        this.isPublic = isPublic;
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }
}