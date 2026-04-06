CREATE TABLE movimentacoes_estoque (
    id BIGSERIAL PRIMARY KEY,
    insumo_id BIGINT,
    produto_id BIGINT,
    quantidade DECIMAL(19, 4) NOT NULL,
    tipo VARCHAR(50) NOT NULL, -- ENTRADA, SAIDA_VENDA, SAIDA_PERDA, AJUSTE
    motivo VARCHAR(255),
    valor_unitario_custo DECIMAL(19, 4),
    data_movimentacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_id BIGINT,
    CONSTRAINT fk_movimentacao_insumo FOREIGN KEY (insumo_id) REFERENCES insumos(id),
    CONSTRAINT fk_movimentacao_produto FOREIGN KEY (produto_id) REFERENCES produtos(id),
    CONSTRAINT fk_movimentacao_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);
