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

-----------------------------------------------------------------------------------------------------------
-- Consulta de PEDIDOS --
-----------------------------------------------------------------------------------------------------------
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
order by
	pe.id,
	prod.nome;
go