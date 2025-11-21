package com.skm.redditclone.repository;

import com.skm.redditclone.model.Comment;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
@RequiredArgsConstructor
public class CommentRepository {

    private final DSLContext dsl;


    public Comment createComment(Comment comment) {
        Timestamp now = Timestamp.from(Instant.now());

        Record r = dsl.insertInto(table("comments")).columns(field("post_id"), field("username"), field("comment"), field("created_at"), field("updated_at")).values(comment.getPostId(), comment.getUsername(), comment.getComment(), now, now).returning(field("id"), field("created_at"), field("updated_at")).fetchOne();


        comment.setId(r.get("id", Long.class));
        comment.setCreatedAt(r.get("created_at", Timestamp.class).toInstant());
        comment.setUpdatedAt(r.get("updated_at", Timestamp.class).toInstant());

        return comment;
    }


    public Optional<Comment> getCommentById(Long id) {

        Record r = dsl.select().from("comments").where(field("id").eq(id)).fetchOne();

        if (r == null) return Optional.empty();

        Comment c = new Comment(r.get("id", Long.class), r.get("post_id", Long.class), r.get("username", String.class), r.get("comment", String.class), r.get("created_at", Timestamp.class).toInstant(), r.get("updated_at", Timestamp.class).toInstant());

        return Optional.of(c);
    }


    public Page<Comment> getCommentsByPostId(Long postId, Pageable pageable) {

        List<Comment> comments = dsl.select().from("comments").where(field("post_id").eq(postId)).orderBy(field("created_at").asc()).limit(pageable.getPageSize()).offset((int) pageable.getOffset()).fetch().map(r -> new Comment(r.get("id", Long.class), r.get("post_id", Long.class), r.get("username", String.class), r.get("comment", String.class), r.get("created_at", Timestamp.class).toInstant(), r.get("updated_at", Timestamp.class).toInstant()));

        int total = dsl.fetchCount(dsl.selectFrom("comments").where(field("post_id").eq(postId)));

        return new PageImpl<>(comments, pageable, total);
    }

    public boolean updateComment(Long id, String comment) {
        int rows = dsl.update(table("comments"))
                .set(field("comment"), comment).
                set(field("updated_at"), Instant.now())
                .where(field("id").eq(id)).execute();
        return rows > 0;
    }


    public boolean deleteComment(Long id) {

        int rows = dsl.deleteFrom(table("comments")).where(field("id").eq(id)).execute();

        return rows > 0;
    }

    public int bulkDeletePosts(List<Long> ids) {
        return dsl.deleteFrom(table("comments"))
                .where(DSL.field("id").in(ids))
                .execute();
    }

    public List<Long> findExistingIds(List<Long> ids) {
        return dsl.select(field("id", Long.class))
                .from(table("comments"))
                .where(field("id").in(ids))
                .fetchInto(Long.class);
    }
}

