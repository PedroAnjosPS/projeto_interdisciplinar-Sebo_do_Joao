CREATE DATABASE Sebo_do_Joao
GO

USE Sebo_do_Joao
GO

CREATE TABLE ufs
(
    sigla VARCHAR(2) PRIMARY KEY NOT NULL,
    nome VARCHAR(100) NOT NULL
)
GO

CREATE TABLE cidades
(
    id INT NOT NULL PRIMARY KEY IDENTITY,
    nome VARCHAR(100) NOT NULL,
    uf_sigla VARCHAR(2) NOT NULL,

    CONSTRAINT fk_cidades_ufs FOREIGN KEY (uf_sigla)
    REFERENCES ufs(sigla)
)
GO

CREATE TABLE ceps
(
    numero_cep VARCHAR(10) PRIMARY KEY NOT NULL,
    cidade_id INT NOT NULL,

    CONSTRAINT fk_ceps_cidades FOREIGN KEY (cidade_id)
    REFERENCES cidades(id)
)
GO

CREATE TABLE usuarios
(
    id INT NOT NULL PRIMARY KEY IDENTITY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    status INT NULL DEFAULT 1,
    telefone VARCHAR(20),
    data_cadastro DATETIME DEFAULT GETDATE(),
    foto_perfil VARCHAR(255) NULL,

    CONSTRAINT ck_usuarios_status
        CHECK (status IN (1,2))
)
GO

CREATE TABLE clientes
(
    usuario_id INT PRIMARY KEY NOT NULL,
    data_nascimento DATE,
    cpf VARCHAR(14) UNIQUE,

    CONSTRAINT fk_clientes_usuarios
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id)
)
GO

CREATE TABLE funcionarios
(
    usuario_id INT PRIMARY KEY NOT NULL,
    codigo_funcionario VARCHAR(50) UNIQUE  NOT NULL,
    admin BIT NOT NULL DEFAULT 0, -- 0 será padrão (assim, admitindo que nem todo funcionário será um administrador)

    CONSTRAINT fk_funcionarios_usuarios
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id)
)
GO

-- No front-end fica ativo = 0, para não quebrar o histórico das entregas.
CREATE TABLE enderecos
(
    id INT NOT NULL PRIMARY KEY IDENTITY,
    logradouro VARCHAR(255) NOT NULL,
    numero VARCHAR(10) NULL,          -- NULL = opção "Sem número" da tela
    complemento VARCHAR(100) NULL,
    bairro VARCHAR(100) NOT NULL,
    ativo BIT NOT NULL DEFAULT 1,     -- 1 = ativo, 0 = deixa de ser o endereço usado pelo cliente (não é o principal)
    -- Nome dado pelo cliente ao endereço (ex.: "Casa", "Trabalho"); se não informar, vale "Endereço"
    apelido VARCHAR(30) NOT NULL DEFAULT 'Endereço',
    -- 1 = endereço padrão (já vem selecionado no checkout); 0 = não é o padrão.
    -- Regra de negócio (feita na aplicação/procedure): só 1 endereço padrão por cliente.
    padrao BIT NOT NULL DEFAULT 0,
    cep_numero VARCHAR(10) NOT NULL,
    cliente_id INT NOT NULL,

    CONSTRAINT fk_enderecos_ceps
        FOREIGN KEY (cep_numero)
        REFERENCES ceps(numero_cep),

    CONSTRAINT fk_enderecos_clientes
        FOREIGN KEY (cliente_id)
        REFERENCES clientes(usuario_id)
)
GO

-- Nunca guardar o número completo nem o CVV: só os 4 últimos dígitos.
CREATE TABLE cartoes
(
    id INT NOT NULL PRIMARY KEY IDENTITY,
    bandeira VARCHAR(30) NOT NULL,
    final_numero CHAR(4) NOT NULL,    -- 4 últimos dígitos
    titular VARCHAR(100) NOT NULL,
    validade CHAR(5) NOT NULL,        -- formato MM/AA, igual à tela
    -- 1 = cartão padrão (já vem selecionado no checkout); 0 = não é o padrão.
    -- Regra de negócio (feita na aplicação/procedure): só 1 cartão padrão por cliente.
    padrao BIT NOT NULL DEFAULT 0,
    cliente_id INT NOT NULL,

    CONSTRAINT ck_cartoes_validade
    CHECK (validade LIKE '0[1-9]/[0-9][0-9]'    -- 01 a 09
        OR validade LIKE '1[0-2]/[0-9][0-9]'),  -- 10 a 12

    CONSTRAINT fk_cartoes_clientes
        FOREIGN KEY (cliente_id)
        REFERENCES clientes(usuario_id)
)
GO

CREATE TABLE categorias
(
    id INT NOT NULL PRIMARY KEY IDENTITY,
    nome VARCHAR(100) NOT NULL,
)
GO

CREATE TABLE generos
(
    id INT NOT NULL PRIMARY KEY IDENTITY,
    nome VARCHAR(100) NOT NULL,
)
GO

CREATE TABLE produtos
(
    id INT NOT NULL PRIMARY KEY IDENTITY,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(MAX),
    estoque INT NOT NULL DEFAULT 0,
    preco DECIMAL(10,2) NOT NULL,
    status INT NULL DEFAULT 1,
    categoria_id INT NOT NULL,
    genero_id INT NOT NULL,
    funcionario_id INT NOT NULL,

    CONSTRAINT ck_produtos_preco
        CHECK (preco > 0),

    CONSTRAINT ck_produtos_estoque
        CHECK (estoque >= 0),

    CONSTRAINT ck_produtos_status
        CHECK (status IN (1,2)),

    CONSTRAINT fk_produtos_categorias
        FOREIGN KEY (categoria_id)
        REFERENCES categorias(id),

    CONSTRAINT fk_produtos_generos
        FOREIGN KEY (genero_id)
        REFERENCES generos(id),
    
    CONSTRAINT fk_produtos_funcionarios
        FOREIGN KEY (funcionario_id)
        REFERENCES funcionarios(usuario_id)
)
GO

CREATE TABLE imagem_produtos
(
    id INT NOT NULL PRIMARY KEY IDENTITY,
    caminho VARCHAR(255) NOT NULL,
    produto_id INT NOT NULL,

    CONSTRAINT fk_imagem_produtos_produtos
        FOREIGN KEY (produto_id)
        REFERENCES produtos(id)
)
GO

CREATE TABLE entregas
(
    id INT NOT NULL PRIMARY KEY IDENTITY,
    transportadora VARCHAR(100),
    status INT DEFAULT 1,
    link_rastreio VARCHAR(255),
    codigo_rastreio VARCHAR(100),
    data_postagem DATETIME DEFAULT GETDATE(),
    data_previsao_entrega DATE,
    data_entrega DATETIME,
    valor_frete DECIMAL(10,2) NOT NULL DEFAULT 0,  -- frete fica na tabela de entregas (pedidos.total = produtos + frete)
    endereco_id INT NOT NULL,                       -- destino da entrega

    CONSTRAINT ck_entregas_status
        CHECK (status IN (1,2,3,4,5)),

    CONSTRAINT ck_entregas_valor_frete
        CHECK (valor_frete >= 0),

    CONSTRAINT fk_entregas_enderecos
        FOREIGN KEY (endereco_id)
        REFERENCES enderecos(id)
)
GO

CREATE TABLE pedidos
(
    id INT NOT NULL PRIMARY KEY IDENTITY,
    data DATETIME NOT NULL DEFAULT GETDATE(),
    total DECIMAL(10,2) NOT NULL DEFAULT 0,
    status INT NULL DEFAULT 1,
    entrega_id INT UNIQUE,
    cliente_id INT NOT NULL,
    funcionario_id INT NOT NULL,

    CONSTRAINT ck_pedidos_total
        CHECK (total >= 0),

    CONSTRAINT ck_pedidos_status
        CHECK (status IN (1,2,3,4)),

    CONSTRAINT fk_pedidos_entregas
        FOREIGN KEY (entrega_id)
        REFERENCES entregas(id),

    CONSTRAINT fk_pedidos_clientes
        FOREIGN KEY (cliente_id)
        REFERENCES clientes(usuario_id),

    CONSTRAINT fk_pedidos_funcionarios
        FOREIGN KEY (funcionario_id)
        REFERENCES funcionarios(usuario_id)
)
GO

CREATE TABLE item_pedidos
(
    pedido_id INT NOT NULL,
    produto_id INT NOT NULL,
    quantidade INT NOT NULL,
    preco_unitario DECIMAL(10,2) NOT NULL,

    CONSTRAINT pk_item_pedidos
        PRIMARY KEY (pedido_id, produto_id),

    CONSTRAINT ck_item_pedidos_quantidade
        CHECK (quantidade > 0),

    CONSTRAINT ck_item_pedidos_preco
        CHECK (preco_unitario > 0),

    CONSTRAINT fk_item_pedidos_pedidos
        FOREIGN KEY (pedido_id)
        REFERENCES pedidos(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_item_pedidos_produtos
        FOREIGN KEY (produto_id)
        REFERENCES produtos(id)
)
GO

CREATE TABLE pagamentos
(
    id INT NOT NULL PRIMARY KEY IDENTITY,
    numero_parcela INT NOT NULL,
    status INT NULL DEFAULT 1,
    valor DECIMAL(10,2) NOT NULL,
    quantidade_parcelas INT NOT NULL,
    forma_pagamento VARCHAR(50) NOT NULL,
    data_vencimento DATE NOT NULL,
    data_pagamento DATE,
    -- Cópia dos dados do cartão usado (bandeira e final_cartao), para o histórico não depender somente de cartões
    -- (o cliente pode pagar com cartão não salvo ou apagar o cartão depois). NULL para Pix/boleto.
    bandeira VARCHAR(30) NULL,
    final_cartao CHAR(4) NULL,
    pedido_id INT NOT NULL,

    CONSTRAINT ck_pagamentos_valor
        CHECK (valor > 0),

    CONSTRAINT ck_pagamentos_parcelas
        CHECK (quantidade_parcelas > 0),

    -- 1 Pendente | 2 Aprovado | 3 Recusado | 4 Compra estornada (pedido cancelado depois de pago)
    CONSTRAINT ck_pagamentos_status
        CHECK (status IN (1,2,3,4)),

    CONSTRAINT fk_pagamentos_pedidos
        FOREIGN KEY (pedido_id)
        REFERENCES pedidos(id)
)
GO

-- Carrinho de compras do cliente.
-- Separado de item_pedidos: não baixa estoque (a trigger é só de item_pedidos),
-- não congela preço (usa produtos.preco atual) e não exige pedido.
CREATE TABLE item_carrinhos
(
    cliente_id INT NOT NULL,
    produto_id INT NOT NULL,
    quantidade INT NOT NULL DEFAULT 1,

    CONSTRAINT pk_item_carrinhos
        PRIMARY KEY (cliente_id, produto_id),

    CONSTRAINT ck_item_carrinhos_quantidade
        CHECK (quantidade > 0),

    CONSTRAINT fk_item_carrinhos_clientes
        FOREIGN KEY (cliente_id)
        REFERENCES clientes(usuario_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_item_carrinhos_produtos
        FOREIGN KEY (produto_id)
        REFERENCES produtos(id)
)
GO