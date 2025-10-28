CREATE TABLE IF NOT EXISTS admin
(
    id         TEXT PRIMARY KEY NOT NULL,
    identifier TEXT,
    password   TEXT,
    creator    TEXT,
    created_at DATE,
    updated_at DATE,
    UNIQUE (identifier)
);

CREATE TABLE IF NOT EXISTS config
(
    id         TEXT PRIMARY KEY NOT NULL,
    key        TEXT,
    value      TEXT,
    created_at DATE,
    updated_at DATE,
    UNIQUE (key)
);

CREATE TABLE IF NOT EXISTS node
(
    id                  TEXT PRIMARY KEY NOT NULL,
    identifier          TEXT,
    display_name        TEXT,
    description         TEXT,
    signing_private_key TEXT,
    signing_public_key  TEXT,
    open                BOOLEAN,
    creator             TEXT,
    created_at          DATE,
    updated_at          DATE,
    UNIQUE (identifier)
);

CREATE TABLE actor
(
    id              TEXT PRIMARY KEY NOT NULL,
    node_identifier TEXT,
    identifier      TEXT,
    password        TEXT,
    type            TEXT,
    display_name    TEXT,
    description     TEXT,
    creator         TEXT,
    created_at      DATE,
    updated_at      DATE,
    UNIQUE (node_identifier, identifier)
);

CREATE TABLE structure
(
    id              TEXT PRIMARY KEY NOT NULL,
    node_identifier TEXT,
    address         TEXT,
    display_name    TEXT,
    `description`   TEXT,
    creator         TEXT,
    created_at      DATE,
    updated_at      DATE,
    UNIQUE (node_identifier, address)
);

CREATE TABLE inheritance
(
    id                          TEXT PRIMARY KEY NOT NULL,
    node_identifier             TEXT,
    structure_address           TEXT,
    inherited_structure_address TEXT,
    creator                     TEXT,
    created_at                  DATE,
    updated_at                  DATE,
    UNIQUE (node_identifier, structure_address, inherited_structure_address)
);

CREATE TABLE relationship
(
    id                     TEXT PRIMARY KEY NOT NULL,
    node_identifier        TEXT,
    from_structure_address TEXT,
    to_structure_address   TEXT,
    type                   TEXT,
    identifier             TEXT,
    display_name           TEXT,
    `description`          TEXT,
    creator                TEXT,
    created_at             DATE,
    updated_at             DATE,
    UNIQUE (node_identifier, from_structure_address, identifier)
);

CREATE TABLE state
(
    id                TEXT NOT NULL PRIMARY KEY,
    node_identifier   TEXT,
    structure_address TEXT,
    identifier        TEXT,
    display_name      TEXT,
    description       TEXT,
    creator           TEXT,
    created_at        DATE,
    updated_at        DATE,
    UNIQUE (node_identifier, structure_address, identifier)
);
