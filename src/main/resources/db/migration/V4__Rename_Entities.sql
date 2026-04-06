-- V4: Renomeando tabelas e colunas para nomes mais simples e intuitivos

-- 1. Renomear Tabelas Principais
ALTER TABLE insumos RENAME TO ingredientes;
ALTER TABLE item_ficha_tecnica RENAME TO composicoes;
ALTER TABLE historicos_pedido RENAME TO pedidos_logs;
ALTER TABLE movimentacoes_estoque RENAME TO movimentacoes;

-- 2. Renomear Colunas de Relacionamento
-- Na tabela composicoes (antiga item_ficha_tecnica)
ALTER TABLE composicoes RENAME COLUMN insumo_id TO ingrediente_id;

-- Na tabela movimentacoes (antiga movimentacoes_estoque)
ALTER TABLE movimentacoes RENAME COLUMN insumo_id TO ingrediente_id;

-- 3. Renomear Tabelas de Auditoria (Envers) - Usando IF EXISTS para seguranca
ALTER TABLE IF EXISTS insumos_audit RENAME TO ingredientes_historico;
ALTER TABLE IF EXISTS produtos_audit RENAME TO produtos_historico;
ALTER TABLE IF EXISTS item_ficha_tecnica_audit RENAME TO composicoes_historico;
ALTER TABLE IF EXISTS revinfo RENAME TO revisao;

-- 4. Ajustar colunas nas tabelas de historico
ALTER TABLE IF EXISTS ingredientes_historico RENAME TO ingredientes_historico; -- Ja renomeado acima
ALTER TABLE IF EXISTS composicoes_historico RENAME COLUMN insumo_id TO ingrediente_id;
ALTER TABLE IF EXISTS revisao RENAME COLUMN revtstmp TO timestamp;
