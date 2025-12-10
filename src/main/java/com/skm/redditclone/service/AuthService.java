package com.skm.redditclone.service;

import com.skm.redditclone.dto.request.AuthRequest;
import com.skm.redditclone.dto.response.AuthResponse;
import com.skm.redditclone.dto.request.RegisterRequest;
import com.skm.redditclone.exception.AppErrorCode;
import com.skm.redditclone.exception.AppException;
import com.skm.redditclone.model.User;
import com.skm.redditclone.repository.UserRepository;
import com.skm.redditclone.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse registerUser(RegisterRequest request) {

        if (userRepository.existsByUsername(request.username())) {
            throw new AppException(AppErrorCode.USER_ALREADY_EXISTS);
        }
        User newUser = new User();
        newUser.setUsername(request.username());
        newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setCreatedAt(Instant.now());
        User savedUser = userRepository.createUser(newUser);
        String token = jwtService.generateToken(savedUser.getUsername());
        return new AuthResponse(token);
    }

    public AuthResponse userLogin(AuthRequest request) {
        User user = userRepository.getUser(request.username())
                .orElseThrow(() -> new AppException(AppErrorCode.INVALID_INPUT));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new AppException(AppErrorCode.PASSWORD_MISMATCH);
        }
        String token = jwtService.generateToken(user.getUsername());
        return new AuthResponse(token);
    }
}
