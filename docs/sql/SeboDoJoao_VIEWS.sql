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
	on		pe.cliente_id = uc.id		INNER JOIN		usuarios uf
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
