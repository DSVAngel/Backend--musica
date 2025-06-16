package com.uv.backend.repository;
import com.uv.backend.entity.Follow;
import com.uv.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Optional<Follow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);
    Boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);

    @Query("SELECT f.following FROM Follow f WHERE f.follower.id = :userId")
    Page<User> findFollowing(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT f.follower FROM Follow f WHERE f.following.id = :userId")
    Page<User> findFollowers(@Param("userId") Long userId, Pageable pageable);

    void deleteByFollowerIdAndFollowingId(Long followerId, Long followingId);
}