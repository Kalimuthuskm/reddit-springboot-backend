package com.skm.redditclone.service;

import com.skm.redditclone.dto.request.PostRequest;
import com.skm.redditclone.dto.request.PostUpdateRequest;
import com.skm.redditclone.dto.response.BulkDeleteResponse;
import com.skm.redditclone.dto.response.PostResponse;
import com.skm.redditclone.dto.response.PostUpdateResponse;
import com.skm.redditclone.exception.AppErrorCode;
import com.skm.redditclone.exception.AppException;
import com.skm.redditclone.model.Post;
import com.skm.redditclone.model.User;
import com.skm.redditclone.repository.PostRepository;
import com.skm.redditclone.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private String getLoggedUsername(Authentication auth) {
        return ((UserDetails) auth.getPrincipal()).getUsername();
    }

    public PostResponse createPost(@Valid @NotNull PostRequest req,
                                   Authentication auth) {
        String username = getLoggedUsername(auth);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new AppException(AppErrorCode.USERNAME_NOT_FOUND));

        Post post = new Post();
        post.setTitle(req.title());
        post.setContent(req.content());
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

    public PostResponse getPostByID(@NotNull @Positive Long id) {
        Post post = postRepository.findById(id);
        return new PostResponse(post);
    }

    @Transactional
    public PostUpdateResponse updatePost(Authentication auth,
                                         @NotNull @Positive Long id,
                                         @Valid @NotNull PostUpdateRequest request) {
        String username = getLoggedUsername(auth);

        String content = request.content();
        String title = request.title();

        boolean response = postRepository.updatePost(id, title, content);
        if (response) {
            String message = "Post Updated Successfully";
            return new PostUpdateResponse(message);
        } else {
            throw new AppException(AppErrorCode.UPDATE_FAILED);
        }
    }

    @Transactional
    public PostUpdateResponse deletePostByID(Authentication auth,
                                             @NotNull @Positive Long id) {
        boolean response = postRepository.deletePost(id);
        if (response) {
            String message = "Post Deleted Successfully";
            return new PostUpdateResponse(message);
        } else {
            throw new AppException(AppErrorCode.POST_NOT_DELETED);
        }
    }

    @Transactional
    public BulkDeleteResponse bulkDeletePosts(@NotNull @Size(min = 1) List<Long> ids,
                                              Authentication auth) {
        List<Long> existingIds = postRepository.findExistingIds(ids);

        int deleted = postRepository.bulkDeletePosts(existingIds);
        List<Long> notFoundId = new ArrayList<>(ids);
        notFoundId.removeAll(existingIds);

        if (deleted == 0) {
            throw new AppException(AppErrorCode.POST_NOT_DELETED);
        } else {
            if (notFoundId.isEmpty()) {
                return new BulkDeleteResponse(
                        deleted,
                        Collections.emptyList()
                );
            } else {
                return new BulkDeleteResponse(
                        deleted,
                        notFoundId
                );
            }
        }
    }
}



