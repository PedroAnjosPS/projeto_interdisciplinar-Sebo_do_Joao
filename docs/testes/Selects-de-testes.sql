------------------------------------------------------------------------------------------------------------------
-- =========================	CONSULTAS SIMPLES	========================= --
------------------------------------------------------------------------------------------------------------------
select * from pedidos;

select * from entregas;

select * from enderecos;

select * from cartoes;

select * from pagamentos;

select * from item_pedidos;

select * from item_carrinhos;
------------------------------------------------------------------------------------------------------------------
-- =========================	CONSULTAS MAIS COMPLEXAS ========================= --
------------------------------------------------------------------------------------------------------------------
-- Consulta de PRODUTOS --
select prod.id [Id do Produto], prod.nome Produto, prod.estoque Estoque, prod.preco [Preço do Produto],
		prod.status,
		img_prod.caminho [URL da imagem do produto]
from produtos prod
	LEFT JOIN imagem_produtos img_prod		ON prod.id = img_prod.produto_id;
go

-- Consulta de CLIENTES --
select usu.id [Id do Cliente], usu.nome AS Cliente, usu.email AS [Email do Cliente], usu.senha AS [Senha do Cliente], 
		CASE usu.status
			WHEN 1 THEN 'ATIVO'
			WHEN 2 THEN	'INATIVO'
		END AS [Status do Cliente],
		cli.cpf AS CPF, usu.telefone AS [Telefone do Cliente], usu.foto_perfil [Url foto de perfil],
		cli.data_nascimento AS [Data de Nascimento]
from usuarios usu
		RIGHT JOIN clientes cli ON	usu.id = cli.usuario_id;
go

update usuarios set status = 1
where status = 2;
go

-- Consulta de FUNCIONÁRIOS --
select usu.id AS [Id do Funcionário], usu.nome AS Funcionario, usu.email AS [Email do Funcionario], usu.senha AS [Senha do Funcionario], 
		CASE usu.status
			WHEN 1 THEN 'ATIVO'
			WHEN 2 THEN	'INATIVO'
		END AS [Status do Funcionario],
		func.codigo_funcionario AS [Codigo do Funcionario], 
		CASE func.admin
			WHEN 0 THEN 'NÃO'
			WHEN 1 THEN 'SIM'
		END AS [Administrador?]
from usuarios usu
		RIGHT JOIN funcionarios func ON	usu.id = func.usuario_id;
go

-- Consulta de PEDIDOS --
select pe.id AS [Id do Pedido], pe.data AS [Data do Pedido], pe.total AS Total, 
	   CASE pe.status
			WHEN 1 THEN 'AGUARDANDO PAGAMENTO'
			WHEN 2 THEN 'PROCESSANDO'
			WHEN 3 THEN 'CONCLUIDO'
			WHEN 4 THEN 'CANCELADO'
	   END AS [Status do Pedido],
	   uc.nome AS Cliente, uf.nome AS [Funcionário], prod.nome AS [Produto], item_pe.quantidade AS [Quantidade Produto],
	   item_pe.preco_unitario [Preço unitário], ent.valor_frete AS [Frete de Entrega],
	   CASE	ent.status
			WHEN 1 THEN 'PENDENTE'
			WHEN 2 THEN 'PREPARANDO'
			WHEN 3 THEN 'EM TRÂNSITO'
			WHEN 4 THEN 'ENTREGUE'
			WHEN 5 THEN 'CANCELADO'
		END AS [Status da Entrega]
from pedidos pe		
	LEFT JOIN		clientes cli			ON		 pe.cliente_id			=	cli.usuario_id	
	RIGHT JOIN		usuarios uc				ON		 uc.id					=	cli.usuario_id	
	RIGHT JOIN		funcionarios func		ON		 pe.funcionario_id		=	func.usuario_id
	RIGHT JOIN		usuarios uf				ON		 uf.id					=	func.usuario_id
	INNER JOIN		item_pedidos item_pe	ON		 pe.id					=	item_pe.pedido_id
	LEFT  JOIN		produtos prod			ON		 item_pe.produto_id		=	prod.id
	LEFT JOIN		entregas ent			ON		 pe.entrega_id			=	ent.id
go