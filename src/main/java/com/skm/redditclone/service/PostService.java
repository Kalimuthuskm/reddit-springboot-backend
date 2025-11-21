package com.skm.redditclone.service;

import com.skm.redditclone.dto.*;
import com.skm.redditclone.exception.AppErrorCode;
import com.skm.redditclone.exception.AppException;
import com.skm.redditclone.model.Post;
import com.skm.redditclone.model.User;
import com.skm.redditclone.repository.PostRepository;
import com.skm.redditclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private String getLoggedUsername(Authentication auth) {
        return ((UserDetails) auth.getPrincipal()).getUsername();
    }

    // Create new post
    public PostResponse createPost(PostRequest req, Authentication auth) {
        String username = getLoggedUsername(auth);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(AppErrorCode.USERNAME_NOT_FOUND));

        Post post = new Post();
        post.setTitle(req.getTitle());
        post.setContent(req.getContent());
        post.setUser_id(user.getId());
        post.setCreatedAt(Instant.now());
        post.setUpdatedAt(Instant.now());

        Post saved = postRepository.createPost(post);
        return new PostResponse(saved);
    }

    public Page<PostResponse> getPost(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);

        List<PostResponse> responses = posts.getContent()
                .stream()
                .map(PostResponse::new)
                .toList();
        return new PageImpl<>(responses, pageable, posts.getTotalElements());
    }

    public PostResponse getPostByID(Long id) {
        Post post = postRepository.findById(id);
        return new PostResponse(post);
    }

    public PostUpdateResponse updatePost(Authentication auth, Long id, PostUpdateRequest request) {
        String username = getLoggedUsername(auth);

        String content = request.getContent();
        String title = request.getTitle();

        boolean response = postRepository.updatePost(id, title, content);
        if (response) {
            String message = "Post Updated Successfully";
            return new PostUpdateResponse(message);
        } else {
            throw new AppException(AppErrorCode.UPDATE_FAILED);
        }
    }

    public PostUpdateResponse deletePostByID(Long id) {
        boolean response = postRepository.deletePost(id);
        if (response) {
            String message = "Post Deleted Successfully";
            return new PostUpdateResponse(message);
        } else {
            throw new AppException(AppErrorCode.POST_NOT_DELETED);
        }
    }

    public BulkDeleteResponse bulkDeletePosts(List<Long> ids, Authentication auth) {
        List<Long> existingIds = postRepository.findExistingIds(ids);

        int deleted = postRepository.bulkDeletePosts(existingIds);
        List<Long> notFoundId = new ArrayList<>(ids);
        notFoundId.removeAll(existingIds);

        if (deleted == 0) {
            throw new AppException(AppErrorCode.POST_NOT_DELETED);
        } else {
            if (notFoundId.isEmpty()) {
                return new BulkDeleteResponse(
                        deleted, Collections.emptyList());
            } else {
                return new BulkDeleteResponse(
                        deleted, notFoundId
                );
            }
        }
    }
}



