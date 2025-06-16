package com.uv.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "playlists")
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Title is required")
    private String title;

    @Column(length = 1000)
    private String description;

    // Campos de imagen de portada actualizados
    private String coverImageUrl;
    private String coverImageFileName;
    private String coverImageFileType;
    private Long coverImageFileSize;

    @Column(nullable = false)
    private Boolean isPublic = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "playlist_tracks",
            joinColumns = @JoinColumn(name = "playlist_id"),
            inverseJoinColumns = @JoinColumn(name = "track_id")
    )
    private Set<Track> tracks = new HashSet<>();

    @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Like> likes = new HashSet<>();

    // Constructors
    public Playlist() {}

    public Playlist(String title, User user) {
        this.title = title;
        this.user = user;
    }

    // Getters and Setters actualizados
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // Cover image getters/setters
    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public String getCoverImageFileName() { return coverImageFileName; }
    public void setCoverImageFileName(String coverImageFileName) { this.coverImageFileName = coverImageFileName; }

    public String getCoverImageFileType() { return coverImageFileType; }
    public void setCoverImageFileType(String coverImageFileType) { this.coverImageFileType = coverImageFileType; }

    public Long getCoverImageFileSize() { return coverImageFileSize; }
    public void setCoverImageFileSize(Long coverImageFileSize) { this.coverImageFileSize = coverImageFileSize; }

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Set<Track> getTracks() { return tracks; }
    public void setTracks(Set<Track> tracks) { this.tracks = tracks; }

    public Set<Like> getLikes() { return likes; }
    public void setLikes(Set<Like> likes) { this.likes = likes; }

    // Helper methods actualizados
    public int getLikesCount() {
        return likes != null ? likes.size() : 0;
    }

    public int getTracksCount() {
        return tracks != null ? tracks.size() : 0;
    }

    public void addTrack(Track track) {
        tracks.add(track);
        track.getPlaylists().add(this);
    }

    public void removeTrack(Track track) {
        tracks.remove(track);
        track.getPlaylists().remove(this);
    }

    public boolean hasCoverImage() {
        return coverImageUrl != null && !coverImageUrl.trim().isEmpty();
    }

    public Integer getTotalDuration() {
        return tracks.stream()
                .filter(track -> track.getDuration() != null)
                .mapToInt(Track::getDuration)
                .sum();
    }

    public String getFormattedTotalDuration() {
        Integer totalDuration = getTotalDuration();
        if (totalDuration == null || totalDuration == 0) return "0:00";

        int hours = totalDuration / 3600;
        int minutes = (totalDuration % 3600) / 60;
        int seconds = totalDuration % 60;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Playlist playlist = (Playlist) o;
        return Objects.equals(id, playlist.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}