package com.skm.redditclone.service;

import com.skm.redditclone.dto.VoteRequest;
import com.skm.redditclone.model.Vote;
import com.skm.redditclone.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VoteService {
    private final VoteRepository voteRepository;

    public int vote(Long postId, VoteRequest request, Authentication authentication) {

        int value = request.getVoteValue();
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

    public int getVoteCount(Long postId) {
        return voteRepository.countVotes(postId);
    }

}
