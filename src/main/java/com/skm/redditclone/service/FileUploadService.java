package com.skm.redditclone.service;

import com.skm.redditclone.dto.response.FileUploadResponse;
import com.skm.redditclone.exception.AppErrorCode;
import com.skm.redditclone.exception.AppException;
import com.skm.redditclone.model.FileUpload;
import com.skm.redditclone.model.User;
import com.skm.redditclone.repository.FileUploadRepository;
import com.skm.redditclone.repository.UserRepository;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Validated
@Slf4j
@RequiredArgsConstructor
@Service
public class FileUploadService {

    private final S3Service s3Service;
    private final FileUploadRepository fileUploadRepository;
    private final UserRepository userRepository;

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp",
            "application/pdf",
            "video/mp4",
            "video/mpeg"
    );
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    public FileUploadResponse uploadFile(
            MultipartFile file,
            String description,
            Long postId,
            Authentication auth
    ) throws IOException {

        validateFile(file);
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(AppErrorCode.USERNAME_NOT_FOUND));
        String folder = "uploads/" + user.getId();
        String s3Key = s3Service.uploadFile(file, folder);
        String s3Url = s3Service.getFileUrl(s3Key);

        FileUpload fileUpload = new FileUpload();
        fileUpload.setUserId(user.getId());
        fileUpload.setOriginalName(file.getOriginalFilename());
        fileUpload.setStoredName(s3Key.substring(s3Key.lastIndexOf("/") + 1));
        fileUpload.setFileSize(file.getSize());
        fileUpload.setContentType(file.getContentType());
        fileUpload.setS3Key(s3Key);
        fileUpload.setS3Url(s3Url);
        fileUpload.setDescription(description);
        fileUpload.setPostId(postId);
        fileUpload.setUploadedAt(LocalDateTime.now());

        FileUpload savedFile = fileUploadRepository.save(fileUpload);

        log.info("File uploaded successfully: {} by user: {}", savedFile.getId(), username);
        return new FileUploadResponse(savedFile);
    }

    public Page<FileUploadResponse> getUserFiles(Authentication auth, Pageable pageable) {
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(AppErrorCode.USERNAME_NOT_FOUND));

        Page<FileUpload> files = fileUploadRepository.findByUserId(user.getId(), pageable);
        return files.map(FileUploadResponse::new);
    }


    public FileUploadResponse getFileById(@NotNull @Positive Long fileId, Authentication auth) {
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(AppErrorCode.USERNAME_NOT_FOUND));

        FileUpload fileUpload = fileUploadRepository.findById(fileId)
                .orElseThrow(() -> new AppException(AppErrorCode.FILE_NOT_FOUND));

        if (!fileUpload.getUserId().equals(user.getId())) {
            throw new AppException(AppErrorCode.FORBIDDEN);
        }

        return new FileUploadResponse(fileUpload);
    }

    public List<FileUploadResponse> getPostFiles(@NotNull @Positive Long postId) {
        List<FileUpload> files = fileUploadRepository.findByPostId(postId);
        return files.stream()
                .map(FileUploadResponse::new)
                .toList();
    }

    public void deleteFile(@NotNull @Positive Long fileId, Authentication auth) {
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(AppErrorCode.USERNAME_NOT_FOUND));

        FileUpload fileUpload = fileUploadRepository.findById(fileId)
                .orElseThrow(() -> new AppException(AppErrorCode.FILE_NOT_FOUND));

        if (!fileUpload.getUserId().equals(user.getId())) {
            throw new AppException(AppErrorCode.FORBIDDEN);
        }

        s3Service.deleteFile(fileUpload.getS3Key());
        fileUploadRepository.deleteById(fileId);
        log.info("File deleted successfully: {} by user: {}", fileId, username);
    }

    private void validateFile(@NotNull MultipartFile file) {
        if (file.isEmpty()) {
            throw new AppException(AppErrorCode.INVALID_INPUT);
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new AppException(AppErrorCode.FILE_SIZE_LIMIT_EXCEEDED);
        }

        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new AppException(AppErrorCode.FILE_FORMAT_NOT_ACCEPTABLE);
        }
    }
}