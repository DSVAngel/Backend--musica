package com.uv.backend.service;

import com.uv.backend.dto.UserDto;
import com.uv.backend.dto.request.UpdateProfileRequest;
import com.uv.backend.entity.User;
import com.uv.backend.exception.ResourceNotFoundException;
import com.uv.backend.repository.UserRepository;
import com.uv.backend.repository.FollowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private FollowService followService;

    // Obtener usuario actual autenticado
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    // Obtener usuario por ID
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    // Obtener usuario por username
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    // Convertir User a UserDto con información de seguimiento
    public UserDto convertToDto(User user, User currentUser) {
        UserDto userDto = new UserDto(user);

        if (currentUser != null && !currentUser.getId().equals(user.getId())) {
            boolean isFollowing = followRepository.existsByFollowerIdAndFollowingId(
                    currentUser.getId(), user.getId());
            userDto.setIsFollowing(isFollowing);
        }

        return userDto;
    }

    /**
     * Actualizar perfil de usuario - Solo metadatos
     */
    public UserDto updateProfile(Long userId, UpdateProfileRequest request) {
        User user = getUserById(userId);

        if (request.getDisplayName() != null) {
            user.setDisplayName(request.getDisplayName());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getLocation() != null) {
            user.setLocation(request.getLocation());
        }
        if (request.getWebsite() != null) {
            user.setWebsite(request.getWebsite());
        }

        User savedUser = userRepository.save(user);
        return new UserDto(savedUser);
    }

    /**
     * Actualizar avatar por URL
     */
    public UserDto updateAvatarUrl(Long userId, String avatarUrl) {
        User user = getUserById(userId);

        if (avatarUrl != null && !avatarUrl.trim().isEmpty()) {
            String validatedAvatarUrl = fileStorageService.saveImageFromUrl(avatarUrl, "avatars");
            user.setAvatarUrl(validatedAvatarUrl);
            user.setAvatarFileName(null); // No tenemos nombre de archivo para URLs externas
            user.setAvatarFileType("image/url");
            user.setAvatarFileSize(null);
        } else {
            user.setAvatarUrl(null);
            user.setAvatarFileName(null);
            user.setAvatarFileType(null);
            user.setAvatarFileSize(null);
        }

        User savedUser = userRepository.save(user);
        return new UserDto(savedUser);
    }

    /**
     * Actualizar imagen de portada por URL
     */
    public UserDto updateCoverImageUrl(Long userId, String coverImageUrl) {
        User user = getUserById(userId);

        if (coverImageUrl != null && !coverImageUrl.trim().isEmpty()) {
            String validatedCoverUrl = fileStorageService.saveImageFromUrl(coverImageUrl, "covers");
            user.setCoverImageUrl(validatedCoverUrl);
            user.setCoverImageFileName(null); // No tenemos nombre de archivo para URLs externas
            user.setCoverImageFileType("image/url");
            user.setCoverImageFileSize(null);
        } else {
            user.setCoverImageUrl(null);
            user.setCoverImageFileName(null);
            user.setCoverImageFileType(null);
            user.setCoverImageFileSize(null);
        }

        User savedUser = userRepository.save(user);
        return new UserDto(savedUser);
    }

    /**
     * Eliminar avatar
     */
    public UserDto removeAvatar(Long userId) {
        User user = getUserById(userId);

        // Solo eliminar si es un archivo local, no una URL externa
        if (user.hasAvatar() && !user.getAvatarUrl().startsWith("http")) {
            fileStorageService.deleteFile(user.getAvatarUrl());
        }

        user.setAvatarUrl(null);
        user.setAvatarFileName(null);
        user.setAvatarFileType(null);
        user.setAvatarFileSize(null);

        User savedUser = userRepository.save(user);
        return new UserDto(savedUser);
    }

    /**
     * Eliminar imagen de portada
     */
    public UserDto removeCoverImage(Long userId) {
        User user = getUserById(userId);

        // Solo eliminar si es un archivo local, no una URL externa
        if (user.hasCoverImage() && !user.getCoverImageUrl().startsWith("http")) {
            fileStorageService.deleteFile(user.getCoverImageUrl());
        }

        user.setCoverImageUrl(null);
        user.setCoverImageFileName(null);
        user.setCoverImageFileType(null);
        user.setCoverImageFileSize(null);

        User savedUser = userRepository.save(user);
        return new UserDto(savedUser);
    }

    /**
     * Buscar usuarios
     */
    public Page<UserDto> searchUsers(String query, Pageable pageable) {
        User currentUser = getCurrentUser();
        return userRepository.searchUsers(query, pageable)
                .map(user -> convertToDto(user, currentUser));
    }

    /**
     * Obtener usuarios sugeridos
     */
    public Page<UserDto> getSuggestedUsers(Long userId, int size) {
        User currentUser = getUserById(userId);
        Pageable pageable = PageRequest.of(0, size);

        return userRepository.findSuggestedUsers(currentUser.getId(), pageable)
                .stream()
                .map(user -> convertToDto(user, currentUser))
                .collect(java.util.stream.Collectors.toList())
                .stream()
                .collect(java.util.stream.Collectors.collectingAndThen(
                        java.util.stream.Collectors.toList(),
                        list -> new org.springframework.data.domain.PageImpl<>(list, pageable, list.size())
                ));
    }

    /**
     * Obtener usuarios con multimedia
     */
    public Page<UserDto> getUsersWithMedia(Pageable pageable) {
        User currentUser = getCurrentUser();
        return userRepository.findUsersWithCompleteProfile(pageable)
                .map(user -> convertToDto(user, currentUser));
    }

    /**
     * Obtener estadísticas de almacenamiento del usuario
     */
    public Long getUserStorageUsage(Long userId) {
        return userRepository.getTotalMediaStorageByUser(userId);
    }

    /**
     * Eliminar usuario
     */
    public void deleteUser(Long userId) {
        User user = getUserById(userId);

        // Eliminar archivos multimedia asociados (solo archivos locales)
        if (user.hasAvatar() && !user.getAvatarUrl().startsWith("http")) {
            fileStorageService.deleteFile(user.getAvatarUrl());
        }
        if (user.hasCoverImage() && !user.getCoverImageUrl().startsWith("http")) {
            fileStorageService.deleteFile(user.getCoverImageUrl());
        }

        userRepository.delete(user);
    }

    /**
     * Seguir usuario
     */
    public UserDto followUser(Long followerId, Long followingId) {
        followService.toggleFollow(followerId, followingId);
        User user = getUserById(followingId);
        User currentUser = getUserById(followerId);

        UserDto userDto = convertToDto(user, currentUser);
        userDto.setIsFollowing(true);
        return userDto;
    }

    /**
     * Dejar de seguir usuario
     */
    public UserDto unfollowUser(Long followerId, Long followingId) {
        followService.toggleFollow(followerId, followingId);
        User user = getUserById(followingId);
        User currentUser = getUserById(followerId);

        UserDto userDto = convertToDto(user, currentUser);
        userDto.setIsFollowing(false);
        return userDto;
    }

    // Verificar si el username está disponible
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    // Verificar si el email está disponible
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
}