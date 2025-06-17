package com.uv.backend.controller;

import com.uv.backend.dto.response.ApiResponse;
import com.uv.backend.entity.MediaFile;
import com.uv.backend.entity.MediaType;
import com.uv.backend.entity.User;
import com.uv.backend.service.FileStorageService;
import com.uv.backend.service.MediaFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType as SpringMediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private MediaFileService mediaFileService;

    @Value("${app.upload.audio.dir:./uploads/audio}")
    private String audioUploadDir;

    /**
     * Subir archivo de audio
     */
    @PostMapping("/upload/audio")
    public ResponseEntity<?> uploadAudio(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description,
            Authentication authentication) {

        try {
            User currentUser = (User) authentication.getPrincipal();

            MediaFile mediaFile = mediaFileService.uploadMediaFile(file, MediaType.AUDIO, currentUser);
            if (description != null) {
                mediaFile.setDescription(description);
                mediaFileService.saveMediaFile(mediaFile);
            }

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(mediaFile, "Audio file uploaded successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error uploading audio file: " + e.getMessage()));
        }
    }

    /**
     * Streaming de archivo de audio
     */
    @GetMapping("/stream/audio/{fileName}")
    public ResponseEntity<Resource> streamAudio(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(audioUploadDir).resolve(fileName);

            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "audio/mpeg";
                }

                return ResponseEntity.ok()
                        .contentType(SpringMediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                        .header(HttpHeaders.CACHE_CONTROL, "public, max-age=3600")
                        .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Descargar archivo de audio
     */
    @GetMapping("/download/audio/{fileName}")
    public ResponseEntity<Resource> downloadAudio(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(audioUploadDir).resolve(fileName);

            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);