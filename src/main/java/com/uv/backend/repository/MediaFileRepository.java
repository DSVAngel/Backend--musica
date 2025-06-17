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

    // Buscar por nombre de archivo almacenado
    Optional<MediaFile> findByStoredFileName(String storedFileName);

    // Buscar por nombre de archivo original
    Page<MediaFile> findByOriginalFileNameContainingIgnoreCaseOrderByCreatedAtDesc(String fileName, Pageable pageable);

    // Buscar por nombre de archivo original y tipo
    Page<MediaFile> findByOriginalFileNameContainingIgnoreCaseAndMediaTypeOrderByCreatedAtDesc(
            String fileName, MediaType mediaType, Pageable pageable);

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
    @Query("SELECT COALESCE(SUM(m.fileSize), 0) FROM MediaFile m WHERE m.uploadedBy.id = :userId")
    Long getTotalStorageUsedByUser(@Param("userId") Long userId);

    // Estadísticas de uso de almacenamiento por usuario y tipo de media
    @Query("SELECT COALESCE(SUM(m.fileSize), 0) FROM MediaFile m WHERE m.uploadedBy.id = :userId AND m.mediaType = :mediaType")
    Long getTotalStorageUsedByUserAndMediaType(@Param("userId") Long userId, @Param("mediaType") MediaType mediaType);

    // Estadísticas de uso de almacenamiento por tipo de media
    @Query("SELECT COALESCE(SUM(m.fileSize), 0) FROM MediaFile m WHERE m.mediaType = :mediaType")
    Long getTotalStorageUsedByMediaType(@Param("mediaType") MediaType mediaType);

    // Contar archivos por usuario
    Long countByUploadedById(Long userId);

    // Contar archivos por usuario y tipo de media
    Long countByUploadedByIdAndMediaType(Long userId, MediaType mediaType);

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

    // Buscar archivos por URL
    Optional<MediaFile> findByFileUrl(String fileUrl);

    // Buscar archivos grandes (por encima de un tamaño específico)
    @Query("SELECT m FROM MediaFile m WHERE m.fileSize > :sizeThreshold ORDER BY m.fileSize DESC")
    Page<MediaFile> findLargeFiles(@Param("sizeThreshold") Long sizeThreshold, Pageable pageable);

    // Obtener archivos más descargados/accedidos (si se implementa tracking)
    @Query("SELECT m FROM MediaFile m WHERE m.mediaType = :mediaType ORDER BY m.createdAt DESC")
    List<MediaFile> findPopularFilesByType(@Param("mediaType") MediaType mediaType, Pageable pageable);

    // Buscar archivos duplicados por nombre original
    @Query("SELECT m FROM MediaFile m WHERE m.originalFileName = :fileName AND m.uploadedBy.id = :userId")
    List<MediaFile> findDuplicatesByOriginalFileName(@Param("fileName") String fileName, @Param("userId") Long userId);

    // Estadísticas globales del sistema
    @Query("SELECT COUNT(m) FROM MediaFile m")
    Long getTotalFilesCount();

    @Query("SELECT COALESCE(SUM(m.fileSize), 0) FROM MediaFile m")
    Long getTotalStorageUsed();

    // Buscar archivos por extensión
    @Query("SELECT m FROM MediaFile m WHERE m.originalFileName LIKE %:extension ORDER BY m.createdAt DESC")
    Page<MediaFile> findByFileExtension(@Param("extension") String extension, Pageable pageable);

    // Obtener archivos de audio para streaming
    @Query("SELECT m FROM MediaFile m WHERE m.mediaType = 'AUDIO' AND m.uploadedBy.id = :userId ORDER BY m.createdAt DESC")
    Page<MediaFile> findAudioFilesByUser(@Param("userId") Long userId, Pageable pageable);

    // Obtener todos los archivos de audio públicos (si se implementa concepto de público/privado)
    @Query("SELECT m FROM MediaFile m WHERE m.mediaType = 'AUDIO' ORDER BY m.createdAt DESC")
    Page<MediaFile> findAllAudioFiles(Pageable pageable);
}