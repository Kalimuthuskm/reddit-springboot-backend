package com.skm.redditclone.service;

import com.skm.redditclone.dto.UpdatePasswordRequest;
import com.skm.redditclone.dto.UpdateUsernameRequest;
import com.skm.redditclone.dto.UserProfileResponse;
import com.skm.redditclone.dto.UsernameResponse;
import com.skm.redditclone.exception.AppErrorCode;
import com.skm.redditclone.exception.AppException;
import com.skm.redditclone.model.User;
import com.skm.redditclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserProfileResponse getUser(Authentication auth) {
        String username = auth.getName();
        Optional<User> user = userRepository.findByUsername(username);
        UserProfileResponse userProfile = user.map(
                        u -> new UserProfileResponse(
                                u.getUsername(),
                                u.getCreatedAt()
                        )
                )
                .orElseThrow(() -> new AppException(AppErrorCode.USERNAME_NOT_FOUND));

        return userProfile;
    }

    public UsernameResponse updateUserName(Authentication auth, UpdateUsernameRequest request) {
        String currentUsername = auth.getName();
        String oldUsername = request.getOldUsername();
        String newUsername = request.getNewUsername();

        if (!oldUsername.equals(currentUsername)) {
            throw new AppException(AppErrorCode.USERNAME_NOT_NOT_MATCH);
        }
        boolean updateSuccess = userRepository.updateUsername(oldUsername, newUsername);
        if (updateSuccess) {
            UsernameResponse response = new UsernameResponse();
            response.setUsername(newUsername);
            return response;
        }
        throw new AppException(AppErrorCode.USERNAME_NOT_FOUND);
    }

    public String userPasswordUpadate(Authentication auth, UpdatePasswordRequest request) {
        String username = auth.getName();
        String oldPassword = request.getOldPassword();
        String newPassword = request.getNewPassword();

        String storedPass = userRepository.findPasswordByUsername(username);
        if (!passwordEncoder.matches(oldPassword, storedPass)) {
            throw new AppException(AppErrorCode.PASSWORD_NOT_MATCH);
        }
        if (passwordEncoder.matches(newPassword, storedPass)) {
            throw new AppException(AppErrorCode.PASSWORD_NOT_MATCH);
        }

        String encodePassword = passwordEncoder.encode(newPassword);

        boolean updateSuccess = userRepository.updatePassword(username, encodePassword);
        if (updateSuccess) {
            return "password updated";
        } else {
            throw new AppException(AppErrorCode.UPDATE_FAILED);
        }

    }
}
