package com.skm.redditclone.dto;

import com.skm.redditclone.model.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private String comment;

    public CommentResponse(Comment savedComment) {
        this.comment = savedComment.getComment();
    }
}

