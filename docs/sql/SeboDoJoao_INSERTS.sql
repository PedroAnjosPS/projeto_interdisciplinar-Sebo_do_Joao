-- INSERTS DO BANCO DE DADOS - SEBO DO JOÃO --
-- Os produtos, os pedidos, as entregas e os pagamentos vêm da planilha de vendas usada na análise estatística --
-- (docs/metodos-estatisticos/vendas_sebo_do_joao.xlsx): 130 vendas fictícias de maio a outubro de 2026. --

-- ORDEM DE EXECUÇÃO (banco novo, recém-criado): --
-- 1-) Script_DELETANDO-BANCO  2-) SeboDoJoao (cria banco e tabelas)  3-) ESTE ARQUIVO --
-- 4-) FUNCTIONS  5-) VIEWS  6-) TRIGGERS  7-) PROCEDURES --
-- Este arquivo deve rodar ANTES das triggers: os pedidos, o estoque e os status abaixo já estão --
-- prontos e coerentes. Com as triggers criadas, a baixa de estoque seria feita duas vezes. --
-- Os IDs dos pedidos e das entregas (1 a 130) assumem um banco novo, onde o IDENTITY começa em 1. --
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

-- Demais estados do Brasil, completando as 27 UFs. --
-- Ao salvar um endereço, o Java procura a UF informada nesta tabela (ela precisa existir) e --
-- cadastra sozinho a cidade e o CEP que ainda não estiverem cadastrados. --
INSERT INTO ufs (sigla, nome) VALUES
('AC', 'Acre'),
('AL', 'Alagoas'),
('AP', 'Amapá'),
('AM', 'Amazonas'),
('CE', 'Ceará'),
('DF', 'Distrito Federal'),
('MA', 'Maranhão'),
('MT', 'Mato Grosso'),
('MS', 'Mato Grosso do Sul'),
('PA', 'Pará'),
('PB', 'Paraíba'),
('PI', 'Piauí'),
('RN', 'Rio Grande do Norte'),
('RO', 'Rondônia'),
('RR', 'Roraima'),
('SE', 'Sergipe'),
('TO', 'Tocantins');
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

-- Cidades da planilha de vendas que ainda não estavam cadastradas (ids 11 a 13). --
INSERT INTO cidades (nome, uf_sigla) VALUES
('São Paulo', 'SP'),
('Ribeirão Preto', 'SP'),
('Brasília', 'DF');
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

-- CEPs das cidades novas e de mais dois bairros de São José do Rio Preto. --
INSERT INTO ceps (numero_cep, cidade_id) VALUES
('01310-100', 11),
('14010-000', 12),
('70040-010', 13),
('15015-000', 1),
('15090-000', 1);
GO


-- Tabela Usuarios --
-- A tabela usuarios guarda só os dados da conta. O endereço fica na tabela enderecos (mais abaixo), --
-- que pertence ao cliente. Os funcionários não têm endereço cadastrado. --
INSERT INTO usuarios (nome, email, senha, status, telefone) VALUES
('João da Silva', 'joao.silva@email.com', 'hash123', 1, '(17) 99111-2222'),
('Maria Oliveira', 'maria.oliveira@email.com', 'hash123', 1, '(11) 98222-3333'),
('Carlos Souza', 'carlos.souza@email.com', 'hash123', 1, '(21) 97333-4444'),
('Ana Paula', 'ana.paula@email.com', 'hash123', 1, '(31) 96444-5555'),
('Lucas Mendes', 'lucas.mendes@email.com', 'hash123', 1, '(41) 95555-6666'),
('Beatriz Lima', 'beatriz.lima@email.com', 'hash123', 1, '(51) 94666-7777'),
('Ana Paula (Dona)', 'ana.dona@sebodojoao.com', 'admin123', 1, '(17) 99444-5555'),
('Pedro Atendente', 'pedro.func@sebodojoao.com', 'func123', 1, '(17) 99555-6666'),
('Marcos Supervisor', 'marcos.sup@sebodojoao.com', 'func123', 1, '(17) 99666-7777'),
('Juliana Estoque', 'juliana.est@sebodojoao.com', 'func123', 1, '(17) 99777-8888');
GO

-- Clientes novos (ids 11 a 22), para que cada cidade da planilha de vendas tenha pelo menos um comprador. --
INSERT INTO usuarios (nome, email, senha, status, telefone) VALUES
('Rafael Teixeira', 'rafael.teixeira@email.com', 'hash123', 1, '(17) 99123-4501'),
('Camila Ferraz', 'camila.ferraz@email.com', 'hash123', 1, '(17) 99123-4502'),
('Diego Martins', 'diego.martins@email.com', 'hash123', 1, '(17) 99123-4503'),
('Fernanda Costa', 'fernanda.costa@email.com', 'hash123', 1, '(17) 99123-4504'),
('Thiago Alves', 'thiago.alves@email.com', 'hash123', 1, '(17) 99123-4505'),
('Patrícia Rocha', 'patricia.rocha@email.com', 'hash123', 1, '(11) 98123-4506'),
('Gustavo Nunes', 'gustavo.nunes@email.com', 'hash123', 1, '(19) 97123-4507'),
('Larissa Duarte', 'larissa.duarte@email.com', 'hash123', 1, '(16) 99123-4508'),
('Bruno Carvalho', 'bruno.carvalho@email.com', 'hash123', 1, '(62) 99123-4509'),
('Aline Barbosa', 'aline.barbosa@email.com', 'hash123', 1, '(61) 99123-4510'),
('Renato Pires', 'renato.pires@email.com', 'hash123', 1, '(71) 99123-4511'),
('Vanessa Moura', 'vanessa.moura@email.com', 'hash123', 1, '(21) 99123-4512');
GO


-- Tabela Clientes --
INSERT INTO clientes (usuario_id, data_nascimento, cpf) VALUES 
-- Os CPFs abaixo têm os dois dígitos verificadores corretos. O site recusa CPFs inválidos, --
-- então CPFs de exemplo com dígitos errados impediriam o cliente de salvar o perfil. --
(1, '1985-04-12', '111.222.333-96'),
(2, '1992-09-25', '222.333.444-05'),
(3, '1978-12-03', '333.444.555-08'),
(4, '1995-06-18', '444.555.666-19'),
(5, '1988-11-30', '555.666.777-20'),
(6, '2000-01-15', '666.777.888-30');
GO

-- CPFs dos clientes novos, também com os dois dígitos verificadores corretos. --
INSERT INTO clientes (usuario_id, data_nascimento, cpf) VALUES
(11, '1990-03-14', '300.087.109-88'),
(12, '1994-07-22', '300.095.028-15'),
(13, '1987-11-05', '300.102.947-16'),
(14, '1998-02-19', '300.110.866-53'),
(15, '1983-09-30', '300.118.785-92'),
(16, '1991-05-08', '300.126.704-64'),
(17, '1986-12-12', '300.134.623-00'),
(18, '1996-04-27', '300.142.542-39'),
(19, '1989-08-16', '300.150.461-76'),
(20, '1993-01-09', '300.158.380-05'),
(21, '1985-06-21', '300.166.299-90'),
(22, '1997-10-03', '300.174.218-61');
GO


-- Tabela Funcionarios --
INSERT INTO funcionarios (usuario_id, codigo_funcionario, admin) VALUES 
(7, 'FUNC-001', 1),
(8, 'FUNC-002', 0),
(9, 'FUNC-003', 0),
(10, 'FUNC-004', 0);
GO


-- Tabela Enderecos --
-- Endereços de entrega dos clientes (1 a 6). O João tem dois endereços, para mostrar que um --
-- cliente pode ter vários. "padrao" = 1 marca o endereço que já vem escolhido no checkout --
-- (só um por cliente). Estes endereços também são usados pelas entregas (coluna endereco_id). --
INSERT INTO enderecos (logradouro, numero, complemento, bairro, ativo, apelido, padrao, cep_numero, cliente_id) VALUES
('Rua das Flores',  '100',  NULL,       'Centro',      1, 'Casa',      1, '15010-000', 1), -- 1
('Av. Brasil',      '2500', 'Sala 12',  'Boa Vista',   1, 'Trabalho',  0, '15010-000', 1), -- 2
('Av. Paulista',    '500',  NULL,       'Bela Vista',  1, 'Casa',      1, '01310-100', 2), -- 3
('Rua da Praia',    '20',   'Apto 3',   'Copacabana',  1, 'Casa',      1, '20010-000', 3), -- 4
('Rua Bahia',       '45',   NULL,       'Savassi',     1, 'Casa',      1, '30110-000', 4), -- 5
('Rua XV',          '300',  NULL,       'Centro',      1, 'Casa',      1, '80010-000', 5), -- 6
('Av. Ipiranga',    '1000', 'Bloco B',  'Menino Deus', 1, 'Casa',      1, '90010-000', 6); -- 7
GO

-- Endereços dos clientes novos (um para cada cliente). --
INSERT INTO enderecos (logradouro, numero, complemento, bairro, ativo, apelido, padrao, cep_numero, cliente_id) VALUES
('Rua Bernardino de Campos', '850', NULL, 'Centro', 1, 'Casa', 1, '15010-000', 11), -- 8
('Av. Alberto Andaló', '3200', NULL, 'Vila Redentora', 1, 'Casa', 1, '15015-000', 12), -- 9
('Rua Independência', '415', NULL, 'Boa Vista', 1, 'Casa', 1, '15090-000', 13), -- 10
('Rua Voluntários de São Paulo', '1200', NULL, 'Centro', 1, 'Casa', 1, '15010-000', 14), -- 11
('Av. Brasil', '900', NULL, 'Boa Vista', 1, 'Casa', 1, '15015-000', 15), -- 12
('Rua Augusta', '1500', NULL, 'Consolação', 1, 'Casa', 1, '01310-100', 16), -- 13
('Rua Barão de Jaguara', '700', NULL, 'Centro', 1, 'Casa', 1, '13010-000', 17), -- 14
('Av. Presidente Vargas', '2100', NULL, 'Jardim América', 1, 'Casa', 1, '14010-000', 18), -- 15
('Av. Goiás', '300', NULL, 'Centro', 1, 'Casa', 1, '74000-000', 19), -- 16
('SQS 308 Bloco A', '12', NULL, 'Asa Sul', 1, 'Casa', 1, '70040-010', 20), -- 17
('Av. Sete de Setembro', '1500', NULL, 'Barra', 1, 'Casa', 1, '40010-000', 21), -- 18
('Rua Voluntários da Pátria', '200', NULL, 'Botafogo', 1, 'Casa', 1, '20010-000', 22); -- 19
GO


-- Tabela Cartoes --
-- Cartões salvos pelos clientes. Ficam guardados só a bandeira, os 4 últimos dígitos, o nome do titular --
-- e a validade (MM/AA). O número completo e o CVV nunca são guardados. "padrao" = 1: cartão já escolhido no checkout. --
INSERT INTO cartoes (bandeira, final_numero, titular, validade, padrao, cliente_id) VALUES
('visa',       '4242', 'JOAO DA SILVA',   '12/30', 1, 1),
('mastercard', '5555', 'MARIA OLIVEIRA',  '08/29', 1, 2),
('elo',        '6362', 'MARIA OLIVEIRA',  '03/30', 0, 2),
('visa',       '1111', 'CARLOS SOUZA',    '05/31', 1, 3);
GO

-- Tabela Categorias --
INSERT INTO categorias (nome) VALUES 
('Livros'),
('HQs'),
('Discos de Vinil'),
('Blu-rays'),
('DVDs'),
('CDs'),
('Fitas Cassetes')
GO

-- A planilha de vendas separa os mangás dos quadrinhos, então eles ganham uma categoria própria (id 8). --
INSERT INTO categorias (nome) VALUES ('Mangás');
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
('Biografia'),
('Romance'),
('Fantasia'),
('Drama'),
('Comédia'),
('Auto-ajuda'),
('Científico'),
('Documentário'),
('Pop'),
('MPB'),
('Jazz')
GO

-- Gêneros da planilha de vendas que ainda não existiam (ids 21 a 29). --
INSERT INTO generos (nome) VALUES
('Clássica'),
('Samba/Pagode'),
('Sertanejo'),
('Blues'),
('Poesia/Crônica'),
('HQ Independente'),
('HQ Super-heróis'),
('Mangá Shonen'),
('Mangá Seinen/Shojo');
GO

-- Tabela Produtos --
-- Cada título da planilha vira um produto por formato (o mesmo álbum em vinil, CD e fita são três produtos). --
-- O preço do produto é o da venda mais recente do título na planilha; o preço de cada venda fica em item_pedidos. --
-- O estoque informado é o inicial, antes das vendas: o que foi vendido (pedidos não cancelados) mais uma sobra de 1 a 5 unidades. --
-- Gêneros: onde a planilha tem dois (ex.: "Suspense/Terror") ficou o primeiro. --
INSERT INTO produtos (nome, descricao, estoque, preco, status, categoria_id, genero_id, funcionario_id) VALUES
('Vinil Purple Rain - Prince', 'Disco de vinil usado, em bom estado.', 5, 94.90, 1, 3, 18, 7), -- 1
('Vinil Elis & Tom - Elis Regina', 'Disco de vinil usado, em bom estado.', 8, 399.90, 1, 3, 19, 7), -- 2
('Vagabond Vol. 1 - Takehiko Inoue', 'Mangá usado, em bom estado.', 6, 44.90, 1, 8, 29, 7), -- 3
('Naruto Vol. 1 - Masashi Kishimoto', 'Mangá usado, em bom estado.', 7, 34.90, 1, 8, 28, 7), -- 4
('Persépolis - Marjane Satrapi', 'HQ usada, em bom estado.', 2, 24.90, 1, 2, 26, 7), -- 5
('CD Falso Brilhante - Elis Regina', 'CD original usado, em bom estado.', 11, 29.90, 1, 6, 19, 7), -- 6
('Fita Cassete Dois - Legião Urbana', 'Fita cassete usada, funcionando.', 6, 29.90, 1, 7, 4, 7), -- 7
('Orgulho e Preconceito - Jane Austen', 'Livro usado, em bom estado.', 12, 29.90, 1, 1, 11, 7), -- 8
('CD As Quatro Estações - Vivaldi', 'CD original usado, em bom estado.', 9, 19.90, 1, 6, 21, 7), -- 9
('Vinil Cartola - Cartola', 'Disco de vinil usado, em bom estado.', 6, 94.90, 1, 3, 22, 7), -- 10
('O Iluminado - Stephen King', 'Livro usado, em bom estado.', 8, 19.90, 1, 1, 3, 7), -- 11
('Watchmen - Alan Moore', 'HQ usada, em bom estado.', 6, 24.90, 1, 2, 27, 7), -- 12
('CD Transa - Caetano Veloso', 'CD original usado, em bom estado.', 3, 49.90, 1, 6, 19, 7), -- 13
('Fita Cassete Falso Brilhante - Elis Regina', 'Fita cassete usada, funcionando.', 5, 19.90, 1, 7, 19, 7), -- 14
('Sociologia - Anthony Giddens', 'Livro usado, em bom estado.', 2, 34.90, 1, 1, 9, 7), -- 15
('A Hora da Estrela - Clarice Lispector', 'Livro usado, em bom estado.', 7, 29.90, 1, 1, 11, 7), -- 16
('CD Sobrevivendo no Inferno - Racionais MC''s', 'CD original usado, em bom estado.', 8, 59.90, 1, 6, 8, 7), -- 17
('CD Texas Flood - Stevie Ray Vaughan', 'CD original usado, em bom estado.', 3, 34.90, 1, 6, 24, 7), -- 18
('Como Fazer Amigos e Influenciar Pessoas - Dale Carnegie', 'Livro usado, em bom estado.', 8, 49.90, 1, 1, 15, 7), -- 19
('CD Getz/Gilberto - Stan Getz e João Gilberto', 'CD original usado, em bom estado.', 4, 49.90, 1, 6, 20, 7), -- 20
('CD Sinfonia nº 9 - Beethoven', 'CD original usado, em bom estado.', 5, 54.90, 1, 6, 21, 7), -- 21
('Vinil Nevermind - Nirvana', 'Disco de vinil usado, em bom estado.', 7, 139.90, 1, 3, 4, 7), -- 22
('CD Divina Luz - Fundo de Quintal', 'CD original usado, em bom estado.', 5, 44.90, 1, 6, 22, 7), -- 23
('Sapiens - Yuval Harari', 'Livro usado, em bom estado.', 9, 19.90, 1, 1, 10, 7), -- 24
('Monster Vol. 1 - Naoki Urasawa', 'Mangá usado, em bom estado.', 4, 29.90, 1, 8, 29, 7), -- 25
('Fita Cassete Cantadores do Brasil - Pena Branca e Xavantinho', 'Fita cassete usada, funcionando.', 5, 19.90, 1, 7, 23, 7), -- 26
('Introdução à Administração - Chiavenato', 'Livro usado, em bom estado.', 8, 59.90, 1, 1, 9, 7), -- 27
('Vinil A Love Supreme - John Coltrane', 'Disco de vinil usado, em bom estado.', 3, 259.90, 1, 3, 20, 7), -- 28
('Vinil Like a Virgin - Madonna', 'Disco de vinil usado, em bom estado.', 8, 179.90, 1, 3, 18, 7), -- 29
('One Piece Vol. 1 - Eiichiro Oda', 'Mangá usado, em bom estado.', 8, 34.90, 1, 8, 28, 7), -- 30
('Getúlio - Lira Neto', 'Livro usado, em bom estado.', 10, 34.90, 1, 1, 10, 7), -- 31
('Dragon Ball Vol. 1 - Akira Toriyama', 'Mangá usado, em bom estado.', 6, 44.90, 1, 8, 28, 7), -- 32
('O Hobbit - J.R.R. Tolkien', 'Livro usado, em bom estado.', 5, 24.90, 1, 1, 1, 7), -- 33
('CD Ideologia - Cazuza', 'CD original usado, em bom estado.', 6, 29.90, 1, 6, 4, 7), -- 34
('Vinil The Dark Side of the Moon - Pink Floyd', 'Disco de vinil usado, em bom estado.', 6, 89.90, 1, 3, 4, 7), -- 35
('Vinil Evidências - Chitãozinho e Xororó', 'Disco de vinil usado, em bom estado.', 4, 74.90, 1, 3, 23, 7), -- 36
('Fita Cassete The Dark Side of the Moon - Pink Floyd', 'Fita cassete usada, funcionando.', 6, 24.90, 1, 7, 4, 7), -- 37
('CD Cantadores do Brasil - Pena Branca e Xavantinho', 'CD original usado, em bom estado.', 3, 34.90, 1, 6, 23, 7), -- 38
('O Guia do Mochileiro das Galáxias - Douglas Adams', 'Livro usado, em bom estado.', 14, 39.90, 1, 1, 1, 7), -- 39
('CD Led Zeppelin IV - Led Zeppelin', 'CD original usado, em bom estado.', 2, 9.90, 1, 6, 4, 7), -- 40
('Berserk Vol. 1 - Kentaro Miura', 'Mangá usado, em bom estado.', 10, 24.90, 1, 8, 29, 7), -- 41
('Vinil Seleção de Ouro - Tonico e Tinoco', 'Disco de vinil usado, em bom estado.', 9, 69.90, 1, 3, 23, 7), -- 42
('Vinil Clube da Esquina - Milton Nascimento', 'Disco de vinil usado, em bom estado.', 3, 69.90, 1, 3, 19, 7), -- 43
('CD Evidências - Chitãozinho e Xororó', 'CD original usado, em bom estado.', 8, 39.90, 1, 6, 23, 7), -- 44
('Vinil The Chronic - Dr. Dre', 'Disco de vinil usado, em bom estado.', 5, 74.90, 1, 3, 8, 7), -- 45
('Homem-Aranha: Edição Clássica', 'HQ usada, em bom estado.', 6, 44.90, 1, 2, 27, 7), -- 46
('Fita Cassete The Chronic - Dr. Dre', 'Fita cassete usada, funcionando.', 5, 19.90, 1, 7, 8, 7), -- 47
('CD Live at the Regal - B.B. King', 'CD original usado, em bom estado.', 3, 44.90, 1, 6, 24, 7), -- 48
('X-Men: A Saga da Fênix Negra', 'HQ usada, em bom estado.', 12, 14.90, 1, 2, 27, 7), -- 49
('Vinil Sobrevivendo no Inferno - Racionais MC''s', 'Disco de vinil usado, em bom estado.', 2, 99.90, 1, 3, 8, 7), -- 50
('Duna - Frank Herbert', 'Livro usado, em bom estado.', 11, 34.90, 1, 1, 1, 7), -- 51
('Cálculo Vol. 1 - James Stewart', 'Livro usado, em bom estado.', 7, 54.90, 1, 1, 9, 7), -- 52
('Maus - Art Spiegelman', 'HQ usada, em bom estado.', 8, 24.90, 1, 2, 26, 7), -- 53
('Casa-Grande & Senzala - Gilberto Freyre', 'Livro usado, em bom estado.', 5, 34.90, 1, 1, 10, 7), -- 54
('Vinil Ideologia - Cazuza', 'Disco de vinil usado, em bom estado.', 4, 219.90, 1, 3, 4, 7), -- 55
('Vinil Falso Brilhante - Elis Regina', 'Disco de vinil usado, em bom estado.', 5, 79.90, 1, 3, 19, 7), -- 56
('CD Nevermind - Nirvana', 'CD original usado, em bom estado.', 6, 39.90, 1, 6, 4, 7), -- 57
('Fullmetal Alchemist Vol. 1 - Hiromu Arakawa', 'Mangá usado, em bom estado.', 4, 24.90, 1, 8, 28, 7), -- 58
('CD Illmatic - Nas', 'CD original usado, em bom estado.', 9, 34.90, 1, 6, 8, 7), -- 59
('CD Acabou Chorare - Novos Baianos', 'CD original usado, em bom estado.', 3, 29.90, 1, 6, 19, 7), -- 60
('Fundamentos de Física - Halliday', 'Livro usado, em bom estado.', 4, 29.90, 1, 1, 9, 7), -- 61
('Fita Cassete Elis & Tom - Elis Regina', 'Fita cassete usada, funcionando.', 7, 24.90, 1, 7, 19, 7), -- 62
('Vinil Bachianas Brasileiras - Villa-Lobos', 'Disco de vinil usado, em bom estado.', 4, 209.90, 1, 3, 21, 7), -- 63
('CD A Love Supreme - John Coltrane', 'CD original usado, em bom estado.', 5, 54.90, 1, 6, 20, 7), -- 64
('Sentimento do Mundo - Carlos Drummond de Andrade', 'Livro usado, em bom estado.', 2, 39.90, 1, 1, 25, 7), -- 65
('O Poder do Hábito - Charles Duhigg', 'Livro usado, em bom estado.', 5, 29.90, 1, 1, 15, 7), -- 66
('CD De Pé no Chão - Beth Carvalho', 'CD original usado, em bom estado.', 5, 14.90, 1, 6, 22, 7), -- 67
('Antologia Poética - Vinicius de Moraes', 'Livro usado, em bom estado.', 3, 44.90, 1, 1, 25, 7), -- 68
('Vinil Led Zeppelin IV - Led Zeppelin', 'Disco de vinil usado, em bom estado.', 8, 179.90, 1, 3, 4, 7), -- 69
('CD The Dark Side of the Moon - Pink Floyd', 'CD original usado, em bom estado.', 2, 29.90, 1, 6, 4, 7), -- 70
('CD 21 - Adele', 'CD original usado, em bom estado.', 4, 24.90, 1, 6, 18, 7), -- 71
('Assassinato no Expresso do Oriente - Agatha Christie', 'Livro usado, em bom estado.', 8, 14.90, 1, 1, 3, 7), -- 72
('Sailor Moon Vol. 1 - Naoko Takeuchi', 'Mangá usado, em bom estado.', 5, 19.90, 1, 8, 29, 7), -- 73
('Vinil Instrumental - Almir Sater', 'Disco de vinil usado, em bom estado.', 5, 149.90, 1, 3, 23, 7), -- 74
('Sandman: Prelúdios e Noturnos - Neil Gaiman', 'HQ usada, em bom estado.', 4, 44.90, 1, 2, 26, 7), -- 75
('Dom Casmurro - Machado de Assis', 'Livro usado, em bom estado.', 4, 14.90, 1, 1, 11, 7), -- 76
('O Amor nos Tempos do Cólera - García Márquez', 'Livro usado, em bom estado.', 7, 14.90, 1, 1, 11, 7), -- 77
('Drácula - Bram Stoker', 'Livro usado, em bom estado.', 3, 14.90, 1, 1, 3, 7), -- 78
('Batman: O Cavaleiro das Trevas - Frank Miller', 'HQ usada, em bom estado.', 6, 49.90, 1, 2, 27, 7), -- 79
('Fita Cassete Acabou Chorare - Novos Baianos', 'Fita cassete usada, funcionando.', 4, 19.90, 1, 7, 19, 7), -- 80
('CD Elis & Tom - Elis Regina', 'CD original usado, em bom estado.', 5, 39.90, 1, 6, 19, 7); -- 81
GO

-- Tabela Imagem_Produtos --
-- Capas baixadas por produto (arquivo imagemProduto<id>.jpg na pasta static/imagens/produtos). --
INSERT INTO imagem_produtos (caminho, produto_id) VALUES
('/imagens/produtos/imagemProduto1.jpg', 1),
('/imagens/produtos/imagemProduto2.jpg', 2),
('/imagens/produtos/imagemProduto3.jpg', 3),
('/imagens/produtos/imagemProduto4.jpg', 4),
('/imagens/produtos/imagemProduto5.jpg', 5),
('/imagens/produtos/imagemProduto6.jpg', 6),
('/imagens/produtos/imagemProduto7.jpg', 7),
('/imagens/produtos/imagemProduto8.jpg', 8),
('/imagens/produtos/imagemProduto9.jpg', 9),
('/imagens/produtos/imagemProduto10.jpg', 10),
('/imagens/produtos/imagemProduto11.jpg', 11),
('/imagens/produtos/imagemProduto12.jpg', 12),
('/imagens/produtos/imagemProduto13.jpg', 13),
('/imagens/produtos/imagemProduto14.jpg', 14),
('/imagens/produtos/imagemProduto15.jpg', 15),
('/imagens/produtos/imagemProduto16.jpg', 16),
('/imagens/produtos/imagemProduto17.jpg', 17),
('/imagens/produtos/imagemProduto18.jpg', 18),
('/imagens/produtos/imagemProduto19.jpg', 19),
('/imagens/produtos/imagemProduto20.jpg', 20),
('/imagens/produtos/imagemProduto21.jpg', 21),
('/imagens/produtos/imagemProduto22.jpg', 22),
('/imagens/produtos/imagemProduto23.jpg', 23),
('/imagens/produtos/imagemProduto24.jpg', 24),
('/imagens/produtos/imagemProduto25.jpg', 25),
('/imagens/produtos/imagemProduto26.jpg', 26),
('/imagens/produtos/imagemProduto27.jpg', 27),
('/imagens/produtos/imagemProduto28.jpg', 28),
('/imagens/produtos/imagemProduto29.jpg', 29),
('/imagens/produtos/imagemProduto30.jpg', 30),
('/imagens/produtos/imagemProduto31.jpg', 31),
('/imagens/produtos/imagemProduto32.jpg', 32),
('/imagens/produtos/imagemProduto33.jpg', 33),
('/imagens/produtos/imagemProduto34.jpg', 34),
('/imagens/produtos/imagemProduto35.jpg', 35),
('/imagens/produtos/imagemProduto36.jpg', 36),
('/imagens/produtos/imagemProduto37.jpg', 37),
('/imagens/produtos/imagemProduto38.jpg', 38),
('/imagens/produtos/imagemProduto39.jpg', 39),
('/imagens/produtos/imagemProduto40.jpg', 40),
('/imagens/produtos/imagemProduto41.jpg', 41),
('/imagens/produtos/imagemProduto42.jpg', 42),
('/imagens/produtos/imagemProduto43.jpg', 43),
('/imagens/produtos/imagemProduto44.jpg', 44),
('/imagens/produtos/imagemProduto45.jpg', 45),
('/imagens/produtos/imagemProduto46.jpg', 46),
('/imagens/produtos/imagemProduto47.jpg', 47),
('/imagens/produtos/imagemProduto48.jpg', 48),
('/imagens/produtos/imagemProduto49.jpg', 49),
('/imagens/produtos/imagemProduto50.jpg', 50),
('/imagens/produtos/imagemProduto51.jpg', 51),
('/imagens/produtos/imagemProduto52.jpg', 52),
('/imagens/produtos/imagemProduto53.jpg', 53),
('/imagens/produtos/imagemProduto54.jpg', 54),
('/imagens/produtos/imagemProduto55.jpg', 55),
('/imagens/produtos/imagemProduto56.jpg', 56),
('/imagens/produtos/imagemProduto57.jpg', 57),
('/imagens/produtos/imagemProduto58.jpg', 58),
('/imagens/produtos/imagemProduto59.jpg', 59),
('/imagens/produtos/imagemProduto60.jpg', 60),
('/imagens/produtos/imagemProduto61.jpg', 61),
('/imagens/produtos/imagemProduto62.jpg', 62),
('/imagens/produtos/imagemProduto63.jpg', 63),
('/imagens/produtos/imagemProduto64.jpg', 64),
('/imagens/produtos/imagemProduto65.jpg', 65),
('/imagens/produtos/imagemProduto66.jpg', 66),
('/imagens/produtos/imagemProduto67.jpg', 67),
('/imagens/produtos/imagemProduto68.jpg', 68),
('/imagens/produtos/imagemProduto69.jpg', 69),
('/imagens/produtos/imagemProduto70.jpg', 70),
('/imagens/produtos/imagemProduto71.jpg', 71),
('/imagens/produtos/imagemProduto72.jpg', 72),
('/imagens/produtos/imagemProduto73.jpg', 73),
('/imagens/produtos/imagemProduto74.jpg', 74),
('/imagens/produtos/imagemProduto75.jpg', 75),
('/imagens/produtos/imagemProduto76.jpg', 76),
('/imagens/produtos/imagemProduto77.jpg', 77),
('/imagens/produtos/imagemProduto78.jpg', 78),
('/imagens/produtos/imagemProduto79.jpg', 79),
('/imagens/produtos/imagemProduto80.jpg', 80),
('/imagens/produtos/imagemProduto81.jpg', 81);
GO

-- Tabela Item_Carrinhos --
-- Carrinhos de compras que ainda não viraram pedido (clientes 3 e 4). Guardam o produto, a quantidade --
-- e o preço do produto quando foi colocado no carrinho. O estoque só é baixado quando a compra é finalizada. --
INSERT INTO item_carrinhos (cliente_id, produto_id, quantidade, preco_unitario) VALUES
(3, 51, 1, 34.90),
(3, 53, 2, 24.90),
(4, 35, 1, 89.90);
GO

-- Tabela Entregas --
-- Uma entrega para cada venda da planilha, no endereço do cliente que comprou (coluna endereco_id). --
-- O tipo de entrega alterna de venda em venda, como as opções da tela de checkout: Econômico, Expresso e --
-- Retirar com o vendedor (sem frete). O valor do Econômico e do Expresso depende da região do CEP, e o --
-- Econômico é grátis quando os produtos somam R$ 150,00 ou mais, igual à regra do site. --
-- A previsão de entrega é a data do pedido mais o prazo máximo da opção escolhida. --
-- O status segue a regra da procedure sp_avancar_entrega: link, código e data de postagem só existem quando a entrega --
-- está EM TRÂNSITO (3) ou ENTREGUE (4), e a data de entrega só existe quando ela foi concluída (4). --
-- A entrega N pertence ao pedido N (entrega 1 -> pedido 1, entrega 2 -> pedido 2...). --
-- Status: 1 Pendente | 2 Preparando | 3 Em trânsito | 4 Entregue | 5 Cancelada --
INSERT INTO entregas (transportadora, status, link_rastreio, codigo_rastreio, data_postagem, data_previsao_entrega, data_entrega, valor_frete, endereco_id) VALUES
('Econômico', 4, 'https://rastreio.sebodojoao.demo/1', 'SJ000000001BR', '2026-05-06T09:00:00', '2026-05-11', '2026-05-12T10:00:00', 0.00, 1), -- 1: Entregue
('Expresso', 4, 'https://rastreio.sebodojoao.demo/2', 'SJ000000002BR', '2026-05-06T09:00:00', '2026-05-07', '2026-05-07T13:00:00', 24.90, 3), -- 2: Entregue
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/3', 'SJ000000003BR', '2026-05-06T13:07:00', '2026-05-06', '2026-05-06T13:37:00', 0.00, 8), -- 3: Entregue
('Econômico', 4, 'https://rastreio.sebodojoao.demo/4', 'SJ000000004BR', '2026-05-08T09:00:00', '2026-05-16', '2026-05-15T14:00:00', 18.90, 6), -- 4: Entregue
('Expresso', 4, 'https://rastreio.sebodojoao.demo/5', 'SJ000000005BR', '2026-05-09T09:00:00', '2026-05-11', '2026-05-11T10:00:00', 34.90, 6), -- 5: Entregue
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/6', 'SJ000000006BR', '2026-05-09T09:46:00', '2026-05-09', '2026-05-09T10:16:00', 0.00, 9), -- 6: Entregue
('Econômico', 4, 'https://rastreio.sebodojoao.demo/7', 'SJ000000007BR', '2026-05-11T09:00:00', '2026-05-16', '2026-05-17T14:00:00', 12.90, 10), -- 7: Entregue
('Expresso', 4, 'https://rastreio.sebodojoao.demo/8', 'SJ000000008BR', '2026-05-14T09:00:00', '2026-05-15', '2026-05-16T11:00:00', 24.90, 11), -- 8: Entregue
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/9', 'SJ000000009BR', '2026-05-18T09:59:00', '2026-05-18', '2026-05-18T10:29:00', 0.00, 12), -- 9: Entregue
('Econômico', 5, NULL, NULL, NULL, '2026-05-25', NULL, 0.00, 2), -- 10: Cancelada
('Expresso', 4, 'https://rastreio.sebodojoao.demo/11', 'SJ000000011BR', '2026-05-22T09:00:00', '2026-05-23', '2026-05-24T17:00:00', 24.90, 8), -- 11: Entregue
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/12', 'SJ000000012BR', '2026-05-21T10:47:00', '2026-05-21', '2026-05-21T11:17:00', 0.00, 9), -- 12: Entregue
('Econômico', 4, 'https://rastreio.sebodojoao.demo/13', 'SJ000000013BR', '2026-05-23T09:00:00', '2026-05-28', '2026-05-29T11:00:00', 12.90, 10), -- 13: Entregue
('Expresso', 4, 'https://rastreio.sebodojoao.demo/14', 'SJ000000014BR', '2026-05-23T09:00:00', '2026-05-25', '2026-05-26T17:00:00', 34.90, 5), -- 14: Entregue
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/15', 'SJ000000015BR', '2026-05-22T10:57:00', '2026-05-22', '2026-05-22T11:27:00', 0.00, 11), -- 15: Entregue
('Econômico', 4, 'https://rastreio.sebodojoao.demo/16', 'SJ000000016BR', '2026-05-23T09:00:00', '2026-05-28', '2026-05-27T12:00:00', 12.90, 12), -- 16: Entregue
('Expresso', 4, 'https://rastreio.sebodojoao.demo/17', 'SJ000000017BR', '2026-05-23T09:00:00', '2026-05-24', '2026-05-24T12:00:00', 24.90, 1), -- 17: Entregue
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/18', 'SJ000000018BR', '2026-05-26T11:46:00', '2026-05-26', '2026-05-26T12:16:00', 0.00, 13), -- 18: Entregue
('Econômico', 4, 'https://rastreio.sebodojoao.demo/19', 'SJ000000019BR', '2026-05-30T09:00:00', '2026-06-12', '2026-06-11T10:00:00', 27.90, 16), -- 19: Entregue
('Expresso', 4, 'https://rastreio.sebodojoao.demo/20', 'SJ000000020BR', '2026-05-30T09:00:00', '2026-05-31', '2026-06-01T14:00:00', 24.90, 8), -- 20: Entregue
('Retirar com o vendedor', 1, NULL, NULL, NULL, '2026-06-02', NULL, 0.00, 9), -- 21: Pendente
('Econômico', 4, 'https://rastreio.sebodojoao.demo/22', 'SJ000000022BR', '2026-06-03T09:00:00', '2026-06-08', '2026-06-07T15:00:00', 12.90, 15), -- 22: Entregue
('Expresso', 4, 'https://rastreio.sebodojoao.demo/23', 'SJ000000023BR', '2026-06-07T09:00:00', '2026-06-08', '2026-06-08T11:00:00', 24.90, 10), -- 23: Entregue
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/24', 'SJ000000024BR', '2026-06-07T14:16:00', '2026-06-07', '2026-06-07T14:46:00', 0.00, 11), -- 24: Entregue
('Econômico', 4, 'https://rastreio.sebodojoao.demo/25', 'SJ000000025BR', '2026-06-10T09:00:00', '2026-06-15', '2026-06-15T12:00:00', 12.90, 12), -- 25: Entregue
('Expresso', 4, 'https://rastreio.sebodojoao.demo/26', 'SJ000000026BR', '2026-06-12T09:00:00', '2026-06-13', '2026-06-13T15:00:00', 24.90, 2), -- 26: Entregue
('Retirar com o vendedor', 5, NULL, NULL, NULL, '2026-06-12', NULL, 0.00, 8), -- 27: Cancelada
('Econômico', 4, 'https://rastreio.sebodojoao.demo/28', 'SJ000000028BR', '2026-06-15T09:00:00', '2026-06-20', '2026-06-20T17:00:00', 12.90, 9), -- 28: Entregue
('Expresso', 4, 'https://rastreio.sebodojoao.demo/29', 'SJ000000029BR', '2026-06-17T09:00:00', '2026-06-18', '2026-06-18T17:00:00', 24.90, 10), -- 29: Entregue
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/30', 'SJ000000030BR', '2026-06-16T10:26:00', '2026-06-16', '2026-06-16T10:56:00', 0.00, 3), -- 30: Entregue
('Econômico', 4, 'https://rastreio.sebodojoao.demo/31', 'SJ000000031BR', '2026-06-19T09:00:00', '2026-06-24', '2026-06-25T11:00:00', 12.90, 11), -- 31: Entregue
('Expresso', 4, 'https://rastreio.sebodojoao.demo/32', 'SJ000000032BR', '2026-06-20T09:00:00', '2026-06-21', '2026-06-22T14:00:00', 24.90, 12), -- 32: Entregue
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/33', 'SJ000000033BR', '2026-06-20T15:08:00', '2026-06-20', '2026-06-20T15:38:00', 0.00, 1), -- 33: Entregue
('Econômico', 4, 'https://rastreio.sebodojoao.demo/34', 'SJ000000034BR', '2026-06-22T09:00:00', '2026-06-27', '2026-06-26T16:00:00', 12.90, 8), -- 34: Entregue
('Expresso', 4, 'https://rastreio.sebodojoao.demo/35', 'SJ000000035BR', '2026-06-24T09:00:00', '2026-06-25', '2026-06-25T17:00:00', 24.90, 9), -- 35: Entregue
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/36', 'SJ000000036BR', '2026-06-26T10:04:00', '2026-06-26', '2026-06-26T10:34:00', 0.00, 10), -- 36: Entregue
('Econômico', 4, 'https://rastreio.sebodojoao.demo/37', 'SJ000000037BR', '2026-06-27T09:00:00', '2026-07-02', '2026-07-02T11:00:00', 12.90, 11), -- 37: Entregue
('Expresso', 4, 'https://rastreio.sebodojoao.demo/38', 'SJ000000038BR', '2026-06-27T09:00:00', '2026-06-28', '2026-06-29T15:00:00', 24.90, 12), -- 38: Entregue
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/39', 'SJ000000039BR', '2026-06-26T09:19:00', '2026-06-26', '2026-06-26T09:49:00', 0.00, 13), -- 39: Entregue
('Econômico', 4, 'https://rastreio.sebodojoao.demo/40', 'SJ000000040BR', '2026-06-28T09:00:00', '2026-07-03', '2026-07-03T15:00:00', 12.90, 2), -- 40: Entregue
('Expresso', 4, 'https://rastreio.sebodojoao.demo/41', 'SJ000000041BR', '2026-07-01T09:00:00', '2026-07-02', '2026-07-02T10:00:00', 24.90, 8), -- 41: Entregue
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/42', 'SJ000000042BR', '2026-07-02T17:31:00', '2026-07-02', '2026-07-02T18:01:00', 0.00, 9), -- 42: Entregue
('Econômico', 4, 'https://rastreio.sebodojoao.demo/43', 'SJ000000043BR', '2026-07-04T09:00:00', '2026-07-12', '2026-07-13T11:00:00', 0.00, 4), -- 43: Entregue
('Expresso', 5, NULL, NULL, NULL, '2026-07-09', NULL, 49.90, 17), -- 44: Cancelada
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/45', 'SJ000000045BR', '2026-07-05T09:18:00', '2026-07-05', '2026-07-05T09:48:00', 0.00, 10), -- 45: Entregue
('Econômico', 4, 'https://rastreio.sebodojoao.demo/46', 'SJ000000046BR', '2026-07-08T09:00:00', '2026-07-13', '2026-07-14T10:00:00', 12.90, 11), -- 46: Entregue
('Expresso', 4, 'https://rastreio.sebodojoao.demo/47', 'SJ000000047BR', '2026-07-08T09:00:00', '2026-07-09', '2026-07-10T13:00:00', 24.90, 12), -- 47: Entregue
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/48', 'SJ000000048BR', '2026-07-08T13:44:00', '2026-07-08', '2026-07-08T14:14:00', 0.00, 1), -- 48: Entregue
('Econômico', 4, 'https://rastreio.sebodojoao.demo/49', 'SJ000000049BR', '2026-07-10T09:00:00', '2026-07-23', '2026-07-21T14:00:00', 0.00, 18), -- 49: Entregue
('Expresso', 4, 'https://rastreio.sebodojoao.demo/50', 'SJ000000050BR', '2026-07-12T09:00:00', '2026-07-13', '2026-07-14T15:00:00', 24.90, 8), -- 50: Entregue
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/51', 'SJ000000051BR', '2026-07-12T14:17:00', '2026-07-12', '2026-07-12T14:47:00', 0.00, 5), -- 51: Entregue
('Econômico', 4, 'https://rastreio.sebodojoao.demo/52', 'SJ000000052BR', '2026-07-15T09:00:00', '2026-07-20', '2026-07-21T15:00:00', 12.90, 9), -- 52: Entregue
('Expresso', 4, 'https://rastreio.sebodojoao.demo/53', 'SJ000000053BR', '2026-07-15T09:00:00', '2026-07-16', '2026-07-16T13:00:00', 24.90, 10), -- 53: Entregue
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/54', 'SJ000000054BR', '2026-07-15T17:08:00', '2026-07-15', '2026-07-15T17:38:00', 0.00, 11), -- 54: Entregue
('Econômico', 4, 'https://rastreio.sebodojoao.demo/55', 'SJ000000055BR', '2026-07-16T09:00:00', '2026-07-21', '2026-07-21T15:00:00', 12.90, 12), -- 55: Entregue
('Expresso', 4, 'https://rastreio.sebodojoao.demo/56', 'SJ000000056BR', '2026-07-16T09:00:00', '2026-07-17', '2026-07-18T14:00:00', 24.90, 2), -- 56: Entregue
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/57', 'SJ000000057BR', '2026-07-18T15:32:00', '2026-07-18', '2026-07-18T16:02:00', 0.00, 8), -- 57: Entregue
('Econômico', 4, 'https://rastreio.sebodojoao.demo/58', 'SJ000000058BR', '2026-07-20T09:00:00', '2026-07-25', '2026-07-26T12:00:00', 0.00, 9), -- 58: Entregue
('Expresso', 4, 'https://rastreio.sebodojoao.demo/59', 'SJ000000059BR', '2026-08-01T09:00:00', '2026-08-03', '2026-08-03T16:00:00', 34.90, 5), -- 59: Entregue
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/60', 'SJ000000060BR', '2026-08-02T15:59:00', '2026-08-02', '2026-08-02T16:29:00', 0.00, 10), -- 60: Entregue
('Econômico', 5, NULL, NULL, NULL, '2026-08-11', NULL, 12.90, 11), -- 61: Cancelada
('Expresso', 4, 'https://rastreio.sebodojoao.demo/62', 'SJ000000062BR', '2026-08-06T09:00:00', '2026-08-07', '2026-08-07T14:00:00', 24.90, 12), -- 62: Entregue
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/63', 'SJ000000063BR', '2026-08-06T15:50:00', '2026-08-06', '2026-08-06T16:20:00', 0.00, 1), -- 63: Entregue
('Econômico', 4, 'https://rastreio.sebodojoao.demo/64', 'SJ000000064BR', '2026-08-08T09:00:00', '2026-08-13', '2026-08-14T11:00:00', 12.90, 3), -- 64: Entregue
('Expresso', 4, 'https://rastreio.sebodojoao.demo/65', 'SJ000000065BR', '2026-08-09T09:00:00', '2026-08-10', '2026-08-10T10:00:00', 24.90, 8), -- 65: Entregue
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/66', 'SJ000000066BR', '2026-08-11T16:45:00', '2026-08-11', '2026-08-11T17:15:00', 0.00, 9), -- 66: Entregue
('Econômico', 4, 'https://rastreio.sebodojoao.demo/67', 'SJ000000067BR', '2026-08-13T09:00:00', '2026-08-18', '2026-08-17T14:00:00', 12.90, 10), -- 67: Entregue
('Expresso', 4, 'https://rastreio.sebodojoao.demo/68', 'SJ000000068BR', '2026-08-15T09:00:00', '2026-08-16', '2026-08-16T11:00:00', 24.90, 11), -- 68: Entregue
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/69', 'SJ000000069BR', '2026-08-18T15:13:00', '2026-08-18', '2026-08-18T15:43:00', 0.00, 17), -- 69: Entregue
('Econômico', 4, 'https://rastreio.sebodojoao.demo/70', 'SJ000000070BR', '2026-08-21T09:00:00', '2026-08-26', '2026-08-25T15:00:00', 12.90, 13), -- 70: Entregue
('Expresso', 4, 'https://rastreio.sebodojoao.demo/71', 'SJ000000071BR', '2026-08-23T09:00:00', '2026-08-24', '2026-08-25T10:00:00', 24.90, 14), -- 71: Entregue
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/72', 'SJ000000072BR', '2026-08-22T17:46:00', '2026-08-22', '2026-08-22T18:16:00', 0.00, 12), -- 72: Entregue
('Econômico', 4, 'https://rastreio.sebodojoao.demo/73', 'SJ000000073BR', '2026-08-26T09:00:00', '2026-08-31', '2026-08-30T13:00:00', 12.90, 2), -- 73: Entregue
('Expresso', 4, 'https://rastreio.sebodojoao.demo/74', 'SJ000000074BR', '2026-08-28T09:00:00', '2026-08-29', '2026-08-29T13:00:00', 24.90, 8), -- 74: Entregue
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/75', 'SJ000000075BR', '2026-08-27T09:30:00', '2026-08-27', '2026-08-27T10:00:00', 0.00, 6), -- 75: Entregue
('Econômico', 4, 'https://rastreio.sebodojoao.demo/76', 'SJ000000076BR', '2026-08-28T09:00:00', '2026-09-02', '2026-09-03T16:00:00', 12.90, 9), -- 76: Entregue
('Expresso', 4, 'https://rastreio.sebodojoao.demo/77', 'SJ000000077BR', '2026-08-30T09:00:00', '2026-08-31', '2026-08-31T17:00:00', 24.90, 10), -- 77: Entregue
('Retirar com o vendedor', 5, NULL, NULL, NULL, '2026-08-29', NULL, 0.00, 19), -- 78: Cancelada
('Econômico', 4, 'https://rastreio.sebodojoao.demo/79', 'SJ000000079BR', '2026-08-31T09:00:00', '2026-09-05', '2026-09-04T13:00:00', 0.00, 11), -- 79: Entregue
('Expresso', 4, 'https://rastreio.sebodojoao.demo/80', 'SJ000000080BR', '2026-08-31T09:00:00', '2026-09-01', '2026-09-01T17:00:00', 24.90, 12), -- 80: Entregue
('Retirar com o vendedor', 1, NULL, NULL, NULL, '2026-09-01', NULL, 0.00, 1), -- 81: Pendente
('Econômico', 4, 'https://rastreio.sebodojoao.demo/82', 'SJ000000082BR', '2026-09-02T09:00:00', '2026-09-07', '2026-09-07T17:00:00', 12.90, 8), -- 82: Entregue
('Expresso', 4, 'https://rastreio.sebodojoao.demo/83', 'SJ000000083BR', '2026-09-04T09:00:00', '2026-09-06', '2026-09-07T10:00:00', 34.90, 7), -- 83: Entregue
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/84', 'SJ000000084BR', '2026-09-04T10:22:00', '2026-09-04', '2026-09-04T10:52:00', 0.00, 14), -- 84: Entregue
('Econômico', 4, 'https://rastreio.sebodojoao.demo/85', 'SJ000000085BR', '2026-09-07T09:00:00', '2026-09-12', '2026-09-12T12:00:00', 0.00, 14), -- 85: Entregue
('Expresso', 4, 'https://rastreio.sebodojoao.demo/86', 'SJ000000086BR', '2026-09-07T09:00:00', '2026-09-08', '2026-09-09T13:00:00', 24.90, 9), -- 86: Entregue
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/87', 'SJ000000087BR', '2026-09-09T16:26:00', '2026-09-09', '2026-09-09T16:56:00', 0.00, 10), -- 87: Entregue
('Econômico', 4, 'https://rastreio.sebodojoao.demo/88', 'SJ000000088BR', '2026-09-10T09:00:00', '2026-09-15', '2026-09-15T16:00:00', 12.90, 11), -- 88: Entregue
('Expresso', 4, 'https://rastreio.sebodojoao.demo/89', 'SJ000000089BR', '2026-09-11T09:00:00', '2026-09-12', '2026-09-12T13:00:00', 24.90, 3), -- 89: Entregue
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/90', 'SJ000000090BR', '2026-09-10T16:20:00', '2026-09-10', '2026-09-10T16:50:00', 0.00, 12), -- 90: Entregue
('Econômico', 4, 'https://rastreio.sebodojoao.demo/91', 'SJ000000091BR', '2026-09-13T09:00:00', '2026-09-18', '2026-09-18T12:00:00', 12.90, 14), -- 91: Entregue
('Expresso', 4, 'https://rastreio.sebodojoao.demo/92', 'SJ000000092BR', '2026-09-14T09:00:00', '2026-09-18', '2026-09-17T10:00:00', 49.90, 17), -- 92: Entregue
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/93', 'SJ000000093BR', '2026-09-14T10:40:00', '2026-09-14', '2026-09-14T11:10:00', 0.00, 15), -- 93: Entregue
('Econômico', 4, 'https://rastreio.sebodojoao.demo/94', 'SJ000000094BR', '2026-09-15T09:00:00', '2026-09-20', '2026-09-21T13:00:00', 0.00, 2), -- 94: Entregue
('Expresso', 5, NULL, NULL, NULL, '2026-09-17', NULL, 24.90, 8), -- 95: Cancelada
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/96', 'SJ000000096BR', '2026-09-16T16:31:00', '2026-09-16', '2026-09-16T17:01:00', 0.00, 9), -- 96: Entregue
('Econômico', 4, 'https://rastreio.sebodojoao.demo/97', 'SJ000000097BR', '2026-09-17T09:00:00', '2026-09-30', '2026-09-28T11:00:00', 27.90, 16), -- 97: Entregue
('Expresso', 4, 'https://rastreio.sebodojoao.demo/98', 'SJ000000098BR', '2026-09-18T09:00:00', '2026-09-20', '2026-09-20T17:00:00', 34.90, 4), -- 98: Entregue
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/99', 'SJ000000099BR', '2026-09-18T16:02:00', '2026-09-18', '2026-09-18T16:32:00', 0.00, 7), -- 99: Entregue
('Econômico', 4, 'https://rastreio.sebodojoao.demo/100', 'SJ000000100BR', '2026-09-19T09:00:00', '2026-09-24', '2026-09-25T17:00:00', 0.00, 14), -- 100: Entregue
('Expresso', 4, 'https://rastreio.sebodojoao.demo/101', 'SJ000000101BR', '2026-09-19T09:00:00', '2026-09-21', '2026-09-22T13:00:00', 34.90, 6), -- 101: Entregue
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/102', 'SJ000000102BR', '2026-09-20T12:03:00', '2026-09-20', '2026-09-20T12:33:00', 0.00, 10), -- 102: Entregue
('Econômico', 4, 'https://rastreio.sebodojoao.demo/103', 'SJ000000103BR', '2026-09-21T09:00:00', '2026-09-26', '2026-09-26T13:00:00', 12.90, 11), -- 103: Entregue
('Expresso', 4, 'https://rastreio.sebodojoao.demo/104', 'SJ000000104BR', '2026-09-22T09:00:00', '2026-09-23', '2026-09-24T14:00:00', 24.90, 12), -- 104: Entregue
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/105', 'SJ000000105BR', '2026-09-22T17:28:00', '2026-09-22', '2026-09-22T17:58:00', 0.00, 1), -- 105: Entregue
('Econômico', 4, 'https://rastreio.sebodojoao.demo/106', 'SJ000000106BR', '2026-09-24T09:00:00', '2026-10-02', '2026-10-01T11:00:00', 18.90, 6), -- 106: Entregue
('Expresso', 4, 'https://rastreio.sebodojoao.demo/107', 'SJ000000107BR', '2026-09-24T09:00:00', '2026-09-25', '2026-09-25T15:00:00', 24.90, 13), -- 107: Entregue
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/108', 'SJ000000108BR', '2026-09-24T12:20:00', '2026-09-24', '2026-09-24T12:50:00', 0.00, 3), -- 108: Entregue
('Econômico', 4, 'https://rastreio.sebodojoao.demo/109', 'SJ000000109BR', '2026-09-28T09:00:00', '2026-10-03', '2026-10-04T16:00:00', 12.90, 8), -- 109: Entregue
('Expresso', 4, 'https://rastreio.sebodojoao.demo/110', 'SJ000000110BR', '2026-10-01T09:00:00', '2026-10-03', '2026-10-04T11:00:00', 34.90, 19), -- 110: Entregue
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/111', 'SJ000000111BR', '2026-09-30T09:09:00', '2026-09-30', '2026-09-30T09:39:00', 0.00, 18), -- 111: Entregue
('Econômico', 5, NULL, NULL, NULL, '2026-10-08', NULL, 12.90, 9), -- 112: Cancelada
('Expresso', 4, 'https://rastreio.sebodojoao.demo/113', 'SJ000000113BR', '2026-10-05T09:00:00', '2026-10-06', '2026-10-06T17:00:00', 24.90, 10), -- 113: Entregue
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/114', 'SJ000000114BR', '2026-10-05T16:13:00', '2026-10-05', '2026-10-05T16:43:00', 0.00, 11), -- 114: Entregue
('Econômico', 4, 'https://rastreio.sebodojoao.demo/115', 'SJ000000115BR', '2026-10-07T09:00:00', '2026-10-12', '2026-10-11T15:00:00', 12.90, 12), -- 115: Entregue
('Expresso', 4, 'https://rastreio.sebodojoao.demo/116', 'SJ000000116BR', '2026-10-09T09:00:00', '2026-10-11', '2026-10-12T17:00:00', 34.90, 4), -- 116: Entregue
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/117', 'SJ000000117BR', '2026-10-08T14:01:00', '2026-10-08', '2026-10-08T14:31:00', 0.00, 2), -- 117: Entregue
('Econômico', 4, 'https://rastreio.sebodojoao.demo/118', 'SJ000000118BR', '2026-10-11T09:00:00', '2026-10-19', '2026-10-20T17:00:00', 18.90, 19), -- 118: Entregue
('Expresso', 4, 'https://rastreio.sebodojoao.demo/119', 'SJ000000119BR', '2026-10-14T09:00:00', '2026-10-16', '2026-10-17T16:00:00', 34.90, 4), -- 119: Entregue
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/120', 'SJ000000120BR', '2026-10-14T13:35:00', '2026-10-14', '2026-10-14T14:05:00', 0.00, 8), -- 120: Entregue
('Econômico', 4, 'https://rastreio.sebodojoao.demo/121', 'SJ000000121BR', '2026-10-16T09:00:00', '2026-10-29', '2026-10-28T16:00:00', 0.00, 17), -- 121: Entregue
('Expresso', 3, 'https://rastreio.sebodojoao.demo/122', 'SJ000000122BR', '2026-10-22T09:00:00', '2026-10-24', NULL, 34.90, 7), -- 122: Em trânsito
('Retirar com o vendedor', 2, NULL, NULL, NULL, '2026-10-21', NULL, 0.00, 9), -- 123: Preparando
('Econômico', 1, NULL, NULL, NULL, '2026-11-03', NULL, 12.90, 10), -- 124: Pendente
('Expresso', 1, NULL, NULL, NULL, '2026-10-31', NULL, 24.90, 11), -- 125: Pendente
('Retirar com o vendedor', 4, 'https://rastreio.sebodojoao.demo/126', 'SJ000000126BR', '2026-10-30T17:55:00', '2026-10-30', '2026-10-30T18:25:00', 0.00, 12), -- 126: Entregue
('Econômico', 3, 'https://rastreio.sebodojoao.demo/127', 'SJ000000127BR', '2026-10-31T09:00:00', '2026-11-05', NULL, 12.90, 1), -- 127: Em trânsito
('Expresso', 2, NULL, NULL, NULL, '2026-11-01', NULL, 24.90, 8), -- 128: Preparando
('Retirar com o vendedor', 5, NULL, NULL, NULL, '2026-10-31', NULL, 0.00, 13), -- 129: Cancelada
('Econômico', 1, NULL, NULL, NULL, '2026-11-06', NULL, 12.90, 9); -- 130: Pendente
GO

-- Tabela Pedidos --
-- O total de cada pedido é a soma dos itens (tabela item_pedidos) mais o frete da entrega. --
-- O funcionário responsável (funcionario_id) só existe depois que alguém avança a entrega; --
-- os pedidos que ainda não foram atendidos (e os cancelados) ficam com NULL. --
-- O status acompanha os pagamentos e a entrega: 1 Aguardando pagamento (há pagamento pendente), --
-- 2 Processando (todos os pagamentos aprovados), 3 Concluído (entrega entregue), 4 Cancelado. --
-- Os pedidos 21 e 81 ficaram sem pagamento até hoje (boleto ou PIX vencido), para mostrar pagamentos atrasados. --
INSERT INTO pedidos (data, total, status, entrega_id, cliente_id, funcionario_id) VALUES
('2026-05-05T09:17:00', 189.80, 3, 1, 1, 10), -- 1: Concluído
('2026-05-05T14:51:00', 1224.60, 3, 2, 2, 10), -- 2: Concluído
('2026-05-06T12:07:00', 39.90, 3, 3, 11, 9), -- 3: Concluído
('2026-05-07T09:31:00', 43.80, 3, 4, 5, 10), -- 4: Concluído
('2026-05-08T15:00:00', 59.80, 3, 5, 5, 9), -- 5: Concluído
('2026-05-09T08:46:00', 39.90, 3, 6, 12, 9), -- 6: Concluído
('2026-05-10T10:05:00', 42.80, 3, 7, 13, 8), -- 7: Concluído
('2026-05-13T10:32:00', 164.50, 3, 8, 14, 10), -- 8: Concluído
('2026-05-18T08:59:00', 99.50, 3, 9, 15, 10), -- 9: Concluído
('2026-05-19T10:53:00', 169.90, 4, 10, 1, NULL), -- 10: Cancelado
('2026-05-21T14:18:00', 74.80, 3, 11, 11, 9), -- 11: Concluído
('2026-05-21T09:47:00', 119.90, 3, 12, 12, 9), -- 12: Concluído
('2026-05-22T11:33:00', 82.70, 3, 13, 13, 8), -- 13: Concluído
('2026-05-22T16:25:00', 74.70, 3, 14, 4, 10), -- 14: Concluído
('2026-05-22T09:57:00', 24.90, 3, 15, 14, 7), -- 15: Concluído
('2026-05-22T14:55:00', 62.80, 3, 16, 15, 9), -- 16: Concluído
('2026-05-22T16:04:00', 44.80, 3, 17, 1, 10), -- 17: Concluído
('2026-05-26T10:46:00', 34.90, 3, 18, 16, 10), -- 18: Concluído
('2026-05-29T12:57:00', 162.60, 3, 19, 19, 8), -- 19: Concluído
('2026-05-29T11:26:00', 204.60, 3, 20, 11, 9), -- 20: Concluído
('2026-06-02T16:55:00', 34.90, 1, 21, 12, NULL), -- 21: Aguardando pagamento
('2026-06-02T11:42:00', 27.80, 3, 22, 18, 10), -- 22: Concluído
('2026-06-06T12:12:00', 174.60, 3, 23, 13, 10), -- 23: Concluído
('2026-06-07T13:16:00', 109.80, 3, 24, 14, 8), -- 24: Concluído
('2026-06-09T12:10:00', 152.80, 3, 25, 15, 8), -- 25: Concluído
('2026-06-11T12:24:00', 69.60, 3, 26, 1, 8), -- 26: Concluído
('2026-06-12T08:19:00', 79.60, 4, 27, 11, NULL), -- 27: Cancelado
('2026-06-14T14:34:00', 147.60, 3, 28, 12, 8), -- 28: Concluído
('2026-06-16T16:19:00', 144.60, 3, 29, 13, 9), -- 29: Concluído
('2026-06-16T09:26:00', 49.80, 3, 30, 2, 7), -- 30: Concluído
('2026-06-18T09:07:00', 52.70, 3, 31, 14, 7), -- 31: Concluído
('2026-06-19T14:41:00', 64.80, 3, 32, 15, 9), -- 32: Concluído
('2026-06-20T14:08:00', 259.90, 3, 33, 1, 10), -- 33: Concluído
('2026-06-21T11:38:00', 52.70, 3, 34, 11, 7), -- 34: Concluído
('2026-06-23T09:22:00', 714.60, 3, 35, 12, 9), -- 35: Concluído
('2026-06-26T09:04:00', 39.90, 3, 36, 13, 8), -- 36: Concluído
('2026-06-26T14:41:00', 147.60, 3, 37, 14, 7), -- 37: Concluído
('2026-06-26T09:33:00', 204.80, 3, 38, 15, 7), -- 38: Concluído
('2026-06-26T08:19:00', 44.90, 3, 39, 16, 10), -- 39: Concluído
('2026-06-27T08:03:00', 27.80, 3, 40, 1, 9), -- 40: Concluído
('2026-06-30T15:37:00', 84.50, 3, 41, 11, 8), -- 41: Concluído
('2026-07-02T16:31:00', 44.90, 3, 42, 12, 9), -- 42: Concluído
('2026-07-03T09:44:00', 164.70, 3, 43, 3, 10), -- 43: Concluído
('2026-07-04T09:16:00', 129.80, 4, 44, 20, NULL), -- 44: Cancelado
('2026-07-05T08:18:00', 169.90, 3, 45, 13, 8), -- 45: Concluído
('2026-07-07T12:02:00', 87.80, 3, 46, 14, 8), -- 46: Concluído
('2026-07-07T09:06:00', 49.80, 3, 47, 15, 7), -- 47: Concluído
('2026-07-08T12:44:00', 29.90, 3, 48, 1, 9), -- 48: Concluído
('2026-07-09T16:01:00', 359.60, 3, 49, 21, 7), -- 49: Concluído
('2026-07-11T09:57:00', 59.80, 3, 50, 11, 9), -- 50: Concluído
('2026-07-12T13:17:00', 104.70, 3, 51, 4, 7), -- 51: Concluído
('2026-07-14T14:26:00', 32.80, 3, 52, 12, 10), -- 52: Concluído
('2026-07-14T11:57:00', 34.80, 3, 53, 13, 7), -- 53: Concluído
('2026-07-15T16:08:00', 39.80, 3, 54, 14, 10), -- 54: Concluído
('2026-07-15T09:00:00', 37.80, 3, 55, 15, 7), -- 55: Concluído
('2026-07-15T13:23:00', 944.50, 3, 56, 1, 7), -- 56: Concluído
('2026-07-18T14:32:00', 69.90, 3, 57, 11, 7), -- 57: Concluído
('2026-07-19T13:52:00', 159.60, 3, 58, 12, 9), -- 58: Concluído
('2026-07-31T15:55:00', 334.50, 3, 59, 4, 9), -- 59: Concluído
('2026-08-02T14:59:00', 24.90, 3, 60, 13, 9), -- 60: Concluído
('2026-08-05T11:00:00', 32.80, 4, 61, 14, NULL), -- 61: Cancelado
('2026-08-05T10:15:00', 69.80, 3, 62, 15, 10), -- 62: Concluído
('2026-08-06T14:50:00', 29.80, 3, 63, 1, 10), -- 63: Concluído
('2026-08-07T14:03:00', 42.80, 3, 64, 2, 8), -- 64: Concluído
('2026-08-08T09:56:00', 124.80, 3, 65, 11, 9), -- 65: Concluído
('2026-08-11T15:45:00', 109.80, 3, 66, 12, 8), -- 66: Concluído
('2026-08-12T11:38:00', 42.80, 3, 67, 13, 9), -- 67: Concluído
('2026-08-14T08:06:00', 49.80, 3, 68, 14, 9), -- 68: Concluído
('2026-08-18T14:13:00', 99.80, 3, 69, 20, 9), -- 69: Concluído
('2026-08-20T14:54:00', 42.80, 3, 70, 16, 8), -- 70: Concluído
('2026-08-22T09:48:00', 144.50, 3, 71, 17, 8), -- 71: Concluído
('2026-08-22T16:46:00', 34.90, 3, 72, 15, 8), -- 72: Concluído
('2026-08-25T13:26:00', 102.70, 3, 73, 1, 7), -- 73: Concluído
('2026-08-27T15:42:00', 684.60, 3, 74, 11, 8), -- 74: Concluído
('2026-08-27T08:30:00', 159.80, 3, 75, 5, 10), -- 75: Concluído
('2026-08-27T15:05:00', 47.80, 3, 76, 12, 7), -- 76: Concluído
('2026-08-29T13:33:00', 64.80, 3, 77, 13, 10), -- 77: Concluído
('2026-08-29T09:53:00', 24.90, 4, 78, 22, NULL), -- 78: Cancelado
('2026-08-30T15:25:00', 174.50, 3, 79, 14, 7), -- 79: Concluído
('2026-08-30T16:28:00', 189.60, 3, 80, 15, 8), -- 80: Concluído
('2026-09-01T15:27:00', 19.90, 1, 81, 1, NULL), -- 81: Aguardando pagamento
('2026-09-01T10:26:00', 152.70, 3, 82, 11, 8), -- 82: Concluído
('2026-09-03T08:51:00', 64.80, 3, 83, 6, 10), -- 83: Concluído
('2026-09-04T09:22:00', 49.80, 3, 84, 17, 8), -- 84: Concluído
('2026-09-06T16:39:00', 419.80, 3, 85, 17, 10), -- 85: Concluído
('2026-09-06T13:05:00', 79.80, 3, 86, 12, 10), -- 86: Concluído
('2026-09-09T15:26:00', 29.90, 3, 87, 13, 8), -- 87: Concluído
('2026-09-09T13:22:00', 72.50, 3, 88, 14, 9), -- 88: Concluído
('2026-09-10T08:18:00', 64.80, 3, 89, 2, 8), -- 89: Concluído
('2026-09-10T15:20:00', 19.90, 3, 90, 15, 10), -- 90: Concluído
('2026-09-12T11:28:00', 72.70, 3, 91, 17, 9), -- 91: Concluído
('2026-09-13T14:11:00', 124.80, 3, 92, 20, 7), -- 92: Concluído
('2026-09-14T09:40:00', 69.80, 3, 93, 18, 10), -- 93: Concluído
('2026-09-14T15:59:00', 159.60, 3, 94, 1, 7), -- 94: Concluído
('2026-09-15T11:36:00', 39.80, 4, 95, 11, NULL), -- 95: Cancelado
('2026-09-16T15:31:00', 49.90, 3, 96, 12, 10), -- 96: Concluído
('2026-09-16T09:02:00', 117.60, 3, 97, 19, 9), -- 97: Concluído
('2026-09-17T14:12:00', 79.80, 3, 98, 3, 8), -- 98: Concluído
('2026-09-18T15:02:00', 29.90, 3, 99, 6, 10), -- 99: Concluído
('2026-09-18T09:01:00', 719.60, 3, 100, 17, 9), -- 100: Concluído
('2026-09-18T16:57:00', 59.80, 3, 101, 5, 10), -- 101: Concluído
('2026-09-20T11:03:00', 59.90, 3, 102, 13, 10), -- 102: Concluído
('2026-09-20T10:03:00', 32.80, 3, 103, 14, 8), -- 103: Concluído
('2026-09-21T09:41:00', 54.80, 3, 104, 15, 10), -- 104: Concluído
('2026-09-22T16:28:00', 24.90, 3, 105, 1, 8), -- 105: Concluído
('2026-09-23T14:14:00', 73.80, 3, 106, 5, 7), -- 106: Concluído
('2026-09-23T12:38:00', 164.50, 3, 107, 16, 9), -- 107: Concluído
('2026-09-24T11:20:00', 44.70, 3, 108, 2, 10), -- 108: Concluído
('2026-09-27T13:30:00', 72.60, 3, 109, 11, 10), -- 109: Concluído
('2026-09-30T08:09:00', 94.60, 3, 110, 22, 8), -- 110: Concluído
('2026-09-30T08:09:00', 199.50, 3, 111, 21, 10), -- 111: Concluído
('2026-10-02T14:11:00', 37.80, 4, 112, 12, NULL), -- 112: Cancelado
('2026-10-04T13:38:00', 54.80, 3, 113, 13, 9), -- 113: Concluído
('2026-10-05T15:13:00', 149.90, 3, 114, 14, 8), -- 114: Concluído
('2026-10-06T13:47:00', 62.70, 3, 115, 15, 10), -- 115: Concluído
('2026-10-08T09:00:00', 169.60, 3, 116, 3, 8), -- 116: Concluído
('2026-10-08T13:01:00', 24.90, 3, 117, 1, 10), -- 117: Concluído
('2026-10-10T10:04:00', 33.80, 3, 118, 22, 8), -- 118: Concluído
('2026-10-13T10:03:00', 79.80, 3, 119, 3, 8), -- 119: Concluído
('2026-10-14T12:35:00', 29.80, 3, 120, 11, 9), -- 120: Concluído
('2026-10-15T14:32:00', 189.80, 3, 121, 20, 10), -- 121: Concluído
('2026-10-21T16:13:00', 49.80, 2, 122, 6, 9), -- 122: Processando
('2026-10-21T14:22:00', 139.90, 2, 123, 12, 7), -- 123: Processando
('2026-10-28T16:26:00', 102.60, 2, 124, 13, NULL), -- 124: Processando
('2026-10-29T11:09:00', 124.70, 1, 125, 14, NULL), -- 125: Aguardando pagamento
('2026-10-30T16:55:00', 104.70, 3, 126, 15, 7), -- 126: Concluído
('2026-10-30T08:26:00', 72.60, 2, 127, 1, 7), -- 127: Processando
('2026-10-30T08:42:00', 59.80, 2, 128, 11, 9), -- 128: Processando
('2026-10-31T16:35:00', 139.80, 4, 129, 16, NULL), -- 129: Cancelado
('2026-10-31T13:40:00', 92.70, 2, 130, 12, NULL); -- 130: Processando
GO

-- Tabela Item_Pedidos --
-- Cada venda da planilha tem um produto, a quantidade vendida e o preço unitário daquela venda. --
-- O mesmo produto aparece em vários pedidos (relacionamento N x N entre pedidos e produtos) e, como --
-- o preço de cada venda pode ser diferente do preço atual do produto, o preço fica guardado no item. --
INSERT INTO item_pedidos (pedido_id, produto_id, quantidade, preco_unitario) VALUES
(1, 1, 2, 94.90),
(2, 2, 3, 399.90),
(3, 3, 1, 39.90),
(4, 4, 1, 24.90),
(5, 5, 1, 24.90),
(6, 6, 1, 39.90),
(7, 7, 1, 29.90),
(8, 8, 4, 34.90),
(9, 9, 5, 19.90),
(10, 10, 1, 169.90),
(11, 8, 1, 49.90),
(12, 10, 1, 119.90),
(13, 4, 2, 34.90),
(14, 11, 2, 19.90),
(15, 12, 1, 24.90),
(16, 13, 1, 49.90),
(17, 14, 1, 19.90),
(18, 15, 1, 34.90),
(19, 16, 3, 44.90),
(20, 17, 3, 59.90),
(21, 18, 1, 34.90),
(22, 19, 1, 14.90),
(23, 20, 3, 49.90),
(24, 21, 2, 54.90),
(25, 22, 1, 139.90),
(26, 11, 3, 14.90),
(27, 11, 4, 19.90),
(28, 23, 3, 44.90),
(29, 24, 3, 39.90),
(30, 25, 2, 24.90),
(31, 26, 2, 19.90),
(32, 27, 1, 39.90),
(33, 28, 1, 259.90),
(34, 24, 2, 19.90),
(35, 29, 3, 229.90),
(36, 30, 1, 39.90),
(37, 31, 3, 44.90),
(38, 29, 1, 179.90),
(39, 32, 1, 44.90),
(40, 33, 1, 14.90),
(41, 6, 4, 14.90),
(42, 34, 1, 44.90),
(43, 31, 3, 54.90),
(44, 22, 1, 79.90),
(45, 35, 1, 169.90),
(46, 36, 1, 74.90),
(47, 37, 1, 24.90),
(48, 16, 1, 29.90),
(49, 35, 4, 89.90),
(50, 38, 1, 34.90),
(51, 30, 3, 34.90),
(52, 39, 1, 19.90),
(53, 40, 1, 9.90),
(54, 41, 2, 19.90),
(55, 8, 1, 24.90),
(56, 42, 4, 229.90),
(57, 43, 1, 69.90),
(58, 44, 4, 39.90),
(59, 45, 4, 74.90),
(60, 46, 1, 24.90),
(61, 47, 1, 19.90),
(62, 48, 1, 44.90),
(63, 49, 2, 14.90),
(64, 25, 1, 29.90),
(65, 50, 1, 99.90),
(66, 10, 2, 54.90),
(67, 51, 1, 29.90),
(68, 52, 1, 24.90),
(69, 49, 2, 49.90),
(70, 53, 1, 29.90),
(71, 41, 4, 29.90),
(72, 54, 1, 34.90),
(73, 46, 2, 44.90),
(74, 55, 3, 219.90),
(75, 56, 2, 79.90),
(76, 8, 1, 34.90),
(77, 57, 1, 39.90),
(78, 58, 1, 24.90),
(79, 59, 5, 34.90),
(80, 51, 3, 54.90),
(81, 60, 1, 19.90),
(82, 19, 2, 69.90),
(83, 61, 1, 29.90),
(84, 62, 2, 24.90),
(85, 63, 2, 209.90),
(86, 64, 1, 54.90),
(87, 34, 1, 29.90),
(88, 49, 4, 14.90),
(89, 65, 1, 39.90),
(90, 58, 1, 19.90),
(91, 66, 2, 29.90),
(92, 27, 1, 74.90),
(93, 3, 2, 34.90),
(94, 39, 4, 39.90),
(95, 67, 1, 14.90),
(96, 19, 1, 49.90),
(97, 6, 3, 29.90),
(98, 68, 1, 44.90),
(99, 60, 1, 29.90),
(100, 69, 4, 179.90),
(101, 41, 1, 24.90),
(102, 27, 1, 59.90),
(103, 58, 1, 19.90),
(104, 70, 1, 29.90),
(105, 71, 1, 24.90),
(106, 52, 1, 54.90),
(107, 51, 4, 34.90),
(108, 72, 3, 14.90),
(109, 73, 3, 19.90),
(110, 53, 3, 19.90),
(111, 39, 5, 39.90),
(112, 58, 1, 24.90),
(113, 53, 1, 29.90),
(114, 74, 1, 149.90),
(115, 33, 2, 24.90),
(116, 75, 3, 44.90),
(117, 53, 1, 24.90),
(118, 76, 1, 14.90),
(119, 3, 1, 44.90),
(120, 77, 2, 14.90),
(121, 10, 2, 94.90),
(122, 78, 1, 14.90),
(123, 22, 1, 139.90),
(124, 8, 3, 29.90),
(125, 79, 2, 49.90),
(126, 30, 3, 34.90),
(127, 80, 3, 19.90),
(128, 31, 1, 34.90),
(129, 42, 2, 69.90),
(130, 81, 2, 39.90);
GO

-- Baixa do estoque dos produtos vendidos. As triggers só são criadas depois deste arquivo, --
-- então a baixa é feita aqui, com o mesmo cálculo da trigger tr_item_pedidos_baixa_estoque. --
-- Pedidos cancelados (status 4) não consomem estoque. O estoque informado na tabela produtos --
-- é o estoque inicial, antes dessas vendas. --
UPDATE prod SET prod.estoque = prod.estoque - vendidos.quantidade_total
FROM produtos prod
INNER JOIN (
	SELECT ip.produto_id, SUM(ip.quantidade) AS quantidade_total
	FROM item_pedidos ip
	INNER JOIN pedidos pe ON pe.id = ip.pedido_id
	WHERE pe.status <> 4
	GROUP BY ip.produto_id
) AS vendidos ON vendidos.produto_id = prod.id;
GO

-- Tabela Pagamentos --
-- A soma das parcelas de cada pedido é igual ao total do pedido (produtos + frete). --
-- As formas de pagamento aceitas pelo sistema são PIX, CARTAO_CREDITO e BOLETO. --
-- O parcelamento só existe no cartão de crédito, em até 3 vezes e com parcela mínima de R$ 20,00, e os centavos --
-- sobram na última parcela, como na função fn_parcelas (ex.: 70,00 em 3x = 23,33 + 23,33 + 23,34). --
-- O status acompanha o pedido: pagamento APROVADO (2) tem data_pagamento e PENDENTE (1) não tem. --
-- No pedido cancelado, o pagamento aprovado virou COMPRA ESTORNADA (4) e o boleto que nunca foi pago ficou RECUSADO (3), --
-- como faz a procedure sp_cancelar_pedido. --
-- As colunas bandeira e final_cartao guardam a bandeira e os 4 últimos dígitos do cartão usado (NULL em PIX e boleto). --
-- Os clientes 1, 2 e 3 usam os cartões salvos deles; os demais pagaram com cartões que não foram salvos no site. --
-- Status: 1 Pendente | 2 Aprovado | 3 Recusado | 4 Compra estornada (pagamento aprovado de pedido cancelado) --
INSERT INTO pagamentos (numero_parcela, status, valor, quantidade_parcelas, forma_pagamento, data_vencimento, data_pagamento, bandeira, final_cartao, pedido_id) VALUES
(1, 2, 189.80, 1, 'BOLETO', '2026-05-10', '2026-05-07', NULL, NULL, 1),
(1, 2, 1224.60, 1, 'CARTAO_CREDITO', '2026-05-05', '2026-05-05', 'mastercard', '5555', 2),
(1, 2, 39.90, 1, 'PIX', '2026-05-06', '2026-05-06', NULL, NULL, 3),
(1, 2, 43.80, 1, 'CARTAO_CREDITO', '2026-05-07', '2026-05-07', 'elo', '4564', 4),
(1, 2, 59.80, 1, 'PIX', '2026-05-08', '2026-05-08', NULL, NULL, 5),
(1, 2, 39.90, 1, 'PIX', '2026-05-09', '2026-05-09', NULL, NULL, 6),
(1, 2, 42.80, 1, 'PIX', '2026-05-10', '2026-05-10', NULL, NULL, 7),
(1, 2, 164.50, 1, 'PIX', '2026-05-13', '2026-05-13', NULL, NULL, 8),
(1, 2, 33.16, 3, 'CARTAO_CREDITO', '2026-05-18', '2026-05-18', 'visa', '3147', 9),
(2, 2, 33.16, 3, 'CARTAO_CREDITO', '2026-06-18', '2026-05-18', 'visa', '3147', 9),
(3, 2, 33.18, 3, 'CARTAO_CREDITO', '2026-07-18', '2026-05-18', 'visa', '3147', 9),
(1, 4, 169.90, 1, 'PIX', '2026-05-19', '2026-05-19', NULL, NULL, 10),
(1, 2, 74.80, 1, 'PIX', '2026-05-21', '2026-05-21', NULL, NULL, 11),
(1, 2, 119.90, 1, 'PIX', '2026-05-21', '2026-05-21', NULL, NULL, 12),
(1, 2, 82.70, 1, 'PIX', '2026-05-22', '2026-05-22', NULL, NULL, 13),
(1, 2, 24.90, 3, 'CARTAO_CREDITO', '2026-05-22', '2026-05-22', 'elo', '3593', 14),
(2, 2, 24.90, 3, 'CARTAO_CREDITO', '2026-06-22', '2026-05-22', 'elo', '3593', 14),
(3, 2, 24.90, 3, 'CARTAO_CREDITO', '2026-07-22', '2026-05-22', 'elo', '3593', 14),
(1, 2, 24.90, 1, 'PIX', '2026-05-22', '2026-05-22', NULL, NULL, 15),
(1, 2, 62.80, 1, 'BOLETO', '2026-05-27', '2026-05-24', NULL, NULL, 16),
(1, 2, 44.80, 1, 'PIX', '2026-05-22', '2026-05-22', NULL, NULL, 17),
(1, 2, 34.90, 1, 'BOLETO', '2026-05-31', '2026-05-28', NULL, NULL, 18),
(1, 2, 162.60, 1, 'CARTAO_CREDITO', '2026-05-29', '2026-05-29', 'elo', '3736', 19),
(1, 2, 204.60, 1, 'PIX', '2026-05-29', '2026-05-29', NULL, NULL, 20),
(1, 1, 34.90, 1, 'PIX', '2026-06-02', NULL, NULL, NULL, 21),
(1, 2, 27.80, 1, 'CARTAO_CREDITO', '2026-06-02', '2026-06-02', 'elo', '1437', 22),
(1, 2, 174.60, 1, 'CARTAO_CREDITO', '2026-06-06', '2026-06-06', 'mastercard', '9915', 23),
(1, 2, 109.80, 1, 'PIX', '2026-06-07', '2026-06-07', NULL, NULL, 24),
(1, 2, 50.93, 3, 'CARTAO_CREDITO', '2026-06-09', '2026-06-09', 'elo', '7180', 25),
(2, 2, 50.93, 3, 'CARTAO_CREDITO', '2026-07-09', '2026-06-09', 'elo', '7180', 25),
(3, 2, 50.94, 3, 'CARTAO_CREDITO', '2026-08-09', '2026-06-09', 'elo', '7180', 25),
(1, 2, 69.60, 1, 'CARTAO_CREDITO', '2026-06-11', '2026-06-11', 'visa', '4242', 26),
(1, 4, 79.60, 1, 'PIX', '2026-06-12', '2026-06-12', NULL, NULL, 27),
(1, 2, 147.60, 1, 'BOLETO', '2026-06-19', '2026-06-16', NULL, NULL, 28),
(1, 2, 144.60, 1, 'CARTAO_CREDITO', '2026-06-16', '2026-06-16', 'mastercard', '4283', 29),
(1, 2, 49.80, 1, 'PIX', '2026-06-16', '2026-06-16', NULL, NULL, 30),
(1, 2, 52.70, 1, 'CARTAO_CREDITO', '2026-06-18', '2026-06-18', 'visa', '7743', 31),
(1, 2, 64.80, 1, 'CARTAO_CREDITO', '2026-06-19', '2026-06-19', 'visa', '1654', 32),
(1, 2, 259.90, 1, 'BOLETO', '2026-06-25', '2026-06-22', NULL, NULL, 33),
(1, 2, 52.70, 1, 'PIX', '2026-06-21', '2026-06-21', NULL, NULL, 34),
(1, 2, 714.60, 1, 'PIX', '2026-06-23', '2026-06-23', NULL, NULL, 35),
(1, 2, 39.90, 1, 'PIX', '2026-06-26', '2026-06-26', NULL, NULL, 36),
(1, 2, 147.60, 1, 'CARTAO_CREDITO', '2026-06-26', '2026-06-26', 'elo', '6609', 37),
(1, 2, 204.80, 1, 'BOLETO', '2026-07-01', '2026-06-28', NULL, NULL, 38),
(1, 2, 44.90, 1, 'PIX', '2026-06-26', '2026-06-26', NULL, NULL, 39),
(1, 2, 27.80, 1, 'PIX', '2026-06-27', '2026-06-27', NULL, NULL, 40),
(1, 2, 84.50, 1, 'BOLETO', '2026-07-05', '2026-07-02', NULL, NULL, 41),
(1, 2, 22.45, 2, 'CARTAO_CREDITO', '2026-07-02', '2026-07-02', 'visa', '7866', 42),
(2, 2, 22.45, 2, 'CARTAO_CREDITO', '2026-08-02', '2026-07-02', 'visa', '7866', 42),
(1, 2, 82.35, 2, 'CARTAO_CREDITO', '2026-07-03', '2026-07-03', 'visa', '1111', 43),
(2, 2, 82.35, 2, 'CARTAO_CREDITO', '2026-08-03', '2026-07-03', 'visa', '1111', 43),
(1, 4, 129.80, 1, 'CARTAO_CREDITO', '2026-07-04', '2026-07-04', 'visa', '6593', 44),
(1, 2, 169.90, 1, 'BOLETO', '2026-07-10', '2026-07-07', NULL, NULL, 45),
(1, 2, 87.80, 1, 'PIX', '2026-07-07', '2026-07-07', NULL, NULL, 46),
(1, 2, 49.80, 1, 'PIX', '2026-07-07', '2026-07-07', NULL, NULL, 47),
(1, 2, 29.90, 1, 'CARTAO_CREDITO', '2026-07-08', '2026-07-08', 'visa', '4242', 48),
(1, 2, 359.60, 1, 'CARTAO_CREDITO', '2026-07-09', '2026-07-09', 'mastercard', '7070', 49),
(1, 2, 59.80, 1, 'PIX', '2026-07-11', '2026-07-11', NULL, NULL, 50),
(1, 2, 104.70, 1, 'CARTAO_CREDITO', '2026-07-12', '2026-07-12', 'elo', '3304', 51),
(1, 2, 32.80, 1, 'CARTAO_CREDITO', '2026-07-14', '2026-07-14', 'elo', '6635', 52),
(1, 2, 34.80, 1, 'BOLETO', '2026-07-19', '2026-07-16', NULL, NULL, 53),
(1, 2, 39.80, 1, 'CARTAO_CREDITO', '2026-07-15', '2026-07-15', 'elo', '5043', 54),
(1, 2, 37.80, 1, 'CARTAO_CREDITO', '2026-07-15', '2026-07-15', 'elo', '8462', 55),
(1, 2, 944.50, 1, 'CARTAO_CREDITO', '2026-07-15', '2026-07-15', 'visa', '4242', 56),
(1, 2, 23.30, 3, 'CARTAO_CREDITO', '2026-07-18', '2026-07-18', 'mastercard', '6321', 57),
(2, 2, 23.30, 3, 'CARTAO_CREDITO', '2026-08-18', '2026-07-18', 'mastercard', '6321', 57),
(3, 2, 23.30, 3, 'CARTAO_CREDITO', '2026-09-18', '2026-07-18', 'mastercard', '6321', 57),
(1, 2, 159.60, 1, 'CARTAO_CREDITO', '2026-07-19', '2026-07-19', 'mastercard', '3195', 58),
(1, 2, 334.50, 1, 'PIX', '2026-07-31', '2026-07-31', NULL, NULL, 59),
(1, 2, 24.90, 1, 'CARTAO_CREDITO', '2026-08-02', '2026-08-02', 'elo', '4399', 60),
(1, 4, 32.80, 1, 'CARTAO_CREDITO', '2026-08-05', '2026-08-05', 'mastercard', '6298', 61),
(1, 2, 69.80, 1, 'CARTAO_CREDITO', '2026-08-05', '2026-08-05', 'mastercard', '8902', 62),
(1, 2, 29.80, 1, 'PIX', '2026-08-06', '2026-08-06', NULL, NULL, 63),
(1, 2, 42.80, 1, 'BOLETO', '2026-08-12', '2026-08-09', NULL, NULL, 64),
(1, 2, 124.80, 1, 'BOLETO', '2026-08-13', '2026-08-10', NULL, NULL, 65),
(1, 2, 109.80, 1, 'PIX', '2026-08-11', '2026-08-11', NULL, NULL, 66),
(1, 2, 42.80, 1, 'PIX', '2026-08-12', '2026-08-12', NULL, NULL, 67),
(1, 2, 24.90, 2, 'CARTAO_CREDITO', '2026-08-14', '2026-08-14', 'elo', '6046', 68),
(2, 2, 24.90, 2, 'CARTAO_CREDITO', '2026-09-14', '2026-08-14', 'elo', '6046', 68),
(1, 2, 49.90, 2, 'CARTAO_CREDITO', '2026-08-18', '2026-08-18', 'mastercard', '5977', 69),
(2, 2, 49.90, 2, 'CARTAO_CREDITO', '2026-09-18', '2026-08-18', 'mastercard', '5977', 69),
(1, 2, 21.40, 2, 'CARTAO_CREDITO', '2026-08-20', '2026-08-20', 'mastercard', '6166', 70),
(2, 2, 21.40, 2, 'CARTAO_CREDITO', '2026-09-20', '2026-08-20', 'mastercard', '6166', 70),
(1, 2, 144.50, 1, 'CARTAO_CREDITO', '2026-08-22', '2026-08-22', 'visa', '6116', 71),
(1, 2, 34.90, 1, 'CARTAO_CREDITO', '2026-08-22', '2026-08-22', 'mastercard', '6626', 72),
(1, 2, 34.23, 3, 'CARTAO_CREDITO', '2026-08-25', '2026-08-25', 'visa', '4242', 73),
(2, 2, 34.23, 3, 'CARTAO_CREDITO', '2026-09-25', '2026-08-25', 'visa', '4242', 73),
(3, 2, 34.24, 3, 'CARTAO_CREDITO', '2026-10-25', '2026-08-25', 'visa', '4242', 73),
(1, 2, 684.60, 1, 'BOLETO', '2026-09-01', '2026-08-29', NULL, NULL, 74),
(1, 2, 159.80, 1, 'CARTAO_CREDITO', '2026-08-27', '2026-08-27', 'visa', '6130', 75),
(1, 2, 47.80, 1, 'PIX', '2026-08-27', '2026-08-27', NULL, NULL, 76),
(1, 2, 21.60, 3, 'CARTAO_CREDITO', '2026-08-29', '2026-08-29', 'mastercard', '4508', 77),
(2, 2, 21.60, 3, 'CARTAO_CREDITO', '2026-09-29', '2026-08-29', 'mastercard', '4508', 77),
(3, 2, 21.60, 3, 'CARTAO_CREDITO', '2026-10-29', '2026-08-29', 'mastercard', '4508', 77),
(1, 4, 24.90, 1, 'CARTAO_CREDITO', '2026-08-29', '2026-08-29', 'visa', '3833', 78),
(1, 2, 174.50, 1, 'PIX', '2026-08-30', '2026-08-30', NULL, NULL, 79),
(1, 2, 189.60, 1, 'PIX', '2026-08-30', '2026-08-30', NULL, NULL, 80),
(1, 1, 19.90, 1, 'PIX', '2026-09-01', NULL, NULL, NULL, 81),
(1, 2, 152.70, 1, 'PIX', '2026-09-01', '2026-09-01', NULL, NULL, 82),
(1, 2, 64.80, 1, 'PIX', '2026-09-03', '2026-09-03', NULL, NULL, 83),
(1, 2, 24.90, 2, 'CARTAO_CREDITO', '2026-09-04', '2026-09-04', 'mastercard', '5546', 84),
(2, 2, 24.90, 2, 'CARTAO_CREDITO', '2026-10-04', '2026-09-04', 'mastercard', '5546', 84),
(1, 2, 419.80, 1, 'BOLETO', '2026-09-11', '2026-09-08', NULL, NULL, 85),
(1, 2, 79.80, 1, 'CARTAO_CREDITO', '2026-09-06', '2026-09-06', 'mastercard', '4962', 86),
(1, 2, 29.90, 1, 'PIX', '2026-09-09', '2026-09-09', NULL, NULL, 87),
(1, 2, 36.25, 2, 'CARTAO_CREDITO', '2026-09-09', '2026-09-09', 'elo', '3132', 88),
(2, 2, 36.25, 2, 'CARTAO_CREDITO', '2026-10-09', '2026-09-09', 'elo', '3132', 88),
(1, 2, 32.40, 2, 'CARTAO_CREDITO', '2026-09-10', '2026-09-10', 'mastercard', '5555', 89),
(2, 2, 32.40, 2, 'CARTAO_CREDITO', '2026-10-10', '2026-09-10', 'mastercard', '5555', 89),
(1, 2, 19.90, 1, 'CARTAO_CREDITO', '2026-09-10', '2026-09-10', 'elo', '8136', 90),
(1, 2, 36.35, 2, 'CARTAO_CREDITO', '2026-09-12', '2026-09-12', 'elo', '3110', 91),
(2, 2, 36.35, 2, 'CARTAO_CREDITO', '2026-10-12', '2026-09-12', 'elo', '3110', 91),
(1, 2, 124.80, 1, 'BOLETO', '2026-09-18', '2026-09-15', NULL, NULL, 92),
(1, 2, 69.80, 1, 'PIX', '2026-09-14', '2026-09-14', NULL, NULL, 93),
(1, 2, 53.20, 3, 'CARTAO_CREDITO', '2026-09-14', '2026-09-14', 'visa', '4242', 94),
(2, 2, 53.20, 3, 'CARTAO_CREDITO', '2026-10-14', '2026-09-14', 'visa', '4242', 94),
(3, 2, 53.20, 3, 'CARTAO_CREDITO', '2026-11-14', '2026-09-14', 'visa', '4242', 94),
(1, 4, 39.80, 1, 'CARTAO_CREDITO', '2026-09-15', '2026-09-15', 'elo', '3344', 95),
(1, 2, 49.90, 1, 'CARTAO_CREDITO', '2026-09-16', '2026-09-16', 'visa', '7357', 96),
(1, 2, 39.20, 3, 'CARTAO_CREDITO', '2026-09-16', '2026-09-16', 'elo', '4586', 97),
(2, 2, 39.20, 3, 'CARTAO_CREDITO', '2026-10-16', '2026-09-16', 'elo', '4586', 97),
(3, 2, 39.20, 3, 'CARTAO_CREDITO', '2026-11-16', '2026-09-16', 'elo', '4586', 97),
(1, 2, 79.80, 1, 'BOLETO', '2026-09-22', '2026-09-19', NULL, NULL, 98),
(1, 2, 29.90, 1, 'CARTAO_CREDITO', '2026-09-18', '2026-09-18', 'mastercard', '2974', 99),
(1, 2, 239.86, 3, 'CARTAO_CREDITO', '2026-09-18', '2026-09-18', 'elo', '1374', 100),
(2, 2, 239.86, 3, 'CARTAO_CREDITO', '2026-10-18', '2026-09-18', 'elo', '1374', 100),
(3, 2, 239.88, 3, 'CARTAO_CREDITO', '2026-11-18', '2026-09-18', 'elo', '1374', 100),
(1, 2, 59.80, 1, 'PIX', '2026-09-18', '2026-09-18', NULL, NULL, 101),
(1, 2, 59.90, 1, 'CARTAO_CREDITO', '2026-09-20', '2026-09-20', 'visa', '2356', 102),
(1, 2, 32.80, 1, 'CARTAO_CREDITO', '2026-09-20', '2026-09-20', 'elo', '6848', 103),
(1, 2, 54.80, 1, 'PIX', '2026-09-21', '2026-09-21', NULL, NULL, 104),
(1, 2, 24.90, 1, 'CARTAO_CREDITO', '2026-09-22', '2026-09-22', 'visa', '4242', 105),
(1, 2, 73.80, 1, 'BOLETO', '2026-09-28', '2026-09-25', NULL, NULL, 106),
(1, 2, 164.50, 1, 'CARTAO_CREDITO', '2026-09-23', '2026-09-23', 'visa', '2207', 107),
(1, 2, 44.70, 1, 'CARTAO_CREDITO', '2026-09-24', '2026-09-24', 'mastercard', '5555', 108),
(1, 2, 24.19, 3, 'CARTAO_CREDITO', '2026-09-27', '2026-09-27', 'mastercard', '4796', 109),
(2, 2, 24.19, 3, 'CARTAO_CREDITO', '2026-10-27', '2026-09-27', 'mastercard', '4796', 109),
(3, 2, 24.22, 3, 'CARTAO_CREDITO', '2026-11-27', '2026-09-27', 'mastercard', '4796', 109),
(1, 2, 94.60, 1, 'BOLETO', '2026-10-05', '2026-10-02', NULL, NULL, 110),
(1, 2, 199.50, 1, 'PIX', '2026-09-30', '2026-09-30', NULL, NULL, 111),
(1, 4, 37.80, 1, 'CARTAO_CREDITO', '2026-10-02', '2026-10-02', 'mastercard', '1589', 112),
(1, 2, 27.40, 2, 'CARTAO_CREDITO', '2026-10-04', '2026-10-04', 'visa', '6468', 113),
(2, 2, 27.40, 2, 'CARTAO_CREDITO', '2026-11-04', '2026-10-04', 'visa', '6468', 113),
(1, 2, 149.90, 1, 'CARTAO_CREDITO', '2026-10-05', '2026-10-05', 'elo', '9167', 114),
(1, 2, 20.90, 3, 'CARTAO_CREDITO', '2026-10-06', '2026-10-06', 'visa', '8822', 115),
(2, 2, 20.90, 3, 'CARTAO_CREDITO', '2026-11-06', '2026-10-06', 'visa', '8822', 115),
(3, 2, 20.90, 3, 'CARTAO_CREDITO', '2026-12-06', '2026-10-06', 'visa', '8822', 115),
(1, 2, 169.60, 1, 'BOLETO', '2026-10-13', '2026-10-10', NULL, NULL, 116),
(1, 2, 24.90, 1, 'CARTAO_CREDITO', '2026-10-08', '2026-10-08', 'visa', '4242', 117),
(1, 2, 33.80, 1, 'CARTAO_CREDITO', '2026-10-10', '2026-10-10', 'mastercard', '5762', 118),
(1, 2, 26.60, 3, 'CARTAO_CREDITO', '2026-10-13', '2026-10-13', 'visa', '1111', 119),
(2, 2, 26.60, 3, 'CARTAO_CREDITO', '2026-11-13', '2026-10-13', 'visa', '1111', 119),
(3, 2, 26.60, 3, 'CARTAO_CREDITO', '2026-12-13', '2026-10-13', 'visa', '1111', 119),
(1, 2, 29.80, 1, 'BOLETO', '2026-10-19', '2026-10-16', NULL, NULL, 120),
(1, 2, 189.80, 1, 'PIX', '2026-10-15', '2026-10-15', NULL, NULL, 121),
(1, 2, 49.80, 1, 'BOLETO', '2026-10-26', '2026-10-23', NULL, NULL, 122),
(1, 2, 139.90, 1, 'CARTAO_CREDITO', '2026-10-21', '2026-10-21', 'visa', '5480', 123),
(1, 2, 51.30, 2, 'CARTAO_CREDITO', '2026-10-28', '2026-10-28', 'elo', '2637', 124),
(2, 2, 51.30, 2, 'CARTAO_CREDITO', '2026-11-28', '2026-10-28', 'elo', '2637', 124),
(1, 1, 124.70, 1, 'PIX', '2026-10-29', NULL, NULL, NULL, 125),
(1, 2, 104.70, 1, 'PIX', '2026-10-30', '2026-10-30', NULL, NULL, 126),
(1, 2, 72.60, 1, 'BOLETO', '2026-11-04', '2026-11-01', NULL, NULL, 127),
(1, 2, 59.80, 1, 'BOLETO', '2026-11-04', '2026-11-01', NULL, NULL, 128),
(1, 4, 139.80, 1, 'CARTAO_CREDITO', '2026-10-31', '2026-10-31', 'visa', '3493', 129),
(1, 2, 92.70, 1, 'BOLETO', '2026-11-05', '2026-11-02', NULL, NULL, 130);
GO
