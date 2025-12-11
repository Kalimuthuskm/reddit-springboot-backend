package com.skm.redditclone.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PostUpdateRequest (@NotBlank String title,
                                 @NotBlank String content){}

