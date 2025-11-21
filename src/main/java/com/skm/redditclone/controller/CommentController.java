package com.skm.redditclone.controller;

import com.skm.redditclone.dto.*;
import com.skm.redditclone.model.Comment;
import com.skm.redditclone.service.CommentService;
import com.skm.redditclone.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/posts/{postId}/comments")
@RestController
public class CommentController {
    private final CommentService commentService;
    private final PostService postService;

    @PostMapping
    public ResponseEntity<?> createComments(@PathVariable Long postId, @RequestBody CommentRequest request, Authentication auth) {
        try {
            CommentResponse response = commentService.createComment(postId, request, auth);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping
    public ResponseEntity<?> getComments(@PathVariable Long postId, Pageable pageable, Authentication auth) {
        try {
            Page<Comment> page = commentService.getCommentByPostID(postId, pageable, auth);
            return ResponseEntity.ok(page);
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long postId, Long commentId, CommentRequest request, Authentication auth) {
        try {
            CommentResponse response = commentService.updateComment(postId, commentId, request, auth);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long postId, @PathVariable Long commentId, Authentication auth) {
        try {
            CommentDeleteResponse response = commentService.deleteComment(postId, commentId, auth);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    @DeleteMapping()
    public ResponseEntity<?> deleteAllComments(@PathVariable Long postId, @RequestBody List<Long> commentIds, Authentication auth) {
        try {
            BulkDeleteResponse response = commentService.bulkDeleteComments(postId,commentIds,auth);
            return ResponseEntity.ok(response);
        }
        catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse();
            error.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
