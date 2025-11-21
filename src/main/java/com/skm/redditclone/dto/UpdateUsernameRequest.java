package com.skm.redditclone.dto;

import lombok.Data;

@Data
public class UpdateUsernameRequest {
    private String oldUsername;
    private String newUsername;
}
