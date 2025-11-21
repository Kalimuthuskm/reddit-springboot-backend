package com.skm.redditclone.exception;

import lombok.Data;
import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
    private final AppErrorCode errorCode;
    public AppException(AppErrorCode errorCode) {
        super();
        this.errorCode = errorCode;
    }

}
