package com.skm.redditclone.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUpload {
    private Long id;
    private Long userId;
    private String originalName;
    private String storedName;
    private Long fileSize;
    private String contentType;
    private String s3Key;
    private String s3Url;
    private String description;
    private LocalDateTime uploadedAt;
    private Long postId;
}