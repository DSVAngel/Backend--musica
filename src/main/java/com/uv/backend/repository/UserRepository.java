// UserRepository.java - Actualizado con consultas multimedia completas
package com.uv.backend.repository;

import com.uv.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.username LIKE %:query% OR u.displayName LIKE %:query%")
    Page<User> searchUsers(@Param("query") String query, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.id NOT IN " +
            "(SELECT f.following.id FROM Follow f WHERE f.follower.id = :userId) " +
            "AND u.id != :userId ORDER BY u.createdAt DESC")
    List<User> findSuggestedUsers(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT COUNT(f) FROM Follow f WHERE f.following.id = :userId")
    Long countFollowers(@Param("userId") Long userId);

    @Query("SELECT COUNT(f) FROM Follow f WHERE f.follower.id = :userId")
    Long countFollowing(@Param("userId") Long userId);

    // Nuevas consultas para archivos multimedia de usuarios
    @Query("SELECT u FROM User u WHERE u.avatarUrl = :avatarUrl")
    List<User> findByAvatarUrl(@Param("avatarUrl") String avatarUrl);

    @Query("SELECT u FROM User u WHERE u.coverImageUrl = :coverImageUrl")
    List<User> findByCoverImageUrl(@Param("coverImageUrl") String coverImageUrl);

    // Buscar usuarios con avatar
    @Query("SELECT u FROM User u WHERE u.avatarUrl IS NOT NULL ORDER BY u.createdAt DESC")
    Page<User> findUsersWithAvatar(Pageable pageable);

    // Buscar usuarios con imagen de portada
    @Query("SELECT u FROM User u WHERE u.coverImageUrl IS NOT NULL ORDER BY u.createdAt DESC")
    Page<User> findUsersWithCoverImage(Pageable pageable);

    // Buscar usuarios con ambos: avatar e imagen de portada
    @Query("SELECT u FROM User u WHERE u.avatarUrl IS NOT NULL AND u.coverImageUrl IS NOT NULL ORDER BY u.createdAt DESC")
    Page<User> findUsersWithCompleteProfile(Pageable pageable);

    // Buscar usuarios verificados con multimedia
    @Query("SELECT u FROM User u WHERE u.verified = true AND (u.avatarUrl IS NOT NULL OR u.coverImageUrl IS NOT NULL) ORDER BY u.createdAt DESC")
    Page<User> findVerifiedUsersWithMedia(Pageable pageable);

    // Buscar usuarios activos recientes con multimedia
    @Query("SELECT u FROM User u WHERE u.updatedAt >= :since AND (u.avatarUrl IS NOT NULL OR u.coverImageUrl IS NOT NULL) ORDER BY u.updatedAt DESC")
    Page<User> findRecentActiveUsersWithMedia(@Param("since") LocalDateTime since, Pageable pageable);

    // Buscar usuarios más seguidos con multimedia
    @Query("SELECT u FROM User u WHERE (u.avatarUrl IS NOT NULL OR u.coverImageUrl IS NOT NULL) " +
            "ORDER BY SIZE(u.followers) DESC, u.createdAt DESC")
    Page<User> findPopularUsersWithMedia(Pageable pageable);

    // Buscar usuarios con más tracks y multimedia
    @Query("SELECT u FROM User u WHERE (u.avatarUrl IS NOT NULL OR u.coverImageUrl IS NOT NULL) " +
            "ORDER BY SIZE(u.tracks) DESC, u.createdAt DESC")
    Page<User> findActiveArtistsWithMedia(Pageable pageable);

    // Estadísticas de usuarios con multimedia
    @Query("SELECT COUNT(u) FROM User u WHERE u.avatarUrl IS NOT NULL")
    Long countUsersWithAvatar();

    @Query("SELECT COUNT(u) FROM User u WHERE u.coverImageUrl IS NOT NULL")
    Long countUsersWithCoverImage();

    @Query("SELECT COUNT(u) FROM User u WHERE u.avatarUrl IS NOT NULL AND u.coverImageUrl IS NOT NULL")
    Long countUsersWithCompleteProfile();

    @Query("SELECT COUNT(u) FROM User u WHERE u.avatarUrl IS NOT NULL OR u.coverImageUrl IS NOT NULL")
    Long countUsersWithAnyMedia();

    // Consultas por tipo de archivo
    @Query("SELECT u FROM User u WHERE u.avatarFileType = :fileType ORDER BY u.createdAt DESC")
    Page<User> findByAvatarFileType(@Param("fileType") String fileType, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.coverImageFileType = :fileType ORDER BY u.createdAt DESC")
    Page<User> findByCoverImageFileType(@Param("fileType") String fileType, Pageable pageable);

    // Consultas por tamaño de archivo
    @Query("SELECT u FROM User u WHERE u.avatarFileSize BETWEEN :minSize AND :maxSize ORDER BY u.avatarFileSize DESC")
    Page<User> findByAvatarFileSize(@Param("minSize") Long minSize, @Param("maxSize") Long maxSize, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.coverImageFileSize BETWEEN :minSize AND :maxSize ORDER BY u.coverImageFileSize DESC")
    Page<User> findByCoverImageFileSize(@Param("minSize") Long minSize, @Param("maxSize") Long maxSize, Pageable pageable);

    // Buscar usuarios por ubicación con multimedia
    @Query("SELECT u FROM User u WHERE u.location LIKE %:location% AND (u.avatarUrl IS NOT NULL OR u.coverImageUrl IS NOT NULL) ORDER BY u.createdAt DESC")
    Page<User> findByLocationWithMedia(@Param("location") String location, Pageable pageable);

    // Buscar usuarios nuevos con multimedia en un rango de fechas
    @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate AND (u.avatarUrl IS NOT NULL OR u.coverImageUrl IS NOT NULL) ORDER BY u.createdAt DESC")
    Page<User> findNewUsersWithMediaInDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    // Obtener estadísticas de almacenamiento por usuario
    @Query("SELECT SUM(COALESCE(u.avatarFileSize, 0) + COALESCE(u.coverImageFileSize, 0)) FROM User u WHERE u.id = :userId")
    Long getTotalMediaStorageByUser(@Param("userId") Long userId);

    // Obtener usuarios que más almacenamiento utilizan
    @Query("SELECT u FROM User u WHERE (u.avatarFileSize IS NOT NULL OR u.coverImageFileSize IS NOT NULL) " +
            "ORDER BY (COALESCE(u.avatarFileSize, 0) + COALESCE(u.coverImageFileSize, 0)) DESC")
    Page<User> findUsersByStorageUsage(Pageable pageable);

    // Buscar usuarios por nombre de archivo específico
    @Query("SELECT u FROM User u WHERE u.avatarFileName = :fileName OR u.coverImageFileName = :fileName")
    List<User> findByMediaFileName(@Param("fileName") String fileName);

    // Búsqueda avanzada combinando texto y multimedia
    @Query("SELECT u FROM User u WHERE (u.username LIKE %:query% OR u.displayName LIKE %:query% OR u.bio LIKE %:query%) " +
            "AND (u.avatarUrl IS NOT NULL OR u.coverImageUrl IS NOT NULL) ORDER BY u.createdAt DESC")
    Page<User> searchUsersWithMedia(@Param("query") String query, Pageable pageable);

    // Encontrar usuarios con problemas de archivos multimedia (URLs rotas)
    @Query("SELECT u FROM User u WHERE (u.avatarUrl IS NOT NULL AND u.avatarUrl = '') " +
            "OR (u.coverImageUrl IS NOT NULL AND u.coverImageUrl = '')")
    List<User> findUsersWithBrokenMediaUrls();

    // Usuarios más activos con multimedia (basado en número de posts, tracks, etc.)
    @Query("SELECT u FROM User u WHERE (u.avatarUrl IS NOT NULL OR u.coverImageUrl IS NOT NULL) " +
            "ORDER BY (SIZE(u.tracks) + SIZE(u.posts) + SIZE(u.playlists)) DESC, u.createdAt DESC")
    Page<User> findMostActiveUsersWithMedia(Pageable pageable);

    // Usuarios recomendados con multimedia (excluye seguidos y el usuario actual)
    @Query("SELECT u FROM User u WHERE u.id NOT IN " +
            "(SELECT f.following.id FROM Follow f WHERE f.follower.id = :userId) " +
            "AND u.id != :userId AND (u.avatarUrl IS NOT NULL OR u.coverImageUrl IS NOT NULL) " +
            "ORDER BY SIZE(u.followers) DESC, u.createdAt DESC")
    Page<User> findRecommendedUsersWithMedia(@Param("userId") Long userId, Pageable pageable);
}