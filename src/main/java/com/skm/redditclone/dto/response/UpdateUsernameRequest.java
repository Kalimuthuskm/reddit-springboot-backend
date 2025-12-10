package com.skm.redditclone.dto.response;

public record UpdateUsernameRequest (String oldUsername,
                                     String newUsername){}
