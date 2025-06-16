package com.uv.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Post type is required")
    private PostType type;

    @Column(length = 1000)
    private String content;

    // Campos multimedia para posts
    @ElementCollection
    @CollectionTable(name = "post_images", joinColumns = @JoinColumn(name = "post_id"))
    private Set<PostImage> images = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "post_videos", joinColumns = @JoinColumn(name = "post_id"))
    private Set<PostVideo> videos = new HashSet<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Relationships (sin cambios)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_id")
    private Track track;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_post_id")
    private Post originalPost;

    @OneToMany(mappedBy = "originalPost", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Post> reposts = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Like> likes = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Comment> comments = new HashSet<>();

    // Constructors
    public Post() {}

    public Post(PostType type, User user) {
        this.type = type;
        this.user = user;
    }

    public Post(PostType type, String content, User user) {
        this.type = type;
        this.content = content;
        this.user = user;
    }

    public Post(PostType type, Track track, User user) {
        this.type = type;
        this.track = track;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PostType getType() { return type; }
    public void setType(PostType type) { this.type = type; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Set<PostImage> getImages() { return images; }
    public void setImages(Set<PostImage> images) { this.images = images; }

    public Set<PostVideo> getVideos() { return videos; }
    public void setVideos(Set<PostVideo> videos) { this.videos = videos; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Track getTrack() { return track; }
    public void setTrack(Track track) { this.track = track; }

    public Post getOriginalPost() { return originalPost; }
    public void setOriginalPost(Post originalPost) { this.originalPost = originalPost; }

    public Set<Post> getReposts() { return reposts; }
    public void setReposts(Set<Post> reposts) { this.reposts = reposts; }

    public Set<Like> getLikes() { return likes; }
    public void setLikes(Set<Like> likes) { this.likes = likes; }

    public Set<Comment> getComments() { return comments; }
    public void setComments(Set<Comment> comments) { this.comments = comments; }

    // Helper methods actualizados
    public int getLikesCount() {
        return likes != null ? likes.size() : 0;
    }

    public int getCommentsCount() {
        return comments != null ? comments.size() : 0;
    }

    public int getRepostsCount() {
        return reposts != null ? reposts.size() : 0;
    }

    public boolean hasImages() {
        return images != null && !images.isEmpty();
    }

    public boolean hasVideos() {
        return videos != null && !videos.isEmpty();
    }

    public boolean hasMultimedia() {
        return hasImages() || hasVideos();
    }

    public void addImage(PostImage image) {
        if (images == null) {
            images = new HashSet<>();
        }
        images.add(image);
    }

    public void addVideo(PostVideo video) {
        if (videos == null) {
            videos = new HashSet<>();
        }
        videos.add(video);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(id, post.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", type=" + type +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}