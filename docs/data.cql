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

CREATE TABLE IF NOT EXISTS requisition.requisitions
(
    cpid           TEXT,
    ocid           TEXT,
    token_entity   TEXT,
    owner          TEXT,
    status         TEXT,
    status_details TEXT,
    json_data      TEXT,
    PRIMARY KEY (cpid, ocid)
);


DROP TABLE IF EXISTS ocds.access_rules;

CREATE TABLE IF NOT EXISTS requisition.history
(
    command_id   TEXT,
    command_name TEXT,
    command_date TIMESTAMP,
    json_data    TEXT,
    PRIMARY KEY (command_id, command_name)
);