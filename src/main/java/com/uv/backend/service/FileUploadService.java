// FileUploadService.java - Servicio para manejo de archivos multimedia
package com.uv.backend.service;

import com.uv.backend.config.MediaTypeConfig;
import com.uv.backend.entity.MediaFile;
import com.uv.backend.entity.MediaType;
import com.uv.backend.entity.User;
import com.uv.backend.exception.FileUploadException;
import com.uv.backend.repository.MediaFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileUploadService {

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    @Value("${app.upload.images.dir:./uploads/images}")
    private String imageUploadDir;

    @Value("${app.upload.audio.dir:./uploads/audio}")
    private String audioUploadDir;

    @Value("${app.upload.videos.dir:./uploads/videos}")
    private String videoUploadDir;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Autowired
    private MediaFileRepository mediaFileRepository;

    // Subir imagen
    public String uploadImage(MultipartFile file, String subfolder) throws IOException {
        validateImageFile(file);
        return uploadFile(file, imageUploadDir, subfolder, MediaType.IMAGE);
    }

    // Subir audio
    public String uploadAudio(MultipartFile file) throws IOException {
        validateAudioFile(file);
        return uploadFile(file, audioUploadDir, null, MediaType.AUDIO);
    }

    // Subir video
    public String uploadVideo(MultipartFile file) throws IOException {
        validateVideoFile(file);
        return uploadFile(file, videoUploadDir, null, MediaType.VIDEO);
    }

    // Método genérico para subir archivos
    private String uploadFile(MultipartFile file, String baseDir, String subfolder, MediaType mediaType) throws IOException {
        // Crear directorios si no existen
        Path uploadPath = Paths.get(baseDir);
        if (subfolder != null) {
            uploadPath = uploadPath.resolve(subfolder);
        }

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generar nombre único para el archivo
        String originalFileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFileName);
        String storedFileName = UUID.randomUUID().toString() + fileExtension;

        // Guardar archivo
        Path filePath = uploadPath.resolve(storedFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Construir URL del archivo
        String fileUrl = buildFileUrl(baseDir, subfolder, storedFileName);

        return fileUrl;
    }

    // Método para guardar archivo con registro en base de datos
    public MediaFile uploadAndSaveMediaFile(MultipartFile file, MediaType mediaType, User user) throws IOException {
        String fileUrl;
        String baseDir;

        switch (mediaType) {
            case IMAGE:
                validateImageFile(file);
                baseDir = imageUploadDir;
                break;
            case AUDIO:
                validateAudioFile(file);
                baseDir = audioUploadDir;
                break;
            case VIDEO:
                validateVideoFile(file);
                baseDir = videoUploadDir;
                break;
            default:
                throw new FileUploadException("Unsupported media type: " + mediaType);
        }

        fileUrl = uploadFile(file, baseDir, null, mediaType);

        // Crear registro en base de datos
        String originalFileName = file.getOriginalFilename();
        String storedFileName = extractStoredFileName(fileUrl);

        MediaFile mediaFile = new MediaFile(
                originalFileName,
                storedFileName,
                fileUrl,
                file.getContentType(),
                file.getSize(),
                mediaType,
                user
        );

        return mediaFileRepository.save(mediaFile);
    }

    // Eliminar archivo
    public boolean deleteFile(String fileUrl) {
        try {
            Path filePath = getFilePathFromUrl(fileUrl);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                return true;
            }
        } catch (IOException e) {
            // Log error but don't throw exception
            System.err.println("Error deleting file: " + fileUrl + " - " + e.getMessage());
        }
        return false;
    }

    // Validaciones de archivos
    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileUploadException("File is empty");
        }

        if (!MediaTypeConfig.isValidImageType(file.getContentType())) {
            throw new FileUploadException("Invalid image type. Allowed types: " +
                    String.join(", ", MediaTypeConfig.ALLOWED_IMAGE_TYPES));
        }

        if (!MediaTypeConfig.isValidImageSize(file.getSize())) {
            throw new FileUploadException("Image file too large. Maximum size: " +
                    formatFileSize(MediaTypeConfig.MAX_IMAGE_SIZE));
        }

        if (!MediaTypeConfig.isValidImageExtension(file.getOriginalFilename())) {
            throw new FileUploadException("Invalid image extension. Allowed extensions: " +
                    String.join(", ", MediaTypeConfig.ALLOWED_IMAGE_EXTENSIONS));
        }
    }

    private void validateAudioFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileUploadException("File is empty");
        }

        if (!MediaTypeConfig.isValidAudioType(file.getContentType())) {
            throw new FileUploadException("Invalid audio type. Allowed types: " +
                    String.join(", ", MediaTypeConfig.ALLOWED_AUDIO_TYPES));
        }

        if (!MediaTypeConfig.isValidAudioSize(file.getSize())) {
            throw new FileUploadException("Audio file too large. Maximum size: " +
                    formatFileSize(MediaTypeConfig.MAX_AUDIO_SIZE));
        }

        if (!MediaTypeConfig.isValidAudioExtension(file.getOriginalFilename())) {
            throw new FileUploadException("Invalid audio extension. Allowed extensions: " +
                    String.join(", ", MediaTypeConfig.ALLOWED_AUDIO_EXTENSIONS));
        }
    }

    private void validateVideoFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileUploadException("File is empty");
        }

        if (!MediaTypeConfig.isValidVideoType(file.getContentType())) {
            throw new FileUploadException("Invalid video type. Allowed types: " +
                    String.join(", ", MediaTypeConfig.ALLOWED_VIDEO_TYPES));
        }

        if (!MediaTypeConfig.isValidVideoSize(file.getSize())) {
            throw new FileUploadException("Video file too large. Maximum size: " +
                    formatFileSize(MediaTypeConfig.MAX_VIDEO_SIZE));
        }

        if (!MediaTypeConfig.isValidVideoExtension(file.getOriginalFilename())) {
            throw new FileUploadException("Invalid video extension. Allowed extensions: " +
                    String.join(", ", MediaTypeConfig.ALLOWED_VIDEO_EXTENSIONS));
        }
    }

    // Métodos utilitarios
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.'));
    }

    private String buildFileUrl(String baseDir, String subfolder, String fileName) {
        StringBuilder urlBuilder = new StringBuilder();

        // Determinar el prefijo de URL basado en el directorio
        if (baseDir.contains("images")) {
            urlBuilder.append("/images");
        } else if (baseDir.contains("audio")) {
            urlBuilder.append("/audio");
        } else if (baseDir.contains("videos")) {
            urlBuilder.append("/videos");
        } else {
            urlBuilder.append("/uploads");
        }

        if (subfolder != null) {
            urlBuilder.append("/").append(subfolder);
        }

        urlBuilder.append("/").append(fileName);

        return urlBuilder.toString();
    }

    private String extractStoredFileName(String fileUrl) {
        return fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
    }

    private Path getFilePathFromUrl(String fileUrl) {
        // Convertir URL a path del sistema de archivos
        String relativePath = fileUrl.startsWith("/") ? fileUrl.substring(1) : fileUrl;

        if (relativePath.startsWith("images")) {
            return Paths.get(imageUploadDir).resolve(relativePath.substring("images/".length()));
        } else if (relativePath.startsWith("audio")) {
            return Paths.get(audioUploadDir).resolve(relativePath.substring("audio/".length()));
        } else if (relativePath.startsWith("videos")) {
            return Paths.get(videoUploadDir).resolve(relativePath.substring("videos/".length()));
        } else {
            return Paths.get(uploadDir).resolve(relativePath);
        }
    }

    private String formatFileSize(long fileSize) {
        String[] units = {"B", "KB", "MB", "GB"};
        int unitIndex = 0;
        double size = fileSize;

        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        return String.format("%.1f %s", size, units[unitIndex]);
    }
}

