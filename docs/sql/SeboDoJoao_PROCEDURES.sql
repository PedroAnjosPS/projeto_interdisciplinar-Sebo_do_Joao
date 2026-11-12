-----------------------------------------------------------------------
-- Acessar o Banco de Dados
-----------------------------------------------------------------------
use Sebo_Do_Joao
go

-----------------------------------------------------------------------
-- PROGRAMAÇÃO DE BANCO DE DADOS - SEBO DO JOÃO
-- PROCEDURES - TRANSACTION
-----------------------------------------------------------------------

-----------------------------------------------------------------------
-- 1) Crie uma procedure que finalize a compra de um cliente. A compra
-- cadastra a entrega (com o endereço de destino e o frete), o pedido,
-- os itens do pedido e as parcelas do pagamento (com a bandeira e o
-- final do cartão). O total é calculado pelo banco, o estoque é baixado
-- pela trigger e o id do pedido criado volta para o Java no parâmetro
-- de saída @pedido_id
-----------------------------------------------------------------------
create procedure sp_finalizar_compra
(
	@cliente_id				int,
	@endereco_id			int,
	@itens_json				nvarchar(max),
	@forma_pagamento		varchar(50),
	@quantidade_parcelas	int				= 1,
	@transportadora			varchar(100)	= 'Correios (PAC)',
	@prazo_dias				int				= 7,
	@valor_frete			decimal(10,2)	= 0,
	@bandeira				varchar(30)		= null,
	@final_cartao			char(4)			= null,
	@pedido_id				int				output
)
as
begin		-- início da procedure
	-- variáveis usadas na procedure
	declare @max_parcelas		int
	declare @entrega_id			int
	declare @total				decimal(10,2)
	declare @status_pagamento	int

	-- a loja aceita parcelar no cartão em até 3 vezes (para mudar é só alterar aqui)
	set @max_parcelas = 3

	-- os THROW com número servem para o Java saber qual foi o erro
	-- verificar se o cliente existe
	if not exists (select 1 from clientes where usuario_id = @cliente_id)
		throw 50001, 'Cliente não encontrado!', 1;

	-- verificar se o endereço é deste cliente e está ativo
	if not exists (select 1 from enderecos where id = @endereco_id and cliente_id = @cliente_id and ativo = 1)
		throw 50009, 'Endereço de entrega inválido!', 1;

	-- verificar a forma de pagamento
	if @forma_pagamento not in ('PIX', 'CARTAO_CREDITO', 'BOLETO')
		throw 50005, 'Forma de pagamento inválida!', 1;

	-- só o cartão de crédito pode ser parcelado
	if @quantidade_parcelas < 1 or @quantidade_parcelas > @max_parcelas
	or (@forma_pagamento <> 'CARTAO_CREDITO' and @quantidade_parcelas <> 1)
		throw 50006, 'Quantidade de parcelas inválida!', 1;

	-- o frete não pode ser negativo
	if @valor_frete is null or @valor_frete < 0
		throw 50008, 'Valor de frete inválido!', 1;

	-- guardar os produtos do carrinho (que chegam em JSON) numa variável do tipo TABLE
	-- se o mesmo produto aparecer duas vezes, as quantidades são somadas
	declare @itens table
	(
		produto_id		int		not null	primary key,
		quantidade		int		not null
	)

	insert into @itens
	select	produtoId, sum(quantidade)
	from	openjson(@itens_json)
			with (produtoId int '$.produtoId', quantidade int '$.quantidade')
	group by produtoId

	-- o carrinho precisa ter pelo menos um produto
	if not exists (select 1 from @itens)
		throw 50002, 'O carrinho está vazio.', 1;

	-- os produtos precisam existir, estar ativos e ter estoque (LEFT JOIN para achar o que não existe)
	if exists	(
					select	1
					from	@itens i		LEFT JOIN		produtos p
					on		i.produto_id = p.id
					where	p.id is null or p.status <> 1 or i.quantidade <= 0
					or		p.estoque < i.quantidade
				)
		throw 50003, 'Algum produto está indisponível ou sem estoque suficiente.', 1;

	-- iniciar o try
	begin try
		-- iniciar a transaction
		begin tran
			-- cadastrar a entrega com status 1 (pendente)
			-- a data de postagem fica null, pois só é preenchida na procedure sp_avancar_entrega
			insert into entregas (transportadora, status, data_postagem, data_previsao_entrega, valor_frete, endereco_id)
			values				 (@transportadora, 1, null, dateadd(day, @prazo_dias, cast(getdate() as date)), @valor_frete, @endereco_id)

			-- recuperar o id da entrega gerado
			set @entrega_id = SCOPE_IDENTITY()

			-- cadastrar o pedido com status 1 (aguardando pagamento) e total zerado
			-- o funcionario_id fica null: o primeiro funcionário que mexer na entrega vira o responsável
			insert into pedidos (total, status, entrega_id, cliente_id)
			values				(0, 1, @entrega_id, @cliente_id)

			-- recuperar o id do pedido gerado e devolver para o Java
			set @pedido_id = SCOPE_IDENTITY()

			-- cadastrar os itens do pedido (o preço vem da tabela de produtos e não do navegador)
			-- a trigger tr_item_pedidos_baixa_estoque dá a baixa no estoque
			insert into item_pedidos (pedido_id, produto_id, quantidade, preco_unitario)
			select	@pedido_id, i.produto_id, i.quantidade, p.preco
			from	@itens i		INNER JOIN		produtos p
			on		i.produto_id = p.id

			-- calcular o total do pedido (itens + frete) usando a function fn_total_pedido
			set @total = dbo.fn_total_pedido(@pedido_id) + @valor_frete

			update pedidos set total = @total
			where id = @pedido_id

			-- cartão de crédito já nasce aprovado (2) e PIX e boleto ficam pendentes (1)
			-- isso é uma simulação para a apresentação do sistema
			if @forma_pagamento = 'CARTAO_CREDITO'
				set @status_pagamento = 2
			else
				set @status_pagamento = 1

			-- cadastrar as parcelas do pagamento usando a function fn_parcelas
			-- a data de pagamento e os dados do cartão só são gravados no cartão de crédito
			-- a trigger tr_pagamentos_confirma_pedido muda o pedido para processando
			insert into pagamentos (numero_parcela, status, valor, quantidade_parcelas, forma_pagamento,
									data_vencimento, data_pagamento, bandeira, final_cartao, pedido_id)
			select	numero_parcela, @status_pagamento, valor, @quantidade_parcelas, @forma_pagamento,
					data_vencimento,
					case when @status_pagamento = 2 then cast(getdate() as date) end,
					case when @forma_pagamento = 'CARTAO_CREDITO' then @bandeira end,
					case when @forma_pagamento = 'CARTAO_CREDITO' then @final_cartao end,
					@pedido_id
			from	dbo.fn_parcelas(@total, @quantidade_parcelas, cast(getdate() as date))

		-- deu tudo certo ===>>> confirmar dados
		commit
	end try		-- finalizando o try
	begin catch -- iniciando o catch
		-- deu erro ===>>> desfazer a operação
		rollback
		print 'ERRO! Compra não finalizada.';

		-- mandar o erro de volta para o Java
		throw;
	end catch  -- finalizando o catch
end  -- finalizando a procedure
go

-----------------------------------------------------------------------
-- 2) Crie uma procedure que avance a entrega de um pedido para a
-- próxima etapa: 1 - pendente, 2 - preparando, 3 - em trânsito e
-- 4 - entregue. A entrega só avança se o pedido estiver processando
-- (pagamento aprovado) e se ela ainda não foi finalizada. Ao entrar
-- em trânsito são gerados o código e o link de rastreio e a data de
-- postagem. Ao ser entregue é gravada a data de entrega e a trigger
-- tr_entregas_conclui_pedido conclui o pedido
-- O pedido nasce sem funcionário responsável. O primeiro funcionário
-- que avança a entrega passa a ser o responsável e, a partir daí, só
-- ele (ou um funcionário administrador) consegue avançar o pedido
-----------------------------------------------------------------------
create procedure sp_avancar_entrega (@pedido_id int, @funcionario_id int)
as
begin		-- início da procedure
	-- variáveis usadas na procedure
	declare @entrega_id			int
	declare @status_entrega		int
	declare @status_pedido		int
	declare @responsavel_id		int
	declare @admin				bit
	declare @novo_status		int

	-- recuperar o id da entrega, o status do pedido e da entrega e o responsável atual
	select	@entrega_id = e.id, @status_entrega = e.status, @status_pedido = pe.status,
			@responsavel_id = pe.funcionario_id
	from	pedidos pe		INNER JOIN		entregas e
	on		pe.entrega_id = e.id
	where	pe.id = @pedido_id

	-- verificar se o pedido e a entrega existem
	if @entrega_id is null
		throw 50010, 'Pedido ou entrega não encontrados.', 1;

	-- o funcionário precisa existir e estar ativo (aproveita para ver se é administrador)
	select	@admin = f.admin
	from	funcionarios f		INNER JOIN		usuarios u
	on		f.usuario_id = u.id
	where	f.usuario_id = @funcionario_id and u.status = 1

	if @admin is null
		throw 50013, 'Funcionário não encontrado ou inativo.', 1;

	-- o pedido precisa estar processando (2)
	if @status_pedido <> 2
		throw 50011, 'A entrega só avança depois que o pagamento for aprovado.', 1;

	-- entrega entregue (4) ou cancelada (5) não avança mais
	if @status_entrega >= 4
		throw 50012, 'A entrega já foi finalizada ou cancelada.', 1;

	-- se o pedido já tem responsável, só ele ou um administrador avança
	if @responsavel_id is not null and @responsavel_id <> @funcionario_id and @admin = 0
		throw 50014, 'Este pedido está sob responsabilidade de outro funcionário.', 1;

	-- avançar uma etapa
	set @novo_status = @status_entrega + 1

	-- iniciar o try
	begin try
		-- iniciar a transaction
		begin tran
			-- quem avança primeiro vira o responsável (o "and funcionario_id is null"
			-- evita que dois funcionários peguem o mesmo pedido ao mesmo tempo)
			update pedidos set funcionario_id = @funcionario_id
			where id = @pedido_id and funcionario_id is null

			-- se ninguém foi atualizado, outro funcionário assumiu o pedido um instante antes
			if @responsavel_id is null and @@ROWCOUNT = 0
				throw 50015, 'Outro funcionário acabou de assumir este pedido.', 1;

			update entregas set status = @novo_status,
				-- em trânsito (3): gerar o código e o link de rastreio e a data de postagem
				codigo_rastreio =	case when @novo_status = 3
									then 'SJ' + right('000000000' + cast(@entrega_id as varchar(9)), 9) + 'BR'
									else codigo_rastreio end,
				link_rastreio	=	case when @novo_status = 3
									then 'https://rastreio.sebodojoao.demo/' + cast(@entrega_id as varchar(9))
									else link_rastreio end,
				data_postagem	=	case when @novo_status = 3 then getdate() else data_postagem end,
				-- entregue (4): gravar a data da entrega
				data_entrega	=	case when @novo_status = 4 then getdate() else data_entrega end
			where id = @entrega_id

		-- deu tudo certo ===>>> confirmar dados
		commit
	end try		-- finalizando o try
	begin catch -- iniciando o catch
		-- deu erro ===>>> desfazer a operação
		rollback
		print 'ERRO! Entrega não avançada.';

		-- mandar o erro de volta para o Java
		throw;
	end catch  -- finalizando o catch
end  -- finalizando a procedure
go

-----------------------------------------------------------------------
-- 3) Crie uma procedure que permita o cliente cancelar o seu pedido
-- enquanto a entrega ainda não estiver em trânsito. Os pagamentos
-- aprovados são estornados (4), os pendentes ficam recusados (3), a
-- entrega e o pedido ficam cancelados. A trigger
-- tr_pedidos_devolver_estoque devolve os produtos ao estoque
-----------------------------------------------------------------------
create procedure sp_cancelar_pedido (@pedido_id int, @cliente_id int)
as
begin		-- início da procedure
	-- variáveis usadas na procedure
	declare @status_pedido		int
	declare @entrega_id			int
	declare @status_entrega		int

	-- recuperar os dados do pedido, mas só se ele for do cliente informado
	select	@status_pedido = pe.status, @entrega_id = pe.entrega_id, @status_entrega = e.status
	from	pedidos pe		INNER JOIN		entregas e
	on		pe.entrega_id = e.id
	where	pe.id = @pedido_id and pe.cliente_id = @cliente_id

	-- verificar se o pedido existe e é deste cliente
	if @status_pedido is null
		throw 50020, 'Pedido não encontrado para este cliente.', 1;

	-- só cancela pedido aguardando pagamento (1) ou processando (2) e entrega que ainda não saiu
	if @status_pedido not in (1, 2) or @status_entrega >= 3
		throw 50021, 'Este pedido não pode mais ser cancelado.', 1;

	-- iniciar o try
	begin try
		-- iniciar a transaction
		begin tran
			-- pagamento aprovado (2) vira compra estornada (4), pois o dinheiro volta para o cliente
			update pagamentos set status = 4
			where pedido_id = @pedido_id and status = 2

			-- pagamento pendente (1) vira recusado (3), pois nunca foi pago
			update pagamentos set status = 3
			where pedido_id = @pedido_id and status = 1

			-- cancelar a entrega (5)
			update entregas set status = 5
			where id = @entrega_id

			-- cancelar o pedido (4)
			update pedidos set status = 4
			where id = @pedido_id

		-- deu tudo certo ===>>> confirmar dados
		commit
	end try		-- finalizando o try
	begin catch -- iniciando o catch
		-- deu erro ===>>> desfazer a operação
		rollback
		print 'ERRO! Pedido não cancelado.';

		-- mandar o erro de volta para o Java
		throw;
	end catch  -- finalizando o catch
end  -- finalizando a procedure
go

-----------------------------------------------------------------------
-- 4) Crie uma procedure que cadastre um cliente, mas lembre-se que todo
-- cliente é um usuário. Os dados de acesso (nome, e-mail, senha...)
-- vão para a tabela usuarios e os dados próprios do cliente (data de
-- nascimento e CPF) vão para a tabela clientes. O id do usuário criado
-- volta para o Java no parâmetro de saída @usuario_id
-----------------------------------------------------------------------
create procedure sp_cadastrar_cliente
(
	@nome				varchar(100),
	@email				varchar(100),
	@senha				varchar(255),
	@telefone			varchar(20),
	@cpf				varchar(14),
	@data_nascimento	date,
	@foto_perfil		varchar(255)	= null,
	@usuario_id			int				output
)
as
begin		-- início da procedure
	-- o e-mail é único entre todos os usuários (clientes e funcionários)
	if exists (select 1 from usuarios where email = @email)
		throw 50030, 'Já existe uma conta com esse e-mail.', 1;

	-- o CPF é comparado só pelos números, pois pode vir com pontos e traço
	if exists	(
					select	1
					from	clientes
					where	replace(replace(cpf, '.', ''), '-', '') = replace(replace(@cpf, '.', ''), '-', '')
				)
		throw 50031, 'Já existe um cliente com esse CPF.', 1;

	-- iniciar o try
	begin try
		-- iniciar a transaction
		begin tran
			-- cadastrar os dados de acesso na tabela usuarios (o status e a data de cadastro têm valor padrão)
			insert into usuarios (nome, email, senha, telefone, foto_perfil)
			values				 (@nome, @email, @senha, @telefone, @foto_perfil)

			-- recuperar o id do usuário gerado e devolver para o Java
			set @usuario_id = SCOPE_IDENTITY()

			-- cadastrar os dados do cliente na tabela clientes (o id é o mesmo do usuário)
			insert into clientes (usuario_id, data_nascimento, cpf)
			values				 (@usuario_id, @data_nascimento, @cpf)

		-- deu tudo certo ===>>> confirmar dados
		commit
	end try		-- finalizando o try
	begin catch -- iniciando o catch
		-- deu erro ===>>> desfazer a operação
		rollback
		print 'ERRO! Dados do Cliente não cadastrados.';

		-- mandar o erro de volta para o Java
		throw;
	end catch  -- finalizando o catch
end  -- finalizando a procedure
go

-----------------------------------------------------------------------
-- 5) Crie uma procedure que cadastre um funcionário, mas lembre-se que
-- todo funcionário é um usuário. Os dados de acesso vão para a tabela
-- usuarios e os dados próprios do funcionário (código e se é
-- administrador) vão para a tabela funcionarios. O id do usuário criado
-- volta para o Java no parâmetro de saída @usuario_id
-----------------------------------------------------------------------
create procedure sp_cadastrar_funcionario
(
	@nome				varchar(100),
	@email				varchar(100),
	@senha				varchar(255),
	@telefone			varchar(20),
	@codigo_funcionario	varchar(50),
	@admin				bit				= 0,
	@status				int				= 1,
	@foto_perfil		varchar(255)	= null,
	@usuario_id			int				output
)
as
begin		-- início da procedure
	-- o e-mail é único entre todos os usuários (clientes e funcionários)
	if exists (select 1 from usuarios where email = @email)
		throw 50030, 'Já existe uma conta com esse e-mail.', 1;

	-- o código do funcionário também é único
	if exists (select 1 from funcionarios where codigo_funcionario = @codigo_funcionario)
		throw 50032, 'Já existe um funcionário com esse código.', 1;

	-- iniciar o try
	begin try
		-- iniciar a transaction
		begin tran
			-- cadastrar os dados de acesso na tabela usuarios
			insert into usuarios (nome, email, senha, telefone, status, foto_perfil)
			values				 (@nome, @email, @senha, @telefone, @status, @foto_perfil)

			-- recuperar o id do usuário gerado e devolver para o Java
			set @usuario_id = SCOPE_IDENTITY()

			-- cadastrar os dados do funcionário na tabela funcionarios (o id é o mesmo do usuário)
			insert into funcionarios (usuario_id, codigo_funcionario, admin)
			values					 (@usuario_id, @codigo_funcionario, @admin)

		-- deu tudo certo ===>>> confirmar dados
		commit
	end try		-- finalizando o try
	begin catch -- iniciando o catch
		-- deu erro ===>>> desfazer a operação
		rollback
		print 'ERRO! Dados do Funcionário não cadastrados.';

		-- mandar o erro de volta para o Java
		throw;
	end catch  -- finalizando o catch
end  -- finalizando a procedure
go
