package com.uv.backend.controller;

import com.uv.backend.dto.CommentDto;
import com.uv.backend.dto.request.CommentRequest;
import com.uv.backend.dto.response.ApiResponse;
import com.uv.backend.dto.response.PaginatedResponse;
import com.uv.backend.entity.Comment;
import com.uv.backend.entity.User;
import com.uv.backend.service.CommentService;
import com.uv.backend.service.LikeService;
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
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @PostMapping("/track/{trackId}")
    public ResponseEntity<?> createTrackComment(
            @PathVariable Long trackId,
            @Valid @RequestBody CommentRequest commentRequest,
            Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            Comment comment = commentService.createTrackComment(trackId, commentRequest, currentUser);
            CommentDto commentDto = new CommentDto(comment);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(commentDto, "Comment created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error creating comment: " + e.getMessage()));
        }
    }

    @PostMapping("/post/{postId}")
    public ResponseEntity<?> createPostComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest commentRequest,
            Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            Comment comment = commentService.createPostComment(postId, commentRequest, currentUser);
            CommentDto commentDto = new CommentDto(comment);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(commentDto, "Comment created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error creating comment: " + e.getMessage()));
        }
    }

    @PostMapping("/{parentId}/reply")
    public ResponseEntity<?> createReply(
            @PathVariable Long parentId,
            @Valid @RequestBody CommentRequest commentRequest,
            Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            Comment reply = commentService.createReply(parentId, commentRequest, currentUser);
            CommentDto commentDto = new CommentDto(reply);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(commentDto, "Reply created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error creating reply: " + e.getMessage()));
        }
    }

    @GetMapping("/track/{trackId}")
    public ResponseEntity<?> getTrackComments(
            @PathVariable Long trackId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Comment> commentPage = commentService.getTrackComments(trackId, pageable);
            
            User currentUser = authentication != null ? (User) authentication.getPrincipal() : null;
            List<CommentDto> commentDtos = commentPage.getContent().stream()
                    .map(comment -> {
                        CommentDto dto = new CommentDto(comment);
                        if (currentUser != null) {
                            boolean isLiked = likeService.isCommentLikedByUser(comment.getId(), currentUser.getId());
                            dto.setIsLiked(isLiked);
                        }
                        return dto;
                    })
                    .collect(Collectors.toList());

            PaginatedResponse<CommentDto> response = new PaginatedResponse<>(
                    commentDtos, commentPage.getTotalElements(), page, size,
                    commentPage.hasNext(), commentPage.hasPrevious());

            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error getting track comments: " + e.getMessage()));
        }
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<?> getPostComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Comment> commentPage = commentService.getPostComments(postId, pageable);
            
            User currentUser = authentication != null ? (User) authentication.getPrincipal() : null;
            List<CommentDto> commentDtos = commentPage.getContent().stream()
                    .map(comment -> {
                        CommentDto dto = new CommentDto(comment);
                        if (currentUser != null) {
                            boolean isLiked = likeService.isCommentLikedByUser(comment.getId(), currentUser.getId());
                            dto.setIsLiked(isLiked);
                        }
                        return dto;
                    })
                    .collect(Collectors.toList());

            PaginatedResponse<CommentDto> response = new PaginatedResponse<>(
                    commentDtos, commentPage.getTotalElements(), page, size,
                    commentPage.hasNext(), commentPage.hasPrevious());

            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error getting post comments: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}/replies")
    public ResponseEntity<?> getCommentReplies(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Comment> repliesPage = commentService.getCommentReplies(id, pageable);
            
            User currentUser = authentication != null ? (User) authentication.getPrincipal() : null;
            List<CommentDto> commentDtos = repliesPage.getContent().stream()
                    .map(comment -> {
                        CommentDto dto = new CommentDto(comment);
                        if (currentUser != null) {
                            boolean isLiked = likeService.isCommentLikedByUser(comment.getId(), currentUser.getId());
                            dto.setIsLiked(isLiked);
                        }
                        return dto;
                    })
                    .collect(Collectors.toList());

            PaginatedResponse<CommentDto> response = new PaginatedResponse<>(
                    commentDtos, repliesPage.getTotalElements(), page, size,
                    repliesPage.hasNext(), repliesPage.hasPrevious());

            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error getting comment replies: " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> likeComment(@PathVariable Long id, Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            boolean isLiked = likeService.toggleCommentLike(id, currentUser.getId());
            
            String message = isLiked ? "Comment liked successfully" : "Comment unliked successfully";
            return ResponseEntity.ok(ApiResponse.success(isLiked, message));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error toggling comment like: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentRequest commentRequest,
            Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            Comment updatedComment = commentService.updateComment(id, commentRequest, currentUser);
            CommentDto commentDto = new CommentDto(updatedComment);
            
            return ResponseEntity.ok(ApiResponse.success(commentDto, "Comment updated successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error updating comment: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id, Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            commentService.deleteComment(id, currentUser);
            
            return ResponseEntity.ok(ApiResponse.success(null, "Comment deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error deleting comment: " + e.getMessage()));
        }
    }
}
