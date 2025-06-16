package com.uv.backend.service;

import com.uv.backend.dto.TrackDto;
import com.uv.backend.entity.Track;
import com.uv.backend.entity.User;
import com.uv.backend.exception.ResourceNotFoundException;
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
import java.util.Set;

@Service
@Transactional
public class TrackService {

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private FileUploadService fileUploadService;

    // Crear nuevo track
    public TrackDto createTrack(String title, String description, MultipartFile audioFile,
                                Integer duration, String genre, Set<String> tags,
                                Boolean isPublic, MultipartFile coverImageFile) throws IOException {
        User currentUser = userService.getCurrentUser();

        // Subir archivo de audio
        String audioUrl = fileUploadService.uploadAudio(audioFile);

        Track track = new Track();
        track.setTitle(title);
        track.setDescription(description);
        track.setAudioUrl(audioUrl);
        track.setAudioFileName(audioFile.getOriginalFilename());
        track.setAudioFileType(audioFile.getContentType());
        track.setAudioFileSize(audioFile.getSize());
        track.setDuration(duration);
        track.setGenre(genre);
        track.setTags(tags);
        track.setIsPublic(isPublic != null ? isPublic : true);
        track.setUser(currentUser);

        // Subir imagen de portada si se proporciona
        if (coverImageFile != null && !coverImageFile.isEmpty()) {
            String coverImageUrl = fileUploadService.uploadImage(coverImageFile, "covers");
            track.setCoverImageUrl(coverImageUrl); // CORREGIDO
            track.setCoverImageFileName(coverImageFile.getOriginalFilename());
            track.setCoverImageFileType(coverImageFile.getContentType());
            track.setCoverImageFileSize(coverImageFile.getSize());
        }

        Track savedTrack = trackRepository.save(track);
        return new TrackDto(savedTrack);
    }

    // Obtener track por ID
    public TrackDto getTrackById(Long id) {
        Track track = trackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Track not found with id: " + id));
        return new TrackDto(track);
    }

    // Actualizar track
    public TrackDto updateTrack(Long id, String title, String description, String genre,
                                Set<String> tags, Boolean isPublic) {
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

        Track savedTrack = trackRepository.save(track);
        return new TrackDto(savedTrack);
    }

    // CORREGIDO - Actualizar imagen de portada
    public TrackDto updateCoverImage(Long trackId, MultipartFile file) throws IOException {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new ResourceNotFoundException("Track not found with id: " + trackId));

        // Eliminar imagen anterior si existe
        if (track.hasCoverImage()) { // CORREGIDO
            fileUploadService.deleteFile(track.getCoverImageUrl()); // CORREGIDO
        }

        // Subir nueva imagen
        String coverImageUrl = fileUploadService.uploadImage(file, "covers");

        track.setCoverImageUrl(coverImageUrl);
        track.setCoverImageFileName(file.getOriginalFilename());
        track.setCoverImageFileType(file.getContentType());
        track.setCoverImageFileSize(file.getSize());

        Track savedTrack = trackRepository.save(track);
        return new TrackDto(savedTrack);
    }

    // Actualizar waveform
    public TrackDto updateWaveform(Long trackId, MultipartFile file) throws IOException {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new ResourceNotFoundException("Track not found with id: " + trackId));

        // Eliminar waveform anterior si existe
        if (track.hasWaveform()) {
            fileUploadService.deleteFile(track.getWaveformUrl());
        }

        // Subir nuevo waveform
        String waveformUrl = fileUploadService.uploadImage(file, "waveforms");

        track.setWaveformUrl(waveformUrl);
        track.setWaveformFileName(file.getOriginalFilename());
        track.setWaveformFileType(file.getContentType());

        Track savedTrack = trackRepository.save(track);
        return new TrackDto(savedTrack);
    }

    // Eliminar track
    public void deleteTrack(Long id) {
        Track track = trackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Track not found with id: " + id));

        // Eliminar archivos asociados
        if (track.getAudioUrl() != null) {
            fileUploadService.deleteFile(track.getAudioUrl());
        }
        if (track.hasCoverImage()) { // CORREGIDO
            fileUploadService.deleteFile(track.getCoverImageUrl()); // CORREGIDO
        }
        if (track.hasWaveform()) {
            fileUploadService.deleteFile(track.getWaveformUrl());
        }

        trackRepository.delete(track);
    }

    // Incrementar contador de reproducciones
    public void incrementPlaysCount(Long trackId) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new ResourceNotFoundException("Track not found with id: " + trackId));

        track.incrementPlaysCount();
        trackRepository.save(track);
    }

    // Obtener tracks del usuario
    public Page<TrackDto> getUserTracks(Long userId, Pageable pageable) {
        return trackRepository.findByUserId(userId, pageable)
                .map(TrackDto::new);
    }

    // Obtener tracks públicos
    public Page<TrackDto> getPublicTracks(Pageable pageable) {
        return trackRepository.findByIsPublicTrueOrderByCreatedAtDesc(pageable)
                .map(TrackDto::new);
    }

    // Buscar tracks
    public Page<TrackDto> searchTracks(String query, Pageable pageable) {
        return trackRepository.searchTracks(query, pageable)
                .map(TrackDto::new);
    }

    // Obtener tracks trending
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

    // Obtener tracks por género
    public Page<TrackDto> getTracksByGenre(String genre, Pageable pageable) {
        return trackRepository.findByGenre(genre, pageable)
                .map(TrackDto::new);
    }

    // Obtener tracks por tag
    public Page<TrackDto> getTracksByTag(String tag, Pageable pageable) {
        return trackRepository.findByTag(tag, pageable)
                .map(TrackDto::new);
    }
}