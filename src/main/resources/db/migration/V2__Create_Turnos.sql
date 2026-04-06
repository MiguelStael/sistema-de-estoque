CREATE TABLE turnos (
    id BIGSERIAL PRIMARY KEY,
    responsavel VARCHAR(100) NOT NULL,
    data_abertura TIMESTAMP NOT NULL,
    data_fechamento TIMESTAMP,
    aberto BOOLEAN NOT NULL DEFAULT TRUE,
    duracao_minutos BIGINT
);
