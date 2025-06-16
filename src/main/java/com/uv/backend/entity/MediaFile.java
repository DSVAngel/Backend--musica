package com.uv.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "media_files")
public class MediaFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Original filename is required")
    private String originalFileName;

    @Column(nullable = false)
    @NotBlank(message = "Stored filename is required")
    private String storedFileName;

    @Column(nullable = false)
    @NotBlank(message = "File URL is required")
    private String fileUrl;

    @Column(nullable = false)
    @NotBlank(message = "File type is required")
    private String fileType;

    @Column(nullable = false)
    @NotNull(message = "File size is required")
    private Long fileSize;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Media type is required")
    private MediaType mediaType;

    // Metadatos específicos para imágenes
    private Integer imageWidth;
    private Integer imageHeight;

    // Metadatos específicos para audio/video
    private Integer duration; // in seconds
    private String bitrate;
    private String sampleRate;
    private String codec;

    // Metadatos adicionales
    private String description;
    private String altText;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User uploadedBy;

    // Constructors
    public MediaFile() {}

    public MediaFile(String originalFileName, String storedFileName, String fileUrl,
                     String fileType, Long fileSize, MediaType mediaType, User uploadedBy) {
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.fileUrl = fileUrl;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.mediaType = mediaType;
        this.uploadedBy = uploadedBy;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }

    public String getStoredFileName() { return storedFileName; }
    public void setStoredFileName(String storedFileName) { this.storedFileName = storedFileName; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public MediaType getMediaType() { return mediaType; }
    public void setMediaType(MediaType mediaType) { this.mediaType = mediaType; }

    public Integer getImageWidth() { return imageWidth; }
    public void setImageWidth(Integer imageWidth) { this.imageWidth = imageWidth; }

    public Integer getImageHeight() { return imageHeight; }
    public void setImageHeight(Integer imageHeight) { this.imageHeight = imageHeight; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public String getBitrate() { return bitrate; }
    public void setBitrate(String bitrate) { this.bitrate = bitrate; }

    public String getSampleRate() { return sampleRate; }
    public void setSampleRate(String sampleRate) { this.sampleRate = sampleRate; }

    public String getCodec() { return codec; }
    public void setCodec(String codec) { this.codec = codec; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAltText() { return altText; }
    public void setAltText(String altText) { this.altText = altText; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public User getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(User uploadedBy) { this.uploadedBy = uploadedBy; }

    // Helper methods
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

    public String getFormattedDuration() {
        if (duration == null) return "0:00";
        int minutes = duration / 60;
        int seconds = duration % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    public String getDimensions() {
        if (imageWidth != null && imageHeight != null) {
            return imageWidth + " x " + imageHeight;
        }
        return null;
    }

    public boolean isImage() {
        return mediaType == MediaType.IMAGE;
    }

    public boolean isAudio() {
        return mediaType == MediaType.AUDIO;
    }

    public boolean isVideo() {
        return mediaType == MediaType.VIDEO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MediaFile mediaFile = (MediaFile) o;
        return Objects.equals(id, mediaFile.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "MediaFile{" +
                "id=" + id +
                ", originalFileName='" + originalFileName + '\'' +
                ", fileType='" + fileType + '\'' +
                ", mediaType=" + mediaType +
                ", fileSize=" + fileSize +
                '}';
    }
}
