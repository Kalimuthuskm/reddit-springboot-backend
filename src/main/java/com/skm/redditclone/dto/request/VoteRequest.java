package com.skm.redditclone.dto.request;

import jakarta.validation.constraints.NotNull;

public record VoteRequest (@NotNull Integer voteValue){}
