package com.skm.redditclone.controller;

import com.skm.redditclone.dto.*;
import com.skm.redditclone.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/posts")
@RestController
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody PostRequest req, Authentication auth) {
        PostResponse response = postService.createPost(req, auth);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping
    public ResponseEntity<Object> getPosts(Pageable pageable) {
        Page<PostResponse> response = postService.getPost(pageable);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getPost(@PathVariable Long id) {

        PostResponse response = postService.getPostByID(id);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updatePost(Authentication auth, @PathVariable Long id, @RequestBody PostUpdateRequest request) {
        PostUpdateResponse response = postService.updatePost(auth, id, request);
        return ResponseEntity.ok().body(response);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePost(Authentication auth, @PathVariable Long id) {
        PostUpdateResponse response = postService.deletePostByID(id);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping
    public ResponseEntity<Object> bulkPostDelete(Authentication auth, @RequestBody List<Long> ids) {
        BulkDeleteResponse response = postService.bulkDeletePosts(ids, auth);
        return ResponseEntity.ok().body(response);

    }

}

