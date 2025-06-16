package com.uv.backend.repository;

import com.uv.backend.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByPostIdAndParentIsNullOrderByCreatedAtDesc(Long postId, Pageable pageable);
    Page<Comment> findByTrackIdAndParentIsNullOrderByCreatedAtDesc(Long trackId, Pageable pageable);
    Page<Comment> findByParentIdOrderByCreatedAtAsc(Long parentId, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId")
    Long countByPostId(@Param("postId") Long postId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.track.id = :trackId")
    Long countByTrackId(@Param("trackId") Long trackId);
}
