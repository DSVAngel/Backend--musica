package com.uv.backend.repository;
import com.uv.backend.entity.Playlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    Page<Playlist> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    Page<Playlist> findByIsPublicTrueOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT p FROM Playlist p WHERE p.isPublic = true AND " +
            "(p.title LIKE %:query% OR p.user.displayName LIKE %:query%)")
    Page<Playlist> searchPlaylists(@Param("query") String query, Pageable pageable);

    @Query("SELECT p FROM Playlist p WHERE p.user.id = :userId AND " +
            "(p.title LIKE %:query% OR p.description LIKE %:query%)")
    Page<Playlist> searchUserPlaylists(@Param("userId") Long userId, @Param("query") String query, Pageable pageable);
}