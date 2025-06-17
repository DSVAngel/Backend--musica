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
public class FileStorageService {

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    @Value("${app.upload.audio.dir:./uploads/audio}")
    private String audioUploadDir;

    @Value("${server.port:8080}")
    private String serverPort;

    @Autowired
    private MediaFileRepository mediaFileRepository;

    /**
     * Guarda una imagen desde URL (para imágenes externas)
     */
    public String saveImageFromUrl(String imageUrl, String subfolder) {
        // Para imágenes, simplemente validamos y retornamos la URL
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new FileUploadException("Image URL cannot be empty");
        }

        // Validar que sea una URL válida de imagen
        if (!isValidImageUrl(imageUrl)) {
            throw new FileUploadException("Invalid image URL format");
        }

        return imageUrl;
    }

    /**
     * Sube archivo de audio al servidor local
     */
    public String uploadAudioFile(MultipartFile file, User user) throws IOException {
        validateAudioFile(file);

        // Crear directorio si no existe
        Path uploadPath = Paths.get(audioUploadDir);
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

        // Crear registro en base de datos
        MediaFile mediaFile = new MediaFile(
                originalFileName,
                storedFileName,
                "/audio/" + storedFileName,
                file.getContentType(),
                file.getSize(),
                MediaType.AUDIO,
                user
        );

        mediaFileRepository.save(mediaFile);

        // Retornar URL local del archivo
        return "/audio/" + storedFileName;
    }

    /**
     * Sube archivo de imagen al servidor (solo para casos especiales)
     */
    public String uploadImageFile(MultipartFile file, String subfolder) throws IOException {
        validateImageFile(file);

        String subDir = "images" + (subfolder != null ? "/" + subfolder : "");
        Path uploadPath = Paths.get(uploadDir, subDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFileName);
        String storedFileName = UUID.randomUUID().toString() + fileExtension;

        Path filePath = uploadPath.resolve(storedFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/" + subDir + "/" + storedFileName;
    }

    /**
     * Elimina archivo del sistema
     */
    public boolean deleteFile(String fileUrl) {
        try {
            if (fileUrl == null || fileUrl.trim().isEmpty()) {
                return false;
            }

            // Si es una URL externa, no hacer nada
            if (fileUrl.startsWith("http://") || fileUrl.startsWith("https://")) {
                return true;
            }

            // Si es un archivo local, eliminarlo
            Path filePath = getFilePathFromUrl(fileUrl);
            if (Files.exists(filePath)) {
                Files.delete(filePath);

                // Eliminar también el registro de la base de datos si existe
                String storedFileName = extractStoredFileName(fileUrl);
                mediaFileRepository.findByStoredFileName(storedFileName)
                        .ifPresent(mediaFileRepository::delete);

                return true;
            }
        } catch (IOException e) {
            System.err.println("Error deleting file: " + fileUrl + " - " + e.getMessage());
        }
        return false;
    }

    // Métodos de validación
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

    // Métodos utilitarios
    private boolean isValidImageUrl(String url) {
        return url.matches("^https?://.*\\.(jpg|jpeg|png|gif|webp|svg)$") ||
                url.matches("^https?://.*\\.(JPG|JPEG|PNG|GIF|WEBP|SVG)$") ||
                url.contains("images") || url.contains("photo") || url.contains("picture");
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.'));
    }

    private String extractStoredFileName(String fileUrl) {
        return fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
    }

    private Path getFilePathFromUrl(String fileUrl) {
        String relativePath = fileUrl.startsWith("/") ? fileUrl.substring(1) : fileUrl;

        if (relativePath.startsWith("audio/")) {
            return Paths.get(audioUploadDir).resolve(relativePath.substring("audio/".length()));
        } else if (relativePath.startsWith("images/")) {
            return Paths.get(uploadDir).resolve(relativePath);
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