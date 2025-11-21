package com.skm.redditclone.controller;

import com.skm.redditclone.dto.*;
import com.skm.redditclone.repository.UserRepository;
import com.skm.redditclone.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getUsers(Authentication auth) {
        UserProfileResponse userProfile = userService.getUser(auth);
        try {
            return ResponseEntity.ok()
                    .body(userProfile);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PostMapping
    public ResponseEntity<?> updateName(Authentication auth, @RequestBody UpdateUsernameRequest request) {

        UsernameResponse response = userService.updateUserName(auth, request);
        try {
            return ResponseEntity.ok()
                    .body(response);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping("update-password")
    public ResponseEntity<?> updatePassword(Authentication auth, @RequestBody UpdatePasswordRequest request) {
        String responce = userService.userPasswordUpadate(auth, request);
        try {
            return ResponseEntity.ok()
                    .body(responce);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}
