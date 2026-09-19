CREATE TABLE tb_order_item (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES tb_order(id),
    seat_id UUID NOT NULL UNIQUE REFERENCES tb_seat(id),
    ticket_batch_id UUID NOT NULL REFERENCES tb_ticket_batch(id)
);