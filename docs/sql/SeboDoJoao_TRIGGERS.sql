-----------------------------------------------------------------------
-- Acessar o Banco de Dados
-----------------------------------------------------------------------
use Sebo_do_Joao
go

-----------------------------------------------------------------------
-- PROGRAMAÇÃO DE BANCO DE DADOS - SEBO DO JOÃO
-- TRIGGERS
-- As tabelas inserted e deleted são criadas pelo SQL Server dentro da
-- trigger: inserted tem as linhas novas e deleted tem as linhas antigas
-----------------------------------------------------------------------

-----------------------------------------------------------------------
-- 1) Crie uma trigger que dê baixa no estoque dos produtos quando os
-- itens de um pedido são cadastrados. Se algum produto não tiver
-- estoque suficiente, a operação é cancelada com uma mensagem de erro
-----------------------------------------------------------------------
create trigger tr_item_pedidos_baixa_estoque
on item_pedidos
after insert
as
begin
	-- o set nocount on evita mensagens de linhas afetadas para quem chamou a trigger
	set nocount on

	-- verificar se algum produto tem estoque menor que a quantidade pedida
	if exists	(
					select	1
					from	produtos p		INNER JOIN		inserted i
					on		p.id = i.produto_id
					where	p.estoque < i.quantidade
				)
		-- o número do erro é usado pelo Java (50007 = estoque insuficiente)
		throw 50007, 'Estoque é insuficiente para realizar o pedido!', 1;

	-- dar baixa no estoque (se o mesmo produto vier em mais de uma linha, as quantidades são somadas)
	update produtos set estoque = estoque -	(
												select	sum(quantidade)
												from	inserted
												where	produto_id = produtos.id
											)
	where id in (select produto_id from inserted)
end
go

-----------------------------------------------------------------------
-- 2) Crie uma trigger que devolva ao estoque os produtos de um pedido
-- quando ele for cancelado (status passa a ser 4)
-----------------------------------------------------------------------
create trigger tr_pedidos_devolver_estoque
on pedidos
after update
as
begin
	set nocount on

	-- guardar os produtos e as quantidades dos pedidos que acabaram de ser cancelados
	-- (antes da alteração o status não era 4)
	declare @devolver table
	(
		produto_id		int,
		quantidade		int
	)

	insert into @devolver
	select	ip.produto_id, sum(ip.quantidade)
	from	item_pedidos ip		INNER JOIN		inserted i
	on		ip.pedido_id = i.id	INNER JOIN		deleted d
	on		i.id = d.id
	where	i.status = 4 and d.status <> 4
	group by ip.produto_id

	-- devolver as quantidades ao estoque
	update produtos set estoque = estoque + dv.quantidade
	from	produtos p		INNER JOIN		@devolver dv
	on		p.id = dv.produto_id
end
go

-----------------------------------------------------------------------
-- 3) Crie uma trigger que mude o pedido para processando (2) quando
-- todos os pagamentos dele estiverem aprovados (2)
-----------------------------------------------------------------------
create trigger tr_pagamentos_confirma_pedido
on pagamentos
after insert, update
as
begin
	set nocount on

	-- só os pedidos que ainda aguardam pagamento (1) e que não têm nenhum pagamento sem aprovar
	update pedidos set status = 2
	where status = 1
	and id in (select pedido_id from inserted)
	and not exists	(
						select	1
						from	pagamentos pg
						where	pg.pedido_id = pedidos.id and pg.status <> 2
					)
end
go

-----------------------------------------------------------------------
-- 4) Crie uma trigger que conclua o pedido (status 3) quando a entrega
-- dele passar para entregue (4)
-----------------------------------------------------------------------
create trigger tr_entregas_conclui_pedido
on entregas
after update
as
begin
	set nocount on

	-- só quando a coluna status foi alterada
	if not update(status)
		return

	-- o pedido precisa estar processando (2) e a entrega acabou de virar entregue (4)
	update pe set pe.status = 3
	from	pedidos pe		INNER JOIN		inserted i
	on		pe.entrega_id = i.id	INNER JOIN		deleted d
	on		i.id = d.id
	where	i.status = 4 and d.status <> 4 and pe.status = 2
end
go
