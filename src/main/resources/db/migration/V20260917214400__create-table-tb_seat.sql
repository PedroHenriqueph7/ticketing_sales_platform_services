CREATE TABLE tb_seat (
    id UUID PRIMARY KEY,
    ticket_batch_id UUID NOT NULL REFERENCES tb_ticket_batch(id),
    code VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL
);