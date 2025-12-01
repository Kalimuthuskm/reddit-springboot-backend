package com.skm.redditclone.controller;

import com.skm.redditclone.dto.FileUploadResponse;
import com.skm.redditclone.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/files")
@RestController
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "postId", required = false) Long postId,
            Authentication auth
    ) throws IOException {
        FileUploadResponse response = fileUploadService.uploadFile(file, description, postId, auth);
        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<Page<FileUploadResponse>> getUserFiles(
            Pageable pageable,
            Authentication auth
    ) {
        Page<FileUploadResponse> files = fileUploadService.getUserFiles(auth, pageable);
        return ResponseEntity.ok(files);
    }


    @GetMapping("/{fileId}")
    public ResponseEntity<FileUploadResponse> getFileById(
            @PathVariable Long fileId,
            Authentication auth
    ) {
        FileUploadResponse response = fileUploadService.getFileById(fileId, auth);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/post/{postId}")
    public ResponseEntity<List<FileUploadResponse>> getPostFiles(
            @PathVariable Long postId
    ) {
        List<FileUploadResponse> files = fileUploadService.getPostFiles(postId);
        return ResponseEntity.ok(files);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<String> deleteFile(
            @PathVariable Long fileId,
            Authentication auth
    ) {
        fileUploadService.deleteFile(fileId, auth);
        return ResponseEntity.ok("File deleted successfully");
    }
}