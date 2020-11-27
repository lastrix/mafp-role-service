CREATE SCHEMA IF NOT EXISTS role_service;
------------------------------------------------------------------------------------------------------------------------
CREATE SEQUENCE role_service.role_seq START 1 INCREMENT BY 1;
CREATE TABLE role_service.role
(
    role_id     INT           NOT NULL DEFAULT nextval('role_service.role_seq')::regclass,
    name        VARCHAR(128)  NOT NULL,
    description VARCHAR(2048) NOT NULL,
    CONSTRAINT role_pk PRIMARY KEY (role_id)
);

CREATE UNIQUE INDEX role_name_uk ON role_service.role (lower(name));
CREATE INDEX role_name_idx ON role_service.role (name);

------------------------------------------------------------------------------------------------------------------------
CREATE TABLE role_service.user_role
(
    user_id    UUID    NOT NULL,
    role_id    INT     NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT user_role_pk PRIMARY KEY (user_id, role_id),
    CONSTRAINT user_role_role_id_fk FOREIGN KEY (role_id) REFERENCES role_service.role (role_id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX user_role_role_id_idx ON role_service.user_role (role_id);
CREATE INDEX user_role_main_idx ON role_service.user_role (user_id, role_id, is_deleted);
