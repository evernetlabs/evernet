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

CREATE TABLE property
(
    id                TEXT NOT NULL PRIMARY KEY,
    node_identifier   TEXT,
    structure_address TEXT,
    identifier        TEXT,
    data_format       TEXT,
    data_schema       TEXT,
    display_name      TEXT,
    description       TEXT,
    creator           TEXT,
    created_at        DATE,
    updated_at        DATE,
    UNIQUE (node_identifier, structure_address, identifier)
);

CREATE TABLE event
(
    id                TEXT NOT NULL PRIMARY KEY,
    node_identifier   TEXT,
    structure_address TEXT,
    identifier        TEXT,
    data_format       TEXT,
    data_schema       TEXT,
    display_name      TEXT,
    description       TEXT,
    creator           TEXT,
    created_at        DATE,
    updated_at        DATE,
    UNIQUE (node_identifier, structure_address, identifier)
);

CREATE TABLE function
(
    id                 TEXT NOT NULL PRIMARY KEY,
    node_identifier    TEXT,
    structure_address  TEXT,
    identifier         TEXT,
    input_data_format  TEXT,
    input_data_schema  TEXT,
    output_data_format TEXT,
    output_data_schema TEXT,
    display_name       TEXT,
    description        TEXT,
    creator            TEXT,
    created_at         DATE,
    updated_at         DATE,
    UNIQUE (node_identifier, structure_address, identifier)
);

CREATE TABLE connection
(
    id                            TEXT NOT NULL PRIMARY KEY,
    node_identifier               TEXT,
    structure_address             TEXT,
    event_parent_reference_chain  TEXT,
    event_identifier              TEXT,
    action_parent_reference_chain TEXT,
    action_type                   TEXT,
    action_identifier             TEXT,
    creator                       TEXT,
    created_at                    DATE,
    updated_at                    DATE,
    UNIQUE (
            node_identifier,
            structure_address,
            event_parent_reference_chain,
            event_identifier,
            action_parent_reference_chain,
            action_identifier
        )
);

CREATE TABLE transition
(
    id                           TEXT NOT NULL PRIMARY KEY,
    node_identifier              TEXT,
    structure_address            TEXT,
    source_state_identifier      TEXT,
    target_state_identifier      TEXT,
    event_parent_reference_chain TEXT,
    event_identifier             TEXT,
    identifier                   TEXT,
    display_name                 TEXT,
    `description`                TEXT,
    creator                      TEXT,
    created_at                   DATE,
    updated_at                   DATE,
    UNIQUE (
            node_identifier,
            structure_address,
            source_state_identifier,
            event_parent_reference_chain,
            event_identifier
        ),
    UNIQUE (
            node_identifier,
            structure_address,
            identifier
        )
);

CREATE TABLE transition_action
(
    id                            TEXT NOT NULL PRIMARY KEY,
    node_identifier               TEXT,
    structure_address             TEXT,
    transition_identifier         TEXT,
    action_parent_reference_chain TEXT,
    action_type                   TEXT,
    action_identifier             TEXT,
    creator                       TEXT,
    created_at                    DATE,
    updated_at                    DATE,
    UNIQUE (
            node_identifier,
            structure_address,
            transition_identifier,
            action_parent_reference_chain,
            action_type,
            action_identifier
        )
);

CREATE TABLE workflow
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
    UNIQUE (
            node_identifier,
            structure_address,
            identifier
        )
);

CREATE TABLE workflow_node
(
    id                              TEXT NOT NULL PRIMARY KEY,
    node_identifier                 TEXT,
    structure_address               TEXT,
    workflow_identifier             TEXT,
    identifier                      TEXT,
    display_name                    TEXT,
    description                     TEXT,
    function_parent_reference_chain TEXT,
    function_identifier             TEXT,
    creator                         TEXT,
    created_at                      DATE,
    updated_at                      DATE,
    UNIQUE (
            node_identifier,
            structure_address,
            workflow_identifier,
            identifier
        )
);

CREATE TABLE workflow_node_dependency
(
    id                                  TEXT NOT NULL PRIMARY KEY,
    node_identifier                     TEXT,
    structure_address                   TEXT,
    workflow_identifier                 TEXT,
    dependent_workflow_node_identifier  TEXT,
    dependency_workflow_node_identifier TEXT,
    creator                             TEXT,
    created_at                          DATE,
    updated_at                          DATE,
    UNIQUE (
            node_identifier,
            structure_address,
            workflow_identifier,
            dependent_workflow_node_identifier,
            dependency_workflow_node_identifier
        )
);
