package com.skm.redditclone.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePasswordRequest(@NotBlank
                                    String oldPassword,
                                    @NotBlank
                                    @Size(min = 6)
                                    String newPassword) {}
