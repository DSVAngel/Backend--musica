package com.uv.backend.controller;

import com.uv.backend.dto.PlaylistDto;
import com.uv.backend.dto.TrackDto;
import com.uv.backend.dto.request.PlaylistRequest;
import com.uv.backend.entity.User;
import com.uv.backend.service.PlaylistService;
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
@RequestMapping("/api/playlists")
@CrossOrigin(origins = "*")
public class PlaylistController {

    @Autowired
    private PlaylistService playlistService;

    @Autowired
    private UserService userService;

    // CORREGIDO - Crear playlist
    @PostMapping
    public ResponseEntity<PlaylistDto> createPlaylist(
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "isPublic", defaultValue = "true") Boolean isPublic) {

        PlaylistDto playlist = playlistService.createPlaylist(title, description, isPublic);
        return ResponseEntity.ok(playlist);
    }

    // Obtener playlist por ID
    @GetMapping("/{id}")
    public ResponseEntity<PlaylistDto> getPlaylistById(@PathVariable Long id) {
        PlaylistDto playlist = playlistService.getPlaylistById(id); // CORREGIDO - ya devuelve PlaylistDto
        return ResponseEntity.ok(playlist);
    }

    // CORREGIDO - Obtener todas las playlists públicas
    @GetMapping("/public")
    public ResponseEntity<Page<PlaylistDto>> getPublicPlaylists(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PlaylistDto> playlists = playlistService.getPublicPlaylists(pageable); // CORREGIDO
        return ResponseEntity.ok(playlists);
    }

    // Buscar playlists
    @GetMapping("/search")
    public ResponseEntity<Page<PlaylistDto>> searchPlaylists(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PlaylistDto> playlists = playlistService.searchPlaylists(query, pageable);
        return ResponseEntity.ok(playlists);
    }

    // CORREGIDO - Obtener playlists de usuario
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PlaylistDto>> getUserPlaylists(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PlaylistDto> playlists = playlistService.getUserPlaylists(userId, pageable); // CORREGIDO - método correcto
        return ResponseEntity.ok(playlists);
    }

    // CORREGIDO - Actualizar playlist
    @PutMapping("/{id}")
    public ResponseEntity<PlaylistDto> updatePlaylist(
            @PathVariable Long id,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "isPublic", required = false) Boolean isPublic) {

        PlaylistDto playlist = playlistService.updatePlaylist(id, title, description, isPublic);
        return ResponseEntity.ok(playlist);
    }

    // CORREGIDO - Eliminar playlist
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable Long id) {
        playlistService.deletePlaylist(id); // CORREGIDO - sin parámetro User
        return ResponseEntity.noContent().build();
    }

    // CORREGIDO - Agregar track a playlist
    @PostMapping("/{playlistId}/tracks/{trackId}")
    public ResponseEntity<PlaylistDto> addTrackToPlaylist(
            @PathVariable Long playlistId,
            @PathVariable Long trackId) {

        PlaylistDto playlist = playlistService.addTrackToPlaylist(playlistId, trackId); // CORREGIDO - sin parámetro User
        return ResponseEntity.ok(playlist);
    }

    // CORREGIDO - Remover track de playlist
    @DeleteMapping("/{playlistId}/tracks/{trackId}")
    public ResponseEntity<PlaylistDto> removeTrackFromPlaylist(
            @PathVariable Long playlistId,
            @PathVariable Long trackId) {

        PlaylistDto playlist = playlistService.removeTrackFromPlaylist(playlistId, trackId); // CORREGIDO - sin parámetro User
        return ResponseEntity.ok(playlist);
    }

    // Subir imagen de portada
    @PostMapping("/{id}/cover")
    public ResponseEntity<PlaylistDto> updateCoverImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {

        PlaylistDto playlist = playlistService.updateCoverImage(id, file); // CORREGIDO - método correcto
        return ResponseEntity.ok(playlist);
    }

    // MÉTODO AGREGADO - Obtener tracks de una playlist
    @GetMapping("/{id}/tracks")
    public ResponseEntity<Page<TrackDto>> getPlaylistTracks(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TrackDto> tracks = playlistService.getPlaylistTracks(id, pageable);
        return ResponseEntity.ok(tracks);
    }
}