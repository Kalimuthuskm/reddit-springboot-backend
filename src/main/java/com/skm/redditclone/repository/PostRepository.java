package com.skm.redditclone.repository;

import com.skm.redditclone.model.Post;
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

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
@RequiredArgsConstructor
public class PostRepository {

    private final DSLContext dsl;

    public Post createPost(Post post) {
        Record record = dsl.insertInto(DSL.table("posts")).columns(field("title"), field("content"), field("user_id"), field("created_at"), field("updated_at")).values(post.getTitle(), post.getContent(), post.getUser_id(), Instant.now(), Instant.now()).returning(field("id", Long.class), field("title", String.class), field("created_at", Instant.class), field("updated_at", Instant.class)).fetchOne();

        post.setId(record.get(field("id", Long.class)));
        post.setCreatedAt(record.get(field("created_at", Instant.class)));
        post.setUpdatedAt(record.get(field("updated_at", Instant.class)));

        return post;
    }

    public Post findById(Long id) {
        Record r = dsl.select().from("posts").where(field("id").eq(id)).fetchOne();

        if (r == null) {
            throw new RuntimeException("Post Not Found");
        }

        return new Post(r.get(field("id", Long.class)), r.get(field("title", String.class)), r.get(field("content", String.class)), r.get(field("user_id", Long.class)), r.get(field("created_at", Timestamp.class)).toInstant(), r.get(field("updated_at", Timestamp.class)).toInstant());
    }
    public boolean existsById(Long id) {
        Integer count =dsl.selectCount()
                .from("posts")
                .where(field("id", Long.class).eq(id))
                .fetchOne(0, Integer.class);
        return count != null && count > 0;
    }

    public Page<Post> findAll(Pageable pageable) {

        List<Post> posts = dsl.select().from("posts").orderBy(DSL.field("created_at").desc()).limit(pageable.getPageSize()).offset((int) pageable.getOffset()).fetch().map(r -> new Post(r.get("id", Long.class), r.get("title", String.class), r.get("content", String.class), r.get("user_id", Long.class), r.get("created_at", Instant.class), r.get("updated_at", Instant.class)));

        // Total number of posts in the table (for total pages)
        int total = dsl.fetchCount(DSL.table("posts"));

        return new PageImpl<>(posts, pageable, total);
    }

//    public List<Post> findByUserId(Long userId) {
//        return dsl.select().from("posts").where(field("user_id").eq(userId)).orderBy(field("created_at").desc()).fetch().map(r -> new Post(r.get("id", Long.class), r.get("title", String.class), r.get("content", String.class), r.get("user_id", Long.class), r.get("created_at", Instant.class), r.get("updated_at", Instant.class)));
//    }


    public boolean updatePost(Long postId, String title, String content) {

        int rows = dsl.update(DSL.table("posts")).set(field("title"), title).set(field("content"), content).set(field("updated_at"), Instant.now()).where(field("id").eq(postId)).execute();

        return rows > 0;
    }


    public boolean deletePost(Long postId) {
        int rows = dsl.deleteFrom(DSL.table("posts")).where(DSL.field("id").eq(postId)).execute();

        return rows > 0;
    }

    public int bulkDeletePosts(List<Long> ids) {
        return dsl.deleteFrom(table("posts"))
                .where(DSL.field("id").in(ids))
                .execute();
    }

    public List<Long> findExistingIds(List<Long> ids) {
        return dsl.select(field("id", Long.class))
                .from(table("posts"))
                .where(field("id").in(ids))
                .fetchInto(Long.class);
    }


}
