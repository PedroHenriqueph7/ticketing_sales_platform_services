CREATE TABLE tb_order (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES tb_user(id),
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    abacate_pay_transaction_id VARCHAR(255) UNIQUE,
    pix_emv TEXT,
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);