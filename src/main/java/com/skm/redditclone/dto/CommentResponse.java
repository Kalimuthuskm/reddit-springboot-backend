package com.skm.redditclone.dto;
import com.skm.redditclone.model.Comment;

public record CommentResponse(String comment) {
    public CommentResponse(Comment savedComment) {
        this(savedComment.getComment());
    }

    public void comment(String commentUpdatedSuccessfully) {

    }
}


