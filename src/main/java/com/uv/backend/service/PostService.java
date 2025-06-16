package com.uv.backend.service;

import com.uv.backend.dto.request.PostRequest;
import com.uv.backend.entity.Post;
import com.uv.backend.entity.PostType;
import com.uv.backend.entity.Track;
import com.uv.backend.entity.User;
import com.uv.backend.repository.PostRepository;
import com.uv.backend.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TrackRepository trackRepository;


    public Post createPost(PostRequest postRequest, User user) {
        Post post = new Post(postRequest.getType(), user);
        post.setContent(postRequest.getContent());

        if (postRequest.getTrackId() != null) {
            Track track = trackRepository.findById(postRequest.getTrackId())
                    .orElseThrow(() -> new RuntimeException("Track not found"));
            post.setTrack(track);
        }

        if (postRequest.getOriginalPostId() != null) {
            Post originalPost = postRepository.findById(postRequest.getOriginalPostId())
                    .orElseThrow(() -> new RuntimeException("Original post not found"));
            post.setOriginalPost(originalPost);
        }
        return postRepository.save(post);
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    public Page<Post> getFeedPosts(Long userId, Pageable pageable) {
        return postRepository.findFeedPosts(userId, pageable);
    }

    public Page<Post> getUserPosts(Long userId, Pageable pageable) {
        return postRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public Post repost(Long postId, User user) {
        Post originalPost = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Post repost = new Post(PostType.REPOST, user);
        repost.setOriginalPost(originalPost);
        return postRepository.save(repost);
    }

    public void deletePost(Long id, User user) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only delete your own posts");
        }

        postRepository.delete(post);
    }

    public boolean removeRepost(Long trackId, User user) {
        // Buscar el repost del usuario para este track
        List<Post> reposts = postRepository.findByUserIdAndTrackIdAndType(
                user.getId(), trackId, PostType.REPOST);

        if (!reposts.isEmpty()) {
            Post repost = reposts.get(0);
            postRepository.delete(repost);
            return true;
        }

        return false;
    }

    public boolean removePostRepost(Long originalPostId, User user) {
        // Buscar el repost del usuario para este post
        List<Post> reposts = postRepository.findByUserIdAndOriginalPostIdAndType(
                user.getId(), originalPostId, PostType.REPOST);

        if (!reposts.isEmpty()) {
            Post repost = reposts.get(0);
            postRepository.delete(repost);
            return true;
        }

        return false;
    }
}