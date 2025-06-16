package com.uv.backend.repository;

import com.uv.backend.entity.Post;
import com.uv.backend.entity.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.user.id IN " +
            "(SELECT f.following.id FROM Follow f WHERE f.follower.id = :userId) " +
            "OR p.user.id = :userId ORDER BY p.createdAt DESC")
    Page<Post> findFeedPosts(@Param("userId") Long userId, Pageable pageable);

    Page<Post> findByTypeOrderByCreatedAtDesc(PostType type, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.originalPost.id = :postId AND p.type = 'REPOST'")
    Long countReposts(@Param("postId") Long postId);

    @Query("SELECT p FROM Post p WHERE p.track.id = :trackId AND p.type = 'REPOST'")
    Page<Post> findRepostsByTrackId(@Param("trackId") Long trackId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.user.id = :userId AND p.track.id = :trackId AND p.type = :type")
    List<Post> findByUserIdAndTrackIdAndType(@Param("userId") Long userId,
                                             @Param("trackId") Long trackId,
                                             @Param("type") PostType type);

    @Query("SELECT p FROM Post p WHERE p.user.id = :userId AND p.originalPost.id = :originalPostId AND p.type = :type")
    List<Post> findByUserIdAndOriginalPostIdAndType(@Param("userId") Long userId,
                                                    @Param("originalPostId") Long originalPostId,
                                                    @Param("type") PostType type);

    // Nuevas consultas para posts multimedia
    @Query("SELECT p FROM Post p WHERE SIZE(p.images) > 0 ORDER BY p.createdAt DESC")
    Page<Post> findPostsWithImages(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE SIZE(p.videos) > 0 ORDER BY p.createdAt DESC")
    Page<Post> findPostsWithVideos(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE (SIZE(p.images) > 0 OR SIZE(p.videos) > 0) ORDER BY p.createdAt DESC")
    Page<Post> findPostsWithMultimedia(Pageable pageable);

    // Buscar posts por usuario con multimedia
    @Query("SELECT p FROM Post p WHERE p.user.id = :userId AND SIZE(p.images) > 0 ORDER BY p.createdAt DESC")
    Page<Post> findPostsWithImagesByUser(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.user.id = :userId AND SIZE(p.videos) > 0 ORDER BY p.createdAt DESC")
    Page<Post> findPostsWithVideosByUser(@Param("userId") Long userId, Pageable pageable);

    // Estadísticas de posts multimedia
    @Query("SELECT COUNT(p) FROM Post p WHERE SIZE(p.images) > 0")
    Long countPostsWithImages();

    @Query("SELECT COUNT(p) FROM Post p WHERE SIZE(p.videos) > 0")
    Long countPostsWithVideos();

    @Query("SELECT COUNT(p) FROM Post p WHERE (SIZE(p.images) > 0 OR SIZE(p.videos) > 0)")
    Long countPostsWithMultimedia();
}