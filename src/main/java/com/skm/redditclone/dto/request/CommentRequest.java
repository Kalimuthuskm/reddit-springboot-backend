package com.skm.redditclone.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CommentRequest (@NotBlank  String comment){}
