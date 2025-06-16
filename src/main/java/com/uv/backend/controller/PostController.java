package com.uv.backend.controller;
import com.uv.backend.dto.PostDto;
import com.uv.backend.dto.request.PostRequest;
import com.uv.backend.dto.response.ApiResponse;
import com.uv.backend.dto.response.PaginatedResponse;
import com.uv.backend.entity.Post;
import com.uv.backend.entity.User;
import com.uv.backend.service.LikeService;
import com.uv.backend.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private LikeService likeService;

    @PostMapping
    public ResponseEntity<?> createPost(
            @Valid @RequestBody PostRequest postRequest,
            Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            Post post = postService.createPost(postRequest, currentUser);
            PostDto postDto = new PostDto(post);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(postDto, "Post created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error creating post: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPost(@PathVariable Long id, Authentication authentication) {
        try {
            Post post = postService.getPostById(id);
            if (post == null) {
                return ResponseEntity.notFound().build();
            }

            PostDto postDto = new PostDto(post);
            
            if (authentication != null) {
                User currentUser = (User) authentication.getPrincipal();
                boolean isLiked = likeService.isPostLikedByUser(id, currentUser.getId());
                postDto.setIsLiked(isLiked);
            }

            return ResponseEntity.ok(ApiResponse.success(postDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error getting post: " + e.getMessage()));
        }
    }

    @GetMapping("/feed")
    public ResponseEntity<?> getFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            Pageable pageable = PageRequest.of(page, size);
            Page<Post> postPage = postService.getFeedPosts(currentUser.getId(), pageable);
            
            List<PostDto> postDtos = postPage.getContent().stream()
                    .map(post -> {
                        PostDto dto = new PostDto(post);
                        boolean isLiked = likeService.isPostLikedByUser(post.getId(), currentUser.getId());
                        dto.setIsLiked(isLiked);
                        return dto;
                    })
                    .collect(Collectors.toList());

            PaginatedResponse<PostDto> response = new PaginatedResponse<>(
                    postDtos, postPage.getTotalElements(), page, size,
                    postPage.hasNext(), postPage.hasPrevious());

            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error getting feed: " + e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserPosts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Post> postPage = postService.getUserPosts(userId, pageable);
            
            User currentUser = authentication != null ? (User) authentication.getPrincipal() : null;
            List<PostDto> postDtos = postPage.getContent().stream()
                    .map(post -> {
                        PostDto dto = new PostDto(post);
                        if (currentUser != null) {
                            boolean isLiked = likeService.isPostLikedByUser(post.getId(), currentUser.getId());
                            dto.setIsLiked(isLiked);
                        }
                        return dto;
                    })
                    .collect(Collectors.toList());

            PaginatedResponse<PostDto> response = new PaginatedResponse<>(
                    postDtos, postPage.getTotalElements(), page, size,
                    postPage.hasNext(), postPage.hasPrevious());

            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error getting user posts: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> likePost(@PathVariable Long id, Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            boolean isLiked = likeService.togglePostLike(id, currentUser.getId());
            
            String message = isLiked ? "Post liked successfully" : "Post unliked successfully";
            return ResponseEntity.ok(ApiResponse.success(isLiked, message));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error toggling post like: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/repost")
    public ResponseEntity<?> repost(@PathVariable Long id, Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            Post repost = postService.repost(id, currentUser);
            PostDto postDto = new PostDto(repost);
            
            return ResponseEntity.ok(ApiResponse.success(postDto, "Post reposted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error reposting: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id, Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            postService.deletePost(id, currentUser);
            
            return ResponseEntity.ok(ApiResponse.success(null, "Post deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error deleting post: " + e.getMessage()));
        }
    }

    @DeleteMapping("/repost/{trackId}")
    public ResponseEntity<?> unrepostTrack(@PathVariable Long trackId, Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            boolean removed = postService.removeRepost(trackId, currentUser);

            String message = removed ? "Track unreposted successfully" : "Repost not found";
            return ResponseEntity.ok(ApiResponse.success(removed, message));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error removing repost: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/repost")
    public ResponseEntity<?> unrepostPost(@PathVariable Long id, Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            boolean removed = postService.removePostRepost(id, currentUser);

            String message = removed ? "Post unreposted successfully" : "Repost not found";
            return ResponseEntity.ok(ApiResponse.success(removed, message));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error removing repost: " + e.getMessage()));
        }
    }
}