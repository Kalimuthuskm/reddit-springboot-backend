-- Create file_uploads table to store file metadata
CREATE TABLE IF NOT EXISTS file_uploads
(
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT       NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    original_name   VARCHAR(255) NOT NULL,
    stored_name     VARCHAR(255) NOT NULL UNIQUE,
    file_size       BIGINT       NOT NULL,
    content_type    VARCHAR(100),
    s3_key          TEXT         NOT NULL,
    s3_url          TEXT         NOT NULL,
    description     TEXT,
    uploaded_at     TIMESTAMP DEFAULT now(),

    -- Optional: link to posts if files are post attachments
    post_id         BIGINT REFERENCES posts (id) ON DELETE CASCADE,

    CONSTRAINT valid_file_size CHECK (file_size > 0)
);

-- Index for faster queries
CREATE INDEX idx_file_uploads_user_id ON file_uploads(user_id);
CREATE INDEX idx_file_uploads_post_id ON file_uploads(post_id);
CREATE INDEX idx_file_uploads_uploaded_at ON file_uploads(uploaded_at DESC);