CREATE TABLE exchange_rate (
    id BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    currency_pair VARCHAR(20) NOT NULL,
    rate DECIMAL(10,5) NOT NULL,
    date_time DATE NOT NULL
);

CREATE TABLE transaction_limit (
    id BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    currency_shortname VARCHAR(20) NOT NULL,
    sum DECIMAL(10,2) NOT NULL,
    expense_category VARCHAR(50) NOT NULL,
    date_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    account_from BIGINT NOT NULL,
    account_to BIGINT NOT NULL,
    currency_shortname VARCHAR(10) NOT NULL,
    sum DECIMAL(10,2) NOT NULL,
    expense_category VARCHAR(50) NOT NULL,
    date_time TIMESTAMP WITH TIME ZONE NOT NULL,
    limit_exceeded BOOLEAN NOT NULL,
    transaction_limit_id BIGINT,
    FOREIGN KEY (transaction_limit_id) REFERENCES transaction_limit(id)
);