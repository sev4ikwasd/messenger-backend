CREATE SCHEMA IF NOT EXISTS messenger;

CREATE TABLE IF NOT EXISTS messenger.user (
	id SERIAL PRIMARY KEY NOT NULL,
    uid VARCHAR NOT NULL,
    last_visited TIMESTAMP NOT NULL,
    master_password_hash BYTEA NOT NULL,
    protected_symmetric_key BYTEA NOT NULL,
    identity_public_key BYTEA NOT NULL,
    signed_public_key BYTEA NOT NULL
);

CREATE TABLE IF NOT EXISTS messenger.user_one_time_key (
	id SERIAL PRIMARY KEY NOT NULL,
    user_id INT NOT NULL,
    public_one_time_key BYTEA NOT NULL,
    key_number INT NOT NULL,
    is_used boolean NOT NULL DEFAULT false,
    FOREIGN KEY (user_id) REFERENCES messenger.user (id)
);

CREATE TABLE IF NOT EXISTS messenger.user_data (
    id INT PRIMARY KEY NOT NULL,
    encrypted_data BYTEA NOT NULL,
    FOREIGN KEY (id) REFERENCES messenger.user (id)
);