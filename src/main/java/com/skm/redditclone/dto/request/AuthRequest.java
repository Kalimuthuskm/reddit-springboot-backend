package com.skm.redditclone.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequest(@NotBlank String username,
                          @NotBlank
                          @Size(min = 6)
                          String password) {}
