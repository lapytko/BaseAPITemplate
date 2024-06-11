CREATE TABLE authorities
(
    authority_name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_authorities PRIMARY KEY (authority_name)
);



CREATE TABLE users_authorities
(
    user_id                    UUID         NOT NULL,
    authorities_authority_name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_users_authorities PRIMARY KEY (user_id, authorities_authority_name)
);

ALTER TABLE users_authorities
    ADD CONSTRAINT fk_useaut_on_authority FOREIGN KEY (authorities_authority_name) REFERENCES authorities (authority_name);

ALTER TABLE users_authorities
    ADD CONSTRAINT fk_useaut_on_user FOREIGN KEY (user_id) REFERENCES users (id);