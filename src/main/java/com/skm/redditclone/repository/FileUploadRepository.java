package com.skm.redditclone.repository;

import com.skm.Tables;
import com.skm.redditclone.model.FileUpload;
import com.skm.tables.records.FileUploadsRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Repository
public class FileUploadRepository {

    private final DSLContext dsl;

    public FileUpload save(FileUpload fileUpload) {
        FileUploadsRecord record = dsl.newRecord(Tables.FILE_UPLOADS);
        record.setUserId(fileUpload.getUserId());
        record.setOriginalName(fileUpload.getOriginalName());
        record.setStoredName(fileUpload.getStoredName());
        record.setFileSize(fileUpload.getFileSize());
        record.setContentType(fileUpload.getContentType());
        record.setS3Key(fileUpload.getS3Key());
        record.setS3Url(fileUpload.getS3Url());
        record.setDescription(fileUpload.getDescription());
        record.setUploadedAt(LocalDateTime.now());
        record.store();
        fileUpload.setId(record.getId());
        fileUpload.setUploadedAt(record.getUploadedAt());
        return fileUpload;
    }

    public Optional<FileUpload> findById(Long id) {
        return dsl.selectFrom(Tables.FILE_UPLOADS)
                .where(Tables.FILE_UPLOADS.ID.eq(id))
                .fetchOptional()
                .map(this::mapToFileUpload);
    }

    public Optional<FileUpload> findByStoredName(String storedName) {
        Optional<FileUpload> fileUpload = dsl.selectFrom(Tables.FILE_UPLOADS)
                .where(Tables.FILE_UPLOADS.STORED_NAME.eq(storedName))
                .fetchOptional()
                .map(this::mapToFileUpload);
        return fileUpload;

    }

    public Page<FileUpload> findByUserId(Long userId,
                                         Pageable pageable) {
        Condition condition = Tables.FILE_UPLOADS.USER_ID.eq(userId);
        List<FileUpload> fileUploadList = dsl.selectFrom(Tables.FILE_UPLOADS)
                .where(condition)
                .orderBy(Tables.FILE_UPLOADS.UPLOADED_AT.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch()
                .map(this::mapToFileUpload);

        int total = dsl.fetchCount(
                dsl.selectFrom(Tables.FILE_UPLOADS)
                .where(condition)
        );
        return new PageImpl<>(fileUploadList,pageable, total);
    }

    public List<FileUpload> findByPostId(Long postId) {
        return dsl.selectFrom(Tables.FILE_UPLOADS)
                .where(Tables.FILE_UPLOADS.POST_ID.eq(postId))
                .fetch()
                .map(this::mapToFileUpload);
    }

    public boolean deleteById(Long id) {
        int rows = dsl.deleteFrom(Tables.FILE_UPLOADS)
                .where(Tables.FILE_UPLOADS.ID.eq(id))
                .execute();
        return rows > 0;
    }

    private FileUpload mapToFileUpload(FileUploadsRecord record) {
        return new FileUpload(
                record.getId(),
                record.getUserId(),
                record.getOriginalName(),
                record.getStoredName(),
                record.getFileSize(),
                record.getContentType(),
                record.getS3Key(),
                record.getS3Url(),
                record.getDescription(),
                record.getUploadedAt(),
                record.getPostId()
        );
    }
}