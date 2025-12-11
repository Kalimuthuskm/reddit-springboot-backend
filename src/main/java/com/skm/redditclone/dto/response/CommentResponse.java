package com.skm.redditclone.dto.response;

import com.skm.redditclone.model.Comment;

import java.io.Serializable;

public record CommentResponse(Comment comment) implements Serializable {}


