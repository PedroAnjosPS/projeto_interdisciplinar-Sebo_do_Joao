-- PROCEDURES DO BANCO DE DADOS - SEBO DO JOÃO --

-- 1-) Finaliza a compra de um cliente, realizando as validações necessárias, --
-- cadastrando a entrega (com o endereço de destino e o valor do frete), o pedido, --
-- seus respectivos produtos e pagamentos (com a bandeira e o final do cartão usado). --
-- Calcula o valor total da compra, atualiza o estoque por meio da trigger --
-- correspondente e retorna o ID do pedido criado para a aplicação Java. --
CREATE PROCEDURE dbo.sp_finalizar_compra
    @cliente_id INT,
    -- ALTERADO: novo parâmetro obrigatório: o endereço de entrega escolhido (tabela enderecos). --
    -- Ele é gravado na entrega (entregas.endereco_id). --
    @endereco_id INT,
    @itens_json NVARCHAR(MAX),
    @forma_pagamento VARCHAR(50),
    @quantidade_parcelas INT = 1,
    @transportadora VARCHAR(100) = 'Correios (PAC)',
    @prazo_dias INT = 7,
    -- ALTERADO: novo parâmetro opcional com o valor do frete escolhido no site (padrão 0 = sem frete). --
    -- Antes o total do pedido era só a soma dos produtos e o frete mostrado na tela não ficava registrado. --
    @valor_frete DECIMAL(10,2) = 0,
    -- ALTERADO: bandeira e 4 últimos dígitos do cartão usado (opcionais; ficam NULL no PIX e no boleto). --
    -- São copiados para cada parcela em pagamentos, para o histórico não depender do cartão salvo. --
    @bandeira VARCHAR(30) = NULL,
    @final_cartao CHAR(4) = NULL,
    @pedido_id INT OUTPUT
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    BEGIN TRY

        -- Verifica se o cliente informado existe --
        IF NOT EXISTS (SELECT 1 FROM clientes WHERE usuario_id = @cliente_id)
            THROW 50001, 'Cliente não encontrado!', 1;

        -- ALTERADO (novo): o endereço de entrega precisa existir, pertencer a ESTE cliente e estar ativo --
        -- (não vale um endereço "excluído" na tela nem o endereço de outra pessoa). Código 50009. --
        IF NOT EXISTS (SELECT 1 FROM enderecos
                       WHERE id = @endereco_id AND cliente_id = @cliente_id AND ativo = 1)
            THROW 50009, 'Endereço de entrega inválido!', 1;

        -- Valida a forma de pagamento escolhida --
        IF @forma_pagamento NOT IN ('PIX', 'CARTAO_CREDITO', 'BOLETO')
            THROW 50005, 'Forma de pagamento inválida!', 1;

        -- Valida a quantidade de parcelas permitida --
        -- Somente o cartão de crédito permite parcelamento, em até @max_parcelas vezes --
        -- ALTERADO: o limite (3) estava escrito direto na condição. Agora fica nesta variável, que é o --
        -- ÚNICO lugar a mudar se a loja resolver aumentar o número de parcelas no futuro. --
        DECLARE @max_parcelas INT = 3;

        IF @quantidade_parcelas < 1 OR @quantidade_parcelas > @max_parcelas OR
            (@forma_pagamento <> 'CARTAO_CREDITO' AND @quantidade_parcelas <> 1)
            THROW 50006, 'Quantidade de parcelas inválida!', 1;


        -- ALTERADO: o frete não pode ser negativo (nem nulo). Código 50008 para o Java identificar o erro. --
        IF @valor_frete IS NULL OR @valor_frete < 0
            THROW 50008, 'Valor de frete inválido!', 1;

        -- Armazena temporariamente os produtos e suas quantidades recebidas em JSON --
        -- Agrupa produtos repetidos, somando suas respectivas quantidades --
        DECLARE @itens TABLE (
            produto_id INT PRIMARY KEY,
            quantidade INT NOT NULL
        );

        INSERT INTO @itens
        SELECT produtoId, SUM(quantidade)
        FROM OPENJSON(@itens_json)
        WITH (
            produtoId INT '$.produtoId',
            quantidade INT '$.quantidade'
        )
        GROUP BY produtoId;


        -- Verifica se o carrinho possui pelo menos um produto --
        IF NOT EXISTS (SELECT 1 FROM @itens)
            THROW 50002, 'O carrinho está vazio.', 1;

        -- Verifica se os produtos existem, estão ativos e possuem estoque suficiente --
        IF EXISTS (
            SELECT 1
            FROM @itens i
            LEFT JOIN produtos p ON p.id = i.produto_id
            WHERE p.id IS NULL OR p.status <> 1 OR i.quantidade <= 0 OR p.estoque < i.quantidade
        )
            THROW 50003, 'Algum produto está indisponível ou sem estoque suficiente.', 1;


        -- Seleciona o funcionário ativo com a menor quantidade de pedidos atribuídos --
        -- Em caso de empate, utiliza o menor ID como critério de desempate --
        DECLARE @funcionario_id INT =
            (
                SELECT TOP 1 f.usuario_id
                FROM funcionarios f
                INNER JOIN usuarios u ON u.id = f.usuario_id AND u.status = 1
                ORDER BY
                    (SELECT COUNT(*) FROM pedidos pe
                     WHERE pe.funcionario_id = f.usuario_id),
                    f.usuario_id
            );

        -- Verifica se existe algum funcionário ativo disponível --
        IF @funcionario_id IS NULL
            THROW 50004, 'Nenhum funcionário disponível.', 1;


        -- Inicia a transação para garantir que todas as operações sejam concluídas --
        -- em conjunto ou desfeitas caso ocorra algum erro --
        BEGIN TRANSACTION;


        -- Cadastra a entrega com status inicial 1 e calcula a previsão de entrega --
        -- ALTERADO: data_postagem agora entra como NULL. Sem isso, a coluna usava GETDATE() e a entrega --
        -- nascia "postada" no momento da compra. A data é preenchida por sp_avancar_entrega (em trânsito). --
        -- ALTERADO: a entrega agora também guarda o valor do frete (valor_frete) e o endereço de destino --
        -- (endereco_id), que antes não tinham onde ser registrados. --
        INSERT INTO entregas (transportadora, status, data_postagem, data_previsao_entrega, valor_frete, endereco_id)
        VALUES (
            @transportadora,
            1,
            NULL,
            DATEADD(DAY, @prazo_dias, CAST(GETDATE() AS DATE)),
            @valor_frete,
            @endereco_id
        );

        -- Recupera o ID da entrega recém-criada --
        DECLARE @entrega_id INT = SCOPE_IDENTITY();


        -- Cadastra o pedido com status inicial 1 (AGUARDANDO_PAGAMENTO) --
        -- O total começa em zero e será atualizado após a inserção dos produtos --
        INSERT INTO pedidos (total, status, entrega_id, cliente_id, funcionario_id)
        VALUES (0, 1, @entrega_id, @cliente_id, @funcionario_id);

        -- Recupera o ID do pedido criado e armazena no parâmetro de saída --
        SET @pedido_id = SCOPE_IDENTITY();


        -- Insere os produtos do carrinho na tabela item_pedidos --
        -- O preço é obtido diretamente da tabela produtos, evitando alterações --
        -- indevidas de valores enviados pelo navegador do cliente --
        INSERT INTO item_pedidos (pedido_id, produto_id, quantidade, preco_unitario)
        SELECT @pedido_id, i.produto_id, i.quantidade, p.preco
        FROM @itens i
        INNER JOIN produtos p ON p.id = i.produto_id;
        -- Dispara automaticamente a trigger de baixa de estoque --


        -- Calcula o valor total do pedido utilizando a função fn_total_pedido --
        -- ALTERADO: soma o frete ao total (produtos + frete). O valor do frete de um pedido pode ser --
        -- recuperado depois fazendo: pedidos.total - dbo.fn_total_pedido(id). As parcelas do pagamento --
        -- também passam a usar este total, que é o mesmo valor que a pessoa viu na tela de finalizar compra. --
        DECLARE @total DECIMAL(10,2) = dbo.fn_total_pedido(@pedido_id) + @valor_frete;

        -- Atualiza o valor total do pedido cadastrado anteriormente --
        UPDATE pedidos SET total = @total WHERE id = @pedido_id;


        -- Define o status inicial dos pagamentos conforme a forma escolhida --
        -- Cartão de crédito: aprovado (2). PIX e boleto: pendente (1) --
        -- Simulação utilizada para a apresentação do sistema --
        DECLARE @status_pagamento INT =
            CASE
                WHEN @forma_pagamento = 'CARTAO_CREDITO' THEN 2
                ELSE 1
            END;


        -- Gera e cadastra as parcelas utilizando a função fn_parcelas --
        -- Define a data de pagamento somente quando o pagamento está aprovado --
        INSERT INTO pagamentos (
            numero_parcela, status, valor, quantidade_parcelas, forma_pagamento,
            data_vencimento, data_pagamento, bandeira, final_cartao, pedido_id
        )
        SELECT
            numero_parcela,
            @status_pagamento,
            valor,
            @quantidade_parcelas,
            @forma_pagamento,
            data_vencimento,
            CASE
                WHEN @status_pagamento = 2 THEN CAST(GETDATE() AS DATE)
            END,
            -- ALTERADO: bandeira e final do cartão só são gravados quando o pagamento é no cartão --
            CASE WHEN @forma_pagamento = 'CARTAO_CREDITO' THEN @bandeira END,
            CASE WHEN @forma_pagamento = 'CARTAO_CREDITO' THEN @final_cartao END,
            @pedido_id
        FROM dbo.fn_parcelas(
            @total,
            @quantidade_parcelas,
            CAST(GETDATE() AS DATE)
        );
        -- Dispara automaticamente a trigger de confirmação do pedido --


        -- Confirma todas as operações realizadas na transação --
        COMMIT TRANSACTION;


    END TRY

    BEGIN CATCH

        -- Desfaz as operações caso ocorra algum erro durante a transação --
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;

        -- Reenvia o erro original para a aplicação Java --
        THROW;

    END CATCH
END
GO




-- 2-) Avança automaticamente o status da entrega associada a um pedido, --
-- seguindo a sequência PENDENTE (1), PREPARANDO (2), EM TRÂNSITO (3) --
-- e ENTREGUE (4). Verifica se o pedido está em processamento e se a --
-- entrega ainda não foi finalizada. Ao entrar em trânsito, gera o código --
-- e o link de rastreamento e registra a data de postagem. Ao ser entregue, --
-- registra a data de conclusão e dispara a trigger de atualização do pedido. --
CREATE PROCEDURE dbo.sp_avancar_entrega
    @pedido_id INT
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    -- Declara variáveis para armazenar os dados do pedido e da entrega --
    DECLARE @entrega_id INT,
            @status_entrega INT,
            @status_pedido INT;


    -- Recupera o ID da entrega e os status atuais do pedido e da entrega --
    SELECT
        @entrega_id = e.id,
        @status_entrega = e.status,
        @status_pedido = pe.status
    FROM pedidos pe
    INNER JOIN entregas e ON e.id = pe.entrega_id
    WHERE pe.id = @pedido_id;


    -- Verifica se o pedido ou sua entrega não foram encontrados --
    IF @entrega_id IS NULL
        THROW 50010, 'Pedido ou entrega não encontrados.', 1;

    -- Permite avançar a entrega somente quando o pedido está em PROCESSANDO (2) --
    IF @status_pedido <> 2
        THROW 50011, 'A entrega só avança depois que o pagamento for aprovado.', 1;

    -- Impede o avanço de entregas que já foram finalizadas ou canceladas --
    IF @status_entrega >= 4
        THROW 50012, 'A entrega já foi finalizada ou cancelada.', 1;


    -- Calcula o próximo status da entrega, avançando uma etapa --
    DECLARE @novo INT = @status_entrega + 1;


    -- Atualiza o status e as informações da entrega correspondente --
    UPDATE entregas
    SET
        status = @novo,

        -- Gera o código de rastreamento quando a entrega entra em trânsito --
        codigo_rastreio = CASE
            WHEN @novo = 3
                THEN CONCAT('SJ', RIGHT(CONCAT('000000000', @entrega_id), 9), 'BR')
            ELSE codigo_rastreio
        END,

        -- Gera o link de rastreamento quando a entrega entra em trânsito --
        link_rastreio = CASE
            WHEN @novo = 3
                THEN CONCAT('https://rastreio.sebodojoao.demo/', @entrega_id)
            ELSE link_rastreio
        END,

        -- Registra a data e hora da postagem quando a entrega entra em trânsito --
        data_postagem = CASE
            WHEN @novo = 3 THEN GETDATE()
            ELSE data_postagem
        END,

        -- Registra a data e hora da entrega quando o pedido é entregue --
        data_entrega = CASE
            WHEN @novo = 4 THEN GETDATE()
            ELSE data_entrega
        END

    WHERE id = @entrega_id;
    -- Dispara automaticamente a trigger de conclusão do pedido --

END
GO



-- 3-) Permite que o cliente cancele seu próprio pedido enquanto a entrega --
-- ainda não estiver em trânsito. Verifica se o pedido pertence ao cliente --
-- informado e se ainda atende às condições de cancelamento. Atualiza os --
-- pagamentos pendentes, cancela a entrega e altera o status do pedido. --
-- A devolução dos produtos ao estoque é realizada automaticamente pela --
-- trigger tr_pedidos_devolver_estoque. --
CREATE PROCEDURE dbo.sp_cancelar_pedido
    @pedido_id INT,
    @cliente_id INT
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    -- Declara variáveis para armazenar os dados do pedido e da entrega --
    DECLARE @status_pedido INT,
            @entrega_id INT,
            @status_entrega INT;

    -- Recupera os dados somente se o pedido pertencer ao cliente informado --
    SELECT
        @status_pedido = pe.status,
        @entrega_id = pe.entrega_id,
        @status_entrega = e.status
    FROM pedidos pe
    LEFT JOIN entregas e ON e.id = pe.entrega_id
    WHERE pe.id = @pedido_id
      AND pe.cliente_id = @cliente_id;

    -- Verifica se o pedido existe e pertence ao cliente informado --
    IF @status_pedido IS NULL
        THROW 50020, 'Pedido não encontrado para este cliente.', 1;

    -- Impede o cancelamento de pedidos fora dos status permitidos --
    -- e de entregas que já entraram em trânsito ou foram finalizadas --
    IF @status_pedido NOT IN (1, 2) OR @status_entrega >= 3
        THROW 50021, 'Este pedido não pode mais ser cancelado.', 1;

    BEGIN TRY
        BEGIN TRANSACTION;

        -- Altera os pagamentos que ainda estão pendentes para recusados --
        UPDATE pagamentos
        SET status = 3
        WHERE pedido_id = @pedido_id
          AND status = 1;

        -- Atualiza o status da entrega para CANCELADA (5) --
        UPDATE entregas
        SET status = 5
        WHERE id = @entrega_id;

        -- Cancela o pedido, disparando a trigger de devolução do estoque --
        UPDATE pedidos
        SET status = 4
        WHERE id = @pedido_id;

        COMMIT TRANSACTION;

    END TRY

    BEGIN CATCH
        -- Desfaz as alterações caso ocorra algum erro durante o cancelamento --
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;

        -- Encaminha o erro para a aplicação Java --
        THROW;
    END CATCH
END
GO