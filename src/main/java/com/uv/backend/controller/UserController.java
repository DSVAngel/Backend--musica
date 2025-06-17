package com.uv.backend.controller;

import com.uv.backend.dto.UserDto;
import com.uv.backend.dto.request.UpdateProfileRequest;
import com.uv.backend.dto.response.ApiResponse;
import com.uv.backend.entity.User;
import com.uv.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<?> getCurrentUserProfile(Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            UserDto userDto = new UserDto(currentUser);
            return ResponseEntity.ok(ApiResponse.success(userDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error getting current user profile"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            UserDto userDto = UserDto.fromUserPublic(user);
            return ResponseEntity.ok(ApiResponse.success(userDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("User not found"));
        }
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        try {
            User user = userService.getUserByUsername(username);
            UserDto userDto = UserDto.fromUserPublic(user);
            return ResponseEntity.ok(ApiResponse.success(userDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("User not found"));
        }
    }

    /**
     * Actualizar perfil de usuario - Solo metadatos
     */
    @PutMapping("/{id}/profile")
    public ResponseEntity<?> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();

            // Verificar que el usuario solo pueda actualizar su propio perfil
            if (!currentUser.getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("You can only update your own profile"));
            }

            UserDto updatedUser = userService.updateProfile(id, request);
            return ResponseEntity.ok(ApiResponse.success(updatedUser, "Profile updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error updating profile: " + e.getMessage()));
        }
    }

    /**
     * Actualizar avatar por URL
     */
    @PutMapping("/{id}/avatar")
    public ResponseEntity<?> updateAvatar(
            @PathVariable Long id,
            @RequestParam("avatarUrl") String avatarUrl,
            Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();

            if (!currentUser.getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("You can only update your own avatar"));
            }

            UserDto updatedUser = userService.updateAvatarUrl(id, avatarUrl);
            return ResponseEntity.ok(ApiResponse.success(updatedUser, "Avatar updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error updating avatar: " + e.getMessage()));
        }
    }

    /**
     * Actualizar imagen de portada por URL
     */
    @PutMapping("/{id}/cover")
    public ResponseEntity<?> updateCoverImage(
            @PathVariable Long id,
            @RequestParam("coverImageUrl") String coverImageUrl,
            Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();

            if (!currentUser.getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("You can only update your own cover image"));
            }

            UserDto updatedUser = userService.updateCoverImageUrl(id, coverImageUrl);
            return ResponseEntity.ok(ApiResponse.success(updatedUser, "Cover image updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error updating cover image: " + e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<UserDto> users = userService.searchUsers(query, pageable);
            return ResponseEntity.ok(ApiResponse.success(users));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error searching users: " + e.getMessage()));
        }
    }

    @GetMapping("/{userId}/suggested")
    public ResponseEntity<?> getSuggestedUsers(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<UserDto> suggestedUsers = userService.getSuggestedUsers(userId, size);
            return ResponseEntity.ok(ApiResponse.success(suggestedUsers));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error getting suggested users: " + e.getMessage()));
        }
    }

    @GetMapping("/with-media")
    public ResponseEntity<?> getUsersWithMedia(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<UserDto> users = userService.getUsersWithMedia(pageable);
            return ResponseEntity.ok(ApiResponse.success(users));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error getting users with media: " + e.getMessage()));
        }
    }

    /**
     * Eliminar avatar
     */
    @DeleteMapping("/{id}/avatar")
    public ResponseEntity<?> removeAvatar(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();

            if (!currentUser.getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("You can only remove your own avatar"));
            }

            UserDto updatedUser = userService.removeAvatar(id);
            return ResponseEntity.ok(ApiResponse.success(updatedUser, "Avatar removed successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error removing avatar: " + e.getMessage()));
        }
    }

    /**
     * Eliminar imagen de portada
     */
    @DeleteMapping("/{id}/cover")
    public ResponseEntity<?> removeCoverImage(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();

            if (!currentUser.getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("You can only remove your own cover image"));
            }

            UserDto updatedUser = userService.removeCoverImage(id);
            return ResponseEntity.ok(ApiResponse.success(updatedUser, "Cover image removed successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error removing cover image: " + e.getMessage()));
        }
    }

    @GetMapping("/storage")
    public ResponseEntity<?> getStorageUsage(Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            Long storageUsage = userService.getUserStorageUsage(currentUser.getId());
            return ResponseEntity.ok(ApiResponse.success(storageUsage));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error getting storage usage: " + e.getMessage()));
        }
    }

    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam String username) {
        try {
            boolean available = userService.isUsernameAvailable(username);
            return ResponseEntity.ok(ApiResponse.success(available,
                    available ? "Username is available" : "Username is already taken"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error checking username availability"));
        }
    }

    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        try {
            boolean available = userService.isEmailAvailable(email);
            return ResponseEntity.ok(ApiResponse.success(available,
                    available ? "Email is available" : "Email is already in use"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error checking email availability"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();

            if (!currentUser.getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.error("You can only delete your own account"));
            }

            userService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error deleting user: " + e.getMessage()));
        }
    }

    /**
     * Seguir usuario
     */
    @PostMapping("/{id}/follow")
    public ResponseEntity<?> followUser(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();

            if (currentUser.getId().equals(id)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("You cannot follow yourself"));
            }

            UserDto userDto = userService.followUser(currentUser.getId(), id);
            return ResponseEntity.ok(ApiResponse.success(userDto, "User followed successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error following user: " + e.getMessage()));
        }
    }

    /**
     * Dejar de seguir usuario
     */
    @DeleteMapping("/{id}/follow")
    public ResponseEntity<?> unfollowUser(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();

            if (currentUser.getId().equals(id)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("You cannot unfollow yourself"));
            }

            UserDto userDto = userService.unfollowUser(currentUser.getId(), id);
            return ResponseEntity.ok(ApiResponse.success(userDto, "User unfollowed successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error unfollowing user: " + e.getMessage()));
        }
    }
}