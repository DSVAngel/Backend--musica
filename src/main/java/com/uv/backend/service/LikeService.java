package com.uv.backend.service;
import com.uv.backend.entity.*;
import com.uv.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PlaylistRepository playlistRepository;


    public boolean toggleCommentLike(Long commentId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        Like existingLike = likeRepository.findByUserIdAndCommentId(userId, commentId).orElse(null);

        if (existingLike != null) {
            // Unlike
            likeRepository.delete(existingLike);
            return false;
        } else {
            // Like
            Like like = new Like(user, comment);
            likeRepository.save(like);

            return true;
        }
    }

    public boolean togglePlaylistLike(Long playlistId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        Like existingLike = likeRepository.findByUserIdAndPlaylistId(userId, playlistId).orElse(null);

        if (existingLike != null) {
            likeRepository.delete(existingLike);
            return false;
        } else {
            Like like = new Like(user, playlist);
            likeRepository.save(like);
            return true;
        }
    }

    public boolean isCommentLikedByUser(Long commentId, Long userId) {
        return likeRepository.existsByUserIdAndCommentId(userId, commentId);
    }

    public boolean isPlaylistLikedByUser(Long playlistId, Long userId) {
        return likeRepository.existsByUserIdAndPlaylistId(userId, playlistId);
    }

    public boolean isTrackLikedByUser(Long id, Long id2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isTrackLikedByUser'");
    }

    public boolean isPostLikedByUser(Long id, Long id2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isPostLikedByUser'");
    }

    public boolean togglePostLike(Long id, Long id2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'togglePostLike'");
    }

    public boolean toggleTrackLike(Long id, Long id2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'toggleTrackLike'");
    }
}