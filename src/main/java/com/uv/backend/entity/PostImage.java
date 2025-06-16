package com.uv.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

@Embeddable
public class PostImage {
    @Column(nullable = false)
    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    private String fileName;
    private String fileType;
    private Long fileSize;
    private Integer width;
    private Integer height;
    private String altText;

    @Column(name = "image_order")
    private Integer order;

    // Constructors
    public PostImage() {}

    public PostImage(String imageUrl, String fileName, String fileType) {
        this.imageUrl = imageUrl;
        this.fileName = fileName;
        this.fileType = fileType;
    }

    // Getters and Setters
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

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

    public String getAltText() { return altText; }
    public void setAltText(String altText) { this.altText = altText; }

    public Integer getOrder() { return order; }
    public void setOrder(Integer order) { this.order = order; }

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

    public String getDimensions() {
        if (width != null && height != null) {
            return width + " x " + height;
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostImage postImage = (PostImage) o;
        return Objects.equals(imageUrl, postImage.imageUrl) &&
                Objects.equals(fileName, postImage.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imageUrl, fileName);
    }
}