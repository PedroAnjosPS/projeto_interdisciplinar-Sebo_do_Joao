-----------------------------------------------------------------------
-- Acessar o Banco de Dados
-----------------------------------------------------------------------
use Sebo_Do_Joao
go

-----------------------------------------------------------------------
-- PROGRAMAÇÃO DE BANCO DE DADOS - SEBO DO JOÃO
-- FUNCTIONS
-----------------------------------------------------------------------

-----------------------------------------------------------------------
-- 1) Crie uma function que calcule o total de um pedido, somando a
-- quantidade vezes o preço de cada item do pedido
-- Saída de dados: o valor total (zero se o pedido não tiver itens)
-----------------------------------------------------------------------
create function fn_total_pedido (@pedido_id int)
returns decimal(10,2)
as
begin
	declare @retorno decimal(10,2)

	set @retorno =	(
						select	sum(quantidade * preco_unitario)
						from	item_pedidos
						where	pedido_id = @pedido_id
					)

	return isnull(@retorno, 0)
end
go

-- testar a function
select dbo.fn_total_pedido(1) Total_Pedido
go

-----------------------------------------------------------------------
-- 2) Crie uma function que traga os itens de um pedido
-- Saída de dados: id e nome do produto, categoria, quantidade, preço
-- unitário e subtotal de cada item
-----------------------------------------------------------------------
create function fn_itens_do_pedido (@pedido_id int)
returns table
as
	return	(
				select	p.id produto_id, p.nome produto, c.nome categoria,
						i.quantidade, i.preco_unitario, (i.quantidade * i.preco_unitario) subtotal
				from	item_pedidos i		INNER JOIN		produtos p
				on		i.produto_id = p.id	INNER JOIN		categorias c
				on		p.categoria_id = c.id
				where	i.pedido_id = @pedido_id
			)
go

-- testar a function
select * from dbo.fn_itens_do_pedido(1)
go

-----------------------------------------------------------------------
-- 3) Crie uma function que gere as parcelas de uma compra
-- Saída de dados: número da parcela, valor e data de vencimento
-- Se o total não dividir certinho, os centavos que sobram ficam na
-- última parcela. Cada parcela vence um mês depois da anterior
-----------------------------------------------------------------------
create function fn_parcelas (@total decimal(10,2), @qtd int, @primeiro_vencimento date)
returns @parcelas table
(
	numero_parcela		int,
	valor				decimal(10,2),
	data_vencimento		date
)
as
begin
	-- variáveis usadas na function
	declare @valor_parcela	decimal(10,2)
	declare @n				int

	-- se os dados forem inválidos a function não retorna nenhuma parcela
	if @total <= 0 or @qtd <= 0 or @primeiro_vencimento is null
		return

	-- valor de cada parcela, cortando os centavos que sobram (ex.: 100,00 / 3 = 33,33)
	set @valor_parcela = floor(@total * 100 / @qtd) / 100.00
	set @n = 1

	-- inserir os dados na variável do tipo TABLE
	while @n <= @qtd
	begin
		if @n < @qtd
			insert into @parcelas values (@n, @valor_parcela, dateadd(month, @n - 1, @primeiro_vencimento))
		else
			-- a última parcela leva o que sobrou
			insert into @parcelas values (@n, @total - (@valor_parcela * (@qtd - 1)), dateadd(month, @n - 1, @primeiro_vencimento))

		set @n = @n + 1
	end
	return
end
go

-- testar a function
select * from dbo.fn_parcelas(100.00, 3, getdate())
go
