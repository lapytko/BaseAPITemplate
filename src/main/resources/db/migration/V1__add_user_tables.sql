CREATE TABLE login_history
(
    id             UUID    NOT NULL,
    creted         TIMESTAMP WITHOUT TIME ZONE,
    updated        TIMESTAMP WITHOUT TIME ZONE,
    user_id        UUID    NOT NULL,
    was_successful BOOLEAN NOT NULL,
    CONSTRAINT pk_login_history PRIMARY KEY (id)
);

CREATE TABLE user_data
(
    id      UUID NOT NULL,
    creted  TIMESTAMP WITHOUT TIME ZONE,
    updated TIMESTAMP WITHOUT TIME ZONE,
    name    VARCHAR(255),
    surname VARCHAR(255),
    email   VARCHAR(255),
    phone   VARCHAR(255),
    CONSTRAINT pk_user_data PRIMARY KEY (id)
);

CREATE TABLE users
(
    id               UUID         NOT NULL,
    creted           TIMESTAMP WITHOUT TIME ZONE,
    updated          TIMESTAMP WITHOUT TIME ZONE,
    username         VARCHAR(255) NOT NULL,
    password         VARCHAR(255) NOT NULL,
    personal_data_id UUID,
    CONSTRAINT pk_users PRIMARY KEY (id)
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