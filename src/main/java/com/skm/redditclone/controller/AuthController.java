package com.skm.redditclone.controller;

import com.skm.redditclone.dto.request.AuthRequest;
import com.skm.redditclone.dto.response.AuthResponse;
import com.skm.redditclone.dto.request.RegisterRequest;
import com.skm.redditclone.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.registerUser(request);
        return ResponseEntity.ok(response);

    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody AuthRequest request) {
        AuthResponse response = authService.userLogin(request);
        return ResponseEntity.ok(response);
    }
}
