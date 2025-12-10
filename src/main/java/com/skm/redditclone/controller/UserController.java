package com.skm.redditclone.controller;

import com.skm.redditclone.dto.request.UpdatePasswordRequest;
import com.skm.redditclone.dto.response.UpdateUsernameRequest;
import com.skm.redditclone.dto.response.UserProfileResponse;
import com.skm.redditclone.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getUsers(Authentication auth) {
        UserProfileResponse userProfile = userService.getUser(auth);
        return ResponseEntity.ok()
                .body(userProfile);
    }

    @PostMapping
    public ResponseEntity<?> updateName(Authentication auth,
                                        @Valid @RequestBody UpdateUsernameRequest request) {

        userService.updateUserName(auth, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("update-password")
    public ResponseEntity<?> updatePassword(Authentication auth,
                                            @Valid @RequestBody UpdatePasswordRequest request) {
        String responce = userService.updateUserPassword(auth, request);
        return ResponseEntity.ok().body(responce);
    }
}
