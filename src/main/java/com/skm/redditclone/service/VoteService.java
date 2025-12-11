package com.skm.redditclone.service;

import com.skm.redditclone.dto.request.VoteRequest;
import com.skm.redditclone.model.Vote;
import com.skm.redditclone.repository.VoteRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Validated
@RequiredArgsConstructor
@Service
public class VoteService {
    private final VoteRepository voteRepository;

    @Transactional
    @CacheEvict(value = "votes" ,key="#postId")
    public int vote(@NonNull @Positive Long postId,
                    @Valid @NotNull VoteRequest request,
                    Authentication authentication) {

        int value = request.voteValue();
        String username = authentication.getName();

        var existing = voteRepository.getUserVote(postId, username);

        if (existing.isEmpty()) {
            Vote vote = new Vote(null, postId, username, value, null);
            voteRepository.addVote(vote);
        } else {
            Vote vote = existing.get();

            if (vote.getVoteValue() == value) {
                voteRepository.deleteVote(vote.getId());
            } else {
                voteRepository.updateVote(vote.getId(), value);
            }
        }
        return voteRepository.countVotes(postId);
    }
    @Cacheable(value = "votes",key = "#postId")
    public int getVoteCount(@NotNull @Positive Long postId) {
        return voteRepository.countVotes(postId);
    }
}
