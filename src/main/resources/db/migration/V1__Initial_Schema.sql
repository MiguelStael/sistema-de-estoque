-- Script de migração inicial V1

CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    tipo_perfil VARCHAR(15) NOT NULL
);

CREATE TABLE insumos (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT DEFAULT 0,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    nome VARCHAR(150) NOT NULL,
    quantidade NUMERIC(10,3) NOT NULL,
    unidade_medida VARCHAR(10) NOT NULL,
    custo_unitario NUMERIC(10,2),
    quantidade_minima NUMERIC(10,3)
);

CREATE TABLE produtos (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT DEFAULT 0,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    nome VARCHAR(150) NOT NULL,
    descricao TEXT,
    preco NUMERIC(10,2) NOT NULL,
    quantidade INTEGER NOT NULL,
    url_imagem VARCHAR(255),
    disponivel BOOLEAN NOT NULL DEFAULT FALSE,
    categoria VARCHAR(20) NOT NULL
);

CREATE TABLE item_ficha_tecnica (
    id BIGSERIAL PRIMARY KEY,
    produto_id BIGINT NOT NULL,
    insumo_id BIGINT NOT NULL,
    quantidade NUMERIC(10,3) NOT NULL,
    CONSTRAINT fk_ficha_produto FOREIGN KEY (produto_id) REFERENCES produtos(id),
    CONSTRAINT fk_ficha_insumo FOREIGN KEY (insumo_id) REFERENCES insumos(id)
);

CREATE TABLE pedidos (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT DEFAULT 0,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    total NUMERIC(10,2) NOT NULL DEFAULT 0,
    observacao VARCHAR(255),
    identificacao VARCHAR(100) NOT NULL,
    tipo_pedido VARCHAR(20) NOT NULL,
    forma_pagamento VARCHAR(20),
    pago BOOLEAN NOT NULL DEFAULT FALSE,
    data_pagamento TIMESTAMP
);

CREATE TABLE itens_pedido (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    produto_id BIGINT NOT NULL,
    quantidade INTEGER NOT NULL,
    preco_unitario NUMERIC(10,2) NOT NULL,
    CONSTRAINT fk_item_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
    CONSTRAINT fk_item_produto FOREIGN KEY (produto_id) REFERENCES produtos(id)
);

CREATE TABLE historicos_pedido (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    descricao TEXT NOT NULL,
    data_registro TIMESTAMP NOT NULL,
    CONSTRAINT fk_historico_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos(id)
);
