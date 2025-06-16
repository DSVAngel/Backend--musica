// UserController.java - Corregido para trabajar con los métodos actualizados
package com.uv.backend.controller;

import com.uv.backend.dto.UserDto;
import com.uv.backend.dto.request.UpdateProfileRequest;
import com.uv.backend.entity.User;
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
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    // Obtener perfil del usuario actual
    @GetMapping("/profile")
    public ResponseEntity<UserDto> getCurrentUserProfile() {
        User currentUser = userService.getCurrentUser();
        UserDto userDto = new UserDto(currentUser);
        return ResponseEntity.ok(userDto);
    }

    // Obtener usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        UserDto userDto = UserDto.fromUserPublic(user); // CORREGIDO
        return ResponseEntity.ok(userDto);
    }

    // Obtener usuario por username
    @GetMapping("/username/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        UserDto userDto = UserDto.fromUserPublic(user); // CORREGIDO
        return ResponseEntity.ok(userDto);
    }

    // CORREGIDO - Actualizar perfil
    @PutMapping("/{id}/profile")
    public ResponseEntity<UserDto> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProfileRequest request) { // CORREGIDO
        UserDto updatedUser = userService.updateProfile(id, request); // CORREGIDO
        return ResponseEntity.ok(updatedUser);
    }

    // CORREGIDO - Subir avatar
    @PostMapping("/{id}/avatar")
    public ResponseEntity<UserDto> uploadAvatar(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {
        UserDto updatedUser = userService.uploadAvatar(id, file); // CORREGIDO
        return ResponseEntity.ok(updatedUser);
    }

    // CORREGIDO - Subir imagen de portada
    @PostMapping("/{id}/cover")
    public ResponseEntity<UserDto> uploadCoverImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {
        UserDto updatedUser = userService.uploadCoverImage(id, file); // CORREGIDO
        return ResponseEntity.ok(updatedUser);
    }

    // CORREGIDO - Buscar usuarios
    @GetMapping("/search")
    public ResponseEntity<Page<UserDto>> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserDto> users = userService.searchUsers(query, pageable); // CORREGIDO - ya devuelve UserDto
        return ResponseEntity.ok(users);
    }

    // CORREGIDO - Obtener usuarios sugeridos
    @GetMapping("/{userId}/suggested")
    public ResponseEntity<Page<UserDto>> getSuggestedUsers(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int size) { // CORREGIDO
        Page<UserDto> suggestedUsers = userService.getSuggestedUsers(userId, size); // CORREGIDO
        return ResponseEntity.ok(suggestedUsers);
    }

    // Obtener usuarios con multimedia
    @GetMapping("/with-media")
    public ResponseEntity<Page<UserDto>> getUsersWithMedia(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserDto> users = userService.getUsersWithMedia(pageable);
        return ResponseEntity.ok(users);
    }

    // Eliminar avatar
    @DeleteMapping("/avatar")
    public ResponseEntity<UserDto> removeAvatar() {
        UserDto updatedUser = userService.removeAvatar();
        return ResponseEntity.ok(updatedUser);
    }

    // Eliminar imagen de portada
    @DeleteMapping("/cover")
    public ResponseEntity<UserDto> removeCoverImage() {
        UserDto updatedUser = userService.removeCoverImage();
        return ResponseEntity.ok(updatedUser);
    }

    // Obtener estadísticas de almacenamiento
    @GetMapping("/storage")
    public ResponseEntity<Long> getStorageUsage() {
        Long storageUsage = userService.getCurrentUserStorageUsage();
        return ResponseEntity.ok(storageUsage);
    }

    // Verificar disponibilidad de username
    @GetMapping("/check-username")
    public ResponseEntity<Boolean> checkUsername(@RequestParam String username) {
        boolean available = userService.isUsernameAvailable(username);
        return ResponseEntity.ok(available);
    }

    // Verificar disponibilidad de email
    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        boolean available = userService.isEmailAvailable(email);
        return ResponseEntity.ok(available);
    }

    // CORREGIDO - Eliminar usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id); // CORREGIDO - método agregado
        return ResponseEntity.noContent().build();
    }

    // Seguir usuario
    @PostMapping("/{id}/follow")
    public ResponseEntity<UserDto> followUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        UserDto userDto = UserDto.fromUserPublic(user); // CORREGIDO
        userDto.setIsFollowing(true);
        return ResponseEntity.ok(userDto);
    }

    // Dejar de seguir usuario
    @DeleteMapping("/{id}/follow")
    public ResponseEntity<UserDto> unfollowUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        UserDto userDto = UserDto.fromUserPublic(user); // CORREGIDO
        userDto.setIsFollowing(false);
        return ResponseEntity.ok(userDto);
    }
}