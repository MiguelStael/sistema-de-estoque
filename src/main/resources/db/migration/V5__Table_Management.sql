-- Script de migração V5: Gestão de Mesas e Categorias Dinâmicas

-- 1. Criar tabela de categorias
CREATE TABLE categorias (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    descricao VARCHAR(255),
    ativa BOOLEAN NOT NULL DEFAULT TRUE
);

-- 1.1 Inserir categorias padrão (conforme solicitado pelo usuário)
INSERT INTO categorias (nome, descricao) VALUES 
('Frutas', 'Frutas frescas e selecionadas'),
('Legumes', 'Legumes e vegetais para preparo'),
('Verduras', 'Folhas e verduras higienizadas'),
('Cereais', 'Grãos, arroz, feijão e cereais em geral'),
('Proteínas', 'Carnes, peixes e aves'),
('Laticínios', 'Leite, queijos e derivados'),
('Bebidas', 'Sucos, refrigerantes, águas e vinhos'),
('Sobremesas', 'Doces, tortas e sobremesas da casa'),
('Pratos Principais', 'Refeições completas e pratos da casa'),
('Entradas', 'Petiscos e entradas frias/quentes');

-- 2. Atualizar tabela de produtos para usar categoria_id
-- Adiciona a coluna permitindo nulo inicialmente para migração
ALTER TABLE produtos ADD COLUMN categoria_id INTEGER;

-- Mapeamento básico para dados existentes (baseado no Enum antigo)
UPDATE produtos SET categoria_id = (SELECT id FROM categorias WHERE nome = 'Pratos Principais') WHERE categoria = 'PRATO';
UPDATE produtos SET categoria_id = (SELECT id FROM categorias WHERE nome = 'Bebidas') WHERE categoria = 'BEBIDA';
UPDATE produtos SET categoria_id = (SELECT id FROM categorias WHERE nome = 'Entradas') WHERE categoria = 'ENTRADA';
UPDATE produtos SET categoria_id = (SELECT id FROM categorias WHERE nome = 'Sobremesas') WHERE categoria = 'SOBREMESA';

-- Define a constraint de chave estrangeira
ALTER TABLE produtos ADD CONSTRAINT fk_produto_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id);

-- 3. Criar tabela de mesas
CREATE TABLE mesas (
    id SERIAL PRIMARY KEY,
    numero INTEGER NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'LIVRE',
    capacidade INTEGER DEFAULT 2,
    ativa BOOLEAN DEFAULT TRUE
);

-- Inserir mesas iniciais (pode ser alterado pelo usuário via API)
INSERT INTO mesas (numero, status) VALUES (1, 'LIVRE'), (2, 'LIVRE'), (3, 'LIVRE'), (4, 'LIVRE'), (5, 'LIVRE');

-- 4. Atualizar tabela de pedidos para vincular com mesa
ALTER TABLE pedidos ADD COLUMN mesa_id INTEGER;
ALTER TABLE pedidos ADD CONSTRAINT fk_pedido_mesa FOREIGN KEY (mesa_id) REFERENCES mesas(id);

-- 5. Tabelas de Auditoria (Envers) - Atualização
-- O Envers criará as colunas automaticamente ao detectar a mudança nas entidades, 
-- mas podemos garantir aqui se preferir. No entanto, é melhor deixar o Envers gerenciar.
