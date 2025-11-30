package com.skm.redditclone.dto;


import java.time.Instant;

public record UserProfileResponse (String username, Instant createdAt){
}
