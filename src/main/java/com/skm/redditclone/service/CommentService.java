package com.skm.redditclone.service;

import com.skm.redditclone.dto.request.CommentRequest;
import com.skm.redditclone.dto.response.BulkDeleteResponse;
import com.skm.redditclone.dto.response.CommentResponse;
import com.skm.redditclone.exception.AppErrorCode;
import com.skm.redditclone.exception.AppException;
import com.skm.redditclone.model.Comment;
import com.skm.redditclone.repository.CommentRepository;
import com.skm.redditclone.repository.PostRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Validated
@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentResponse createComment(@NotNull @Positive Long postId,
                                         @Valid @NotNull CommentRequest request,
                                          Authentication auth) {
        String username = auth.getName();
        if (!postRepository.existsById(postId)) {
            throw new AppException(AppErrorCode.POST_NOT_EXISTS);
        }
        Comment newComment = new Comment();
        newComment.setPostId(postId);
        newComment.setUsername(username);
        newComment.setComment(request.comment());
        newComment.setCreatedAt(Instant.now());
        newComment.setUpdatedAt(Instant.now());
        Comment savedComment = commentRepository.createComment(newComment);
        return new CommentResponse(savedComment);
    }

    public Page<Comment> getCommentByPostID(@NotNull @Positive Long postId,
                                            Pageable pageable,
                                           Authentication auth) {
        String username = auth.getName();
        if (!postRepository.existsById(postId)) {
            throw new AppException(AppErrorCode.POST_NOT_EXISTS);
        }
        return commentRepository.getCommentsByPostId(postId, pageable);
    }

    @Transactional
    public void updateComment(@NotNull @Positive Long postId,
                              @NotNull @Positive Long commentId,
                              @Valid @NotNull CommentRequest request,
                              Authentication auth) {
        String username = auth.getName();

        if (!postRepository.existsById(postId)) {
            throw new AppException(AppErrorCode.POST_NOT_EXISTS);
        }
        boolean comment = commentRepository.updateComment(commentId, request.comment());
       if (!comment) {
           throw new AppException(AppErrorCode.POST_NOT_EXISTS);
       }
    }

    @Transactional
    public void deleteComment(@NotNull @Positive Long postId,
                              @NotNull @Positive Long commentId,
                              Authentication auth) {
        String username = auth.getName();
        if (!postRepository.existsById(postId)) {
            throw new AppException(AppErrorCode.POST_NOT_EXISTS);
        }
        boolean comment = commentRepository.deleteComment(commentId);
        if (!comment) {
          throw  new AppException(AppErrorCode.COMMENT_NOT_DELETED);
        }
    }

    @Transactional
    public BulkDeleteResponse bulkDeleteComments(@NotNull @Positive Long postId,
                                                 @NotNull @Size(min = 1) List<Long> commentIds,
                                                 Authentication auth) {
        if (!postRepository.existsById(postId)) {
            throw new AppException(AppErrorCode.POST_NOT_EXISTS);
        }
        List<Long> exitingCommentIds = commentRepository.findExistingIds(commentIds);
        int deleted = commentRepository.bulkDeletePosts(exitingCommentIds);
        List<Long> notFound = new ArrayList<>();
        notFound.removeAll(exitingCommentIds);

        if (deleted == 0) {
            throw new RuntimeException("Comment not deleted");
        } else {
            if (notFound.isEmpty()) {
                return new BulkDeleteResponse(
                        deleted, Collections.emptyList()
                );
            } else {
                return new BulkDeleteResponse(
                        deleted, notFound
                );
            }
        }
    }
}
