package com.skm.redditclone.dto.response;

import java.io.Serializable;
import java.time.Instant;

public record UserProfileResponse (String username,
                                   Instant createdAt) implements Serializable {}
