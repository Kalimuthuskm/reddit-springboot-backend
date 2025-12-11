-- Add OAuth fields to users table
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS oauth_provider VARCHAR(50),
    ADD COLUMN IF NOT EXISTS oauth_id VARCHAR(255),
    ADD COLUMN IF NOT EXISTS email VARCHAR(255),
    ADD COLUMN IF NOT EXISTS profile_image_url TEXT;

-- Make password nullable for OAuth users
ALTER TABLE users
    ALTER COLUMN password DROP NOT NULL;

-- Add unique constraint on oauth_provider + oauth_id combination
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_oauth
    ON users(oauth_provider, oauth_id)
    WHERE oauth_provider IS NOT NULL;

-- Add index on email
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);