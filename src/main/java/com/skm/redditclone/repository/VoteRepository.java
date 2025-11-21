package com.skm.redditclone.repository;

import com.skm.redditclone.model.Vote;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import static org.jooq.impl.DSL.*;

@Repository
@RequiredArgsConstructor
public class VoteRepository {

    private final DSLContext dsl;


    public Optional<Vote> getUserVote(Long postId, String username) {

        Record r = dsl.select()
                .from(table("votes"))
                .where(field("post_id", Long.class).eq(postId)
                        .and(field("username",String.class).eq(username)))
                .fetchOne();

        if (r == null) return Optional.empty();

        Timestamp ts = r.get("created_at", Timestamp.class);
        Instant createdAt = ts != null ? ts.toInstant() : null;

        Vote vote = new Vote(
                r.get("id", Long.class),
                r.get("post_id", Long.class),
                r.get("username", String.class),
                r.get("vote_value", Integer.class),
                createdAt
        );

        return Optional.of(vote);
    }

    public Vote addVote(Vote vote) {

        Record r = dsl.insertInto(table("votes"))
                .columns(
                        field("post_id"),
                        field("username"),
                        field("vote_value"),
                        field("created_at")
                )
                .values(
                        vote.getPostId(),
                        vote.getUsername(),
                        vote.getVoteValue(),
                        Instant.now()
                )
                .returning(
                        field("id"),
                        field("created_at")
                )
                .fetchOne();

        vote.setId(r.get("id", Long.class));
        Timestamp ts = r.get("created_at", Timestamp.class);
        vote.setCreatedAt(ts != null ? ts.toInstant() : null);

        return vote;
    }


    public boolean updateVote(Long id, int value) {
        int rows = dsl.update(table("votes"))
                .set(field("vote_value"), value)
                .where(field("id").eq(id))
                .execute();
        return rows > 0;
    }


    public boolean deleteVote(Long id) {
        int rows = dsl.deleteFrom(table("votes"))
                .where(field("id").eq(id))
                .execute();
        return rows > 0;
    }


    public int countVotes(Long postId) {
        Integer sum = dsl
                .select(sum(field("vote_value", Integer.class)))
                .from("votes")
                .where(field("post_id").eq(postId))
                .fetchOneInto(Integer.class);

        return sum == null ? 0 : sum;
    }
}
