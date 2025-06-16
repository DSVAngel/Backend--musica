package com.uv.backend.config;

import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class MediaTypeConfig {

    // Tipos de archivo de imagen permitidos
    public static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp", "image/svg+xml"
    );

    // Tipos de archivo de audio permitidos
    public static final List<String> ALLOWED_AUDIO_TYPES = Arrays.asList(
            "audio/mpeg", "audio/mp3", "audio/wav", "audio/flac", "audio/aac",
            "audio/ogg", "audio/wma", "audio/m4a"
    );

    // Tipos de archivo de video permitidos
    public static final List<String> ALLOWED_VIDEO_TYPES = Arrays.asList(
            "video/mp4", "video/avi", "video/mov", "video/wmv", "video/flv",
            "video/webm", "video/mkv", "video/m4v"
    );

    // Tamaños máximos de archivo (en bytes)
    public static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024; // 10MB
    public static final long MAX_AUDIO_SIZE = 500 * 1024 * 1024; // 500MB
    public static final long MAX_VIDEO_SIZE = 1024 * 1024 * 1024; // 1GB

    // Extensiones de archivo permitidas
    public static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".webp", ".svg"
    );

    public static final List<String> ALLOWED_AUDIO_EXTENSIONS = Arrays.asList(
            ".mp3", ".wav", ".flac", ".aac", ".ogg", ".wma", ".m4a"
    );

    public static final List<String> ALLOWED_VIDEO_EXTENSIONS = Arrays.asList(
            ".mp4", ".avi", ".mov", ".wmv", ".flv", ".webm", ".mkv", ".m4v"
    );

    // Métodos de validación
    public static boolean isValidImageType(String contentType) {
        return ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase());
    }

    public static boolean isValidAudioType(String contentType) {
        return ALLOWED_AUDIO_TYPES.contains(contentType.toLowerCase());
    }

    public static boolean isValidVideoType(String contentType) {
        return ALLOWED_VIDEO_TYPES.contains(contentType.toLowerCase());
    }

    public static boolean isValidImageExtension(String filename) {
        return ALLOWED_IMAGE_EXTENSIONS.stream()
                .anyMatch(ext -> filename.toLowerCase().endsWith(ext));
    }

    public static boolean isValidAudioExtension(String filename) {
        return ALLOWED_AUDIO_EXTENSIONS.stream()
                .anyMatch(ext -> filename.toLowerCase().endsWith(ext));
    }

    public static boolean isValidVideoExtension(String filename) {
        return ALLOWED_VIDEO_EXTENSIONS.stream()
                .anyMatch(ext -> filename.toLowerCase().endsWith(ext));
    }

    public static boolean isValidImageSize(long fileSize) {
        return fileSize <= MAX_IMAGE_SIZE;
    }

    public static boolean isValidAudioSize(long fileSize) {
        return fileSize <= MAX_AUDIO_SIZE;
    }

    public static boolean isValidVideoSize(long fileSize) {
        return fileSize <= MAX_VIDEO_SIZE;
    }
}