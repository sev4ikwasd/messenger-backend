CREATE SCHEMA IF NOT EXISTS messenger;

CREATE TABLE IF NOT EXISTS messenger.user (
	id SERIAL PRIMARY KEY NOT NULL,
    uid VARCHAR NOT NULL,
    last_visited TIMESTAMP NOT NULL,
    master_password_hash BYTEA NOT NULL,
    protected_symmetric_key BYTEA NOT NULL
);

CREATE TABLE IF NOT EXISTS messenger.user_private_key (
	id SERIAL PRIMARY KEY NOT NULL,
    user_id INT NOT NULL,
    encrypted_key BYTEA NOT NULL,
    FOREIGN KEY (user_id) REFERENCES messenger.user (id)
);

CREATE TABLE IF NOT EXISTS messenger.user_data (
	id SERIAL PRIMARY KEY NOT NULL,
    user_id INT NOT NULL,
    encrypted_data BYTEA NOT NULL,
    start_datetime TIMESTAMP NOT NULL,
    end_datetime TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES messenger.user (id)
);