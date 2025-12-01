package com.skm.redditclone.repository;

import com.skm.Tables;
import com.skm.redditclone.model.FileUpload;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FileUploadRepository {

    private final DSLContext dsl;

    public FileUpload save(FileUpload fileUpload) {
        Record record = dsl.insertInto(Tables.FILE_UPLOADS)
                .columns(
                        Tables.FILE_UPLOADS.USER_ID,
                        Tables.FILE_UPLOADS.ORIGINAL_NAME,
                        Tables.FILE_UPLOADS.STORED_NAME,
                        Tables.FILE_UPLOADS.FILE_SIZE,
                        Tables.FILE_UPLOADS.CONTENT_TYPE,
                        Tables.FILE_UPLOADS.S3_KEY,
                        Tables.FILE_UPLOADS.S3_URL,
                        Tables.FILE_UPLOADS.DESCRIPTION,
                        Tables.FILE_UPLOADS.POST_ID,
                        Tables.FILE_UPLOADS.UPLOADED_AT
                )
                .values(
                        fileUpload.getUserId(),
                        fileUpload.getOriginalName(),
                        fileUpload.getStoredName(),
                        fileUpload.getFileSize(),
                        fileUpload.getContentType(),
                        fileUpload.getS3Key(),
                        fileUpload.getS3Url(),
                        fileUpload.getDescription(),
                        fileUpload.getPostId(),
                        LocalDateTime.now()
                )
                .returning(
                        Tables.FILE_UPLOADS.ID,
                        Tables.FILE_UPLOADS.UPLOADED_AT
                )
                .fetchOne();

        assert record != null;
        fileUpload.setId(record.get(Tables.FILE_UPLOADS.ID, Long.class));
        fileUpload.setUploadedAt(record.get(Tables.FILE_UPLOADS.UPLOADED_AT, Instant.class));

        return fileUpload;
    }

    public Optional<FileUpload> findById(Long id) {
        Record record = dsl.select()
                .from(Tables.FILE_UPLOADS)
                .where(Tables.FILE_UPLOADS.ID.eq(id))
                .fetchOne();

        if (record == null) {
            return Optional.empty();
        }

        return Optional.of(mapToFileUpload(record));
    }

    public Optional<FileUpload> findByStoredName(String storedName) {
        Record record = dsl.select()
                .from(Tables.FILE_UPLOADS)
                .where(Tables.FILE_UPLOADS.STORED_NAME.eq(storedName))
                .fetchOne();

        if (record == null) {
            return Optional.empty();
        }

        return Optional.of(mapToFileUpload(record));
    }

    public Page<FileUpload> findByUserId(Long userId, Pageable pageable) {
        List<FileUpload> files = dsl.select()
                .from(Tables.FILE_UPLOADS)
                .where(Tables.FILE_UPLOADS.USER_ID.eq(userId))
                .orderBy(Tables.FILE_UPLOADS.UPLOADED_AT.desc())
                .limit(pageable.getPageSize())
                .offset((int) pageable.getOffset())
                .fetch()
                .map(this::mapToFileUpload);

        int total = dsl.fetchCount(
                dsl.selectFrom(Tables.FILE_UPLOADS)
                        .where(Tables.FILE_UPLOADS.USER_ID.eq(userId))
        );

        return new PageImpl<>(files, pageable, total);
    }

    public List<FileUpload> findByPostId(Long postId) {
        return dsl.select()
                .from(Tables.FILE_UPLOADS)
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

    private FileUpload mapToFileUpload(Record record) {
        Timestamp uploadedTs = record.get(Tables.FILE_UPLOADS.UPLOADED_AT, Timestamp.class);

        return new FileUpload(
                record.get(Tables.FILE_UPLOADS.ID, Long.class),
                record.get(Tables.FILE_UPLOADS.USER_ID, Long.class),
                record.get(Tables.FILE_UPLOADS.ORIGINAL_NAME, String.class),
                record.get(Tables.FILE_UPLOADS.STORED_NAME, String.class),
                record.get(Tables.FILE_UPLOADS.FILE_SIZE, Long.class),
                record.get(Tables.FILE_UPLOADS.CONTENT_TYPE, String.class),
                record.get(Tables.FILE_UPLOADS.S3_KEY, String.class),
                record.get(Tables.FILE_UPLOADS.S3_URL, String.class),
                record.get(Tables.FILE_UPLOADS.DESCRIPTION, String.class),
                uploadedTs != null ? uploadedTs.toInstant() : null,
                record.get(Tables.FILE_UPLOADS.POST_ID, Long.class)
        );
    }
}