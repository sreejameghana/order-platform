CREATE TABLE payments (
    id                UUID         PRIMARY KEY,
    idempotency_key   VARCHAR(100) NOT NULL,
    request_hash      VARCHAR(64)  NOT NULL,
    payer_account_id  VARCHAR(50)  NOT NULL,
    payee_account_id  VARCHAR(50)  NOT NULL,
    amount_minor      BIGINT       NOT NULL,
    currency          VARCHAR(3)   NOT NULL,
    status            VARCHAR(20)  NOT NULL,
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT uq_payments_idempotency_key UNIQUE (idempotency_key),
    CONSTRAINT ck_payments_amount_positive CHECK (amount_minor > 0),
    CONSTRAINT ck_payments_currency_format CHECK (currency ~ '^[A-Z]{3}$'),
    CONSTRAINT ck_payments_status CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED'))
);

CREATE INDEX idx_payments_payer_created ON payments (payer_account_id, created_at DESC);
