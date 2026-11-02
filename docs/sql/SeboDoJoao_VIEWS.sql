-- VIEWS DO BANCO DE DADOS - SEBO DO JOÃO --
-- Exibe os pedidos com informações do cliente, funcionário responsável, --
-- status do pedido e dados de entrega, incluindo transportadora, rastreamento, --
-- valor do frete e endereço de entrega. --
-- Utilizada para consultas no histórico de pedidos e no painel administrativo. --
CREATE VIEW dbo.vw_pedidos_detalhados AS
SELECT pe.id AS pedido_id, pe.data, pe.total,
	   CASE pe.status 
			WHEN 1 THEN 'Aguardando pagamento'
			WHEN 2 THEN 'Processando'
			WHEN 3 THEN 'Concluido'
			WHEN 4 THEN 'Cancelado'
		END AS status_pedido,
		pe.cliente_id, uc.nome AS cliente, uc.email AS email_cliente,
		pe.funcionario_id, uf.nome AS funcionario,
		en.transportadora, en.codigo_rastreio, en.data_previsao_entrega,
		CASE en.status
			WHEN 1 THEN	'Pendente'
			WHEN 2 THEN 'Preparando'
			WHEN 3 THEN 'Em transito'
			WHEN 4 THEN 'Entregue'
			WHEN 5 THEN 'Cancelado'
		END AS status_entrega,
		-- ALTERADO: colunas novas no FINAL da view (as antigas continuam iguais, então quem já usa a view não é afetado): --
		-- (destinatário e telefone_entrega são o nome e o telefone da conta do cliente: o endereço não guarda mais esses dados) --
		-- o valor do frete (entregas.valor_frete) e o endereço de entrega (tabelas enderecos, ceps, cidades). --
		en.valor_frete,
		uc.nome AS destinatario, uc.telefone AS telefone_entrega, ed.logradouro, ed.numero AS numero_endereco,
		ed.complemento, ed.bairro, ed.cep_numero, ci.nome AS cidade, ci.uf_sigla AS uf
FROM pedidos pe
INNER JOIN	clientes cl			ON cl.usuario_id	=	pe.cliente_id
INNER JOIN	usuarios uc			ON uc.id			=	cl.usuario_id
INNER JOIN	funcionarios fu		ON fu.usuario_id	=	pe.funcionario_id
INNER JOIN	usuarios uf			ON uf.id			=	fu.usuario_id
LEFT JOIN	entregas en			ON en.id			=   pe.entrega_id
-- ALTERADO: junta o endereço da entrega, com o CEP, a cidade e a UF dele --
LEFT JOIN	enderecos ed		ON ed.id			=   en.endereco_id
LEFT JOIN	ceps ce				ON ce.numero_cep	=   ed.cep_numero
LEFT JOIN	cidades ci			ON ci.id			=   ce.cidade_id;
GO



-- Apresenta um resumo de vendas por produto, categoria e gênero, --
-- contabilizando a quantidade de pedidos, unidades vendidas e faturamento. --
-- Desconsidera os pedidos cancelados. Utilizada para relatórios e dashboards administrativos. --
CREATE VIEW dbo.vw_vendas_por_produto AS
SELECT prod.id AS produto_id, prod.nome AS produto, cat.nome AS categoria, gen.nome AS genero,
	   COUNT(DISTINCT i.pedido_id) AS qtd_pedidos,
	   SUM(i.quantidade) AS unidades_vendidas,
	   SUM(i.quantidade * i.preco_unitario) AS faturamento
FROM item_pedidos i
INNER JOIN	pedidos pe			ON pe.id			=	i.pedido_id AND pe.status IN (2, 3) -- só aceita os pedidos com status 2 (Processando) e 3 (Concluido)
INNER JOIN	produtos prod		ON prod.id			=	i.produto_id
INNER JOIN	categorias cat		ON cat.id			=	prod.categoria_id
INNER JOIN	generos gen			ON gen.id			=	prod.genero_id
GROUP BY	prod.id, prod.nome, cat.nome, gen.nome;
GO



-- Lista os pagamentos pendentes, apresentando os dados do cliente, --
-- informações das parcelas, valores e datas de vencimento. --
-- Calcula automaticamente a quantidade de dias de atraso de cada parcela. --
-- Utilizada para acompanhamento financeiro e controle de pagamentos. --
CREATE VIEW dbo.vw_pagamentos_pendentes AS
SELECT pg.id AS pagamento_id, pg.pedido_id, uc.nome AS cliente, pg.forma_pagamento,
	   pg.numero_parcela, pg.quantidade_parcelas, pg.valor, pg.data_vencimento,
	   CASE 
			WHEN pg.data_vencimento < CAST(GETDATE() AS DATE)
			THEN DATEDIFF(
					DAY, 
					pg.data_vencimento, 
					CAST(GETDATE() AS DATE)
				 ) 
			ELSE 0
	   END AS dias_de_atraso
FROM pagamentos pg
INNER JOIN	pedidos pe			ON	pe.id = pg.pedido_id
INNER JOIN	usuarios uc			ON  uc.id = pe.cliente_id
WHERE		pg.status = 1		AND			pe.status IN (1, 2, 3);
GO