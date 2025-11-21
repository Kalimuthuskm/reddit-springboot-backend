package com.skm.redditclone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class UserProfileResponse {
    private String username;
    private Instant createdAt;
}
