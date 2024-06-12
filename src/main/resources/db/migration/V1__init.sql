CREATE TABLE authorities
(
    authority_name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_authorities PRIMARY KEY (authority_name)
);

CREATE TABLE login_history
(
    id             UUID    NOT NULL,
    created        TIMESTAMP WITHOUT TIME ZONE,
    updated        TIMESTAMP WITHOUT TIME ZONE,
    is_deleted     BOOLEAN NOT NULL,
    user_id        UUID    NOT NULL,
    was_successful BOOLEAN NOT NULL,
    CONSTRAINT pk_login_history PRIMARY KEY (id)
);

CREATE TABLE user_data
(
    id         UUID         NOT NULL,
    created    TIMESTAMP WITHOUT TIME ZONE,
    updated    TIMESTAMP WITHOUT TIME ZONE,
    is_deleted BOOLEAN      NOT NULL,
    name       VARCHAR(255),
    surname    VARCHAR(255),
    email      VARCHAR(255) NOT NULL,
    phone      VARCHAR(255),
    CONSTRAINT pk_user_data PRIMARY KEY (id)
);

CREATE TABLE users
(
    id               UUID         NOT NULL,
    created          TIMESTAMP WITHOUT TIME ZONE,
    updated          TIMESTAMP WITHOUT TIME ZONE,
    is_deleted       BOOLEAN      NOT NULL,
    username         VARCHAR(255) NOT NULL,
    password         VARCHAR(255) NOT NULL,
    personal_data_id UUID,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE users_authorities
(
    user_id                    UUID         NOT NULL,
    authorities_authority_name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_users_authorities PRIMARY KEY (user_id, authorities_authority_name)
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_personal_data UNIQUE (personal_data_id);

ALTER TABLE users
    ADD CONSTRAINT uc_users_username UNIQUE (username);

ALTER TABLE login_history
    ADD CONSTRAINT FK_LOGIN_HISTORY_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);

CREATE INDEX IDX_LOGIN_HISTORY_ON_USER ON login_history (user_id);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_PERSONAL_DATA FOREIGN KEY (personal_data_id) REFERENCES user_data (id);

CREATE UNIQUE INDEX IDX_USERS_ON_PERSONAL_DATA ON users (personal_data_id);

ALTER TABLE users_authorities
    ADD CONSTRAINT fk_useaut_on_authority FOREIGN KEY (authorities_authority_name) REFERENCES authorities (authority_name);

ALTER TABLE users_authorities
    ADD CONSTRAINT fk_useaut_on_user FOREIGN KEY (user_id) REFERENCES users (id);