package com.skm.redditclone.controller;

import com.skm.redditclone.dto.request.CommentRequest;
import com.skm.redditclone.dto.response.BulkDeleteResponse;
import com.skm.redditclone.dto.response.CommentResponse;
import com.skm.redditclone.model.Comment;
import com.skm.redditclone.service.CommentService;
import com.skm.redditclone.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/posts/{postId}/comments")
@RestController
public class CommentController {

    private final CommentService commentService;
    private final PostService postService;

    @PostMapping
    public ResponseEntity<?> createComments(@PathVariable @NotNull @Positive Long postId,
                                            @Valid @RequestBody CommentRequest request,
                                            Authentication auth) {
        CommentResponse response = commentService.createComment(postId, request, auth);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getComments(@PathVariable @NotNull @Positive Long postId,
                                         Pageable pageable,
                                         Authentication auth) {
        Page<Comment> page = commentService.getCommentByPostID(postId, pageable, auth);
        return ResponseEntity.ok(page);

    }

    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable @NotNull @Positive Long postId,
                                           @PathVariable @NotNull @Positive Long commentId,
                                           @RequestBody @Valid @NotNull CommentRequest request,
                                           Authentication auth) {
        commentService.updateComment(postId, commentId, request, auth);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable @NotNull @Positive Long postId,
                                           @PathVariable @NotNull @Positive Long commentId,
                                           Authentication auth) {
         commentService.deleteComment(postId, commentId, auth);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteAllComments(@PathVariable @NotNull @Positive Long postId,
                                               @RequestBody @NotNull @Size(min = 1) List<Long> commentIds,
                                               Authentication auth) {
        BulkDeleteResponse response = commentService.bulkDeleteComments(postId, commentIds, auth);
        return ResponseEntity.ok(response);
    }
}
