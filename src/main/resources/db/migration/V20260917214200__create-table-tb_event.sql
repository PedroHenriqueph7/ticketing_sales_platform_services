CREATE TABLE tb_event (
    id UUID PRIMARY KEY,
    producer_id UUID NOT NULL REFERENCES tb_user(id),
    category_id BIGINT NOT NULL REFERENCES tb_category(id),
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    starts_at TIMESTAMPTZ NOT NULL,
    ends_at TIMESTAMPTZ NOT NULL,


    address_zipcode VARCHAR(20),
    address_street VARCHAR(255),
    address_number VARCHAR(50),
    address_city VARCHAR(100),
    address_neighborhood VARCHAR(100)
);