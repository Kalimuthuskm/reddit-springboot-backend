package com.skm.redditclone.repository;

import com.skm.Tables;
import com.skm.redditclone.model.Post;
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

import static org.jooq.impl.DSL.field;

@Repository
@RequiredArgsConstructor
public class PostRepository {

    private final DSLContext dsl;

    public Post createPost(Post post) {
        Record record = dsl
                .insertInto(Tables.POSTS)
                .columns(Tables.POSTS.TITLE,
                        Tables.POSTS.CONTENT,
                        Tables.POSTS.USER_ID,
                        Tables.POSTS.CREATED_AT,
                        Tables.POSTS.UPDATED_AT)
                .values(post.getTitle(),
                        post.getContent(),
                        post.getUser_id(),
                        LocalDateTime.now(),
                        LocalDateTime.now()).
                returning(
                        Tables.POSTS.ID,
                        Tables.POSTS.TITLE,
                        Tables.POSTS.CREATED_AT,
                        Tables.POSTS.UPDATED_AT).fetchOne();

        assert record != null;
        post.setId(record.get(Tables.POSTS.ID,Long.class));
        post.setCreatedAt(record.get(Tables.POSTS.CREATED_AT,Instant.class));
        post.setUpdatedAt(record.get(Tables.POSTS.UPDATED_AT,Instant.class));
        return post;
    }

    public Post findById(Long id) {
        Record r = dsl
                .select()
                .from(Tables.POSTS)
                .where(Tables.POSTS.ID.eq(id)).fetchOne();

        if (r == null) {
            throw new RuntimeException("Post Not Found");
        }

        return new Post(
                r.get(Tables.POSTS.ID, Long.class),
                r.get(Tables.POSTS.TITLE, String.class),
                r.get(Tables.POSTS.CONTENT, String.class),
                r.get(Tables.POSTS.USER_ID, Long.class),
                r.get(Tables.POSTS.CREATED_AT, Timestamp.class).toInstant(),
                r.get(Tables.POSTS.UPDATED_AT, Timestamp.class).toInstant()
        );
    }

    public boolean existsById(Long id) {
        Integer count = dsl.selectCount()
                .from(Tables.POSTS)
                .where(Tables.POSTS.ID.eq(id))
                .fetchOne(0, Integer.class);
        return count != null && count > 0;
    }

    public Page<Post> findAll(Pageable pageable) {

        List<Post> posts = dsl.select()
                .from(Tables.POSTS)
                .orderBy(Tables.POSTS.CREATED_AT.desc())
                .limit(pageable.getPageSize())
                .offset((int) pageable.getOffset())
                .fetch().map(
                        r -> new Post(r.get(Tables.POSTS.ID, Long.class),
                                r.get(Tables.POSTS.TITLE, String.class),
                                r.get(Tables.POSTS.CONTENT, String.class),
                                r.get(Tables.POSTS.USER_ID, Long.class),
                                r.get(Tables.POSTS.CREATED_AT, Instant.class),
                                r.get(Tables.POSTS.UPDATED_AT, Instant.class)));

        // Total number of posts in the table (for total pages)
        int total = dsl.fetchCount(Tables.POSTS);

        return new PageImpl<>(posts, pageable, total);
    }


    public boolean updatePost(Long postId, String title, String content) {

        int rows = dsl.update(Tables.POSTS)
                .set(Tables.POSTS.TITLE, title)
                .set(Tables.POSTS.CONTENT, content)
                .set(Tables.POSTS.UPDATED_AT, LocalDateTime.now())
                .where(Tables.POSTS.ID.eq(postId))
                .execute();

        return rows > 0;
    }


    public boolean deletePost(Long postId) {
        int rows = dsl.deleteFrom(Tables.POSTS)
                .where(Tables.POSTS.ID.eq(postId))
                .execute();

        return rows > 0;
    }

    public int bulkDeletePosts(List<Long> ids) {
        return dsl.deleteFrom(Tables.POSTS)
                .where(Tables.POSTS.ID.in(ids))
                .execute();
    }

    public List<Long> findExistingIds(List<Long> ids) {
        return dsl.select(Tables.POSTS.ID)
                .from(Tables.POSTS)
                .where(Tables.POSTS.ID.in(ids))
                .fetchInto(Long.class);
    }
}
