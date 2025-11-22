package com.skm.redditclone.repository;

import com.skm.Tables;
import com.skm.redditclone.model.Vote;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.jooq.impl.DSL.*;

@Repository
@RequiredArgsConstructor
public class VoteRepository {

    private final DSLContext dsl;


    public Optional<Vote> getUserVote(Long postId, String username) {

        Record r = dsl.select()
                .from(Tables.VOTES)
                .where(Tables.VOTES.POST_ID.eq(postId)
                        .and(Tables.VOTES.USER_NAME.eq(username)))
                .fetchOne();

        if (r == null) return Optional.empty();

        Timestamp ts = r.get("created_at", Timestamp.class);
        Instant createdAt = ts != null ? ts.toInstant() : null;

        Vote vote = new Vote(
                r.get(Tables.VOTES.ID, Long.class),
                r.get(Tables.VOTES.POST_ID, Long.class),
                r.get(Tables.VOTES.USER_NAME, String.class),
                r.get(Tables.VOTES.VOTE_VALUE, Integer.class),
                createdAt
        );

        return Optional.of(vote);
    }

    public Vote addVote(Vote vote) {

        Record r = dsl.insertInto(Tables.VOTES)
                .columns(
                        Tables.VOTES.POST_ID,
                        Tables.VOTES.USER_NAME,
                        Tables.VOTES.VOTE_VALUE,
                        Tables.VOTES.CREATED_AT
                ).values(
                        vote.getPostId(),
                        vote.getUsername(),
                        vote.getVoteValue(),
                        LocalDateTime.now()
                )
                .returning(
                        Tables.VOTES.POST_ID,
                        Tables.VOTES.CREATED_AT
                )
                .fetchOne();

        vote.setId(r.get(Tables.VOTES.ID, Long.class));
        Timestamp ts = r.get(Tables.VOTES.CREATED_AT, Timestamp.class);
        vote.setCreatedAt(ts != null ? ts.toInstant() : null);

        return vote;
    }


    public boolean updateVote(Long id, int value) {
        int rows = dsl.update(Tables.VOTES)
                .set(Tables.VOTES.VOTE_VALUE, value)
                .where(Tables.VOTES.ID.eq(id))
                .execute();
        return rows > 0;
    }


    public boolean deleteVote(Long id) {
        int rows = dsl.deleteFrom(Tables.VOTES)
                .where(Tables.VOTES.ID.eq(id))
                .execute();
        return rows > 0;
    }


    public int countVotes(Long postId) {
        Integer sum = dsl
                .select(sum(Tables.VOTES.VOTE_VALUE))
                .from(Tables.VOTES)
                .where(Tables.VOTES.POST_ID.eq(postId))
                .fetchOneInto(Integer.class);

        return sum == null ? 0 : sum;
    }
}
