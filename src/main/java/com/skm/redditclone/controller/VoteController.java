package com.skm.redditclone.controller;

import com.skm.redditclone.dto.request.VoteRequest;
import com.skm.redditclone.service.VoteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RequestMapping("/{postId}/votes")
@RestController
public class VoteController {

    private final VoteService voteService;

    @PostMapping("")
    public ResponseEntity<?> addVote(
            @PathVariable @NotNull @Positive  Long postId,
            @RequestBody @Valid @NotNull VoteRequest request,
            Authentication auth
    ) {
            int count = voteService.vote(postId, request, auth);
            return ResponseEntity.ok(count);

    }

    @GetMapping("")
    public ResponseEntity<?> getVote(
            @PathVariable @NotNull @Positive Long postId
    ) {
            int count = voteService.getVoteCount(postId);
            return ResponseEntity.ok(count);
    }
}
