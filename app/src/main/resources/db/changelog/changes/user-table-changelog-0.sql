--liquibase formatted sql

--changeset dongp:0
CREATE TABLE "user" (
    "name"          TEXT PRIMARY KEY,
    active          BOOL NOT NULL DEFAULT true,
    "token"         TEXT NOT NULL,
    roles           TEXT NOT NULL,
    church_id       UUID NULL,
    created_date    TIMESTAMP NOT NULL,
    created_by      TEXT NOT NULL,
    updated_date    TIMESTAMP NOT NULL,
    updated_by      TEXT NOT NULL
);

--changeset dongp:1
INSERT INTO "user" (active,"name","token",roles,church_id,created_date,created_by,updated_date,updated_by)
VALUES (true,'admin','$2a$10$RNreel.K5lvsCyuQGm8qDeyEbeQ94QMuydiX5vkKZ9qVaV8OQLJ6q','SUPER',NULL,current_timestamp,'SYS',current_timestamp,'SYS');

--changeset dongp:2
ALTER TABLE "user" ADD COLUMN member_id UUID;
ALTER TABLE "user" ADD CONSTRAINT fk_user_member_id FOREIGN KEY (member_id) REFERENCES "member"(id);

