package com.skm.redditclone.dto.response;

import com.skm.redditclone.model.FileUpload;
import org.jetbrains.annotations.NotNull;

import java.time.LocalTime;

public record FileUploadResponse(
        Long id,
        String originalName,
        String storedName,
        Long fileSize,
        String contentType,
        String s3Url,
        String description,
        Long postId,
        LocalTime uploadedAt
) {
    public FileUploadResponse(@NotNull FileUpload fileUpload) {
        this(
                fileUpload.getId(),
                fileUpload.getOriginalName(),
                fileUpload.getStoredName(),
                fileUpload.getFileSize(),
                fileUpload.getContentType(),
                fileUpload.getS3Url(),
                fileUpload.getDescription(),
                fileUpload.getPostId(),
                fileUpload.getUploadedAt() !=null ?fileUpload.getUploadedAt().toLocalTime() :null
        );
    }
}

