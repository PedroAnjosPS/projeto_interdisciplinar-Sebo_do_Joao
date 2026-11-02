-- INSERTS DO BANCO DE DADOS - SEBO DO JOÃO --
-- 10 INSERTS PARA CADA TABELA --

-- ORDEM DE EXECUÇÃO (banco novo, recém-criado): --
-- 1-) Script_DELETANDO-BANCO  2-) SeboDoJoao (cria banco e tabelas)  3-) ESTE ARQUIVO --
-- 4-) FUNCTIONS  5-) VIEWS  6-) TRIGGERS  7-) PROCEDURES --
-- Este arquivo deve rodar ANTES das triggers: os pedidos, o estoque e os status abaixo já estão --
-- prontos e coerentes. Com as triggers criadas, a baixa de estoque seria feita duas vezes. --
-- Os IDs dos pedidos e das entregas (1 a 10) assumem um banco novo, onde o IDENTITY começa em 1. --
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

-- ALTERADO: os 17 estados que faltavam. Agora as 27 UFs do Brasil estão cadastradas. --
-- Motivo: ao salvar um endereço, o Java procura a UF informada nesta tabela (ela precisa existir, pois a --
-- coluna "nome" é obrigatória) e cria sozinho a cidade e o CEP que ainda não estiverem cadastrados. --
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
-- ALTERADO: a tabela usuarios não tem mais logradouro, bairro e cep_numero. O endereço agora fica na --
-- tabela enderecos (veja mais abaixo), que pertence ao cliente. Funcionários não têm endereço cadastrado. --
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

-- Tabela Clientes --
INSERT INTO clientes (usuario_id, data_nascimento, cpf) VALUES 
-- ALTERADO: os CPFs abaixo mantêm os mesmos 9 primeiros números de antes, mas com os 2 dígitos --
-- verificadores corretos. Antes (ex.: 111.222.333-44) o site os recusava como "CPF inválido" --
-- e o cliente não conseguia salvar o perfil. --
(1, '1985-04-12', '111.222.333-96'),
(2, '1992-09-25', '222.333.444-05'),
(3, '1978-12-03', '333.444.555-08'),
(4, '1995-06-18', '444.555.666-19'),
(5, '1988-11-30', '555.666.777-20'),
(6, '2000-01-15', '666.777.888-30');
GO

-- Tabela Funcionarios --
INSERT INTO funcionarios (usuario_id, codigo_funcionario, admin) VALUES 
(7, 'FUNC-001', 1),
(8, 'FUNC-002', 0),
(9, 'FUNC-003', 0),
(10, 'FUNC-004', 0);
GO

-- Tabela Enderecos --
-- NOVO: endereços de entrega dos clientes (1 a 6). O endereço de cada cliente é o que antes ficava --
-- na tabela usuarios (rua, bairro e CEP); o João tem um segundo endereço para demonstrar que um --
-- cliente pode ter vários. "padrao" = 1 marca o endereço que já vem escolhido no checkout (só um por cliente). --
-- Os endereços também são usados pelas entregas (veja a tabela entregas, coluna endereco_id). --
INSERT INTO enderecos (logradouro, numero, complemento, bairro, ativo, apelido, padrao, cep_numero, cliente_id) VALUES
('Rua das Flores',  '100',  NULL,       'Centro',      1, 'Casa',      1, '15010-000', 1), -- 1
('Av. Brasil',      '2500', 'Sala 12',  'Boa Vista',   1, 'Trabalho',  0, '15010-000', 1), -- 2
('Av. Paulista',    '500',  NULL,       'Bela Vista',  1, 'Casa',      1, '13010-000', 2), -- 3
('Rua da Praia',    '20',   'Apto 3',   'Copacabana',  1, 'Casa',      1, '20010-000', 3), -- 4
('Rua Bahia',       '45',   NULL,       'Savassi',     1, 'Casa',      1, '30110-000', 4), -- 5
('Rua XV',          '300',  NULL,       'Centro',      1, 'Casa',      1, '80010-000', 5), -- 6
('Av. Ipiranga',    '1000', 'Bloco B',  'Menino Deus', 1, 'Casa',      1, '90010-000', 6); -- 7
GO

-- Tabela Cartoes --
-- NOVO: cartões salvos pelos clientes. Só ficam a bandeira, os 4 últimos dígitos, o nome do titular e a --
-- validade (MM/AA). O número completo e o CVV NUNCA são guardados. "padrao" = 1: cartão já escolhido no checkout. --
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

/*UPDATE Categorias SET nome = 'Discos de Vinil'
Where nome = 'Disco de Vinil';
GO*/

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
('2001: Uma Odisséia no Espaço', 'Obra-prima de Arthur C. Clarke.', 3, 45.50, 1, 1, 1, 7),                -- Gênero: Ficção Científica (1)
('Uma Breve História do Tempo', 'Física por Stephen Hawking.', 2, 40.00, 1, 1, 9, 7),       -- Gênero: Didático (9)
-- Categoria: HQs (2)
('Batman: O Cavaleiro das Trevas', 'HQ clássica da DC Comics.', 4, 55.00, 1, 2, 6, 7), -- Gênero: Ação (6)
('The Sandman Vol. 1', 'Obra de Neil Gaiman.', 6, 60.00, 1, 2, 7, 7),                  -- Gênero: Aventura (7)
-- Categoria: Disco de Vinil (3)
('Vinil Pink Floyd - The Dark Side Of the Moon', 'Disco de vinil original em bom estado.', 2, 150.00, 1, 3, 4, 7), -- Gênero: Rock (4)
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
('/imagens/produtos/imagemProduto1.jpg', 1),
('/imagens/produtos/imagemProduto2.jpg', 2),
('/imagens/produtos/imagemProduto3.jpg', 3),
('/imagens/produtos/imagemProduto4.jpg', 4),
('/imagens/produtos/imagemProduto5.jpg', 5),
('/imagens/produtos/imagemProduto6.jpg', 6),
('/imagens/produtos/imagemProduto7.jpg', 7),
('/imagens/produtos/imagemProduto8.jpg', 8),
('/imagens/produtos/imagemProduto9.jpg', 9),
('/imagens/produtos/ImagemProduto10.jpg', 10); -- ALTERADO: o arquivo se chama "ImagemProduto10.jpg" (I maiúsculo); em sistemas que diferenciam maiúsculas o caminho antigo não achava a imagem
GO

-- Tabela Item_Carrinhos --
-- NOVO: carrinhos de compras que ainda não viraram pedido (clientes 3 e 4). Guarda só o produto e a --
-- quantidade; o preço é sempre o preço atual do produto, e o estoque só é baixado quando a compra é finalizada. --
INSERT INTO item_carrinhos (cliente_id, produto_id, quantidade) VALUES
(3, 1, 1),
(3, 5, 2),
(4, 6, 1);
GO

-- Tabela Entregas --
-- ALTERADO (nova coluna endereco_id): cada entrega aponta para o endereço do cliente que fez o pedido. --
-- O valor do frete (valor_frete) não foi informado: vale o padrão 0, porque os totais dos pedidos abaixo --
-- são só a soma dos itens (compras com retirada/frete grátis). --
-- ALTERADO: antes todas as entregas estavam "Pendente (1)", mas já tinham código de rastreio, e a --
-- data de postagem era a do dia em que o script rodava. Agora cada entrega tem um status coerente --
-- com o seu pedido, seguindo a mesma regra da procedure sp_avancar_entrega: --
--   * link, código e data de postagem só existem quando a entrega está EM TRÂNSITO (3) ou ENTREGUE (4); --
--   * a data de entrega só existe quando a entrega foi concluída (4). --
-- A entrega N pertence ao pedido N (entrega 1 -> pedido 1, entrega 2 -> pedido 2...). --
-- Status: 1 Pendente | 2 Preparando | 3 Em trânsito | 4 Entregue | 5 Cancelada --
INSERT INTO entregas (transportadora, status, link_rastreio, codigo_rastreio, data_postagem, data_previsao_entrega, data_entrega, endereco_id) VALUES
('Correios (PAC)',   4, 'https://rastreio.com/AB1',     'AB123456789BR', '2026-10-07T09:00:00', '2026-10-15', '2026-10-14T15:30:00', 1), -- 1: Entregue
('Correios (SEDEX)', 4, 'https://rastreio.com/AB2',     'AB987654321BR', '2026-10-09T10:00:00', '2026-10-13', '2026-10-12T11:20:00', 3), -- 2: Entregue
('Jadlog',           1, NULL,                           NULL,            NULL,                  '2026-10-22', NULL, 1),                  -- 3: Pendente
('Loggi',            2, NULL,                           NULL,            NULL,                  '2026-11-04', NULL, 3),                  -- 4: Preparando
('Correios (PAC)',   1, NULL,                           NULL,            NULL,                  '2026-11-06', NULL, 2),                  -- 5: Pendente
('Jadlog',           3, 'https://jadlog.com.br/JD2',    'JD444555666SP', '2026-10-26T14:00:00', '2026-11-03', NULL, 3),                  -- 6: Em trânsito
('Loggi',            5, NULL,                           NULL,            NULL,                  '2026-10-27', NULL, 1),                  -- 7: Cancelada
('Correios (SEDEX)', 4, 'https://rastreio.com/AB4',     'AB999888777BR', '2026-10-15T08:30:00', '2026-10-19', '2026-10-18T17:10:00', 3), -- 8: Entregue
('Jadlog',           1, NULL,                           NULL,            NULL,                  '2026-11-07', NULL, 1),                  -- 9: Pendente
('Correios (PAC)',   2, NULL,                           NULL,            NULL,                  '2026-11-05', NULL, 3);                  -- 10: Preparando
GO

-- Tabela Pedidos --
-- ALTERADO: --
--   * cliente_id 2002 não existe neste script (os clientes são os IDs 1 a 6); trocado por 2; --
--   * o total de cada pedido agora é a soma dos seus itens (veja a tabela item_pedidos); --
--   * "data" passou a ser informada (antes valia o dia em que o script rodava, depois dos vencimentos e --
--     das previsões de entrega); --
--   * o status acompanha os pagamentos e a entrega: 1 Aguardando pagamento (há pagamento pendente), --
--     2 Processando (todos os pagamentos aprovados), 3 Concluído (entrega entregue), 4 Cancelado. --
INSERT INTO pedidos (data, total, status, entrega_id, cliente_id, funcionario_id) VALUES
('2026-10-05T10:15:00',  80.50, 3,  1, 1, 7),   -- 1: Concluído
('2026-10-08T14:30:00',  45.50, 3,  2, 2, 8),   -- 2: Concluído
('2026-10-12T09:40:00',  40.00, 1,  3, 1, 9),   -- 3: Aguardando pagamento (boleto)
('2026-10-28T16:20:00',  55.00, 2,  4, 2, 10),  -- 4: Processando
('2026-10-30T11:05:00',  60.00, 2,  5, 1, 7),   -- 5: Processando
('2026-10-24T18:45:00', 150.00, 2,  6, 2, 8),   -- 6: Processando (entrega em trânsito)
('2026-10-20T13:10:00',  90.00, 4,  7, 1, 9),   -- 7: Cancelado
('2026-10-14T08:55:00',  30.00, 3,  8, 2, 10),  -- 8: Concluído
('2026-10-31T19:30:00',  35.00, 1,  9, 1, 7),   -- 9: Aguardando pagamento (PIX)
('2026-10-29T15:00:00',  70.00, 2, 10, 2, 8);   -- 10: Processando
GO

select * from pedidos
select * from produtos

-- Tabela Item_Pedidos --
-- ALTERADO: --
--   * os pedidos agora são 1 a 10 (antes apontavam para 1004 a 1013, que só existiam no banco antigo); --
--   * o preço de cada item é o preço do produto cadastrado (antes eram valores sem relação com ele); --
--   * os pedidos 1 e 10 têm dois produtos e o produto 2 e o produto 8 aparecem em mais de um pedido, --
--     o que demonstra o relacionamento N x N entre pedidos e produtos; --
--   * o pedido 7 tem 2 unidades do produto 7. --
INSERT INTO item_pedidos (pedido_id, produto_id, quantidade, preco_unitario) VALUES
(1,  1, 1,  35.00),
(1,  2, 1,  45.50),
(2,  2, 1,  45.50),
(3,  3, 1,  40.00),
(4,  4, 1,  55.00),
(5,  5, 1,  60.00),
(6,  6, 1, 150.00),
(7,  7, 2,  45.00),
(8,  8, 1,  30.00),
(9,  9, 1,  35.00),
(10, 10, 1, 40.00),
(10, 8, 1,  30.00);
GO

-- ALTERADO: baixa do estoque dos produtos vendidos. As triggers só são criadas depois deste arquivo, --
-- então a baixa é feita aqui, no mesmo cálculo da trigger tr_item_pedidos_baixa_estoque. --
-- Pedidos cancelados (status 4) não consomem estoque. Os dados da tabela produtos não foram mexidos: --
-- o estoque inserido lá é o estoque inicial. --
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
-- ALTERADO: --
--   * pedido_id agora é 1 a 10 (antes 1004 a 1013); --
--   * a soma das parcelas de cada pedido é igual ao total do pedido; --
--   * "CARTAO_DEBITO" não é uma forma aceita pelo sistema (só PIX, CARTAO_CREDITO e BOLETO); --
--   * parcelamento só no cartão de crédito e em até 3 vezes (regra da loja); os centavos sobram na --
--     última parcela, como na função fn_parcelas (ex.: 70,00 em 3x = 23,33 + 23,33 + 23,34); --
--   * o status acompanha o pedido: pagamento APROVADO (2) tem data_pagamento; PENDENTE (1) não tem; --
--     o boleto do pedido cancelado (7) ficou RECUSADO (3), como faz a procedure sp_cancelar_pedido; --
--   * os vencimentos não são mais anteriores à data do pedido. --
-- ALTERADO: duas colunas novas, bandeira e final_cartao: guardam a bandeira e os 4 últimos dígitos do cartão usado no pagamento (NULL em PIX e boleto). --
-- Status: 1 Pendente | 2 Aprovado | 3 Recusado --
INSERT INTO pagamentos (numero_parcela, status, valor, quantidade_parcelas, forma_pagamento, data_vencimento, data_pagamento, bandeira, final_cartao, pedido_id) VALUES
(1, 2,  80.50, 1, 'PIX',            '2026-10-05', '2026-10-05', NULL, NULL, 1),
(1, 2,  45.50, 1, 'CARTAO_CREDITO', '2026-10-08', '2026-10-08', 'mastercard', '5555', 2),
(1, 1,  40.00, 1, 'BOLETO',         '2026-10-17', NULL, NULL, NULL, 3),
(1, 2,  55.00, 1, 'PIX',            '2026-10-28', '2026-10-28', NULL, NULL, 4),
(1, 2,  30.00, 2, 'CARTAO_CREDITO', '2026-10-30', '2026-10-30', 'visa', '4242', 5),
(2, 2,  30.00, 2, 'CARTAO_CREDITO', '2026-11-30', '2026-10-30', 'visa', '4242', 5),
(1, 2, 150.00, 1, 'PIX',            '2026-10-24', '2026-10-24', NULL, NULL, 6),
(1, 3,  90.00, 1, 'BOLETO',         '2026-10-25', NULL, NULL, NULL, 7),
(1, 2,  30.00, 1, 'CARTAO_CREDITO', '2026-10-14', '2026-10-14', 'elo', '6362', 8),
(1, 1,  35.00, 1, 'PIX',            '2026-10-31', NULL, NULL, NULL, 9),
(1, 2,  23.33, 3, 'CARTAO_CREDITO', '2026-10-29', '2026-10-29', 'mastercard', '5555', 10),
(2, 2,  23.33, 3, 'CARTAO_CREDITO', '2026-11-29', '2026-10-29', 'mastercard', '5555', 10),
(3, 2,  23.34, 3, 'CARTAO_CREDITO', '2026-12-29', '2026-10-29', 'mastercard', '5555', 10);
GO