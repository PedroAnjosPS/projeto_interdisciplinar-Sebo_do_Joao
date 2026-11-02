-- ==== SELECTs SEBO DO JOAO ==== --

-- SELECTs DE CONFIRMAÇÃO -- 
-- Tabela UFs -- 
SELECT * FROM ufs uf ORDER BY uf.nome;

-- Tabela Cidades --
SELECT * FROM cidades cid ORDER BY cid.nome;

-- Tabela Ceps --
SELECT * FROM ceps cep ORDER BY cep.numero_cep;

-- Tabela Usuarios --
SELECT * FROM usuarios usu ORDER BY usu.nome;
SELECT * FROM usuarios usu;

-- Tabela Clientes --
SELECT * FROM clientes cli ORDER BY cli.data_nascimento;

-- Tabela Funcionarios --
SELECT * FROM funcionarios func ORDER BY func.codigo_funcionario;

-- Tabela Categorias --
SELECT * FROM categorias cat ORDER BY cat.nome;
SELECT * FROM categorias cat;

-- Tabela Generos --
SELECT * FROM generos gen ORDER BY gen.nome;
SELECT * FROM generos gen;

-- Tabela Produtos --
SELECT * FROM produtos prod ORDER BY prod.nome;

-- Tabela Imagem_Produtos --
SELECT * FROM imagem_produtos imag ORDER BY imag.produto_id;

-- Tabela Entregas --
SELECT * FROM entregas ent ORDER BY ent.transportadora;
-- Select mais elaborado para consulta das informações de entrega --
SELECT ent.id AS 'Id da Entrega', ent.transportadora AS Transportadora, 
        CASE ent.status
            WHEN 1 THEN 'AGUARDANDO PAGAMENTO'
            WHEN 2 THEN 'PROCESSANDO'
            WHEN 3 THEN 'CONCLUIDO'
            WHEN 4 THEN 'CANCELADO'
        END AS 'Status da Entrega',
        ent.link_rastreio AS 'Link de Rastreio', ent.codigo_rastreio AS 'Código de Rastreio',
        ent.data_postagem AS 'Data de Postagem', ent.data_previsao_entrega AS 'Data de Previsão de Entrega',
        ent.data_entrega AS 'Data Real de Entrega', pe.id AS 'Id do Pedido',
        CASE pe.status
            WHEN 1 THEN 'PENDENTE'
            WHEN 2 THEN 'PREPARANDO'
            WHEN 3 THEN 'EM TRÂNSITO'
            WHEN 4 THEN 'ENTREGUE'
            WHEN 5 THEN 'CANCELADO'
        END AS 'Status do Pedido' 
FROM entregas ent 
    LEFT JOIN pedidos pe    ON pe.entrega_id = ent.id;
GO

-- Tabela Pedidos --
SELECT * FROM pedidos pe ORDER BY pe.total DESC;
SELECT * FROM pedidos pe;

SELECT pe.id AS 'Id do Pedido', pe.data AS 'Data do pedido', 
        CONCAT('R$ ', FORMAT(pe.total, 'N2', 'pt-BR')) AS 'Total do Pedido',
        CASE ent.status
            WHEN 1 THEN 'AGUARDANDO PAGAMENTO'
            WHEN 2 THEN 'PROCESSANDO'
            WHEN 3 THEN 'CONCLUIDO'
            WHEN 4 THEN 'CANCELADO'
            ELSE 'DESCONHECIDO'
        END AS [Status do Pedido], 
        pe.entrega_id AS 'Id da Entrega',
        CASE pe.status
            WHEN 1 THEN 'PENDENTE'
            WHEN 2 THEN 'PREPARANDO'
            WHEN 3 THEN 'EM TRÂNSITO'
            WHEN 4 THEN 'ENTREGUE'
            WHEN 5 THEN 'CANCELADO'
            ELSE 'DESCONHECIDO'
        END AS [Status do Pedido],
        pe.cliente_id AS 'Id do Cliente', uc.nome AS Cliente, pe.funcionario_id AS 'Id do Funcionário',
        uf.nome AS 'Funcionário'
FROM pedidos pe
    LEFT JOIN entregas ent    ON pe.entrega_id = ent.id 
    LEFT JOIN clientes cli    ON pe.cliente_id = cli.usuario_id
    LEFT JOIN usuarios uc     ON cli.usuario_id = uc.id 
    LEFT JOIN funcionarios f  ON pe.funcionario_id = f.usuario_id
    LEFT JOIN usuarios uf     ON f.usuario_id = uf.id;
GO


-- Tabela Item_Pedidos --
SELECT * FROM item_pedidos;

SELECT pe.id ID_Pedido, prod.nome Produto, prod.id ID_Produto,
       prod.preco Preco_Produto, pe.total Total_Pedido  
FROM produtos prod, pedidos pe, item_pedidos item 
WHERE 
    item.pedido_id = pe.id AND
    item.produto_id = prod.id 
ORDER BY pe.id;

-- Tabela Pagamentos --
SELECT * FROM pagamentos pag ORDER BY pag.pedido_id;


-- As informações dos clientes cadastrados --
SELECT 
    uc.id AS Id_Cliente,
    uc.nome AS Cliente,
    uc.email AS Email,
    CONCAT(
        uc.logradouro, ', ',
        uc.bairro, ' - ',
        cid.nome, ', ',
        uf.sigla, ' - ',
        uc.cep_numero
    ) AS Endereco,
    cli.data_nascimento AS Data_Nascimento,
    cli.cpf AS CPF
FROM usuarios uc
INNER JOIN clientes cli
    ON uc.id = cli.usuario_id
LEFT JOIN ceps cep
    ON uc.cep_numero = cep.numero_cep
LEFT JOIN cidades cid
    ON cep.cidade_id = cid.id
LEFT JOIN ufs uf
    ON cid.uf_sigla = uf.sigla;
GO

-- Consultando funcionários --
SELECT * 
FROM usuarios, funcionarios
WHERE usuarios.id = funcionarios.usuario_id AND funcionarios.usuario_id = 9;
GO

select * from usuarios;
GO