package com.skm.redditclone.exception;

import com.skm.redditclone.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> AppException(AppException ex) {
        AppErrorCode code = ex.getErrorCode();
        ErrorResponse errorResponse = new ErrorResponse(
                code.getError(),
                code.name()
        );

        return new ResponseEntity<>(errorResponse,code.getHttpStatus());
    }
}
