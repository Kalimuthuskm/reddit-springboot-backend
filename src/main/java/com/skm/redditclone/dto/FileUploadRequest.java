package com.skm.redditclone.dto;

public record FileUploadRequest(
        String description,
        Long postId
) {
}