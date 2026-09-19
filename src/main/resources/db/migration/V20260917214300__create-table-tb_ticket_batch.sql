CREATE TABLE tb_ticket_batch (
    id UUID PRIMARY KEY,
    event_id UUID NOT NULL REFERENCES tb_event(id),
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    capacity INTEGER NOT NULL,
    starts_at TIMESTAMPTZ NOT NULL,
    ends_at TIMESTAMPTZ NOT NULL
);