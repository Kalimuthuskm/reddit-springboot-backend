package com.skm.redditclone.controller;

import com.skm.redditclone.dto.request.PostRequest;
import com.skm.redditclone.dto.request.PostUpdateRequest;
import com.skm.redditclone.dto.response.BulkDeleteResponse;
import com.skm.redditclone.dto.response.FileUploadResponse;
import com.skm.redditclone.dto.response.PostResponse;
import com.skm.redditclone.dto.response.PostUpdateResponse;
import com.skm.redditclone.service.FileUploadService;
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
@RequestMapping("/api/posts")
@RestController
public class PostController {

    private final PostService postService;
    private final FileUploadService fileUploadService;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid @NotNull PostRequest req,
                                         Authentication auth) {
        PostResponse response = postService.createPost(req, auth);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping
    public ResponseEntity<Object> getPosts(Pageable pageable) {
        Page<PostResponse> response = postService.getPost(pageable);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getPost(@PathVariable @NotNull @Positive Long id) {

        PostResponse response = postService.getPostByID(id);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updatePost(Authentication auth,
                                             @PathVariable @NotNull @Positive Long id,
                                             @RequestBody @Valid @NotNull PostUpdateRequest request) {
        PostUpdateResponse response = postService.updatePost(auth, id, request);
        return ResponseEntity.ok().body(response);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePost(Authentication auth,
                                             @PathVariable @NotNull @Positive Long id) {
        PostUpdateResponse response = postService.deletePostByID(auth,id);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping
    public ResponseEntity<Object> bulkPostDelete(Authentication auth,
                                                 @RequestBody @NotNull @Size(min = 1) List<Long> ids) {
        BulkDeleteResponse response = postService.bulkDeletePosts(ids, auth);
        return ResponseEntity.ok().body(response);

    }

    @GetMapping("/api/files/post/{postId}")
    public ResponseEntity<List<FileUploadResponse>> getPostFiles(@PathVariable @NotNull @Positive Long postId) {
        List<FileUploadResponse> files = fileUploadService.getPostFiles(postId);
        return ResponseEntity.ok(files);
    }
}

