package com.skm.redditclone.service;

import com.skm.redditclone.dto.*;
import com.skm.redditclone.exception.AppErrorCode;
import com.skm.redditclone.exception.AppException;
import com.skm.redditclone.model.Comment;
import com.skm.redditclone.repository.CommentRepository;
import com.skm.redditclone.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentResponse createComment(Long postId, CommentRequest request, Authentication auth) {
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

    public Page<Comment> getCommentByPostID(Long postId, Pageable pageable, Authentication auth) {
        String username = auth.getName();
        if (!postRepository.existsById(postId)) {
            throw new AppException(AppErrorCode.POST_NOT_EXISTS);
        }
        return commentRepository.getCommentsByPostId(postId, pageable);
    }

    public CommentUpdateResponse updateComment(Long postId, Long commentId, CommentRequest request, Authentication auth) {
        String username = auth.getName();

        if (!postRepository.existsById(postId)) {
            throw new AppException(AppErrorCode.POST_NOT_EXISTS);
        }
        boolean comment = commentRepository.updateComment(commentId, request.comment());
        if (comment) {
            return new CommentUpdateResponse("");
        } else {
            throw new AppException(AppErrorCode.UPDATE_FAILED);
        }
    }

    public void deleteComment(Long postId, Long commentId, Authentication auth) {
        String username = auth.getName();
        if (!postRepository.existsById(postId)) {
            throw new AppException(AppErrorCode.POST_NOT_EXISTS);
        }
        boolean comment = commentRepository.deleteComment(commentId);
        if (comment) {
          return;
        } else {
          throw  new AppException(AppErrorCode.COMMENT_NOT_DELETED);
        }
    }



    public BulkDeleteResponse bulkDeleteComments(Long postId, List<Long> commentIds, Authentication auth) {
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
