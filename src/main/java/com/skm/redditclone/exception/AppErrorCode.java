package com.skm.redditclone.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AppErrorCode {
    USERNAME_NOT_FOUND(1000,HttpStatus.UNAUTHORIZED),
    USERNAME_NOT_NOT_MATCH(1001,HttpStatus.FORBIDDEN),
    USERNAME_ALREADY_EXIST(1002,HttpStatus.CONFLICT),
    PASSWORD_NOT_MATCH(1003,HttpStatus.FORBIDDEN),
    PASSWORD_ALREADY_EXIST(1004,HttpStatus.CONFLICT),
    PASSWORD_MISMATCH(1005,HttpStatus.FORBIDDEN),
    UPDATE_FAILED(1006,HttpStatus.CONFLICT),
    POST_NOT_EXISTS(1007,HttpStatus.NOT_FOUND),
    POST_NOT_DELETED(1008,HttpStatus.FORBIDDEN),
    COMMENT_NOT_DELETED(1009,HttpStatus.FORBIDDEN),
    USER_ALREADY_EXISTS(1010, HttpStatus.BAD_REQUEST),
    INVALID_INPUT(1011,HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(401, HttpStatus.UNAUTHORIZED),
    FORBIDDEN(403, HttpStatus.FORBIDDEN),
    INTERNAL_SERVER_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR);

    private final int error;
    private final HttpStatus httpStatus;

    AppErrorCode(int error, HttpStatus httpStatus) {
        this.error = error;
        this.httpStatus = httpStatus;
    }
}
