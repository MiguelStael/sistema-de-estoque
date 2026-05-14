-- Senha 'admin123' criptografada com BCrypt
INSERT INTO usuarios (nome, email, senha, tipo_perfil) 
VALUES ('Administrador', 'admin@coreestoque.com', '$2a$10$8.UnVuG9HHgffUDAlk8q6uy.03v16G6X6S7C1.wQ8Xh8/7X6Y5C6S', 'ADMIN');

INSERT INTO categorias (nome, descricao, ativa) 
VALUES 
('Bebidas', 'Sucos, Refrigerantes e Águas', true),
('Cozinha', 'Pratos Principais e Entradas', true),
('Sobremesas', 'Doces e Frutas', true),
('Hortifruti', 'Frutas, Legumes e Verduras', true),
('Mercearia', 'Grãos, Enlatados e Condimentos', true);

INSERT INTO mesas (numero, status, capacidade, ativa)
VALUES 
(1, 'LIVRE', 2, true),
(2, 'LIVRE', 2, true),
(3, 'LIVRE', 4, true),
(4, 'LIVRE', 4, true),
(5, 'LIVRE', 6, true);
