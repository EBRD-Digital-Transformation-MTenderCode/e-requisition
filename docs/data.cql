CREATE KEYSPACE IF NOT EXISTS requisition
    WITH REPLICATION = {
        'class' : 'SimpleStrategy',
        'replication_factor' : 1
        };

CREATE TABLE IF NOT EXISTS requisition.history
(
    command_id   TEXT,
    command_name TEXT,
    command_date TIMESTAMP,
    json_data    TEXT,
    PRIMARY KEY (command_id)
);

CREATE TABLE IF NOT EXISTS requisition.rules
(
    country        TEXT,
    pmd            TEXT,
    operation_type TEXT,
    parameter      TEXT,
    value          TEXT,
    PRIMARY KEY (country, pmd, operation_type, parameter)
);

CREATE TABLE IF NOT EXISTS qualification.requisitions
(
    cpid         TEXT,
    ocid         TEXT,
    token_entity UUID,
    owner        TEXT,
    created_date TIMESTAMP,
    json_data    TEXT,
    PRIMARY KEY (cpid, ocid)
);
