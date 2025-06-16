package com.uv.backend.controller;

import com.uv.backend.dto.TrackDto;
import com.uv.backend.dto.request.TrackRequest;
import com.uv.backend.entity.User;
import com.uv.backend.service.TrackService;
import com.uv.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/api/tracks")
@CrossOrigin(origins = "*")
public class TrackController {

    @Autowired
    private TrackService trackService;

    @Autowired
    private UserService userService;

    // CORREGIDO - Crear track usando parámetros separados
    @PostMapping
    public ResponseEntity<TrackDto> createTrack(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("audioFile") MultipartFile audioFile,
            @RequestParam("duration") Integer duration,
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "tags", required = false) String[] tags,
            @RequestParam(value = "isPublic", defaultValue = "true") Boolean isPublic,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage) throws IOException {

        java.util.Set<String> tagSet = tags != null ?
                java.util.Set.of(tags) : java.util.Collections.emptySet();

        TrackDto track = trackService.createTrack(title, description, audioFile,
                duration, genre, tagSet, isPublic, coverImage);
        return ResponseEntity.ok(track);
    }

    // Subir track alternativo (usando form data)
    @PostMapping("/upload")
    public ResponseEntity<TrackDto> uploadTrack(
            @RequestParam("audioFile") MultipartFile audioFile,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("duration") Integer duration,
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "tags", required = false) String[] tags,
            @RequestParam(value = "isPublic", defaultValue = "true") Boolean isPublic) throws IOException {

        java.util.Set<String> tagSet = tags != null ?
                java.util.Set.of(tags) : java.util.Collections.emptySet();

        TrackDto track = trackService.createTrack(title, description, audioFile,
                duration, genre, tagSet, isPublic, coverImage);
        return ResponseEntity.ok(track);
    }

    // Obtener track por ID
    @GetMapping("/{id}")
    public ResponseEntity<TrackDto> getTrackById(@PathVariable Long id) {
        TrackDto track = trackService.getTrackById(id); // CORREGIDO - ya devuelve TrackDto
        return ResponseEntity.ok(track);
    }

    // CORREGIDO - Obtener todos los tracks públicos
    @GetMapping("/public")
    public ResponseEntity<Page<TrackDto>> getPublicTracks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TrackDto> tracks = trackService.getPublicTracks(pageable); // CORREGIDO
        return ResponseEntity.ok(tracks);
    }

    // Obtener tracks trending
    @GetMapping("/trending")
    public ResponseEntity<Page<TrackDto>> getTrendingTracks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TrackDto> tracks = trackService.getTrendingTracks(pageable); // CORREGIDO - usa Pageable
        return ResponseEntity.ok(tracks);
    }

    // Buscar tracks
    @GetMapping("/search")
    public ResponseEntity<Page<TrackDto>> searchTracks(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TrackDto> tracks = trackService.searchTracks(query, pageable); // CORREGIDO - ya devuelve Page<TrackDto>
        return ResponseEntity.ok(tracks);
    }

    // CORREGIDO - Obtener tracks de usuario
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<TrackDto>> getUserTracks(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TrackDto> tracks = trackService.getUserTracks(userId, pageable); // CORREGIDO - método correcto
        return ResponseEntity.ok(tracks);
    }

    // CORREGIDO - Actualizar track
    @PutMapping("/{id}")
    public ResponseEntity<TrackDto> updateTrack(
            @PathVariable Long id,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "genre", required = false) String genre,
            @RequestParam(value = "tags", required = false) String[] tags,
            @RequestParam(value = "isPublic", required = false) Boolean isPublic) {

        java.util.Set<String> tagSet = tags != null ?
                java.util.Set.of(tags) : null;

        TrackDto track = trackService.updateTrack(id, title, description, genre, tagSet, isPublic);
        return ResponseEntity.ok(track);
    }

    // CORREGIDO - Eliminar track
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrack(@PathVariable Long id) {
        trackService.deleteTrack(id); // CORREGIDO - sin parámetro User
        return ResponseEntity.noContent().build();
    }

    // Subir imagen de portada
    @PostMapping("/{id}/cover")
    public ResponseEntity<TrackDto> updateCoverImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {
        TrackDto track = trackService.updateCoverImage(id, file);
        return ResponseEntity.ok(track);
    }

    // CORREGIDO - Incrementar contador de reproducciones
    @PostMapping("/{id}/play")
    public ResponseEntity<Void> incrementPlayCount(@PathVariable Long id) {
        trackService.incrementPlaysCount(id); // CORREGIDO - nombre del método
        return ResponseEntity.ok().build();
    }

    // Obtener tracks por género
    @GetMapping("/genre/{genre}")
    public ResponseEntity<Page<TrackDto>> getTracksByGenre(
            @PathVariable String genre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TrackDto> tracks = trackService.getTracksByGenre(genre, pageable); // CORREGIDO - ya devuelve Page<TrackDto>
        return ResponseEntity.ok(tracks);
    }

    // Obtener tracks por tag
    @GetMapping("/tag/{tag}")
    public ResponseEntity<Page<TrackDto>> getTracksByTag(
            @PathVariable String tag,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TrackDto> tracks = trackService.getTracksByTag(tag, pageable); // CORREGIDO - ya devuelve Page<TrackDto>
        return ResponseEntity.ok(tracks);
    }

    // Subir waveform
    @PostMapping("/{id}/waveform")
    public ResponseEntity<TrackDto> updateWaveform(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {
        TrackDto track = trackService.updateWaveform(id, file);
        return ResponseEntity.ok(track);
    }
}