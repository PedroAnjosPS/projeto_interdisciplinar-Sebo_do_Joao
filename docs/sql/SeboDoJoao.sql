CREATE DATABASE Sebo_do_Joao
GO

USE Sebo_do_Joao
GO

CREATE TABLE ufs
(
    sigla VARCHAR(2) NOT NULL,
    nome VARCHAR(100) NOT NULL,

    CONSTRAINT pk_ufs PRIMARY KEY (sigla)
)
GO

CREATE TABLE cidades
(
    id INT NOT NULL IDENTITY,
    nome VARCHAR(100) NOT NULL,
    uf_sigla VARCHAR(2) NOT NULL,

    CONSTRAINT pk_cidades PRIMARY KEY (id),
    CONSTRAINT fk_cidades_ufs FOREIGN KEY (uf_sigla)
    REFERENCES ufs(sigla)
)
GO

CREATE TABLE ceps
(
    nr VARCHAR(10) NOT NULL,
    cidade_id INT NOT NULL,

    CONSTRAINT pk_ceps PRIMARY KEY (nr),
    CONSTRAINT fk_ceps_cidades FOREIGN KEY (cidade_id)
    REFERENCES cidades(id)
)
GO

CREATE TABLE usuarios
(
    id INT NOT NULL IDENTITY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    status INT NOT NULL DEFAULT 1,
    logradouro VARCHAR(255),
    bairro VARCHAR(100),
    telefone VARCHAR(20),
    cep_nr VARCHAR(10) NOT NULL,

    CONSTRAINT pk_usuarios PRIMARY KEY (id),
    CONSTRAINT uq_usuarios_email UNIQUE (email),

    CONSTRAINT fk_usuarios_ceps FOREIGN KEY (cep_nr)
        REFERENCES ceps(nr),

    CONSTRAINT ck_usuarios_status
        CHECK (status IN (1,2,3,4))
)
GO

CREATE TABLE clientes
(
    usuario_id INT NOT NULL,
    data_nascimento DATE NOT NULL,
    cpf VARCHAR(14) NOT NULL,

    CONSTRAINT pk_clientes PRIMARY KEY (usuario_id),
    CONSTRAINT uq_clientes_cpf
        UNIQUE (cpf),
    CONSTRAINT fk_clientes_usuarios
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id)
)
GO

CREATE TABLE funcionarios
(
    usuario_id INT NOT NULL,
    funcionario_cod VARCHAR(50) NOT NULL,

    CONSTRAINT pk_funcionarios PRIMARY KEY (usuario_id),
    CONSTRAINT uq_funcionarios_codigo
        UNIQUE (funcionario_cod),
    CONSTRAINT fk_funcionarios_usuarios
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id)
)
GO

CREATE TABLE categorias
(
    id INT NOT NULL IDENTITY,
    nome VARCHAR(100) NOT NULL,

    CONSTRAINT pk_categorias PRIMARY KEY (id)
)
GO

CREATE TABLE generos
(
    id INT NOT NULL IDENTITY,
    nome VARCHAR(100) NOT NULL,

    CONSTRAINT pk_generos PRIMARY KEY (id)
)
GO

CREATE TABLE produtos
(
    id INT NOT NULL IDENTITY,

    nome VARCHAR(100) NOT NULL,
    tipo VARCHAR(50),
    descricao VARCHAR(MAX),
    estoque INT NOT NULL DEFAULT 0,
    preco DECIMAL(10,2) NOT NULL,
    status INT NOT NULL DEFAULT 1,
    categoria_id INT NOT NULL,
    genero_id INT NOT NULL,
    funcionario_id INT NOT NULL,

    created_at DATETIME DEFAULT GETDATE(),

    CONSTRAINT pk_produtos PRIMARY KEY (id),

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
    id INT NOT NULL IDENTITY,
    caminho VARCHAR(255) NOT NULL,
    produto_id INT NOT NULL,

    CONSTRAINT pk_imagem_produtos PRIMARY KEY (id),

    CONSTRAINT fk_imagem_produtos_produtos
        FOREIGN KEY (produto_id)
        REFERENCES produtos(id)
)
GO

CREATE TABLE entregas
(
    id INT NOT NULL IDENTITY,

    transportadora VARCHAR(100),
    status INT DEFAULT 1,

    link_rastreio VARCHAR(255),
    codigo_rastreio VARCHAR(100),

    data_postagem DATETIME DEFAULT GETDATE(),
    data_entrega DATETIME,
    data_previsao_entrega DATETIME,

    CONSTRAINT pk_entregas PRIMARY KEY (id),

    CONSTRAINT ck_entregas_status
        CHECK (status IN (1,2,3,4,5,6))
)
GO

CREATE TABLE pedidos
(
    id INT NOT NULL IDENTITY,
    data DATETIME NOT NULL DEFAULT GETDATE(),
    total DECIMAL(10,2) NOT NULL DEFAULT 0,
    status INT NOT NULL DEFAULT 1,
    entrega_id INT UNIQUE,
    cliente_id INT NOT NULL,
    funcionario_id INT NOT NULL,

    CONSTRAINT pk_pedidos PRIMARY KEY (id),

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
        REFERENCES pedidos(id),

    CONSTRAINT fk_item_pedidos_produtos
        FOREIGN KEY (produto_id)
        REFERENCES produtos(id)
)
GO

CREATE TABLE pagamentos
(
    id INT NOT NULL IDENTITY,
    numero_parcela INT NOT NULL,
    status INT NOT NULL DEFAULT 1,
    valor DECIMAL(10,2) NOT NULL,
    quantidade_parcelas INT NOT NULL,
    data_vencimento DATE NOT NULL,
    data_pagamento DATE,
    pedido_id INT NOT NULL,

    CONSTRAINT pk_pagamentos PRIMARY KEY (id),

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



-- 10 INSERTS PARA CADA TABELA --
-- Tabela UFs --
INSERT INTO ufs (sigla, nome) VALUES ('SP', 'São Paulo');
INSERT INTO ufs (sigla, nome) VALUES ('RJ', 'Rio de Janeiro');
INSERT INTO ufs (sigla, nome) VALUES ('MG', 'Minas Gerais');
INSERT INTO ufs (sigla, nome) VALUES ('ES', 'Espírito Santo');
INSERT INTO ufs (sigla, nome) VALUES ('PR', 'Paraná');
INSERT INTO ufs (sigla, nome) VALUES ('SC', 'Santa Catarina');
INSERT INTO ufs (sigla, nome) VALUES ('RS', 'Rio Grande do Sul');
INSERT INTO ufs (sigla, nome) VALUES ('BA', 'Bahia');
INSERT INTO ufs (sigla, nome) VALUES ('PE', 'Pernambuco');
INSERT INTO ufs (sigla, nome) VALUES ('CE', 'Ceará');
GO

-- Tabela Cidades -- 
INSERT INTO cidades (nome, uf_sigla) VALUES ('São Paulo', 'SP');
INSERT INTO cidades (nome, uf_sigla) VALUES ('Campinas', 'SP');
INSERT INTO cidades (nome, uf_sigla) VALUES ('Rio de Janeiro', 'RJ');
INSERT INTO cidades (nome, uf_sigla) VALUES ('Niterói', 'RJ');
INSERT INTO cidades (nome, uf_sigla) VALUES ('Belo Horizonte', 'MG');
INSERT INTO cidades (nome, uf_sigla) VALUES ('Curitiba', 'PR');
INSERT INTO cidades (nome, uf_sigla) VALUES ('Florianópolis', 'SC');
INSERT INTO cidades (nome, uf_sigla) VALUES ('Porto Alegre', 'RS');
INSERT INTO cidades (nome, uf_sigla) VALUES ('Salvador', 'BA');
INSERT INTO cidades (nome, uf_sigla) VALUES ('Recife', 'PE');
GO

-- Tabela Ceps --
INSERT INTO ceps (nr, cidade_id) VALUES ('01001-000', 1); -- São Paulo
INSERT INTO ceps (nr, cidade_id) VALUES ('13010-000', 2); -- Campinas
INSERT INTO ceps (nr, cidade_id) VALUES ('20040-000', 3); -- Rio de Janeiro
INSERT INTO ceps (nr, cidade_id) VALUES ('24020-000', 4); -- Niterói
INSERT INTO ceps (nr, cidade_id) VALUES ('30130-000', 5); -- Belo Horizonte
INSERT INTO ceps (nr, cidade_id) VALUES ('80010-000', 6); -- Curitiba
INSERT INTO ceps (nr, cidade_id) VALUES ('88010-000', 7); -- Florianópolis
INSERT INTO ceps (nr, cidade_id) VALUES ('90010-000', 8); -- Porto Alegre
INSERT INTO ceps (nr, cidade_id) VALUES ('40010-000', 9); -- Salvador
INSERT INTO ceps (nr, cidade_id) VALUES ('50010-000', 10); -- Recife
GO

-- Tabela Usuarios --
INSERT INTO usuarios (nome, email, senha, status, logradouro, bairro, telefone, cep_nr) 
VALUES ('João Silva', 'joao.silva@email.com', 'hash_senha_1', 1, 'Praça da Sé, 1', 'Sé', '(11) 98765-0001', '01001-000');

INSERT INTO usuarios (nome, email, senha, status, logradouro, bairro, telefone, cep_nr) 
VALUES ('Maria Santos', 'maria.santos@email.com', 'hash_senha_2', 2, 'Avenida Francisco Glicério, 100', 'Centro', '(19) 98765-0002', '13010-000');

INSERT INTO usuarios (nome, email, senha, status, logradouro, bairro, telefone, cep_nr) 
VALUES ('Carlos Oliveira', 'carlos.oliveira@email.com', 'hash_senha_3', 1, 'Avenida Rio Branco, 200', 'Centro', '(21) 98765-0003', '20040-000');

INSERT INTO usuarios (nome, email, senha, status, logradouro, bairro, telefone, cep_nr) 
VALUES ('Ana Costa', 'ana.costa@email.com', 'hash_senha_4', 3, 'Rua da Conceição, 50', 'Centro', '(21) 98765-0004', '24020-000');

INSERT INTO usuarios (nome, email, senha, status, logradouro, bairro, telefone, cep_nr) 
VALUES ('Pedro Souza', 'pedro.souza@email.com', 'hash_senha_5', 1, 'Praça Sete de Setembro, 300', 'Centro', '(31) 98765-0005', '30130-000');

INSERT INTO usuarios (nome, email, senha, status, logradouro, bairro, telefone, cep_nr) 
VALUES ('Lucas Pereira', 'lucas.pereira@email.com', 'hash_senha_6', 4, 'Rua XV de Novembro, 400', 'Centro', '(41) 98765-0006', '80010-000');

INSERT INTO usuarios (nome, email, senha, status, logradouro, bairro, telefone, cep_nr) 
VALUES ('Mariana Alves', 'mariana.alves@email.com', 'hash_senha_7', 1, 'Rua Felipe Schmidt, 500', 'Centro', '(48) 98765-0007', '88010-000');

INSERT INTO usuarios (nome, email, senha, status, logradouro, bairro, telefone, cep_nr) 
VALUES ('Juliana Lima', 'juliana.lima@email.com', 'hash_senha_8', 2, 'Rua dos Andradas, 600', 'Centro', '(51) 98765-0008', '90010-000');

INSERT INTO usuarios (nome, email, senha, status, logradouro, bairro, telefone, cep_nr) 
VALUES ('Roberto Carvalho', 'roberto.carvalho@email.com', 'hash_senha_9', 1, 'Avenida Sete de Setembro, 700', 'Dois de Julho', '(71) 98765-0009', '40010-000');

INSERT INTO usuarios (nome, email, senha, status, logradouro, bairro, telefone, cep_nr) 
VALUES ('Fernanda Gomes', 'fernanda.gomes@email.com', 'hash_senha_0', 3, 'Avenida Conde da Boa Vista, 800', 'Boa Vista', '(81) 98765-0010', '50010-000');
GO

-- Tabela Clientes --
INSERT INTO clientes (usuario_id, data_nascimento, cpf) VALUES (1, '1990-05-14', '111.222.333-44');
INSERT INTO clientes (usuario_id, data_nascimento, cpf) VALUES (2, '1985-08-22', '222.333.444-55');
INSERT INTO clientes (usuario_id, data_nascimento, cpf) VALUES (3, '1992-11-10', '333.444.555-66');
INSERT INTO clientes (usuario_id, data_nascimento, cpf) VALUES (4, '1978-03-30', '444.555.666-77');
INSERT INTO clientes (usuario_id, data_nascimento, cpf) VALUES (5, '2000-01-25', '555.666.777-88');
INSERT INTO clientes (usuario_id, data_nascimento, cpf) VALUES (6, '1995-07-12', '666.777.888-99');
INSERT INTO clientes (usuario_id, data_nascimento, cpf) VALUES (7, '1988-09-05', '777.888.999-00');
INSERT INTO clientes (usuario_id, data_nascimento, cpf) VALUES (8, '1982-12-18', '888.999.000-11');
INSERT INTO clientes (usuario_id, data_nascimento, cpf) VALUES (9, '1998-04-02', '999.000.111-22');
INSERT INTO clientes (usuario_id, data_nascimento, cpf) VALUES (10, '1993-06-20', '000.111.222-33');
GO

-- Tabela Funcionarios --
INSERT INTO funcionarios (usuario_id, funcionario_cod) VALUES (1, 'FUNC-2026-001');
INSERT INTO funcionarios (usuario_id, funcionario_cod) VALUES (2, 'FUNC-2026-002');
INSERT INTO funcionarios (usuario_id, funcionario_cod) VALUES (3, 'FUNC-2026-003');
INSERT INTO funcionarios (usuario_id, funcionario_cod) VALUES (4, 'FUNC-2026-004');
INSERT INTO funcionarios (usuario_id, funcionario_cod) VALUES (5, 'FUNC-2026-005');
INSERT INTO funcionarios (usuario_id, funcionario_cod) VALUES (6, 'FUNC-2026-006');
INSERT INTO funcionarios (usuario_id, funcionario_cod) VALUES (7, 'FUNC-2026-007');
INSERT INTO funcionarios (usuario_id, funcionario_cod) VALUES (8, 'FUNC-2026-008');
INSERT INTO funcionarios (usuario_id, funcionario_cod) VALUES (9, 'FUNC-2026-009');
INSERT INTO funcionarios (usuario_id, funcionario_cod) VALUES (10, 'FUNC-2026-010');
GO

-- Tabela Categorias --
INSERT INTO categorias (nome) VALUES ('Mangás');
INSERT INTO categorias (nome) VALUES ('Fitas Cassete');
INSERT INTO categorias (nome) VALUES ('Histórias em Quadrinhos (Ocidente)');
INSERT INTO categorias (nome) VALUES ('Livros');
INSERT INTO categorias (nome) VALUES ('Blu-rays');
INSERT INTO categorias (nome) VALUES ('DVDs');
INSERT INTO categorias (nome) VALUES ('CDs');
INSERT INTO categorias (nome) VALUES ('Discos de Vinil');
INSERT INTO categorias (nome) VALUES ('Revistas');
INSERT INTO categorias (nome) VALUES ('Apostilas e Didáticos');
GO

-- Tabela Generos --
INSERT INTO generos (nome) VALUES ('Ficção Científica');
INSERT INTO generos (nome) VALUES ('Terror');
INSERT INTO generos (nome) VALUES ('Suspense');
INSERT INTO generos (nome) VALUES ('Rock');
INSERT INTO generos (nome) VALUES ('Heavy Metal');
INSERT INTO generos (nome) VALUES ('Ação');
INSERT INTO generos (nome) VALUES ('Aventura');
INSERT INTO generos (nome) VALUES ('Rap');
INSERT INTO generos (nome) VALUES ('Didático');
INSERT INTO generos (nome) VALUES ('Biografia');
GO

-- Tabela Produtos --
INSERT INTO produtos (nome, tipo, descricao, estoque, preco, status, categoria_id, genero_id, funcionario_id) 
VALUES ('Jantar Secreto', 'Novo', 'Romance instigante e perturbador com reviravoltas chocantes.', 5, 45.90, 1, 4, 3, 1); -- Livros / Suspense

INSERT INTO produtos (nome, tipo, descricao, estoque, preco, status, categoria_id, genero_id, funcionario_id) 
VALUES ('CD Metallica - Master of Puppets', 'Usado', 'Clássico absoluto do thrash metal em excelente estado de conservação.', 2, 35.00, 1, 7, 5, 2); -- CDs / Heavy Metal

INSERT INTO produtos (nome, tipo, descricao, estoque, preco, status, categoria_id, genero_id, funcionario_id) 
VALUES ('Vinil Guns N'' Roses - Appetite for Destruction', 'Usado', 'Disco de vinil original da época. Capa apresenta leves desgastes.', 1, 150.00, 2, 8, 4, 3); -- Discos de Vinil / Rock

INSERT INTO produtos (nome, tipo, descricao, estoque, preco, status, categoria_id, genero_id, funcionario_id) 
VALUES ('Alien: A História Ilustrada', 'Usado', 'Adaptação em quadrinhos do clássico do cinema, mídia física rara.', 3, 60.00, 1, 3, 1, 4); -- HQ (Ocidente) / Ficção Científica

INSERT INTO produtos (nome, tipo, descricao, estoque, preco, status, categoria_id, genero_id, funcionario_id) 
VALUES ('Spring Boot em Ação', 'Usado', 'Livro essencial para desenvolvimento de APIs e backend.', 4, 85.50, 1, 10, 9, 5); -- Apostilas e Didáticos / Didático

INSERT INTO produtos (nome, tipo, descricao, estoque, preco, status, categoria_id, genero_id, funcionario_id) 
VALUES ('HQ Homem-Aranha: A Última Caçada de Kraven', 'Usado', 'Edição encadernada em capa dura. Item de colecionador.', 2, 75.00, 3, 3, 6, 6); -- HQ (Ocidente) / Ação

INSERT INTO produtos (nome, tipo, descricao, estoque, preco, status, categoria_id, genero_id, funcionario_id) 
VALUES ('Mangá One Piece Vol. 1', 'Novo', 'Primeiro volume da aclamada série dos mares.', 10, 29.90, 1, 1, 7, 7); -- Mangás / Aventura

INSERT INTO produtos (nome, tipo, descricao, estoque, preco, status, categoria_id, genero_id, funcionario_id) 
VALUES ('DVD The Babadook', 'Usado', 'Filme de terror psicológico em edição especial (ótimo para quem não acha no streaming).', 1, 25.00, 1, 6, 2, 8); -- DVDs / Terror

INSERT INTO produtos (nome, tipo, descricao, estoque, preco, status, categoria_id, genero_id, funcionario_id) 
VALUES ('Dungeons & Dragons: Livro do Jogador', 'Usado', 'Manual básico e regras para iniciar no RPG de mesa.', 2, 120.00, 2, 4, 7, 9); -- Livros / Aventura (substituindo RPG)

INSERT INTO produtos (nome, tipo, descricao, estoque, preco, status, categoria_id, genero_id, funcionario_id) 
VALUES ('Autobiografia Rita Lee', 'Novo', 'Edição com fotos exclusivas da rainha do rock nacional.', 3, 55.00, 1, 4, 10, 10); -- Livros / Biografia
GO

-- Tabela Imagem_Produtos --
INSERT INTO imagem_produtos (caminho, produto_id) VALUES ('/assets/images/produtos/livros/jantar_secreto_capa.jpg', 1);
INSERT INTO imagem_produtos (caminho, produto_id) VALUES ('/assets/images/produtos/cds/metallica_master_puppets.png', 2);
INSERT INTO imagem_produtos (caminho, produto_id) VALUES ('/assets/images/produtos/vinil/gnr_appetite_destruction.jpg', 3);
INSERT INTO imagem_produtos (caminho, produto_id) VALUES ('/assets/images/produtos/hqs/alien_ilustrada_frente.jpg', 4);
INSERT INTO imagem_produtos (caminho, produto_id) VALUES ('/assets/images/produtos/didaticos/spring_boot_acao_2ed.png', 5);
INSERT INTO imagem_produtos (caminho, produto_id) VALUES ('/assets/images/produtos/hqs/homem_aranha_kraven.jpg', 6);
INSERT INTO imagem_produtos (caminho, produto_id) VALUES ('/assets/images/produtos/mangas/one_piece_v1_br.jpg', 7);
INSERT INTO imagem_produtos (caminho, produto_id) VALUES ('/assets/images/produtos/dvds/the_babadook_dvd_br.jpg', 8);
INSERT INTO imagem_produtos (caminho, produto_id) VALUES ('/assets/images/produtos/livros/dnd_livro_jogador_5e.png', 9);
INSERT INTO imagem_produtos (caminho, produto_id) VALUES ('/assets/images/produtos/livros/rita_lee_autobiografia.jpg', 10);
GO

-- Tabela Entregas --
INSERT INTO entregas (transportadora, status, link_rastreio, codigo_rastreio, data_postagem, data_entrega, data_previsao_entrega) 
VALUES ('Correios - SEDEX', 5, 'https://rastreamento.correios.com.br', 'BR123456789BR', '2026-05-10 10:00:00', '2026-05-12 14:30:00', '2026-05-14 23:59:00');

INSERT INTO entregas (transportadora, status, link_rastreio, codigo_rastreio, data_postagem, data_entrega, data_previsao_entrega) 
VALUES ('Loggi', 3, 'https://www.loggi.com/rastreador/', 'LOGGI987654321', '2026-05-25 09:15:00', NULL, '2026-05-29 18:00:00');

INSERT INTO entregas (transportadora, status, link_rastreio, codigo_rastreio, data_previsao_entrega) 
VALUES ('Jadlog', 1, NULL, NULL, '2026-06-02 18:00:00'); 

INSERT INTO entregas (transportadora, status, link_rastreio, codigo_rastreio, data_postagem, data_entrega, data_previsao_entrega) 
VALUES ('Correios - PAC', 5, 'https://rastreamento.correios.com.br', 'BR987654321BR', '2026-05-01 11:20:00', '2026-05-08 16:45:00', '2026-05-10 23:59:00');

INSERT INTO entregas (transportadora, status, link_rastreio, codigo_rastreio, data_postagem, data_entrega, data_previsao_entrega) 
VALUES ('Total Express', 6, 'https://tracking.totalexpress.com.br/', 'TX123456', '2026-05-15 14:00:00', NULL, '2026-05-20 18:00:00');

INSERT INTO entregas (transportadora, status, link_rastreio, codigo_rastreio, data_postagem, data_entrega, data_previsao_entrega) 
VALUES ('Loggi', 4, 'https://www.loggi.com/rastreador/', 'LOGGI11223344', '2026-05-26 08:30:00', NULL, '2026-05-28 18:00:00');

INSERT INTO entregas (transportadora, status, link_rastreio, codigo_rastreio, data_postagem, data_entrega, data_previsao_entrega) 
VALUES ('Jadlog', 2, 'https://www.jadlog.com.br/tracking', 'JAD55667788', '2026-05-27 10:00:00', NULL, '2026-06-05 18:00:00');

INSERT INTO entregas (transportadora, status, link_rastreio, codigo_rastreio, data_postagem, data_entrega, data_previsao_entrega) 
VALUES ('Correios - SEDEX', 5, 'https://rastreamento.correios.com.br', 'BR555444333BR', '2026-04-20 09:00:00', '2026-04-22 11:15:00', '2026-04-25 23:59:00');

INSERT INTO entregas (transportadora, status, link_rastreio, codigo_rastreio, data_postagem, data_entrega, data_previsao_entrega) 
VALUES ('Azul Cargo', 3, 'https://www.azulcargoexpress.com.br/', 'AZUL998877', '2026-05-24 16:20:00', NULL, '2026-05-30 18:00:00');

INSERT INTO entregas (transportadora, status, link_rastreio, codigo_rastreio, data_postagem, data_entrega, data_previsao_entrega) 
VALUES ('Correios - PAC', 5, 'https://rastreamento.correios.com.br', 'BR111222333BR', '2026-05-05 13:40:00', '2026-05-15 15:10:00', '2026-05-18 23:59:00');
GO

-- Tabela Pedidos --
INSERT INTO pedidos (data, total, status, entrega_id, cliente_id, funcionario_id) 
VALUES ('2026-05-09 14:20:00', 45.90, 5, 1, 1, 2);

INSERT INTO pedidos (data, total, status, entrega_id, cliente_id, funcionario_id) 
VALUES ('2026-05-24 10:15:00', 150.00, 3, 2, 2, 3);

INSERT INTO pedidos (total, status, entrega_id, cliente_id, funcionario_id) 
VALUES (60.00, 1, 3, 3, 4);

INSERT INTO pedidos (data, total, status, entrega_id, cliente_id, funcionario_id) 
VALUES ('2026-04-30 11:10:00', 85.50, 5, 4, 4, 5);

INSERT INTO pedidos (data, total, status, entrega_id, cliente_id, funcionario_id) 
VALUES ('2026-05-14 16:45:00', 75.00, 4, 5, 5, 6);

INSERT INTO pedidos (data, total, status, entrega_id, cliente_id, funcionario_id) 
VALUES ('2026-05-25 09:00:00', 29.90, 3, 6, 6, 7);

INSERT INTO pedidos (data, total, status, entrega_id, cliente_id, funcionario_id) 
VALUES ('2026-05-26 15:30:00', 25.00, 2, 7, 7, 8);

INSERT INTO pedidos (data, total, status, entrega_id, cliente_id, funcionario_id) 
VALUES ('2026-04-19 14:20:00', 120.00, 5, 8, 8, 9);

INSERT INTO pedidos (data, total, status, entrega_id, cliente_id, funcionario_id) 
VALUES ('2026-05-23 11:11:00', 55.00, 3, 9, 9, 10);

INSERT INTO pedidos (total, status, entrega_id, cliente_id, funcionario_id) 
VALUES (35.00, 5, 10, 10, 1);
GO

-- Tabela Item_Pedidos --
-- Pedido 1: Livro Jantar Secreto
INSERT INTO item_pedidos (pedido_id, produto_id, quantidade, preco_unitario) 
VALUES (1, 1, 1, 45.90);

-- Pedido 2: Vinil Guns N' Roses
INSERT INTO item_pedidos (pedido_id, produto_id, quantidade, preco_unitario) 
VALUES (2, 3, 1, 150.00);

-- Pedido 3: HQ Alien: A História Ilustrada
INSERT INTO item_pedidos (pedido_id, produto_id, quantidade, preco_unitario) 
VALUES (3, 4, 1, 60.00);

-- Pedido 4: Livro Spring Boot em Ação
INSERT INTO item_pedidos (pedido_id, produto_id, quantidade, preco_unitario) 
VALUES (4, 5, 1, 85.50);

-- Pedido 5: HQ Homem-Aranha: A Última Caçada de Kraven
INSERT INTO item_pedidos (pedido_id, produto_id, quantidade, preco_unitario) 
VALUES (5, 6, 1, 75.00);

-- Pedido 6: Mangá One Piece Vol. 1
INSERT INTO item_pedidos (pedido_id, produto_id, quantidade, preco_unitario) 
VALUES (6, 7, 1, 29.90);

-- Pedido 7: DVD The Babadook
INSERT INTO item_pedidos (pedido_id, produto_id, quantidade, preco_unitario) 
VALUES (7, 8, 1, 25.00);

-- Pedido 8: Dungeons & Dragons: Livro do Jogador
INSERT INTO item_pedidos (pedido_id, produto_id, quantidade, preco_unitario) 
VALUES (8, 9, 1, 120.00);

-- Pedido 9: Autobiografia Rita Lee
INSERT INTO item_pedidos (pedido_id, produto_id, quantidade, preco_unitario) 
VALUES (9, 10, 1, 55.00);

-- Pedido 10: CD Metallica - Master of Puppets
INSERT INTO item_pedidos (pedido_id, produto_id, quantidade, preco_unitario) 
VALUES (10, 2, 1, 35.00);
GO

-- Tabela Pagamentos --
-- Pedido 1 (R$ 45,90) - Pago à vista (1 parcela)
INSERT INTO pagamentos (numero_parcela, status, valor, quantidade_parcelas, data_vencimento, data_pagamento, pedido_id) 
VALUES (1, 2, 45.90, 1, '2026-05-12', '2026-05-09', 1);

-- Pedido 2 (R$ 150,00) - Dividido em 2 parcelas (Parcela 1 Paga, Parcela 2 Pendente)
INSERT INTO pagamentos (numero_parcela, status, valor, quantidade_parcelas, data_vencimento, data_pagamento, pedido_id) 
VALUES (1, 2, 75.00, 2, '2026-05-24', '2026-05-24', 2);

INSERT INTO pagamentos (numero_parcela, status, valor, quantidade_parcelas, data_vencimento, data_pagamento, pedido_id) 
VALUES (2, 1, 75.00, 2, '2026-06-24', NULL, 2);

-- Pedido 3 (R$ 60,00) - Pendente (Aguardando pagamento)
INSERT INTO pagamentos (numero_parcela, status, valor, quantidade_parcelas, data_vencimento, data_pagamento, pedido_id) 
VALUES (1, 1, 60.00, 1, '2026-06-05', NULL, 3);

-- Pedido 4 (R$ 85,50) - Pago à vista
INSERT INTO pagamentos (numero_parcela, status, valor, quantidade_parcelas, data_vencimento, data_pagamento, pedido_id) 
VALUES (1, 2, 85.50, 1, '2026-05-05', '2026-04-30', 4);

-- Pedido 5 (R$ 75,00) - Pago à vista
INSERT INTO pagamentos (numero_parcela, status, valor, quantidade_parcelas, data_vencimento, data_pagamento, pedido_id) 
VALUES (1, 2, 75.00, 1, '2026-05-17', '2026-05-15', 5);

-- Pedido 6 (R$ 29,90) - Pendente
INSERT INTO pagamentos (numero_parcela, status, valor, quantidade_parcelas, data_vencimento, data_pagamento, pedido_id) 
VALUES (1, 1, 29.90, 1, '2026-05-28', NULL, 6);

-- Pedido 8 (R$ 120,00) - Pago à vista
INSERT INTO pagamentos (numero_parcela, status, valor, quantidade_parcelas, data_vencimento, data_pagamento, pedido_id) 
VALUES (1, 2, 120.00, 1, '2026-04-22', '2026-04-19', 8);

-- Pedido 9 (R$ 55,00) - Status 3 (Simulando um pagamento Cancelado/Não Aprovado)
INSERT INTO pagamentos (numero_parcela, status, valor, quantidade_parcelas, data_vencimento, data_pagamento, pedido_id) 
VALUES (1, 3, 55.00, 1, '2026-05-26', NULL, 9);

-- Pedido 10 (R$ 35,00) - Pago à vista (usando a função GETDATE() para registrar o pagamento no momento da inserção)
INSERT INTO pagamentos (numero_parcela, status, valor, quantidade_parcelas, data_vencimento, data_pagamento, pedido_id) 
VALUES (1, 2, 35.00, 1, '2026-06-05', GETDATE(), 10);
GO


-- SELECT DE CONFIRMAÇÃO -- 
-- Tabela UFs -- 
SELECT * FROM ufs uf ORDER BY uf.nome;

-- Tabela Cidades --
SELECT * FROM cidades cid ORDER BY cid.nome;

-- Tabela Ceps --
SELECT * FROM ceps cep ORDER BY cep.nr;

-- Tabela Usuarios --
SELECT * FROM usuarios usu ORDER BY usu.nome;

-- Tabela Clientes --
SELECT * FROM clientes cli ORDER BY cli.data_nascimento;

-- Tabela Funcionarios --
SELECT * FROM funcionarios func ORDER BY func.funcionario_cod;

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
