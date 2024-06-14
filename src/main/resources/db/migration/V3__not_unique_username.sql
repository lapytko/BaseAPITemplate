ALTER TABLE users
    ALTER COLUMN account_non_expired SET NOT NULL;

ALTER TABLE users
    ALTER COLUMN enabled SET NOT NULL;

ALTER TABLE users
    ALTER COLUMN locked SET NOT NULL;
ALTER TABLE users
    DROP CONSTRAINT uc_users_username;