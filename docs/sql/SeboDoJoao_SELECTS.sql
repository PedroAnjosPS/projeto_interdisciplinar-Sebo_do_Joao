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

-- Tabela Clientes --
SELECT * FROM clientes cli ORDER BY cli.data_nascimento;

-- Tabela Funcionarios --
SELECT * FROM funcionarios func ORDER BY func.codigo_funcionario;

-- Tabela Categorias --
SELECT * FROM categorias cat ORDER BY cat.nome;

-- Tabela Generos --
SELECT * FROM generos gen ORDER BY gen.nome;

-- Tabela Produtos --
SELECT * FROM produtos prod ORDER BY prod.nome;

-- Tabela Imagem_Produtos --
SELECT * FROM imagem_produtos imag ORDER BY imag.produto_id;

-- Tabela Entregas --
SELECT * FROM entregas ent ORDER BY ent.transportadora;

-- Tabela Pedidos --
SELECT * FROM pedidos pe ORDER BY pe.total DESC;

-- Tabela Item_Pedidos --
SELECT pe.id ID_Pedido, prod.nome Produto, pe.total Total_Pedido  
FROM produtos prod, pedidos pe, item_pedidos item 
WHERE 
    item.pedido_id = pe.id AND
    item.produto_id = prod.id 
ORDER BY pe.id;

-- Tabela Pagamentos --
SELECT * FROM pagamentos pag ORDER BY pag.pedido_id;


-- 1-) Verificando o preço dos produtos cadastrados em ordem decrescente --
Select Prod.preco
from produtos Prod
ORDER BY preco DESC