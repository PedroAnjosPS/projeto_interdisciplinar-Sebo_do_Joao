-- INSERTS DO BANCO DE DADOS - SEBO DO JOÃO --
-- 10 INSERTS PARA CADA TABELA --
-- Tabela UFs --
INSERT INTO ufs (sigla, nome) VALUES 
('SP', 'São Paulo'),
('RJ', 'Rio de Janeiro'),
('MG', 'Minas Gerais'),
('PR', 'Paraná'),
('RS', 'Rio Grande do Sul'),
('BA', 'Bahia'),
('SC', 'Santa Catarina'),
('GO', 'Goiás'),
('PE', 'Pernambuco'),
('ES', 'Espírito Santo');
GO

-- Tabela Cidades -- 
INSERT INTO cidades (nome, uf_sigla) VALUES 
('São José do Rio Preto', 'SP'),
('Campinas', 'SP'),
('Rio de Janeiro', 'RJ'),
('Belo Horizonte', 'MG'),
('Curitiba', 'PR'),
('Porto Alegre', 'RS'),
('Salvador', 'BA'),
('Florianópolis', 'SC'),
('Goiânia', 'GO'),
('Recife', 'PE');
GO

-- Tabela Ceps --
INSERT INTO ceps (numero_cep, cidade_id) VALUES 
('15010-000', 1),
('13010-000', 2),
('20010-000', 3),
('30110-000', 4),
('80010-000', 5),
('90010-000', 6),
('40010-000', 7),
('88010-000', 8),
('74000-000', 9),
('50010-000', 10);
GO

-- Tabela Usuarios --
INSERT INTO usuarios (nome, email, senha, status, logradouro, bairro, telefone, cep_numero) VALUES 
('João da Silva', 'joao.silva@email.com', 'hash123', 1, 'Rua das Flores, 100', 'Centro', '(17) 99111-2222', '15010-000'),
('Maria Oliveira', 'maria.oliveira@email.com', 'hash123', 1, 'Av. Paulista, 500', 'Bela Vista', '(11) 98222-3333', '13010-000'),
('Carlos Souza', 'carlos.souza@email.com', 'hash123', 1, 'Rua da Praia, 20', 'Copacabana', '(21) 97333-4444', '20010-000'),
('Ana Paula', 'ana.paula@email.com', 'hash123', 1, 'Rua Bahia, 45', 'Savassi', '(31) 96444-5555', '30110-000'),
('Lucas Mendes', 'lucas.mendes@email.com', 'hash123', 1, 'Rua XV, 300', 'Centro', '(41) 95555-6666', '80010-000'),
('Beatriz Lima', 'beatriz.lima@email.com', 'hash123', 1, 'Av. Ipiranga, 1000', 'Menino Deus', '(51) 94666-7777', '90010-000'),
('Ana Paula (Dona)', 'ana.dona@sebodojoao.com', 'admin123', 1, 'Rua XV de Novembro, 300', 'Centro', '(17) 99444-5555', '15010-000'),
('Pedro Atendente', 'pedro.func@sebodojoao.com', 'func123', 1, 'Rua Tiradentes, 150', 'Boa Vista', '(17) 99555-6666', '15010-000'),
('Marcos Supervisor', 'marcos.sup@sebodojoao.com', 'func123', 1, 'Rua Voluntários, 89', 'Centro', '(17) 99666-7777', '15010-000'),
('Juliana Estoque', 'juliana.est@sebodojoao.com', 'func123', 1, 'Rua Redentora, 450', 'Redentora', '(17) 99777-8888', '15010-000');
GO

-- Tabela Clientes --
INSERT INTO clientes (usuario_id, data_nascimento, cpf) VALUES 
(1, '1985-04-12', '111.222.333-44'),
(2, '1992-09-25', '222.333.444-55'),
(3, '1978-12-03', '333.444.555-66'),
(4, '1995-06-18', '444.555.666-77'),
(5, '1988-11-30', '555.666.777-88'),
(6, '2000-01-15', '666.777.888-99');
GO

-- Tabela Funcionarios --
INSERT INTO funcionarios (usuario_id, codigo_funcionario, admin) VALUES 
(7, 'FUNC-001', 1),
(8, 'FUNC-002', 0),
(9, 'FUNC-003', 0),
(10, 'FUNC-004', 0);
GO

-- Tabela Categorias --
INSERT INTO categorias (nome) VALUES 
('Livros'),
('HQs'),
('Disco de Vinil'),
('Blu-rays'),
('DVDs'),
('CDs'),
('Fitas Cassetes')
GO

-- Tabela Generos --
INSERT INTO generos (nome) VALUES 
('Ficção Científica'),
('Terror'),
('Suspense'),
('Rock'),
('Heavy Metal'),
('Ação'),
('Aventura'),
('Rap'),
('Didático'),
('Biografia')
GO

-- Tabela Produtos --
INSERT INTO produtos (nome, descricao, estoque, preco, status, categoria_id, genero_id, funcionario_id) VALUES 
-- Categoria: Livros (1)
('Dom Casmurro', 'Edição clássica de Machado de Assis.', 5, 35.00, 1, 1, 9, 7),         -- Gênero: Didático (9)
('Neuromancer', 'Obra-prima de William Gibson.', 3, 45.50, 1, 1, 1, 7),                -- Gênero: Ficção Científica (1)
('Breve História do Tempo', 'Física por Stephen Hawking.', 2, 40.00, 1, 1, 9, 7),       -- Gênero: Didático (9)
-- Categoria: HQs (2)
('Batman: O Cavaleiro das Trevas', 'HQ clássica da DC Comics.', 4, 55.00, 1, 2, 6, 7), -- Gênero: Ação (6)
('The Sandman Vol. 1', 'Obra de Neil Gaiman.', 6, 60.00, 1, 2, 7, 7),                  -- Gênero: Aventura (7)
-- Categoria: Disco de Vinil (3)
('Vinil Pink Floyd - The Dark Side', 'Disco de vinil original em bom estado.', 2, 150.00, 1, 3, 4, 7), -- Gênero: Rock (4)
-- Categoria: Blu-rays (4)
('Blu-ray Matrix', 'Filme em alta definição.', 8, 45.00, 1, 4, 1, 7),                  -- Gênero: Ficção Científica (1)
-- Categoria: DVDs (5)
('DVD O Poderoso Chefão', 'Clássico do cinema.', 5, 30.00, 1, 5, 6, 7),                -- Gênero: Ação (6)
-- Categoria: CDs (6)
('CD Iron Maiden - Number of the Beast', 'CD original de Heavy Metal.', 4, 35.00, 1, 6, 5, 7), -- Gênero: Heavy Metal (5)
-- Categoria: Fitas Cassetes (7)
('Fita Cassete Eminem - The Eminem Show', 'Fita cassete raridade do rap.', 3, 40.00, 1, 7, 8, 7); -- Gênero: Rap (8)
GO

SELECT * FROM Produtos

-- Tabela Imagem_Produtos --
INSERT INTO imagem_produtos (caminho, produto_id) VALUES 
('/img/dom_casmurro.jpg', 1),
('/img/neuromancer.jpg', 2),
('/img/breve_historia.jpg', 3),
('/img/o_principe.jpg', 4),
('/img/pequeno_principe.jpg', 5),
('/img/babadook.jpg', 6),
('/img/minha_vida_ford.jpg', 7),
('/img/antologia_drummond.jpg', 8),
('/img/poder_habito.jpg', 9),
('/img/1984_orwell.jpg', 10);
GO

-- Tabela Entregas --
INSERT INTO entregas (transportadora, status, link_rastreio, codigo_rastreio, data_previsao_entrega) VALUES 
('Correios (PAC)', 1, 'https://rastreio.com/AB1', 'AB123456789BR', '2026-10-25'),
('Correios (SEDEX)', 1, 'https://rastreio.com/AB2', 'AB987654321BR', '2026-10-20'),
('Jadlog', 1, 'https://jadlog.com.br/JD1', 'JD123456789SP', '2026-10-22'),
('Loggi', 1, 'https://loggi.com/LG1', 'LG987654321SP', '2026-10-21'),
('Correios (PAC)', 1, 'https://rastreio.com/AB3', 'AB111222333BR', '2026-10-28'),
('Jadlog', 1, 'https://jadlog.com.br/JD2', 'JD444555666SP', '2026-10-24'),
('Loggi', 1, 'https://loggi.com/LG2', 'LG777888999SP', '2026-10-23'),
('Correios (SEDEX)', 1, 'https://rastreio.com/AB4', 'AB999888777BR', '2026-10-19'),
('Jadlog', 1, 'https://jadlog.com.br/JD3', 'JD333222111SP', '2026-10-26'),
('Correios (PAC)', 1, 'https://rastreio.com/AB5', 'AB555444333BR', '2026-10-30');
GO

-- Tabela Pedidos --
INSERT INTO pedidos (total, status, entrega_id, cliente_id, funcionario_id) VALUES 
(80.50, 1, 1, 1, 7),
(45.50, 1, 2, 2, 8),
(40.00, 1, 3, 3, 9),
(25.00, 1, 4, 4, 10),
(20.00, 1, 5, 5, 7),
(50.00, 1, 6, 6, 8),
(38.00, 1, 7, 1, 9),
(30.00, 1, 8, 2, 10),
(42.00, 1, 9, 3, 7),
(49.90, 1, 10, 4, 8);
GO

select * from pedidos
select * from produtos

-- Tabela Item_Pedidos --
INSERT INTO item_pedidos (pedido_id, produto_id, quantidade, preco_unitario) VALUES 
(1, 1, 1, 35.00),
(1, 2, 1, 45.50),
(2, 2, 1, 45.50),
(3, 3, 1, 40.00),
(4, 4, 1, 25.00),
(5, 5, 1, 20.00),
(6, 6, 1, 50.00),
(7, 7, 1, 38.00),
(8, 8, 1, 30.00),
(9, 9, 1, 42.00);
GO

-- Tabela Pagamentos --
INSERT INTO pagamentos (numero_parcela, status, valor, quantidade_parcelas, forma_pagamento, data_vencimento, pedido_id) VALUES 
(1, 1, 80.50, 1, 'PIX', '2026-10-15', 1),
(1, 1, 45.50, 1, 'CARTAO_CREDITO', '2026-10-15', 2),
(1, 1, 40.00, 1, 'BOLETO', '2026-10-18', 3),
(1, 1, 25.00, 1, 'PIX', '2026-10-15', 4),
(1, 1, 20.00, 1, 'CARTAO_DEBITO', '2026-10-15', 5),
(1, 1, 50.00, 1, 'PIX', '2026-10-15', 6),
(1, 1, 38.00, 1, 'BOLETO', '2026-10-20', 7),
(1, 1, 30.00, 1, 'CARTAO_CREDITO', '2026-10-15', 8),
(1, 1, 42.00, 1, 'PIX', '2026-10-15', 9),
(1, 1, 49.90, 1, 'CARTAO_CREDITO', '2026-10-15', 10);
GO