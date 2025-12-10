package com.skm.redditclone.service;

import com.skm.redditclone.dto.request.UpdatePasswordRequest;
import com.skm.redditclone.dto.response.UpdateUsernameRequest;
import com.skm.redditclone.dto.response.UserProfileResponse;
import com.skm.redditclone.exception.AppErrorCode;
import com.skm.redditclone.exception.AppException;
import com.skm.redditclone.model.User;
import com.skm.redditclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void updateUserName(Authentication auth,
                               UpdateUsernameRequest request) {
        String currentUsername = auth.getName();
        String oldUsername = request.oldUsername();
        String newUsername = request.newUsername();

        if (!oldUsername.equals(currentUsername)) {
            throw new AppException(AppErrorCode.USERNAME_NOT_NOT_MATCH);
        }
        boolean updateSuccess = userRepository.updateUsername(oldUsername, newUsername);
        if (!updateSuccess) {
            throw new AppException(AppErrorCode.USERNAME_NOT_FOUND);
        }
    }

    @Transactional
    public String updateUserPassword(Authentication auth,
                                     UpdatePasswordRequest request) {
        String username = auth.getName();
        String oldPassword = request.oldPassword();
        String newPassword = request.newPassword();

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