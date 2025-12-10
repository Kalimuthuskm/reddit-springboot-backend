package com.skm.redditclone.dto.request;

public record UpdatePasswordRequest(String oldPassword,
                                    String newPassword) {}
