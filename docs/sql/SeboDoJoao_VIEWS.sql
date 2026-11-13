-----------------------------------------------------------------------
-- Acessar o Banco de Dados
-----------------------------------------------------------------------
use Sebo_do_Joao
go

-----------------------------------------------------------------------
-- PROGRAMAÇÃO DE BANCO DE DADOS - SEBO DO JOÃO
-- VIEWS
-----------------------------------------------------------------------

-----------------------------------------------------------------------
-- 1) Crie uma view para consultar todos os pedidos
-- Saída de dados: dados do pedido, do cliente, do funcionário que
-- cuida do pedido, da entrega e do endereço de entrega
-- O funcionário usa LEFT JOIN, pois o pedido só tem funcionário
-- responsável depois que alguém avança a entrega (antes disso é null)
-----------------------------------------------------------------------
create view vw_pedidos_detalhados
as
	select	pe.id pedido_id, pe.data, pe.total,
			case pe.status
				when 1 then 'Aguardando pagamento'
				when 2 then 'Processando'
				when 3 then 'Concluido'
				else 'Cancelado'
			end status_pedido,
			pe.cliente_id, uc.nome cliente, uc.email email_cliente,
			pe.funcionario_id, uf.nome funcionario,
			en.transportadora, en.codigo_rastreio, en.data_previsao_entrega,
			case en.status
				when 1 then 'Pendente'
				when 2 then 'Preparando'
				when 3 then 'Em transito'
				when 4 then 'Entregue'
				else 'Cancelado'
			end status_entrega,
			en.valor_frete,
			-- o destinatário e o telefone são os da conta do cliente
			uc.nome destinatario, uc.telefone telefone_entrega,
			ed.logradouro, ed.numero numero_endereco, ed.complemento, ed.bairro, ed.cep_numero,
			ci.nome cidade, ci.uf_sigla uf
	from	pedidos pe					INNER JOIN		usuarios uc
	on		pe.cliente_id = uc.id		LEFT JOIN		usuarios uf
	on		pe.funcionario_id = uf.id	INNER JOIN		entregas en
	on		pe.entrega_id = en.id		INNER JOIN		enderecos ed
	on		en.endereco_id = ed.id		INNER JOIN		ceps ce
	on		ed.cep_numero = ce.numero_cep	INNER JOIN	cidades ci
	on		ce.cidade_id = ci.id
go

-- executar a view
select * from vw_pedidos_detalhados
go

-----------------------------------------------------------------------
-- 2) Crie uma view para consultar as vendas de cada produto
-- Saída de dados: produto, categoria, gênero, quantidade de pedidos,
-- unidades vendidas e faturamento
-- Só entram os pedidos processando (2) e concluídos (3)
-----------------------------------------------------------------------
create view vw_vendas_por_produto
as
	select	pr.id produto_id, pr.nome produto, ca.nome categoria, ge.nome genero,
			count(distinct i.pedido_id) qtd_pedidos,
			sum(i.quantidade) unidades_vendidas,
			sum(i.quantidade * i.preco_unitario) faturamento
	from	item_pedidos i					INNER JOIN		pedidos pe
	on		i.pedido_id = pe.id				INNER JOIN		produtos pr
	on		i.produto_id = pr.id			INNER JOIN		categorias ca
	on		pr.categoria_id = ca.id			INNER JOIN		generos ge
	on		pr.genero_id = ge.id
	where	pe.status in (2, 3)
	group by pr.id, pr.nome, ca.nome, ge.nome
go

-- executar a view
select * from vw_vendas_por_produto
go

-----------------------------------------------------------------------
-- 3) Crie uma view para consultar os pagamentos pendentes
-- Saída de dados: pagamento, pedido, cliente, parcela, valor, data de
-- vencimento e quantos dias a parcela está atrasada
-----------------------------------------------------------------------
create view vw_pagamentos_pendentes
as
	select	pg.id pagamento_id, pg.pedido_id, uc.nome cliente, pg.forma_pagamento,
			pg.numero_parcela, pg.quantidade_parcelas, pg.valor, pg.data_vencimento,
			case
				when pg.data_vencimento < cast(getdate() as date)
				then datediff(day, pg.data_vencimento, cast(getdate() as date))
				else 0
			end dias_de_atraso
	from	pagamentos pg				INNER JOIN		pedidos pe
	on		pg.pedido_id = pe.id		INNER JOIN		usuarios uc
	on		pe.cliente_id = uc.id
	where	pg.status = 1 and pe.status in (1, 2, 3)
go

-- executar a view
select * from vw_pagamentos_pendentes
go


-----------------------------------------------------------------------------------------------------------
-- 4-) VIEW de Consulta de PEDIDOS (bem mais detalhado), para uso de dados estatísticos --
-- na planilha do Excel na parte de estatística do trabalho (TESTE) --
-----------------------------------------------------------------------------------------------------------
create view vw_relatorio_pedidos
as
select	
	-- Dados de pedidos --
		pe.id AS [Id do Pedido], 
		pe.data AS [Data do Pedido], 
		pe.total AS Total, 
		CASE pe.status
			WHEN 1 THEN 'AGUARDANDO PAGAMENTO'
			WHEN 2 THEN 'PROCESSANDO'
			WHEN 3 THEN 'CONCLUIDO'
			WHEN 4 THEN 'CANCELADO'
			ELSE 'NÃO INFORMADO'
		END AS [Status do Pedido],
	-- Dados de clientes e funcionários --
		uc.nome AS Cliente, 
		uf.nome AS [Funcionário], 
	-- Dados de produtos --
		prod.nome AS [Produto], 
		cat.nome AS Categoria,
		gen.nome AS Genero,
		item_pe.quantidade AS [Quantidade de Produto],
		item_pe.preco_unitario [Preço Unitário], 
	-- Valor do subtotal de produtos --
		(item_pe.quantidade * item_pe.preco_unitario) AS [Subtotal de Produto],
	-- Dados de entregas --
		ent.valor_frete AS [Frete de Entrega],
		CASE ent.status
			WHEN 1 THEN 'PENDENTE'
			WHEN 2 THEN 'PREPARANDO'
			WHEN 3 THEN 'EM TRÂNSITO'
			WHEN 4 THEN 'ENTREGUE'
			WHEN 5 THEN 'CANCELADO'
			ELSE 'NÃO INFORMADO'
		END AS [Status da Entrega],
	-- Dados de endereço da entrega --
		cid.nome AS Cidade,
		estado.nome AS Estado,
	-- Dados de pagamentos --
		CASE pag.status_pagamento
			WHEN 1 THEN 'PENDENTE'
			WHEN 2 THEN 'APROVADO'
			WHEN 3 THEN 'RECUSADO'
			WHEN 4 THEN 'COMPRA ESTORNADA'
			ELSE 'NÃO INFORMADO'
		END AS [Status de Pagamento],
		pag.forma_pagamento AS [Forma de Pagamento],
		pag.quantidade_parcelas AS [Quantidade de Parcelas],
		pag.valor_total AS [Valor Total Pago]

from pedidos pe		
-- Junção com cliente --
	LEFT JOIN		clientes cli			ON		 pe.cliente_id			=	cli.usuario_id	
	LEFT JOIN		usuarios uc				ON		 uc.id					=	cli.usuario_id	
-- Junção com funcionário responsável pelo pedido --
	LEFT JOIN		funcionarios func		ON		 pe.funcionario_id		=	func.usuario_id
	LEFT JOIN		usuarios uf				ON		 uf.id					=	func.usuario_id
-- Junção com produtos e itens --
	LEFT JOIN		item_pedidos item_pe	ON		 pe.id					=	item_pe.pedido_id
	LEFT JOIN		produtos prod			ON		 item_pe.produto_id		=	prod.id
	LEFT JOIN		categorias cat			ON		 cat.id					=	prod.categoria_id
	LEFT JOIN		generos gen				ON		 gen.id					=	prod.genero_id
-- Junção com entregas e endereços --
	LEFT JOIN		entregas ent			ON		 pe.entrega_id			=	ent.id
	LEFT JOIN		enderecos ender			ON		 ender.id				=	ent.endereco_id
	LEFT JOIN		ceps cep				ON		 cep.numero_cep			=	ender.cep_numero
	LEFT JOIN		cidades cid				ON		 cid.id					=	cep.cidade_id
	LEFT JOIN		ufs estado				ON		 estado.sigla			=	cid.uf_sigla
-- Junção com pagamentos com uso de um select mais robusto com o uso de funções, assim, -- 
-- evitando a repetição de vários registros de um mesmo pedido, pois pagamento pode ser parcelado em até 3 vezes --
-- e, consequentemente, cada pagamento de uma parcela possui um registro próprio no banco. --
	LEFT JOIN
	(
		select
			pedido_id,
			MAX(status) AS status_pagamento,
			MAX(forma_pagamento) AS forma_pagamento,
			MAX(quantidade_parcelas) AS quantidade_parcelas,
			SUM(valor) AS valor_total

		from pagamentos
		group by pedido_id
	) pag									ON		 pe.id					=	pag.pedido_id
go

-- testando a view --
select * from vw_relatorio_pedidos;
go