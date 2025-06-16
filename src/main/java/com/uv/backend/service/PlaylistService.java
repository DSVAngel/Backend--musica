// PlaylistService.java - Corregido para métodos multimedia
package com.uv.backend.service;

import com.uv.backend.dto.PlaylistDto;
import com.uv.backend.dto.TrackDto;
import com.uv.backend.entity.Playlist;
import com.uv.backend.entity.Track;
import com.uv.backend.entity.User;
import com.uv.backend.exception.ResourceNotFoundException;
import com.uv.backend.repository.PlaylistRepository;
import com.uv.backend.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Transactional
public class PlaylistService {

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private FileUploadService fileUploadService;

    // Crear nueva playlist
    public PlaylistDto createPlaylist(String title, String description, Boolean isPublic) {
        User currentUser = userService.getCurrentUser();

        Playlist playlist = new Playlist();
        playlist.setTitle(title);
        playlist.setDescription(description);
        playlist.setIsPublic(isPublic != null ? isPublic : true);
        playlist.setUser(currentUser);

        Playlist savedPlaylist = playlistRepository.save(playlist);
        return new PlaylistDto(savedPlaylist);
    }

    // Obtener playlist por ID
    public PlaylistDto getPlaylistById(Long id) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found with id: " + id));
        return new PlaylistDto(playlist);
    }

    // Actualizar playlist
    public PlaylistDto updatePlaylist(Long id, String title, String description, Boolean isPublic) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found with id: " + id));

        if (title != null) {
            playlist.setTitle(title);
        }
        if (description != null) {
            playlist.setDescription(description);
        }
        if (isPublic != null) {
            playlist.setIsPublic(isPublic);
        }

        Playlist savedPlaylist = playlistRepository.save(playlist);
        return new PlaylistDto(savedPlaylist);
    }

    // CORREGIDO - Actualizar imagen de portada de playlist
    public PlaylistDto updateCoverImage(Long playlistId, MultipartFile file) throws IOException {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found with id: " + playlistId));

        // Eliminar imagen anterior si existe
        if (playlist.hasCoverImage()) { // CORREGIDO
            fileUploadService.deleteFile(playlist.getCoverImageUrl()); // CORREGIDO
        }

        // Subir nueva imagen
        String coverImageUrl = fileUploadService.uploadImage(file, "covers");

        playlist.setCoverImageUrl(coverImageUrl); // CORREGIDO
        playlist.setCoverImageFileName(file.getOriginalFilename());
        playlist.setCoverImageFileType(file.getContentType());
        playlist.setCoverImageFileSize(file.getSize());

        Playlist savedPlaylist = playlistRepository.save(playlist);
        return new PlaylistDto(savedPlaylist);
    }

    // Eliminar playlist
    public void deletePlaylist(Long id) {
        Playlist playlist = playlistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found with id: " + id));

        // CORREGIDO - Eliminar imagen de portada si existe
        if (playlist.hasCoverImage()) { // CORREGIDO
            fileUploadService.deleteFile(playlist.getCoverImageUrl()); // CORREGIDO
        }

        playlistRepository.delete(playlist);
    }

    // Agregar track a playlist
    public PlaylistDto addTrackToPlaylist(Long playlistId, Long trackId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found with id: " + playlistId));

        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new ResourceNotFoundException("Track not found with id: " + trackId));

        playlist.addTrack(track);
        Playlist savedPlaylist = playlistRepository.save(playlist);
        return new PlaylistDto(savedPlaylist);
    }

    // Remover track de playlist
    public PlaylistDto removeTrackFromPlaylist(Long playlistId, Long trackId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found with id: " + playlistId));

        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new ResourceNotFoundException("Track not found with id: " + trackId));

        playlist.removeTrack(track);
        Playlist savedPlaylist = playlistRepository.save(playlist);
        return new PlaylistDto(savedPlaylist);
    }

    // Obtener playlists del usuario
    public Page<PlaylistDto> getUserPlaylists(Long userId, Pageable pageable) {
        return playlistRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(PlaylistDto::new);
    }

    // Obtener playlists públicas
    public Page<PlaylistDto> getPublicPlaylists(Pageable pageable) {
        return playlistRepository.findByIsPublicTrueOrderByCreatedAtDesc(pageable)
                .map(PlaylistDto::new);
    }

    // Buscar playlists
    public Page<PlaylistDto> searchPlaylists(String query, Pageable pageable) {
        return playlistRepository.searchPlaylists(query, pageable)
                .map(PlaylistDto::new);
    }

    public Page<TrackDto> getPlaylistTracks(Long playlistId, Pageable pageable) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found with id: " + playlistId));

        return playlist.getTracks()
                .stream()
                .map(TrackDto::new)
                .collect(java.util.stream.Collectors.toList())
                .stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .collect(java.util.stream.Collectors.collectingAndThen(
                        java.util.stream.Collectors.toList(),
                        list -> new org.springframework.data.domain.PageImpl<>(list, pageable, playlist.getTracks().size())
                ));
    }
}
