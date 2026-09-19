CREATE INDEX idx_event_producer_id ON tb_event(producer_id);
CREATE INDEX idx_batch_event_id ON tb_ticket_batch(event_id);
CREATE INDEX idx_seat_batch_id ON tb_seat(ticket_batch_id);
CREATE INDEX idx_order_user_id ON tb_order(user_id);
CREATE INDEX idx_order_item_order_id ON tb_order_item(order_id);

CREATE INDEX idx_event_category_status ON tb_event(category_id, status);