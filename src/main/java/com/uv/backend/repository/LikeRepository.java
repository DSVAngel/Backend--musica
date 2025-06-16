package com.uv.backend.repository;
import com.uv.backend.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserIdAndPostId(Long userId, Long postId);
    Optional<Like> findByUserIdAndTrackId(Long userId, Long trackId);
    Optional<Like> findByUserIdAndCommentId(Long userId, Long commentId);

    Boolean existsByUserIdAndPostId(Long userId, Long postId);
    Boolean existsByUserIdAndTrackId(Long userId, Long trackId);
    Boolean existsByUserIdAndCommentId(Long userId, Long commentId);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.post.id = :postId")
    Long countByPostId(@Param("postId") Long postId);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.track.id = :trackId")
    Long countByTrackId(@Param("trackId") Long trackId);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.comment.id = :commentId")
    Long countByCommentId(@Param("commentId") Long commentId);

    void deleteByUserIdAndPostId(Long userId, Long postId);
    void deleteByUserIdAndTrackId(Long userId, Long trackId);
    void deleteByUserIdAndCommentId(Long userId, Long commentId);
    Optional<Like> findByUserIdAndPlaylistId(Long userId, Long playlistId);
    Boolean existsByUserIdAndPlaylistId(Long userId, Long playlistId);
    void deleteByUserIdAndPlaylistId(Long userId, Long playlistId);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.playlist.id = :playlistId")
    Long countByPlaylistId(@Param("playlistId") Long playlistId);

}
