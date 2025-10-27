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
