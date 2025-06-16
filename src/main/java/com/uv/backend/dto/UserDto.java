package com.uv.backend.dto;

import com.uv.backend.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class UserDto {
    private Long id;
    private String username;
    private String displayName;
    private String email;

    // Campos multimedia actualizados
    private String avatarUrl;
    private String avatarFileName;
    private String avatarFileType;
    private Long avatarFileSize;

    private String coverImageUrl;
    private String coverImageFileName;
    private String coverImageFileType;
    private Long coverImageFileSize;

    private String bio;
    private String location;
    private String website;
    private Boolean verified;
    private Boolean enabled;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // Campos calculados
    private Integer followersCount;
    private Integer followingCount;
    private Integer tracksCount;
    private Boolean isFollowing;

    // Constructor vacío
    public UserDto() {}

    // Constructor desde entidad User
    public UserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.displayName = user.getDisplayName();
        this.email = user.getEmail();
        this.avatarUrl = user.getAvatarUrl();
        this.avatarFileName = user.getAvatarFileName();
        this.avatarFileType = user.getAvatarFileType();
        this.avatarFileSize = user.getAvatarFileSize();
        this.coverImageUrl = user.getCoverImageUrl();
        this.coverImageFileName = user.getCoverImageFileName();
        this.coverImageFileType = user.getCoverImageFileType();
        this.coverImageFileSize = user.getCoverImageFileSize();
        this.bio = user.getBio();
        this.location = user.getLocation();
        this.website = user.getWebsite();
        this.verified = user.getVerified();
        this.enabled = user.getEnabled();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        this.followersCount = user.getFollowersCount();
        this.followingCount = user.getFollowingCount();
        this.tracksCount = user.getTracksCount();
        this.isFollowing = false; // Se establecerá desde el servicio si es necesario
    }

    // Constructor para respuestas públicas (sin email)
    public UserDto(User user, boolean includePrivateData) {
        this(user);
        if (!includePrivateData) {
            this.email = null;
        }
    }

    // MÉTODOS ESTÁTICOS FALTANTES
    public static UserDto fromUserPublic(User user) {
        return new UserDto(user, false); // Sin datos privados como email
    }

    public static UserDto fromUser(User user) {
        return new UserDto(user, true); // Con todos los datos
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // Avatar getters/setters
    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getAvatarFileName() { return avatarFileName; }
    public void setAvatarFileName(String avatarFileName) { this.avatarFileName = avatarFileName; }

    public String getAvatarFileType() { return avatarFileType; }
    public void setAvatarFileType(String avatarFileType) { this.avatarFileType = avatarFileType; }

    public Long getAvatarFileSize() { return avatarFileSize; }
    public void setAvatarFileSize(Long avatarFileSize) { this.avatarFileSize = avatarFileSize; }

    // Cover image getters/setters
    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public String getCoverImageFileName() { return coverImageFileName; }
    public void setCoverImageFileName(String coverImageFileName) { this.coverImageFileName = coverImageFileName; }

    public String getCoverImageFileType() { return coverImageFileType; }
    public void setCoverImageFileType(String coverImageFileType) { this.coverImageFileType = coverImageFileType; }

    public Long getCoverImageFileSize() { return coverImageFileSize; }
    public void setCoverImageFileSize(Long coverImageFileSize) { this.coverImageFileSize = coverImageFileSize; }

    // Resto de getters/setters
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean verified) { this.verified = verified; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Integer getFollowersCount() { return followersCount; }
    public void setFollowersCount(Integer followersCount) { this.followersCount = followersCount; }

    public Integer getFollowingCount() { return followingCount; }
    public void setFollowingCount(Integer followingCount) { this.followingCount = followingCount; }

    public Integer getTracksCount() { return tracksCount; }
    public void setTracksCount(Integer tracksCount) { this.tracksCount = tracksCount; }

    public Boolean getIsFollowing() { return isFollowing; }
    public void setIsFollowing(Boolean isFollowing) { this.isFollowing = isFollowing; }

    // Métodos de conveniencia
    public boolean hasAvatar() {
        return avatarUrl != null && !avatarUrl.trim().isEmpty();
    }

    public boolean hasCoverImage() {
        return coverImageUrl != null && !coverImageUrl.trim().isEmpty();
    }

    public String getFormattedAvatarFileSize() {
        if (avatarFileSize == null) return null;
        return formatFileSize(avatarFileSize);
    }

    public String getFormattedCoverImageFileSize() {
        if (coverImageFileSize == null) return null;
        return formatFileSize(coverImageFileSize);
    }

    private String formatFileSize(long fileSize) {
        String[] units = {"B", "KB", "MB", "GB"};
        int unitIndex = 0;
        double size = fileSize;

        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        return String.format("%.1f %s", size, units[unitIndex]);
    }
}