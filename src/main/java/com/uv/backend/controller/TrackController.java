package com.uv.backend.controller;

import com.uv.backend.dto.TrackDto;
import com.uv.backend.dto.response.ApiResponse;
import com.uv.backend.entity.User;
import com.uv.backend.service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

@RestController
@RequestMapping("/api/tracks")
@CrossOrigin(origins = "*")
public class TrackController {

    @Autowired
    private TrackService trackService;

    /**
     * Crear track - Audio como archivo, imagen de portada como URL
     */
    @PostMapping
    public ResponseEntity<?> createTrack(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("audioFile") MultipartFile audioFile,
            @RequestParam("duration") Integer duration,
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "tags", required = false) String[] tags,
            @RequestParam(value = "isPublic", defaultValue = "true") Boolean isPublic,
            @RequestParam(value = "coverImageUrl", required = false) String coverImageUrl,
            Authentication authentication) {

        try {
            User currentUser = (User) authentication.getPrincipal();

            Set<String> tagSet = tags != null ?
                    Set.of(tags) : Set.of();

            TrackDto track = trackService.createTrack(
                    title, description, audioFile, duration,
                    genre, tagSet, isPublic, coverImageUrl, currentUser);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(track, "Track created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error creating track: " + e.getMessage()));
        }
    }

    /**
     * Subir track alternativo (manteniendo compatibilidad)
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadTrack(
            @RequestParam("audioFile") MultipartFile audioFile,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("duration") Integer duration,
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "tags", required = false) String[] tags,
            @RequestParam(value = "isPublic", defaultValue = "true") Boolean isPublic,
            @RequestParam(value = "coverImageUrl", required = false) String coverImageUrl,
            Authentication authentication) {

        try {
            User currentUser = (User) authentication.getPrincipal();

            Set<String> tagSet = tags != null ?
                    Set.of(tags) : Set.of();

            TrackDto track = trackService.createTrack(
                    title, description, audioFile, duration,
                    genre, tagSet, isPublic, coverImageUrl, currentUser);

            return ResponseEntity.ok(ApiResponse.success(track, "Track uploaded successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error uploading track: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTrackById(@PathVariable Long id) {
        try {
            TrackDto track = trackService.getTrackById(id);
            return ResponseEntity.ok(ApiResponse.success(track));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Track not found"));
        }
    }

    @GetMapping("/public")
    public ResponseEntity<?> getPublicTracks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TrackDto> tracks = trackService.getPublicTracks(pageable);
        return ResponseEntity.ok(ApiResponse.success(tracks));
    }

    @GetMapping("/trending")
    public ResponseEntity<?> getTrendingTracks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TrackDto> tracks = trackService.getTrendingTracks(pageable);
        return ResponseEntity.ok(ApiResponse.success(tracks));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchTracks(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TrackDto> tracks = trackService.searchTracks(query, pageable);
        return ResponseEntity.ok(ApiResponse.success(tracks));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserTracks(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TrackDto> tracks = trackService.getUserTracks(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(tracks));
    }

    /**
     * Actualizar track - Solo metadatos
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTrack(
            @PathVariable Long id,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "tags", required = false) String[] tags,
            @RequestParam(value = "isPublic", required = false) Boolean isPublic,
            @RequestParam(value = "coverImageUrl", required = false) String coverImageUrl,
            Authentication authentication) {

        try {
            Set<String> tagSet = tags != null ? Set.of(tags) : null;

            TrackDto track = trackService.updateTrack(
                    id, title, description, genre, tagSet, isPublic, coverImageUrl);

            return ResponseEntity.ok(ApiResponse.success(track, "Track updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error updating track: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTrack(@PathVariable Long id, Authentication authentication) {
        try {
            trackService.deleteTrack(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Track deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error deleting track: " + e.getMessage()));
        }
    }

    /**
     * Actualizar solo la imagen de portada por URL
     */
    @PutMapping("/{id}/cover")
    public ResponseEntity<?> updateCoverImage(
            @PathVariable Long id,
            @RequestParam("coverImageUrl") String coverImageUrl,
            Authentication authentication) {

        try {
            TrackDto track = trackService.updateCoverImageUrl(id, coverImageUrl);
            return ResponseEntity.ok(ApiResponse.success(track, "Cover image updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error updating cover image: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/play")
    public ResponseEntity<?> incrementPlayCount(@PathVariable Long id) {
        try {
            trackService.incrementPlaysCount(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Play count incremented"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error incrementing play count: " + e.getMessage()));
        }
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<?> getTracksByGenre(
            @PathVariable String genre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TrackDto> tracks = trackService.getTracksByGenre(genre, pageable);
        return ResponseEntity.ok(ApiResponse.success(tracks));
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<?> getTracksByTag(
            @PathVariable String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TrackDto> tracks = trackService.getTracksByTag(tag, pageable);
        return ResponseEntity.ok(ApiResponse.success(tracks));
    }

    /**
     * Subir waveform como archivo (opcional)
     */
    @PostMapping("/{id}/waveform")
    public ResponseEntity<?> updateWaveform(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        try {
            TrackDto track = trackService.updateWaveform(id, file);
            return ResponseEntity.ok(ApiResponse.success(track, "Waveform updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error updating waveform: " + e.getMessage()));
        }
    }
}