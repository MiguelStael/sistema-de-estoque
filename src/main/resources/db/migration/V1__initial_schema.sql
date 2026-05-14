CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    tipo_perfil VARCHAR(15) NOT NULL
);

CREATE TABLE ingredientes (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    nome VARCHAR(150) NOT NULL,
    quantidade DECIMAL(10, 3) NOT NULL,
    unidade_medida VARCHAR(10) NOT NULL,
    custo_unitario DECIMAL(10, 2),
    quantidade_minima DECIMAL(10, 3),
    data_validade DATE
);

CREATE INDEX idx_estoque_ingrediente ON ingredientes (quantidade);
CREATE INDEX idx_validade_ingrediente ON ingredientes (data_validade);

CREATE TABLE categorias (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao VARCHAR(255),
    ativa BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE produtos (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    nome VARCHAR(150) NOT NULL,
    descricao TEXT,
    preco DECIMAL(10, 2) NOT NULL,
    quantidade INTEGER NOT NULL,
    url_imagem VARCHAR(255),
    quantidade_minima INTEGER,
    disponivel BOOLEAN NOT NULL DEFAULT FALSE,
    categoria_id BIGINT,
    CONSTRAINT fk_produto_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);

CREATE INDEX idx_estoque_produto ON produtos (quantidade);

CREATE TABLE composicoes (
    id BIGSERIAL PRIMARY KEY,
    produto_id BIGINT NOT NULL,
    ingrediente_id BIGINT NOT NULL,
    quantidade DECIMAL(10, 3) NOT NULL,
    CONSTRAINT fk_composicao_produto FOREIGN KEY (produto_id) REFERENCES produtos(id),
    CONSTRAINT fk_composicao_ingrediente FOREIGN KEY (ingrediente_id) REFERENCES ingredientes(id)
);

CREATE TABLE mesas (
    id BIGSERIAL PRIMARY KEY,
    numero INTEGER NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL DEFAULT 'LIVRE',
    capacidade INTEGER NOT NULL DEFAULT 2,
    ativa BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE pedidos (
    id BIGSERIAL PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    total DECIMAL(10, 2) DEFAULT 0,
    observacao VARCHAR(255),
    identificacao VARCHAR(100) NOT NULL,
    mesa_id BIGINT,
    tipo_pedido VARCHAR(50) NOT NULL,
    forma_pagamento VARCHAR(50),
    pago BOOLEAN NOT NULL DEFAULT FALSE,
    data_pagamento TIMESTAMP,
    cliente_nome VARCHAR(150),
    cliente_telefone VARCHAR(20),
    endereco_entrega VARCHAR(500),
    taxa_entrega DECIMAL(10, 2) DEFAULT 0,
    taxa_servico DECIMAL(10, 2) DEFAULT 0,
    CONSTRAINT fk_pedido_mesa FOREIGN KEY (mesa_id) REFERENCES mesas(id)
);

CREATE TABLE itens_pedido (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    produto_id BIGINT NOT NULL,
    quantidade INTEGER NOT NULL,
    preco_unitario DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_item_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
    CONSTRAINT fk_item_produto FOREIGN KEY (produto_id) REFERENCES produtos(id)
);

CREATE TABLE pedidos_logs (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    descricao TEXT NOT NULL,
    data_registro TIMESTAMP NOT NULL,
    usuario_id BIGINT,
    CONSTRAINT fk_log_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
    CONSTRAINT fk_log_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE movimentacoes (
    id BIGSERIAL PRIMARY KEY,
    ingrediente_id BIGINT,
    produto_id BIGINT,
    quantidade DECIMAL(19, 4) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    motivo VARCHAR(255),
    valor_unitario_custo DECIMAL(19, 4),
    data_movimentacao TIMESTAMP NOT NULL,
    usuario_id BIGINT,
    CONSTRAINT fk_movimentacao_ingrediente FOREIGN KEY (ingrediente_id) REFERENCES ingredientes(id),
    CONSTRAINT fk_movimentacao_produto FOREIGN KEY (produto_id) REFERENCES produtos(id),
    CONSTRAINT fk_movimentacao_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);
