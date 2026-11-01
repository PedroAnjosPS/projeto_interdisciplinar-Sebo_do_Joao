-- 3 FUNCTIONS DO BANCO DE DADOS - SEBO DO JOÃO --
-- Retorna o valor do TOTAL do Pedido: Soma os itens de um pedido --
-- e retorna o valor final totalizado. --
CREATE FUNCTION dbo.fn_total_pedido (@pedido_id INT)
RETURNS DECIMAL(10,2)
AS 
BEGIN
	RETURN ISNULL((SELECT SUM(quantidade * preco_unitario)
				   FROM item_pedidos WHERE pedido_id = @pedido_id), 0);
END
GO



-- Retorna uma tabela dos produtos e seus respectivos detalhes --
-- (categoria, quantidade, preço unitário e subtotal) de um --
-- pedido específico, informado pelo parâmetro @pedido_id. --
CREATE FUNCTION dbo.fn_itens_do_pedido (@pedido_id INT)
RETURNS TABLE 
AS 
RETURN 
(
	SELECT p.id AS produto_id, p.nome AS produto, c.nome AS categoria,
		i.quantidade, i.preco_unitario, i.quantidade * i.preco_unitario AS subtotal
	FROM item_pedidos i
	INNER JOIN produtos p ON p.id = i.produto_id
	INNER JOIN categorias c ON c.id = p.categoria_id
	WHERE i.pedido_id = @pedido_id
)
GO



-- Gera as parcelas de uma compra, calculando o valor e a data de vencimento de cada uma, --
-- com ajuste de centavos na última parcela. Considera o valor total, a quantidade de parcelas -- 
-- e a data do primeiro vencimento. --
CREATE FUNCTION dbo.fn_parcelas (@total DECIMAL(10,2), @qtd INT, @primeiro_vencimento DATE)
RETURNS @parcelas TABLE (numero_parcela INT, valor DECIMAL(10,2), data_vencimento DATE)
AS 
BEGIN 
	-- Faz a validação e impede valores inválidos
    IF @total <= 0 OR @qtd <= 0 OR @primeiro_vencimento IS NULL
        RETURN;

	DECLARE @base DECIMAL(10,2) = FLOOR(@total * 100 / @qtd) / 100.00;
	DECLARE @n INT = 1;
	WHILE @n <= @qtd
	BEGIN
		INSERT INTO @parcelas
		VALUES (@n,
				CASE 
					WHEN @n = @qtd 
					THEN @total - @base * (@qtd - 1) 
					ELSE @base 
				END, -- os centavos sobram na última parcela
				DATEADD(MONTH, @n - 1, @primeiro_vencimento));
		SET @n = @n + 1;
	END
	RETURN;
END
GO