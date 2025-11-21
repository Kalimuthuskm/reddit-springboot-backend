package com.skm.redditclone.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vote {
    private Long id;
    private Long postId;
    private String username;
    private Integer voteValue; // +1 or -1
    private Instant createdAt;
}
