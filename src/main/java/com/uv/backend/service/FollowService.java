package com.uv.backend.service;
import com.uv.backend.entity.Follow;
import com.uv.backend.entity.User;
import com.uv.backend.repository.FollowRepository;
import com.uv.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FollowService {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;


    public boolean toggleFollow(Long followerId, Long followingId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Follower not found"));
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("User to follow not found"));

        Follow existingFollow = followRepository.findByFollowerIdAndFollowingId(followerId, followingId)
                .orElse(null);

        if (existingFollow != null) {
            // Unfollow
            followRepository.delete(existingFollow);
            return false;
        } else {
            // Follow
            Follow follow = new Follow(follower, following);
            followRepository.save(follow);

            return true;
        }
    }

    public boolean isFollowing(Long followerId, Long followingId) {
        return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    public Page<User> getFollowers(Long userId, Pageable pageable) {
        return followRepository.findFollowers(userId, pageable);
    }

    public Page<User> getFollowing(Long userId, Pageable pageable) {
        return followRepository.findFollowing(userId, pageable);
    }
}