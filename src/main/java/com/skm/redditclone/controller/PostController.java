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
        try {
            return ResponseEntity.ok().body(response);
        } catch (RuntimeException e) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping
    public ResponseEntity<Object> getPosts(Pageable pageable) {
        Page<PostResponse> response = postService.getPost(pageable);
        try {
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getPost(@PathVariable Long id) {
        try {
            PostResponse response = postService.getPostByID(id);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updatePost(Authentication auth, @PathVariable Long id, @RequestBody PostUpdateRequest request) {
        PostUpdateResponse response = postService.updatePost(auth, id, request);
        try {
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePost(Authentication auth, @PathVariable Long id) {
        PostUpdateResponse response = postService.deletePostByID(id);
        try {
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping
    public ResponseEntity<Object> bulkPostDelete(Authentication auth, @RequestBody List<Long> ids) {
        try {
            BulkDeleteResponse response = postService.bulkDeletePosts(ids, auth);
            return ResponseEntity.ok().body(response);
        } catch (RuntimeException e) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

}

