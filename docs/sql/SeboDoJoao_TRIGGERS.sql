-- TRIGGERS DO BANCO DE DADOS - SEBO DO JOÃO --
-- 1-) Atualiza automaticamente o estoque dos produtos após a inserção de itens --
-- na tabela item_pedidos. Verifica se existe quantidade suficiente em estoque --
-- antes de realizar a baixa. Caso contrário, interrompe a operação e retorna --
-- uma mensagem de erro, impedindo que o estoque fique negativo. --
CREATE TRIGGER dbo.tr_item_pedidos_baixa_estoque ON item_pedidos AFTER INSERT AS
BEGIN
	-- SET NOCOUNT ON: Evita o envio de mensagens informando a quantidade de linhas afetadas --
	-- pelas instruções SQL executadas na trigger, reduzindo mensagens desnecessárias. --
	SET NOCOUNT ON;

	-- Verifica se existe estoque suficiente para todos os produtos inseridos. --
	IF EXISTS (
		SELECT 1
		FROM produtos prod WITH (UPDLOCK, HOLDLOCK)
		INNER JOIN (
			SELECT produto_id, SUM(quantidade) AS quantidade_total
			FROM inserted
			GROUP BY produto_id
		)	AS itens_agrupados
		ON		itens_agrupados.produto_id = prod.id
		WHERE	prod.estoque < itens_agrupados.quantidade_total 
	)
	BEGIN
		-- ALTERADO: o código do erro era 50001, o mesmo de "Cliente não encontrado!" na procedure --
		-- sp_finalizar_compra. Como o Java identifica o erro pelo código, cada erro precisa de um número próprio. --
		;THROW 50007, 'Estoque é insuficiente para realizar o pedido!', 1;
	END;

	-- Realiza a baixa das quantidades no estoque. --
	UPDATE prod SET prod.estoque = prod.estoque - itens_agrupados.quantidade_total
	FROM produtos prod
	INNER JOIN (
		SELECT produto_id, SUM(quantidade) AS quantidade_total
		FROM inserted
		GROUP BY produto_id
	)   AS itens_agrupados
	ON itens_agrupados.produto_id = prod.id;
END
GO



-- 2-) Devolve automaticamente ao estoque os produtos de um pedido cancelado --
-- Verifica se o status do pedido foi alterado para CANCELADO (4), identifica --
-- os produtos e suas respectivas quantidades na tabela item_pedidos e realiza --
-- a reposição correspondente no estoque da tabela produtos. --
CREATE TRIGGER dbo.tr_pedidos_devolver_estoque ON pedidos AFTER UPDATE AS
BEGIN
	SET NOCOUNT ON;

	-- Identifica os pedidos que tiveram seu status alterado para CANCELADO --
	UPDATE prod SET	prod.estoque	=	prod.estoque	+ itens_agrupados.quantidade_total
	FROM produtos prod
	INNER JOIN (
		SELECT ip.produto_id, SUM(ip.quantidade) AS quantidade_total
		FROM item_pedidos ip
		INNER JOIN		inserted i		ON		i.id = ip.pedido_id
		INNER JOIN		deleted  d		ON		d.id = i.id
		WHERE			i.status = 4	AND		d.status <> 4
		GROUP BY		ip.produto_id
	)	AS itens_agrupados
	ON	itens_agrupados.produto_id	=	prod.id;
END
GO



-- 3-) Atualiza automaticamente o status do pedido para PROCESSANDO (2) --
-- (numeração corrigida: eram duas triggers com o número 2) --
-- quando todos os pagamentos vinculados a ele estiverem aprovados. --
-- Utiliza a tabela lógica inserted para identificar os pedidos afetados --
-- e NOT EXISTS para verificar se ainda existem pagamentos não aprovados. --
CREATE TRIGGER dbo.tr_pagamentos_confirma_pedido ON pagamentos AFTER INSERT, UPDATE AS
BEGIN
    SET NOCOUNT ON;

    -- Atualiza somente pedidos que ainda aguardam pagamento --
    UPDATE pe SET pe.status = 2
    FROM pedidos pe
    WHERE pe.status = 1
      AND pe.id IN (SELECT pedido_id FROM inserted)

      -- Verifica se não existe nenhum pagamento ainda não aprovado --
      AND NOT EXISTS (
          SELECT 1
          FROM pagamentos pg
          WHERE pg.pedido_id = pe.id
            AND pg.status <> 2
      );
END
GO



-- 4-) Atualiza automaticamente o status do pedido para CONCLUÍDO (3) --
-- quando a entrega associada passa para ENTREGUE (4). Verifica se o campo --
-- status foi atualizado e compara os valores anteriores e atuais da entrega --
-- utilizando as tabelas lógicas inserted e deleted. Realiza a atualização --
-- somente quando o pedido ainda está em PROCESSANDO (2). --
CREATE TRIGGER dbo.tr_entregas_conclui_pedido ON entregas AFTER UPDATE AS
BEGIN
    SET NOCOUNT ON;
    IF NOT UPDATE(status) RETURN;
    UPDATE pe SET pe.status = 3
    FROM pedidos pe
    INNER JOIN inserted i ON i.id = pe.entrega_id
    INNER JOIN deleted d  ON d.id = i.id
    WHERE i.status = 4 AND d.status <> 4 AND pe.status = 2;
END
GO

-- OBSERVAÇÕES: --
-- inserted e deleted são tabelas temporárias que o SQL Server --
-- cria dentro do trigger com as linhas novas e antigas. --