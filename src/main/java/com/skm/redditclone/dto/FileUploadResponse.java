package com.skm.redditclone.dto;

import com.skm.redditclone.model.FileUpload;
import java.time.Instant;

public record FileUploadResponse(
        Long id,
        String originalName,
        String storedName,
        Long fileSize,
        String contentType,
        String s3Url,
        String description,
        Instant uploadedAt
) {
    public FileUploadResponse(FileUpload fileUpload) {
        this(
                fileUpload.getId(),
                fileUpload.getOriginalName(),
                fileUpload.getStoredName(),
                fileUpload.getFileSize(),
                fileUpload.getContentType(),
                fileUpload.getS3Url(),
                fileUpload.getDescription(),
                fileUpload.getUploadedAt()
        );
    }
}

