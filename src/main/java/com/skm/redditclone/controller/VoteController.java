package com.skm.redditclone.controller;

import com.skm.redditclone.dto.ErrorResponse;
import com.skm.redditclone.dto.VoteRequest;
import com.skm.redditclone.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/{postId}/votes")
@RestController
public class VoteController {
    private final VoteService voteService;

    @PostMapping("")
    public ResponseEntity<?> addVote(
            @PathVariable Long postId,
            @RequestBody VoteRequest request,
            Authentication auth
    ) {

            int count = voteService.vote(postId, request, auth);
            return ResponseEntity.ok(count);

    }

    @GetMapping("")
    public ResponseEntity<?> getVote(
            @PathVariable Long postId
    ) {

            int count = voteService.getVoteCount(postId);
            return ResponseEntity.ok(count);

    }
}
