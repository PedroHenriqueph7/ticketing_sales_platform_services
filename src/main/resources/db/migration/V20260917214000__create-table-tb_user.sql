CREATE TABLE tb_user (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    document_cpf VARCHAR(14) UNIQUE,
    document_cnpj VARCHAR(18) UNIQUE,
    age INTEGER
);