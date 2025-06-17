package com.uv.backend.service;

import com.uv.backend.dto.TrackDto;
import com.uv.backend.entity.Track;
import com.uv.backend.entity.User;
import com.uv.backend.exception.ResourceNotFoundException;
import com.uv.backend.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

@Service
@Transactional
public class TrackService {

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Crear nuevo track - Audio como archivo, imagen por URL
     */
    public TrackDto createTrack(String title, String description, MultipartFile audioFile,
                                Integer duration, String genre, Set<String> tags,
                                Boolean isPublic, String coverImageUrl, User currentUser) throws IOException {

        // Subir archivo de audio
        String audioUrl = fileStorageService.uploadAudioFile(audioFile, currentUser);

        Track track = new Track();
        track.setTitle(title);
        track.setDescription(description);
        track.setAudioUrl(audioUrl);
        track.setAudioFileName(audioFile.getOriginalFilename());
        track.setAudioFileType(audioFile.getContentType());
        track.setAudioFileSize(audioFile.getSize());
        track.setDuration(duration);
        track.setGenre(genre);
        track.setTags(tags != null ? tags : Set.of());
        track.setIsPublic(isPublic != null ? isPublic : true);
        track.setUser(currentUser);

        // Configurar imagen de portada desde URL si se proporciona
        if (coverImageUrl != null && !coverImageUrl.trim().isEmpty()) {
            String validatedImageUrl = fileStorageService.saveImageFromUrl(coverImageUrl, "covers");
            track.setCoverImageUrl(validatedImageUrl);
        }

        Track savedTrack = trackRepository.save(track);
        return new TrackDto(savedTrack);
    }

    /**
     * Obtener track por ID
     */
    public TrackDto getTrackById(Long id) {
        Track track = trackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Track not found with id: " + id));
        return new TrackDto(track);
    }

    /**
     * Actualizar track - Incluye actualización de imagen por URL
     */
    public TrackDto updateTrack(Long id, String title, String description, String genre,
                                Set<String> tags, Boolean isPublic, String coverImageUrl) {
        Track track = trackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Track not found with id: " + id));

        if (title != null) {
            track.setTitle(title);
        }
        if (description != null) {
            track.setDescription(description);
        }
        if (genre != null) {
            track.setGenre(genre);
        }
        if (tags != null) {
            track.setTags(tags);
        }
        if (isPublic != null) {
            track.setIsPublic(isPublic);
        }
        if (coverImageUrl != null) {
            if (coverImageUrl.trim().isEmpty()) {
                // Eliminar imagen de portada
                track.setCoverImageUrl(null);
            } else {
                // Actualizar imagen de portada
                String validatedImageUrl = fileStorageService.saveImageFromUrl(coverImageUrl, "covers");
                track.setCoverImageUrl(validatedImageUrl);
            }
        }

        Track savedTrack = trackRepository.save(track);
        return new TrackDto(savedTrack);
    }

    /**
     * Actualizar solo la imagen de portada por URL
     */
    public TrackDto updateCoverImageUrl(Long trackId, String coverImageUrl) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new ResourceNotFoundException("Track not found with id: " + trackId));

        if (coverImageUrl != null && !coverImageUrl.trim().isEmpty()) {
            String validatedImageUrl = fileStorageService.saveImageFromUrl(coverImageUrl, "covers");
            track.setCoverImageUrl(validatedImageUrl);
        } else {
            track.setCoverImageUrl(null);
        }

        Track savedTrack = trackRepository.save(track);
        return new TrackDto(savedTrack);
    }

    /**
     * Actualizar waveform como archivo
     */
    public TrackDto updateWaveform(Long trackId, MultipartFile file) throws IOException {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new ResourceNotFoundException("Track not found with id: " + trackId));

        // Eliminar waveform anterior si existe
        if (track.hasWaveform()) {
            fileStorageService.deleteFile(track.getWaveformUrl());
        }

        // Subir nuevo waveform como imagen
        String waveformUrl = fileStorageService.uploadImageFile(file, "waveforms");

        track.setWaveformUrl(waveformUrl);
        track.setWaveformFileName(file.getOriginalFilename());
        track.setWaveformFileType(file.getContentType());

        Track savedTrack = trackRepository.save(track);
        return new TrackDto(savedTrack);
    }

    /**
     * Eliminar track y todos sus archivos asociados
     */
    public void deleteTrack(Long id) {
        Track track = trackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Track not found with id: " + id));

        // Eliminar archivos asociados
        if (track.getAudioUrl() != null) {
            fileStorageService.deleteFile(track.getAudioUrl());
        }
        if (track.hasWaveform()) {
            fileStorageService.deleteFile(track.getWaveformUrl());
        }
        // No eliminar la imagen de portada porque es una URL externa

        trackRepository.delete(track);
    }

    /**
     * Incrementar contador de reproducciones
     */
    public void incrementPlaysCount(Long trackId) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new ResourceNotFoundException("Track not found with id: " + trackId));

        track.incrementPlaysCount();
        trackRepository.save(track);
    }

    /**
     * Obtener tracks del usuario
     */
    public Page<TrackDto> getUserTracks(Long userId, Pageable pageable) {
        return trackRepository.findByUserId(userId, pageable)
                .map(TrackDto::new);
    }

    /**
     * Obtener tracks públicos
     */
    public Page<TrackDto> getPublicTracks(Pageable pageable) {
        return trackRepository.findByIsPublicTrueOrderByCreatedAtDesc(pageable)
                .map(TrackDto::new);
    }

    /**
     * Buscar tracks
     */
    public Page<TrackDto> searchTracks(String query, Pageable pageable) {
        return trackRepository.searchTracks(query, pageable)
                .map(TrackDto::new);
    }

    /**
     * Obtener tracks trending
     */
    public Page<TrackDto> getTrendingTracks(Pageable pageable) {
        return trackRepository.findTrendingTracks(pageable)
                .stream()
                .map(TrackDto::new)
                .collect(java.util.stream.Collectors.toList())
                .stream()
                .collect(java.util.stream.Collectors.collectingAndThen(
                        java.util.stream.Collectors.toList(),
                        list -> new org.springframework.data.domain.PageImpl<>(list, pageable, list.size())
                ));
    }

    /**
     * Obtener tracks por género
     */
    public Page<TrackDto> getTracksByGenre(String genre, Pageable pageable) {
        return trackRepository.findByGenre(genre, pageable)
                .map(TrackDto::new);
    }

    /**
     * Obtener tracks por tag
     */
    public Page<TrackDto> getTracksByTag(String tag, Pageable pageable) {
        return trackRepository.findByTag(tag, pageable)
                .map(TrackDto::new);
    }
}