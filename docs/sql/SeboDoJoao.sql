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
    nome VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(100) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    status INT NOT NULL DEFAULT 1,
    logradouro VARCHAR(255),
    bairro VARCHAR(100),
    telefone VARCHAR(20),
    cep_numero VARCHAR(10) NOT NULL,
    data_cadastro DATETIME DEFAULT GETDATE(),

    CONSTRAINT fk_usuarios_ceps FOREIGN KEY (cep_numero)
        REFERENCES ceps(numero_cep),

    CONSTRAINT ck_usuarios_status
        CHECK (status IN (1,2,3,4))
)
GO

CREATE TABLE clientes
(
    usuario_id INT PRIMARY KEY NOT NULL,
    data_nascimento DATE NOT NULL,
    cpf VARCHAR(14) UNIQUE NOT NULL,

    CONSTRAINT fk_clientes_usuarios
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id)
)
GO

CREATE TABLE funcionarios
(
    usuario_id INT PRIMARY KEY NOT NULL,
    codigo_funcionario VARCHAR(50) UNIQUE  NOT NULL,
    admin BIT NOT NULL DEFAULT 0 -- 0 será padrão (assim, admitindo que nem todo funcionário será um administrador)

    CONSTRAINT fk_funcionarios_usuarios
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id)
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
    status INT NOT NULL DEFAULT 1,
    categoria_id INT NOT NULL,
    genero_id INT NOT NULL,
    funcionario_id INT NOT NULL,

    CONSTRAINT ck_produtos_preco
        CHECK (preco > 0),

    CONSTRAINT ck_produtos_estoque
        CHECK (estoque >= 0),

    CONSTRAINT ck_produtos_status
        CHECK (status IN (1,2,3,4)),

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

    CONSTRAINT ck_entregas_status
        CHECK (status IN (1,2,3,4,5,6))
)
GO

CREATE TABLE pedidos
(
    id INT NOT NULL PRIMARY KEY IDENTITY,
    data DATETIME NOT NULL DEFAULT GETDATE(),
    total DECIMAL(10,2) NOT NULL DEFAULT 0,
    status INT NOT NULL DEFAULT 1,
    entrega_id INT UNIQUE,
    cliente_id INT NOT NULL,
    funcionario_id INT NOT NULL,

    CONSTRAINT ck_pedidos_total
        CHECK (total >= 0),

    CONSTRAINT ck_pedidos_status
        CHECK (status IN (1,2,3,4,5)),

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
    status INT NOT NULL DEFAULT 1,
    valor DECIMAL(10,2) NOT NULL,
    quantidade_parcelas INT NOT NULL,
    forma_pagamento VARCHAR(50) NOT NULL,
    data_vencimento DATE NOT NULL,
    data_pagamento DATE,
    pedido_id INT NOT NULL,

    CONSTRAINT ck_pagamentos_valor
        CHECK (valor > 0),

    CONSTRAINT ck_pagamentos_parcelas
        CHECK (quantidade_parcelas > 0),

    CONSTRAINT ck_pagamentos_status
        CHECK (status IN (1,2,3,4,5)),

    CONSTRAINT fk_pagamentos_pedidos
        FOREIGN KEY (pedido_id)
        REFERENCES pedidos(id)
)
GO

-- SELECT DE CONFIRMAÇÃO -- 
-- Tabela UFs -- 
SELECT * FROM ufs uf ORDER BY uf.nome;

-- Tabela Cidades --
SELECT * FROM cidades cid ORDER BY cid.nome;

-- Tabela Ceps --
SELECT * FROM ceps cep ORDER BY cep.numero_cep;

-- Tabela Usuarios --
SELECT * FROM usuarios usu ORDER BY usu.nome;

-- Tabela Clientes --
SELECT * FROM clientes cli ORDER BY cli.data_nascimento;

-- Tabela Funcionarios --
SELECT * FROM funcionarios func ORDER BY func.codigo_funcionario;

-- Tabela Categorias --
SELECT * FROM categorias cat ORDER BY cat.nome;

-- Tabela Generos --
SELECT * FROM generos gen ORDER BY gen.nome;

-- Tabela Produtos --
SELECT * FROM produtos prod ORDER BY prod.nome;

-- Tabela Imagem_Produtos --
SELECT * FROM imagem_produtos imag ORDER BY imag.produto_id;

-- Tabela Entregas --
SELECT * FROM entregas ent ORDER BY ent.transportadora;

-- Tabela Pedidos --
SELECT * FROM pedidos pe ORDER BY pe.total DESC;

-- Tabela Item_Pedidos --
SELECT pe.id ID_Pedido, prod.nome Produto, pe.total Total_Pedido  
FROM produtos prod, pedidos pe, item_pedidos item 
WHERE 
    item.pedido_id = pe.id AND
    item.produto_id = prod.id 
ORDER BY pe.id;

-- Tabela Pagamentos --
SELECT * FROM pagamentos pag ORDER BY pag.pedido_id;