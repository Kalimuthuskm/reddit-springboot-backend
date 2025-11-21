CREATE TABLE IF NOT EXISTS votes
(
    id         BIGSERIAL PRIMARY KEY,
    post_id    BIGINT NOT NULL REFERENCES posts (id) ON DELETE CASCADE,
    user_name  TEXT   NOT NULL REFERENCES users (username),
    vote_value INT    NOT NULL, -- +1 = upvote, -1 = downvote
    created_at TIMESTAMP DEFAULT now(),
    UNIQUE (post_id, user_name)
);
