package com.skm.redditclone.service;

import com.skm.redditclone.dto.AuthRequest;
import com.skm.redditclone.dto.AuthResponse;
import com.skm.redditclone.dto.RegisterRequest;
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

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setCreatedAt(Instant.now());

        User savedUser = userRepository.createUser(newUser);

        String token = jwtService.generateToken(savedUser.getUsername());

        return new AuthResponse(token);
    }

    public AuthResponse userLogin(AuthRequest request) {

        User user = userRepository.getUser(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String token = jwtService.generateToken(user.getUsername());

        return new AuthResponse(token);
    }
}
