package com.uv.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tracks")
public class Track {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Title is required")
    private String title;

    @Column(length = 1000)
    private String description;

    // Campos de audio actualizados
    @Column(nullable = false)
    @NotBlank(message = "Audio URL is required")
    private String audioUrl;

    private String audioFileName;
    private String audioFileType;
    private Long audioFileSize;
    private String audioBitrate;
    private String audioSampleRate;

    // Campos de waveform
    private String waveformUrl;
    private String waveformFileName;
    private String waveformFileType;

    // Campos de imagen de portada actualizados
    private String coverImageUrl;
    private String coverImageFileName;
    private String coverImageFileType;
    private Long coverImageFileSize;

    @Column(nullable = false)
    @NotNull(message = "Duration is required")
    @Positive(message = "Duration must be positive")
    private Integer duration; // in seconds

    private String genre;

    @ElementCollection
    @CollectionTable(name = "track_tags", joinColumns = @JoinColumn(name = "track_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    @Column(nullable = false)
    private Integer playsCount = 0;

    @Column(nullable = false)
    private Boolean isPublic = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Relationships (sin cambios)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "track", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Like> likes = new HashSet<>();

    @OneToMany(mappedBy = "track", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "track", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Post> posts = new HashSet<>();

    @ManyToMany(mappedBy = "tracks", fetch = FetchType.LAZY)
    private Set<Playlist> playlists = new HashSet<>();

    // Constructors
    public Track() {}

    public Track(String title, String audioUrl, Integer duration, User user) {
        this.title = title;
        this.audioUrl = audioUrl;
        this.duration = duration;
        this.user = user;
    }

    // Getters and Setters actualizados
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // Audio getters/setters
    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }

    public String getAudioFileName() { return audioFileName; }
    public void setAudioFileName(String audioFileName) { this.audioFileName = audioFileName; }

    public String getAudioFileType() { return audioFileType; }
    public void setAudioFileType(String audioFileType) { this.audioFileType = audioFileType; }

    public Long getAudioFileSize() { return audioFileSize; }
    public void setAudioFileSize(Long audioFileSize) { this.audioFileSize = audioFileSize; }

    public String getAudioBitrate() { return audioBitrate; }
    public void setAudioBitrate(String audioBitrate) { this.audioBitrate = audioBitrate; }

    public String getAudioSampleRate() { return audioSampleRate; }
    public void setAudioSampleRate(String audioSampleRate) { this.audioSampleRate = audioSampleRate; }

    // Waveform getters/setters
    public String getWaveformUrl() { return waveformUrl; }
    public void setWaveformUrl(String waveformUrl) { this.waveformUrl = waveformUrl; }

    public String getWaveformFileName() { return waveformFileName; }
    public void setWaveformFileName(String waveformFileName) { this.waveformFileName = waveformFileName; }

    public String getWaveformFileType() { return waveformFileType; }
    public void setWaveformFileType(String waveformFileType) { this.waveformFileType = waveformFileType; }

    // Cover image getters/setters
    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public String getCoverImageFileName() { return coverImageFileName; }
    public void setCoverImageFileName(String coverImageFileName) { this.coverImageFileName = coverImageFileName; }

    public String getCoverImageFileType() { return coverImageFileType; }
    public void setCoverImageFileType(String coverImageFileType) { this.coverImageFileType = coverImageFileType; }

    public Long getCoverImageFileSize() { return coverImageFileSize; }
    public void setCoverImageFileSize(Long coverImageFileSize) { this.coverImageFileSize = coverImageFileSize; }

    // Resto de getters/setters (sin cambios)
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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Set<Like> getLikes() { return likes; }
    public void setLikes(Set<Like> likes) { this.likes = likes; }

    public Set<Comment> getComments() { return comments; }
    public void setComments(Set<Comment> comments) { this.comments = comments; }

    public Set<Post> getPosts() { return posts; }
    public void setPosts(Set<Post> posts) { this.posts = posts; }

    public Set<Playlist> getPlaylists() { return playlists; }
    public void setPlaylists(Set<Playlist> playlists) { this.playlists = playlists; }

    // Helper methods actualizados
    public int getLikesCount() {
        return likes != null ? likes.size() : 0;
    }

    public int getCommentsCount() {
        return comments != null ? comments.size() : 0;
    }

    public int getRepostsCount() {
        return posts != null ? (int) posts.stream().filter(p -> p.getType() == PostType.REPOST).count() : 0;
    }

    public void incrementPlaysCount() {
        this.playsCount++;
    }

    // Métodos de conveniencia para archivos multimedia
    public boolean hasCoverImage() {
        return coverImageUrl != null && !coverImageUrl.trim().isEmpty();
    }

    public boolean hasWaveform() {
        return waveformUrl != null && !waveformUrl.trim().isEmpty();
    }

    public String getFormattedDuration() {
        if (duration == null) return "0:00";
        int minutes = duration / 60;
        int seconds = duration % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    public String getFormattedFileSize() {
        if (audioFileSize == null) return "0 B";
        long size = audioFileSize;
        String[] units = {"B", "KB", "MB", "GB"};
        int unitIndex = 0;
        double fileSize = size;

        while (fileSize >= 1024 && unitIndex < units.length - 1) {
            fileSize /= 1024;
            unitIndex++;
        }

        return String.format("%.1f %s", fileSize, units[unitIndex]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Track track = (Track) o;
        return Objects.equals(id, track.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Track{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", duration=" + duration +
                ", playsCount=" + playsCount +
                ", isPublic=" + isPublic +
                '}';
    }
}