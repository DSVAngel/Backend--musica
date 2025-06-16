package com.uv.backend.repository;

import com.uv.backend.entity.MediaFile;
import com.uv.backend.entity.MediaType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {

    // Buscar por usuario
    Page<MediaFile> findByUploadedByIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // Buscar por tipo de media
    Page<MediaFile> findByMediaTypeOrderByCreatedAtDesc(MediaType mediaType, Pageable pageable);

    // Buscar por usuario y tipo de media
    Page<MediaFile> findByUploadedByIdAndMediaTypeOrderByCreatedAtDesc(Long userId, MediaType mediaType, Pageable pageable);

    // Buscar por nombre de archivo
    Optional<MediaFile> findByStoredFileName(String storedFileName);

    // Buscar archivos por rango de fechas
    @Query("SELECT m FROM MediaFile m WHERE m.createdAt BETWEEN :startDate AND :endDate ORDER BY m.createdAt DESC")
    Page<MediaFile> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate,
                                    Pageable pageable);

    // Buscar archivos por tamaño
    @Query("SELECT m FROM MediaFile m WHERE m.fileSize BETWEEN :minSize AND :maxSize ORDER BY m.createdAt DESC")
    Page<MediaFile> findByFileSizeRange(@Param("minSize") Long minSize,
                                        @Param("maxSize") Long maxSize,
                                        Pageable pageable);

    // Estadísticas de uso de almacenamiento por usuario
    @Query("SELECT SUM(m.fileSize) FROM MediaFile m WHERE m.uploadedBy.id = :userId")
    Long getTotalStorageUsedByUser(@Param("userId") Long userId);

    // Estadísticas de uso de almacenamiento por tipo de media
    @Query("SELECT SUM(m.fileSize) FROM MediaFile m WHERE m.mediaType = :mediaType")
    Long getTotalStorageUsedByMediaType(@Param("mediaType") MediaType mediaType);

    // Contar archivos por usuario
    Long countByUploadedById(Long userId);

    // Contar archivos por tipo de media
    Long countByMediaType(MediaType mediaType);

    // Buscar archivos huérfanos (no referenciados)
    @Query("SELECT m FROM MediaFile m WHERE m.createdAt < :cutoffDate AND NOT EXISTS " +
            "(SELECT 1 FROM User u WHERE u.avatarUrl = m.fileUrl OR u.coverImageUrl = m.fileUrl) AND NOT EXISTS " +
            "(SELECT 1 FROM Track t WHERE t.audioUrl = m.fileUrl OR t.coverImageUrl = m.fileUrl OR t.waveformUrl = m.fileUrl) AND NOT EXISTS " +
            "(SELECT 1 FROM Playlist p WHERE p.coverImageUrl = m.fileUrl)")
    List<MediaFile> findOrphanedFiles(@Param("cutoffDate") LocalDateTime cutoffDate);

    // Buscar archivos recientes
    @Query("SELECT m FROM MediaFile m WHERE m.createdAt >= :since ORDER BY m.createdAt DESC")
    List<MediaFile> findRecentFiles(@Param("since") LocalDateTime since, Pageable pageable);
}