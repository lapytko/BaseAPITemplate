ALTER TABLE users
    ADD account_non_expired BOOLEAN;

ALTER TABLE users
    ADD enabled BOOLEAN;

ALTER TABLE users
    ADD locked BOOLEAN;

ALTER TABLE users
    ALTER COLUMN account_non_expired SET DEFAULT FALSE ;

ALTER TABLE users
    ALTER COLUMN enabled SET DEFAULT TRUE ;

ALTER TABLE users
    ALTER COLUMN locked SET DEFAULT FALSE ;

UPDATE users
SET account_non_expired = TRUE, -- в зависимости от ваших потребностей
    enabled = TRUE, -- или FALSE, в зависимости от ваших потребностей
    locked = FALSE; -- или TRUE, в зависимости от ваших потребностей