-- ═══════════════════════════════════════════════
-- FazendaPro — Schema inicial
-- V001__create_schema.sql
-- ═══════════════════════════════════════════════

-- Usuários do sistema
CREATE TABLE IF NOT EXISTS usuarios (
    id          BIGSERIAL PRIMARY KEY,
    nome        VARCHAR(100)  NOT NULL,
    email       VARCHAR(150)  NOT NULL UNIQUE,
    senha       VARCHAR(255)  NOT NULL,
    perfil      VARCHAR(30)   NOT NULL,
    ativo       BOOLEAN       NOT NULL DEFAULT TRUE,
    criado_em   TIMESTAMP     NOT NULL DEFAULT NOW(),
    atualizado_em TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- Raças dos animais
CREATE TABLE IF NOT EXISTS racas (
    id        BIGSERIAL PRIMARY KEY,
    nome      VARCHAR(80) NOT NULL UNIQUE,
    descricao TEXT,
    ativo     BOOLEAN NOT NULL DEFAULT TRUE
);

-- Animais do rebanho
CREATE TABLE IF NOT EXISTS animais (
    id              BIGSERIAL PRIMARY KEY,
    nbr             VARCHAR(30)    NOT NULL UNIQUE,
    nome            VARCHAR(80),
    raca_id         BIGINT         REFERENCES racas(id),
    sexo            VARCHAR(10)    NOT NULL,
    status          VARCHAR(20)    NOT NULL DEFAULT 'ATIVO',
    data_nascimento DATE,
    pai_id          BIGINT         REFERENCES animais(id),
    mae_id          BIGINT         REFERENCES animais(id),
    peso_entrada    DECIMAL(8,2),
    observacoes     TEXT,
    criado_em       TIMESTAMP      NOT NULL DEFAULT NOW(),
    atualizado_em   TIMESTAMP      NOT NULL DEFAULT NOW()
);

-- Produção de leite diária
CREATE TABLE IF NOT EXISTS producoes_leite (
    id                 BIGSERIAL PRIMARY KEY,
    animal_id          BIGINT         NOT NULL REFERENCES animais(id),
    data               DATE           NOT NULL,
    quantidade_manha   DECIMAL(8,2)   NOT NULL DEFAULT 0,
    quantidade_tarde   DECIMAL(8,2)   NOT NULL DEFAULT 0,
    quantidade_total   DECIMAL(8,2)   NOT NULL DEFAULT 0,
    classificacao      VARCHAR(20)    NOT NULL DEFAULT 'NORMAL',
    ccs                INTEGER,
    cbt                DECIMAL(12,2),
    observacoes        TEXT,
    criado_em          TIMESTAMP      NOT NULL DEFAULT NOW(),
    UNIQUE(animal_id, data)
);

-- Eventos sanitários (vacinas, medicamentos, doenças)
CREATE TABLE IF NOT EXISTS eventos_sanitarios (
    id               BIGSERIAL PRIMARY KEY,
    animal_id        BIGINT       NOT NULL REFERENCES animais(id),
    tipo             VARCHAR(30)  NOT NULL,
    descricao        VARCHAR(200) NOT NULL,
    data_evento      DATE         NOT NULL,
    data_retorno     DATE,
    carencia_dias    INTEGER      NOT NULL DEFAULT 0,
    data_fim_carencia DATE,
    veterinario      VARCHAR(100),
    produto_aplicado VARCHAR(100),
    dose             VARCHAR(50),
    custo            DECIMAL(10,2) NOT NULL DEFAULT 0,
    observacoes      TEXT,
    criado_em        TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Reprodução (cobertura, diagnóstico, parto)
CREATE TABLE IF NOT EXISTS reproducoes (
    id                    BIGSERIAL PRIMARY KEY,
    femea_id              BIGINT       NOT NULL REFERENCES animais(id),
    tipo_cobertura        VARCHAR(20)  NOT NULL,
    data_cobertura        DATE         NOT NULL,
    data_dpp              DATE,
    touro_id              BIGINT       REFERENCES animais(id),
    nome_touro_externo    VARCHAR(100),
    semen_codigo          VARCHAR(50),
    diagnostico_gestacao  VARCHAR(20),
    data_diagnostico      DATE,
    data_parto            DATE,
    cria_id               BIGINT       REFERENCES animais(id),
    resultado             VARCHAR(20),
    observacoes           TEXT,
    criado_em             TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Estoque de insumos (rações, medicamentos, etc.)
CREATE TABLE IF NOT EXISTS produtos_estoque (
    id               BIGSERIAL PRIMARY KEY,
    nome             VARCHAR(100)   NOT NULL,
    categoria        VARCHAR(30)    NOT NULL,
    unidade          VARCHAR(20)    NOT NULL,
    quantidade_atual DECIMAL(12,3)  NOT NULL DEFAULT 0,
    quantidade_minima DECIMAL(12,3) NOT NULL DEFAULT 0,
    custo_medio      DECIMAL(10,4)  NOT NULL DEFAULT 0,
    data_validade    DATE,
    fabricante       VARCHAR(100),
    ativo            BOOLEAN        NOT NULL DEFAULT TRUE,
    criado_em        TIMESTAMP      NOT NULL DEFAULT NOW(),
    atualizado_em    TIMESTAMP      NOT NULL DEFAULT NOW()
);

-- Movimentações de estoque (entradas e saídas)
CREATE TABLE IF NOT EXISTS movimentacoes_estoque (
    id                 BIGSERIAL PRIMARY KEY,
    produto_id         BIGINT         NOT NULL REFERENCES produtos_estoque(id),
    tipo               VARCHAR(10)    NOT NULL,
    quantidade         DECIMAL(12,3)  NOT NULL,
    custo_unitario     DECIMAL(10,4),
    custo_total        DECIMAL(12,4),
    data_movimentacao  DATE           NOT NULL,
    animal_id          BIGINT         REFERENCES animais(id),
    motivo             VARCHAR(200),
    numero_nota        VARCHAR(50),
    criado_em          TIMESTAMP      NOT NULL DEFAULT NOW()
);

-- Lançamentos financeiros (receitas e despesas)
CREATE TABLE IF NOT EXISTS lancamentos_financeiros (
    id               BIGSERIAL PRIMARY KEY,
    tipo             VARCHAR(10)    NOT NULL,
    categoria        VARCHAR(40)    NOT NULL,
    descricao        VARCHAR(200)   NOT NULL,
    valor            DECIMAL(12,2)  NOT NULL,
    data_lancamento  DATE           NOT NULL,
    data_vencimento  DATE,
    data_pagamento   DATE,
    pago             BOOLEAN        NOT NULL DEFAULT FALSE,
    animal_id        BIGINT         REFERENCES animais(id),
    observacoes      TEXT,
    criado_em        TIMESTAMP      NOT NULL DEFAULT NOW(),
    atualizado_em    TIMESTAMP      NOT NULL DEFAULT NOW()
);

-- ─── Dados iniciais ───
INSERT INTO racas (nome, descricao) VALUES
    ('Girolando',    'Raça mais popular para pecuária leiteira no Brasil'),
    ('Holandesa',    'Alta produção de leite — ideal para clima temperado'),
    ('Gir Leiteiro', 'Excelente adaptação ao clima tropical e semiárido'),
    ('Jersey',       'Leite com alto teor de gordura e proteína'),
    ('Pardo-Suíço',  'Boa produção e resistência a doenças');

-- Usuário admin padrão (senha: admin123)
INSERT INTO usuarios (nome, email, senha, perfil) VALUES
    ('Administrador', 'admin@fazendapro.com',
     '$2a$10$slYQmyNdgzSx95f7zNODZ.i8iEDPzwbzpOiAGioKTAHHTgCqxpJSy',
     'ADMIN');
