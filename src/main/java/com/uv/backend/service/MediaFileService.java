package com.uv.backend.service;

import com.uv.backend.entity.MediaFile;
import com.uv.backend.entity.MediaType;
import com.uv.backend.entity.User;
import com.uv.backend.exception.ResourceNotFoundException;
import com.uv.backend.repository.MediaFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class MediaFileService {

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Subir archivo multimedia
     */
    public MediaFile uploadMediaFile(MultipartFile file, MediaType mediaType, User user) throws IOException {
        String fileUrl;

        switch (mediaType) {
            case AUDIO:
                fileUrl = fileStorageService.uploadAudioFile(file, user);
                break;
            case IMAGE:
                fileUrl = fileStorageService.uploadImageFile(file, null);
                break;
            default:
                throw new IllegalArgumentException("Unsupported media type: " + mediaType);
        }

        MediaFile mediaFile = new MediaFile(
                file.getOriginalFilename(),
                extractStoredFileName(fileUrl),
                fileUrl,
                file.getContentType(),
                file.getSize(),
                mediaType,
                user
        );

        return mediaFileRepository.save(mediaFile);
    }

    /**
     * Obtener archivo multimedia por ID
     */
    public MediaFile getMediaFileById(Long id) {
        return mediaFileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Media file not found with id: " + id));
    }

    /**
     * Guardar archivo multimedia
     */
    public MediaFile saveMediaFile(MediaFile mediaFile) {
        return mediaFileRepository.save(mediaFile);
    }

    /**
     * Eliminar archivo multimedia
     */
    public void deleteMediaFile(Long id, User user) {
        MediaFile mediaFile = getMediaFileById(id);

        // Verificar que el usuario sea el propietario del archivo
        if (!mediaFile.getUploadedBy().getId().equals(user.getId())) {
            throw new SecurityException("You can only delete your own files");
        }

        // Eliminar archivo del sistema de archivos
        fileStorageService.deleteFile(mediaFile.getFileUrl());

        // Eliminar registro de la base de datos
        mediaFileRepository.delete(mediaFile);
    }

    /**
     * Obtener archivos multimedia del usuario
     */
    public Page<MediaFile> getUserMediaFiles(Long userId, int page, int size, MediaType mediaType) {
        Pageable pageable = PageRequest.of(page, size);

        if (mediaType != null) {
            return mediaFileRepository.findByUploadedByIdAndMediaTypeOrderByCreatedAtDesc(userId, mediaType, pageable);
        } else {
            return mediaFileRepository.findByUploadedByIdOrderByCreatedAtDesc(userId, pageable);
        }
    }

    /**
     * Obtener estadísticas de almacenamiento del usuario
     */
    public Map<String, Object> getStorageStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();

        // Uso total de almacenamiento
        Long totalStorage = mediaFileRepository.getTotalStorageUsedByUser(userId);
        stats.put("totalStorageUsed", totalStorage != null ? totalStorage : 0L);

        // Conteo de archivos por tipo
        Long audioCount = mediaFileRepository.countByUploadedByIdAndMediaType(userId, MediaType.AUDIO);
        Long imageCount = mediaFileRepository.countByUploadedByIdAndMediaType(userId, MediaType.IMAGE);
        Long videoCount = mediaFileRepository.countByUploadedByIdAndMediaType(userId, MediaType.VIDEO);

        stats.put("audioFilesCount", audioCount);
        stats.put("imageFilesCount", imageCount);
        stats.put("videoFilesCount", videoCount);
        stats.put("totalFilesCount", audioCount + imageCount + videoCount);

        // Uso de almacenamiento por tipo
        Long audioStorage = mediaFileRepository.getTotalStorageUsedByUserAndMediaType(userId, MediaType.AUDIO);
        Long imageStorage = mediaFileRepository.getTotalStorageUsedByUserAndMediaType(userId, MediaType.IMAGE);
        Long videoStorage = mediaFileRepository.getTotalStorageUsedByUserAndMediaType(userId, MediaType.VIDEO);

        stats.put("audioStorageUsed", audioStorage != null ? audioStorage : 0L);
        stats.put("imageStorageUsed", imageStorage != null ? imageStorage : 0L);
        stats.put("videoStorageUsed", videoStorage != null ? videoStorage : 0L);

        return stats;
    }

    /**
     * Buscar archivos multimedia
     */
    public Page<MediaFile> searchMediaFiles(String query, MediaType mediaType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        if (mediaType != null) {
            return mediaFileRepository.findByOriginalFileNameContainingIgnoreCaseAndMediaTypeOrderByCreatedAtDesc(
                    query, mediaType, pageable);
        } else {
            return mediaFileRepository.findByOriginalFileNameContainingIgnoreCaseOrderByCreatedAtDesc(
                    query, pageable);
        }
    }

    /**
     * Obtener archivos multimedia por tipo
     */
    public Page<MediaFile> getMediaFilesByType(MediaType mediaType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return mediaFileRepository.findByMediaTypeOrderByCreatedAtDesc(mediaType, pageable);
    }

    /**
     * Obtener archivos huérfanos (no referenciados)
     */
    public void cleanupOrphanedFiles() {
        // Buscar archivos huérfanos (más de 7 días sin referencias)
        var cutoffDate = java.time.LocalDateTime.now().minusDays(7);
        var orphanedFiles = mediaFileRepository.findOrphanedFiles(cutoffDate);

        for (MediaFile file : orphanedFiles) {
            // Eliminar archivo del sistema de archivos
            fileStorageService.deleteFile(file.getFileUrl());
            // Eliminar registro de la base de datos
            mediaFileRepository.delete(file);
        }
    }

    // Métodos utilitarios
    private String extractStoredFileName(String fileUrl) {
        return fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
    }

    private Long countByUploadedByIdAndMediaType(Long userId, MediaType mediaType) {
        return mediaFileRepository.findByUploadedByIdAndMediaTypeOrderByCreatedAtDesc(userId, mediaType, Pageable.unpaged())
                .getTotalElements();
    }
}