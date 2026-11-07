-- Consulta de Produtos --
select prod.id [Id do Produto], prod.nome Produto, prod.estoque Estoque, prod.preco [Preço do Produto],
		prod.status,
		img_prod.caminho [URL da imagem do produto]
from produtos prod
	LEFT JOIN imagem_produtos img_prod		ON prod.id = img_prod.produto_id;
GO

-- Consulta completa de um pedido (cliente, funcionário, itens, entrega, endereço e pagamento) --
SELECT p.id AS pedido, p.data, p.total,
       uc.nome AS cliente, uf_.nome AS funcionario,
       pr.nome AS produto, ip.quantidade, ip.preco_unitario,
       e.transportadora, e.codigo_rastreio, e.status AS status_entrega,
       en.logradouro, en.numero, en.bairro,
       pg.forma_pagamento, pg.numero_parcela, pg.status AS status_pagamento
FROM pedidos p
INNER JOIN usuarios uc     ON uc.id  = p.cliente_id
INNER JOIN usuarios uf_    ON uf_.id = p.funcionario_id
INNER JOIN item_pedidos ip ON ip.pedido_id = p.id
INNER JOIN produtos pr     ON pr.id = ip.produto_id
INNER JOIN entregas e      ON e.id = p.entrega_id
INNER JOIN enderecos en    ON en.id = e.endereco_id
LEFT JOIN pagamentos pg    ON pg.pedido_id = p.id
WHERE p.id = 1          -- troque pelo número do pedido que quiser mostrar
ORDER BY pr.nome, pg.numero_parcela;
GO

-- Consulta dos status do pedido, da entrega e do pagamento com o cliente e o funcionário --
SELECT p.id AS pedido,
       uc.nome AS cliente,
       uf.nome AS funcionario,
       CASE p.status WHEN 1 THEN 'Aguardando pagamento' WHEN 2 THEN 'Processando'
                     WHEN 3 THEN 'Concluído' WHEN 4 THEN 'Cancelado' END AS status_pedido,
       CASE e.status WHEN 1 THEN 'Pendente' WHEN 2 THEN 'Preparando' WHEN 3 THEN 'Em trânsito'
                     WHEN 4 THEN 'Entregue' WHEN 5 THEN 'Cancelado' END AS status_entrega,
       CASE pg.status WHEN 1 THEN 'Pendente' WHEN 2 THEN 'Aprovado' WHEN 3 THEN 'Recusado'
                      WHEN 4 THEN 'Compra estornada' END AS status_pagamento
FROM pedidos p
INNER JOIN usuarios uc  ON uc.id = p.cliente_id
INNER JOIN usuarios uf  ON uf.id = p.funcionario_id
INNER JOIN entregas e   ON e.id  = p.entrega_id
LEFT JOIN pagamentos pg ON pg.pedido_id = p.id AND pg.numero_parcela = 1
ORDER BY p.id;


select * from entregas;

-- Consulta  --

select * from enderecos;

select * from cartoes;

select * from pagamentos;

select * from item_pedidos;

select * from item_carrinhos;

-- Consulta de clientes --
select usu.id [Id do Cliente], usu.nome AS Cliente, usu.email AS [Email do Cliente], usu.senha AS [Senha do Cliente], 
		CASE usu.status
			WHEN 1 THEN 'ATIVO'
			WHEN 2 THEN	'INATIVO'
		END AS [Status do Cliente],
		cli.cpf AS CPF, usu.telefone AS [Telefone do Cliente], usu.foto_perfil [Url foto de perfil],
		cli.data_nascimento AS [Data de Nascimento]
from usuarios usu
		RIGHT JOIN clientes cli ON	usu.id = cli.usuario_id;
GO

-- Consulta de funcionários --
select usu.nome AS Funcionario, usu.email AS [Email do Funcionario], usu.senha AS [Senha do Funcionario], 
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
GO


-- Consulta dos pagamentos de cada pedido com os produtos, o frete e o total (para conferir a compra) --
SELECT p.id AS pedido,
       u.nome AS cliente,
       it.produtos,
       it.subtotal_produtos,
       e.valor_frete AS frete,
       p.total AS total_pedido,
       pg.numero_parcela,
       pg.quantidade_parcelas,
       pg.forma_pagamento,
       pg.valor AS valor_parcela,
       CASE pg.status WHEN 1 THEN 'Pendente' WHEN 2 THEN 'Aprovado' WHEN 3 THEN 'Recusado'
                      WHEN 4 THEN 'Compra estornada' END AS status_pagamento,
       pg.data_vencimento,
       pg.data_pagamento,
       pg.bandeira,
       pg.final_cartao
FROM pedidos p
INNER JOIN usuarios u   ON u.id = p.cliente_id
INNER JOIN entregas e   ON e.id = p.entrega_id
LEFT JOIN pagamentos pg ON pg.pedido_id = p.id
CROSS APPLY (
    SELECT STRING_AGG(CONCAT(pr.nome, ' (', ip.quantidade, 'x R$ ', ip.preco_unitario, ')'), '; ') AS produtos,
           SUM(ip.quantidade * ip.preco_unitario) AS subtotal_produtos
    FROM item_pedidos ip
    INNER JOIN produtos pr ON pr.id = ip.produto_id
    WHERE ip.pedido_id = p.id
) it
ORDER BY p.id, pg.numero_parcela;
