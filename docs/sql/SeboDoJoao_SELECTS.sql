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
            WHEN 1 THEN 'PENDENTE'
            WHEN 2 THEN 'PREPARANDO'
            WHEN 3 THEN 'EM TRÂNSITO'
            WHEN 4 THEN 'ENTREGUE'
            WHEN 5 THEN 'CANCELADO'
        END AS 'Status da Entrega',
        ent.link_rastreio AS 'Link de Rastreio', ent.codigo_rastreio AS 'Código de Rastreio',
        ent.data_postagem AS 'Data de Postagem', ent.data_previsao_entrega AS 'Data de Previsão de Entrega',
        ent.data_entrega AS 'Data Real de Entrega', pe.id AS 'Id do Pedido',
        CASE pe.status
            WHEN 1 THEN 'AGUARDANDO PAGAMENTO'
            WHEN 2 THEN 'PROCESSANDO'
            WHEN 3 THEN 'CONCLUIDO'
            WHEN 4 THEN 'CANCELADO'
        END AS 'Status do Pedido'
FROM    entregas ent        INNER JOIN      pedidos pe
ON      pe.entrega_id = ent.id;
GO

-- Tabela Pedidos --
SELECT * FROM pedidos pe ORDER BY pe.total DESC;
SELECT * FROM pedidos pe;

SELECT pe.id AS 'Id do Pedido', pe.data AS 'Data do pedido', 
        CONCAT('R$ ', FORMAT(pe.total, 'N2', 'pt-BR')) AS 'Total do Pedido',
        CASE pe.status
            WHEN 1 THEN 'AGUARDANDO PAGAMENTO'
            WHEN 2 THEN 'PROCESSANDO'
            WHEN 3 THEN 'CONCLUIDO'
            WHEN 4 THEN 'CANCELADO'
            ELSE 'DESCONHECIDO'
        END AS [Status do Pedido],
        pe.entrega_id AS 'Id da Entrega',
        CASE ent.status
            WHEN 1 THEN 'PENDENTE'
            WHEN 2 THEN 'PREPARANDO'
            WHEN 3 THEN 'EM TRÂNSITO'
            WHEN 4 THEN 'ENTREGUE'
            WHEN 5 THEN 'CANCELADO'
            ELSE 'DESCONHECIDO'
        END AS [Status da Entrega],
        pe.cliente_id AS 'Id do Cliente', uc.nome AS Cliente, pe.funcionario_id AS 'Id do Funcionário',
        uf.nome AS 'Funcionário'
FROM    pedidos pe                      INNER JOIN      entregas ent
ON      pe.entrega_id = ent.id          INNER JOIN      usuarios uc
ON      pe.cliente_id = uc.id           INNER JOIN      usuarios uf
ON      pe.funcionario_id = uf.id;
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
-- O endereço fica na tabela enderecos (e não em usuarios). Por isso, --
-- esta consulta traz o endereço PADRÃO de cada cliente (enderecos.padrao = 1 e ativo = 1). --
-- LEFT JOIN: o cliente aparece mesmo que ainda não tenha endereço cadastrado. --
SELECT
    uc.id AS Id_Cliente,
    uc.nome AS Cliente,
    uc.email AS Email,
    CONCAT(
        en.logradouro, ', ', ISNULL(en.numero, 's/n'), ' - ',
        en.bairro, ' - ',
        cid.nome, ', ',
        uf.sigla, ' - ',
        en.cep_numero
    ) AS Endereco,
    cli.data_nascimento AS Data_Nascimento,
    cli.cpf AS CPF
FROM usuarios uc
INNER JOIN clientes cli
    ON uc.id = cli.usuario_id
LEFT JOIN enderecos en
    ON en.cliente_id = cli.usuario_id AND en.padrao = 1 AND en.ativo = 1
LEFT JOIN ceps cep
    ON en.cep_numero = cep.numero_cep
LEFT JOIN cidades cid
    ON cep.cidade_id = cid.id
LEFT JOIN ufs uf
    ON cid.uf_sigla = uf.sigla;
GO

-- Tabela Enderecos --
-- Todos os endereços ativos, com cliente, CEP, cidade e UF. --
SELECT en.id AS Id_Endereco, uc.nome AS Cliente, en.apelido AS Apelido,
       CASE en.padrao WHEN 1 THEN 'SIM' ELSE 'NAO' END AS Padrao,
       uc.telefone AS Telefone,
       en.logradouro AS Logradouro, ISNULL(en.numero, 's/n') AS Numero, en.complemento AS Complemento,
       en.bairro AS Bairro, cid.nome AS Cidade, cid.uf_sigla AS UF, en.cep_numero AS CEP
FROM enderecos en
INNER JOIN usuarios uc    ON uc.id = en.cliente_id
INNER JOIN ceps cep       ON cep.numero_cep = en.cep_numero
INNER JOIN cidades cid    ON cid.id = cep.cidade_id
WHERE en.ativo = 1
ORDER BY uc.nome, en.padrao DESC, en.id;
GO

-- Tabela Cartoes --
-- Só bandeira, final do cartão, titular e validade. O número completo e o CVV nunca são guardados. --
SELECT ca.id AS Id_Cartao, uc.nome AS Cliente, ca.bandeira AS Bandeira, ca.final_numero AS Final,
       ca.titular AS Titular, ca.validade AS Validade,
       CASE ca.padrao WHEN 1 THEN 'SIM' ELSE 'NAO' END AS Padrao
FROM cartoes ca
INNER JOIN usuarios uc    ON uc.id = ca.cliente_id
ORDER BY uc.nome, ca.padrao DESC, ca.id;
GO

-- Tabela Item_Carrinhos --
-- O carrinho de cada cliente, com o preço guardado quando o item entrou no carrinho e o preço atual do produto. --
SELECT uc.nome AS Cliente, prod.nome AS Produto, ic.quantidade AS Quantidade,
       ic.preco_unitario AS Preco_No_Carrinho, prod.preco AS Preco_Atual,
       ic.quantidade * ic.preco_unitario AS Subtotal
FROM item_carrinhos ic
INNER JOIN usuarios uc    ON uc.id = ic.cliente_id
INNER JOIN produtos prod  ON prod.id = ic.produto_id
ORDER BY uc.nome, prod.nome;
GO

-- Consultando funcionários --
SELECT * 
FROM usuarios, funcionarios
WHERE usuarios.id = funcionarios.usuario_id AND funcionarios.usuario_id = 9;
GO

select * from usuarios;
GO