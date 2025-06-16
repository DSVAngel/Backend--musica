package com.uv.backend.repository;

import com.uv.backend.entity.Track;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {
    Page<Track> findByUserIdAndIsPublicTrue(Long userId, Pageable pageable);
    Page<Track> findByUserId(Long userId, Pageable pageable);
    Page<Track> findByIsPublicTrueOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT t FROM Track t WHERE t.isPublic = true AND " +
            "(t.title LIKE %:query% OR t.user.displayName LIKE %:query% OR " +
            "EXISTS (SELECT tag FROM t.tags tag WHERE tag LIKE %:query%))")
    Page<Track> searchTracks(@Param("query") String query, Pageable pageable);

    @Query("SELECT t FROM Track t WHERE t.isPublic = true ORDER BY t.playsCount DESC")
    List<Track> findTrendingTracks(Pageable pageable);

    @Query("SELECT t FROM Track t WHERE t.isPublic = true AND t.user.id IN " +
            "(SELECT f.following.id FROM Follow f WHERE f.follower.id = :userId) " +
            "ORDER BY t.createdAt DESC")
    Page<Track> findTracksFromFollowedUsers(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT t FROM Track t WHERE t.isPublic = true AND t.genre = :genre ORDER BY t.createdAt DESC")
    Page<Track> findByGenre(@Param("genre") String genre, Pageable pageable);

    @Query("SELECT t FROM Track t WHERE t.isPublic = true AND " +
            "EXISTS (SELECT tag FROM t.tags tag WHERE tag = :tag) ORDER BY t.createdAt DESC")
    Page<Track> findByTag(@Param("tag") String tag, Pageable pageable);

    // Nuevas consultas para archivos multimedia
    @Query("SELECT t FROM Track t WHERE t.audioUrl = :audioUrl")
    List<Track> findByAudioUrl(@Param("audioUrl") String audioUrl);

    @Query("SELECT t FROM Track t WHERE t.coverImageUrl = :coverImageUrl")
    List<Track> findByCoverImageUrl(@Param("coverImageUrl") String coverImageUrl);

    @Query("SELECT t FROM Track t WHERE t.waveformUrl = :waveformUrl")
    List<Track> findByWaveformUrl(@Param("waveformUrl") String waveformUrl);

    // Buscar tracks con archivos multimedia específicos
    @Query("SELECT t FROM Track t WHERE t.coverImageUrl IS NOT NULL AND t.isPublic = true ORDER BY t.createdAt DESC")
    Page<Track> findTracksWithCoverImage(Pageable pageable);

    @Query("SELECT t FROM Track t WHERE t.waveformUrl IS NOT NULL AND t.isPublic = true ORDER BY t.createdAt DESC")
    Page<Track> findTracksWithWaveform(Pageable pageable);

    // Estadísticas de archivos
    @Query("SELECT COUNT(t) FROM Track t WHERE t.coverImageUrl IS NOT NULL")
    Long countTracksWithCoverImage();

    @Query("SELECT COUNT(t) FROM Track t WHERE t.waveformUrl IS NOT NULL")
    Long countTracksWithWaveform();
}