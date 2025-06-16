package com.uv.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.util.Objects;

@Embeddable
public class PostVideo {
    @Column(nullable = false)
    @NotBlank(message = "Video URL is required")
    private String videoUrl;

    private String fileName;
    private String fileType;
    private Long fileSize;
    private Integer width;
    private Integer height;

    @Positive(message = "Duration must be positive")
    private Integer duration; // in seconds

    private String thumbnailUrl;
    private String quality;
    private String codec;
    private String bitrate;

    @Column(name = "video_order")
    private Integer order;

    // Constructors
    public PostVideo() {}

    public PostVideo(String videoUrl, String fileName, String fileType, Integer duration) {
        this.videoUrl = videoUrl;
        this.fileName = fileName;
        this.fileType = fileType;
        this.duration = duration;
    }

    // Getters and Setters
    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public Integer getWidth() { return width; }
    public void setWidth(Integer width) { this.width = width; }

    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public String getQuality() { return quality; }
    public void setQuality(String quality) { this.quality = quality; }

    public String getCodec() { return codec; }
    public void setCodec(String codec) { this.codec = codec; }

    public String getBitrate() { return bitrate; }
    public void setBitrate(String bitrate) { this.bitrate = bitrate; }

    public Integer getOrder() { return order; }
    public void setOrder(Integer order) { this.order = order; }

    // Helper methods
    public String getFormattedDuration() {
        if (duration == null) return "0:00";
        int minutes = duration / 60;
        int seconds = duration % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    public String getFormattedFileSize() {
        if (fileSize == null) return "0 B";
        long size = fileSize;
        String[] units = {"B", "KB", "MB", "GB"};
        int unitIndex = 0;
        double formattedSize = size;

        while (formattedSize >= 1024 && unitIndex < units.length - 1) {
            formattedSize /= 1024;
            unitIndex++;
        }

        return String.format("%.1f %s", formattedSize, units[unitIndex]);
    }

    public String getDimensions() {
        if (width != null && height != null) {
            return width + " x " + height;
        }
        return null;
    }

    public boolean hasThumbnail() {
        return thumbnailUrl != null && !thumbnailUrl.trim().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostVideo postVideo = (PostVideo) o;
        return Objects.equals(videoUrl, postVideo.videoUrl) &&
                Objects.equals(fileName, postVideo.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(videoUrl, fileName);
    }
}
