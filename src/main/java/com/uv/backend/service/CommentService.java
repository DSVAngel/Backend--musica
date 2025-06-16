package com.uv.backend.service;
import com.uv.backend.dto.request.CommentRequest;
import com.uv.backend.entity.Comment;
import com.uv.backend.entity.Post;
import com.uv.backend.entity.Track;
import com.uv.backend.entity.User;
import com.uv.backend.repository.CommentRepository;
import com.uv.backend.repository.PostRepository;
import com.uv.backend.repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private PostRepository postRepository;


    public Comment createTrackComment(Long trackId, CommentRequest commentRequest, User user) {
        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        Comment comment = new Comment(commentRequest.getContent(), user);
        comment.setTrack(track);

        Comment savedComment = commentRepository.save(comment);

        return savedComment;
    }

    public Comment createPostComment(Long postId, CommentRequest commentRequest, User user) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = new Comment(commentRequest.getContent(), user);
        comment.setPost(post);

        Comment savedComment = commentRepository.save(comment);

        return savedComment;
    }

    public Comment createReply(Long parentId, CommentRequest commentRequest, User user) {
        Comment parentComment = commentRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent comment not found"));

        Comment reply = new Comment(commentRequest.getContent(), user);
        reply.setParent(parentComment);
        
        // Heredar el contexto del comentario padre
        if (parentComment.getTrack() != null) {
            reply.setTrack(parentComment.getTrack());
        }
        if (parentComment.getPost() != null) {
            reply.setPost(parentComment.getPost());
        }

        Comment savedReply = commentRepository.save(reply);

        return savedReply;
    }

    public Page<Comment> getTrackComments(Long trackId, Pageable pageable) {
        return commentRepository.findByTrackIdAndParentIsNullOrderByCreatedAtDesc(trackId, pageable);
    }

    public Page<Comment> getPostComments(Long postId, Pageable pageable) {
        return commentRepository.findByPostIdAndParentIsNullOrderByCreatedAtDesc(postId, pageable);
    }

    public Page<Comment> getCommentReplies(Long commentId, Pageable pageable) {
        return commentRepository.findByParentIdOrderByCreatedAtAsc(commentId, pageable);
    }

    public Comment updateComment(Long commentId, CommentRequest commentRequest, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only update your own comments");
        }

        comment.setContent(commentRequest.getContent());
        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only delete your own comments");
        }

        commentRepository.delete(comment);
    }

    public Comment getCommentById(Long id) {
        return commentRepository.findById(id).orElse(null);
    }
}