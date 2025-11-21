package com.skm.redditclone.dto;

import com.skm.redditclone.model.Post;
import lombok.Data;

@Data

public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private Long userid;
    private java.time.Instant createdAt;
    private java.time.Instant updatedAt;

    public PostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.userid = post.getUser_id();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }


}
