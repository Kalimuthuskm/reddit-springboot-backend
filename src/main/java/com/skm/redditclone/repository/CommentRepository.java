package com.skm.redditclone.repository;

import com.skm.Tables;
import com.skm.redditclone.model.Comment;
import com.skm.tables.Comments;
import com.skm.tables.records.CommentsRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static com.skm.Tables.COMMENTS;
import static java.time.Instant.now;
import static org.jooq.impl.DSL.field;

@Repository
@RequiredArgsConstructor
public class CommentRepository {

    private final DSLContext dsl;

    public Comment createComment(Comment comment) {
        Timestamp now = Timestamp.from(now());
        CommentsRecord record = dsl.newRecord(Tables.COMMENTS);
        record.setPostId(comment.getPostId());
        record.setUsername(comment.getUsername());
        record.setComment(comment.getComment());
        record.setCreatedAt(now.toLocalDateTime());
        record.setUpdatedAt(now.toLocalDateTime());
        record.store();
        comment.setId(record.get("id", Long.class));
        comment.setCreatedAt(record.get("created_at", Timestamp.class).toInstant());
        comment.setUpdatedAt(record.get("updated_at", Timestamp.class).toInstant());
        return comment;
    }

    public Optional<Comment> getCommentById(Long id) {
        Record r = dsl.selectFrom(COMMENTS)
                .where(field(Comments.COMMENTS.ID).eq(id))
                .fetchOne();

        if (r == null) return Optional.empty();

        Comment c = new Comment(
                r.get("id", Long.class),
                r.get("post_id", Long.class),
                r.get("username", String.class),
                r.get("comment", String.class),
                r.get("created_at", Timestamp.class).toInstant(),
                r.get("updated_at", Timestamp.class).toInstant());
        return Optional.of(c);
    }

    public Page<Comment> getCommentsByPostId(Long postId,
                                             Pageable pageable) {

        List<Comment> comments = dsl.selectFrom(COMMENTS)
                .where(field(COMMENTS.POST_ID).eq(postId))
                .orderBy(field(COMMENTS.CREATED_AT).asc())
                .limit(pageable.getPageSize())
                .offset((int) pageable.getOffset())
                .fetch()
                .map(r -> new Comment(
                                r.get("id", Long.class),
                                r.get("post_id", Long.class),
                                r.get("username", String.class),
                                r.get("comment", String.class),
                                r.get("created_at", Timestamp.class).toInstant(),
                                r.get("updated_at", Timestamp.class).toInstant()));

        int total = dsl.fetchCount(
                dsl.selectFrom(COMMENTS)
                        .where(field(COMMENTS.POST_ID).eq(postId))
        );
        return new PageImpl<>(comments, pageable, total);
    }

    public boolean updateComment(Long id,
                                 String comment) {
        int rows = dsl.update(COMMENTS)
                .set(field(COMMENTS.COMMENT), comment)
                .set(field(COMMENTS.UPDATED_AT), LocalDateTime.now())
                .where(field(COMMENTS.ID).eq(id))
                .execute();
        return rows > 0;
    }

    public boolean deleteComment(Long id) {
        int rows = dsl.deleteFrom(COMMENTS)
                .where(field(COMMENTS.ID).eq(id))
                .execute();
        return rows > 0;
    }

    public int bulkDeletePosts(List<Long> ids) {
        return dsl.deleteFrom(COMMENTS)
                .where(DSL.field(COMMENTS.ID).in(ids))
                .execute();
    }

    public List<Long> findExistingIds(List<Long> ids) {
        return dsl.select(COMMENTS.ID)
                .from(COMMENTS)
                .where(field(COMMENTS.ID).in(ids))
                .fetchInto(Long.class);
    }
}