package com.skm.redditclone.dto.response;

import com.skm.redditclone.model.Post;

import java.io.Serializable;

public record PostResponse(
        Long id,
        String title,
        String content,
        Long userid,
        java.time.Instant createdAt,
        java.time.Instant updatedAt
) implements Serializable {
    public PostResponse(Post post) {
        this(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getUser_id(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
