package com.skm.redditclone.dto;


public record UpdatePasswordRequest(String oldPassword, String newPassword) {
}
