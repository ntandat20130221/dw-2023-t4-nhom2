DROP TABLE config;
DROP TABLE process_control;

CREATE TABLE config
(
    id               INTEGER PRIMARY KEY,
    source_url       TEXT,
    source_suffix    TEXT,
    file_format      TEXT,
    file_pattern     TEXT,
    file_destination TEXT,
    file_delimiter   TEXT,
    date_format      TEXT,
    userAgent        TEXT,
    description      TEXT
);

CREATE TABLE process_control
(
    id           INTEGER PRIMARY KEY,
    process_name VARCHAR(64),
    status       VARCHAR(20),
    start_time   DATE DEFAULT (datetime('now', 'localtime'))
);

INSERT INTO config
VALUES (1, 'https://xoso.com.vn/kqxs-', '.html', '.csv', 'ddMMyyyy_HHmmss', 'D://extract/', ',', 'dd-MM-yyyy',
        'Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)', 'Configuration info');

UPDATE process_control
SET status = 'FE'
WHERE id = 1;