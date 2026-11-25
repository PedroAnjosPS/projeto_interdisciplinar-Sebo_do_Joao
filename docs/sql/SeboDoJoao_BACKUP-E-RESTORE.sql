-- BACKUP E RESTORE DO BANCO DE DADOS - SEBO DO JOÃO --

-- O banco roda dentro do Docker, então o arquivo .bak fica dentro do contêiner --
-- (pasta /var/opt/mssql/backup), e não numa pasta do Windows. --

-- === ANTES DE COMEÇAR === --
-- Criar a pasta de backup dentro do contêiner (PowerShell, só uma vez): --
/*
	docker exec -u root meu_sql_server sh -c "mkdir -p /var/opt/mssql/backup && chown mssql /var/opt/mssql/backup"
*/


-- === BACKUP === --
-- Consulta o diretório padrão de backups do SQL Server. --
SELECT
    SERVERPROPERTY('InstanceDefaultBackupPath') AS Diretorio_Backup;
GO

-- Faz o backup completo do banco (comando do próprio SQL Server, sem PowerShell) --
-- * WITH FORMAT, INIT: Cria um novo conjunto de mídia e sobrescreve arquivos de backup anteriores no mesmo caminho. --
-- * COMPRESSION: Compacta o arquivo de backup para economizar espaço em disco (disponível nas edições compatíveis do SQL Server). --
-- * STATS = 10: Exibe o progresso da operação a cada 10% concluídos. --
BACKUP DATABASE Sebo_Do_Joao
TO DISK = N'/var/opt/mssql/backup/Sebo_Do_Joao_Teste.bak'
WITH FORMAT, INIT, COMPRESSION, STATS = 10;
GO

-- Confere se o arquivo de backup está íntegro. --
RESTORE VERIFYONLY
FROM DISK = N'/var/opt/mssql/backup/Sebo_Do_Joao_Teste.bak';
GO

-- Para levar o arquivo para o Windows (PowerShell): --
/*
	docker cp meu_sql_server:/var/opt/mssql/backup/Sebo_Do_Joao_Teste.bak .\Sebo_Do_Joao_Teste.bak
*/


-- === RESTORE === --
-- Faz a consulta do arquivo backup. --
RESTORE FILELISTONLY
FROM DISK = N'/var/opt/mssql/backup/Sebo_Do_Joao_Teste.bak';
GO


-- Confirmação para ver se já não existe um banco de dados restaurado --
-- (se aparecer uma linha, apague o banco com o bloco do final do arquivo antes de restaurar) --
SELECT name
FROM sys.databases
WHERE name = 'Sebo_Do_Joao_Teste';
GO


-- Faz o restore do banco de dados com outro nome, sem mexer no banco original. --
RESTORE DATABASE Sebo_Do_Joao_Teste
FROM DISK = N'/var/opt/mssql/backup/Sebo_Do_Joao_Teste.bak'
WITH MOVE 'Sebo_do_Joao'     TO '/var/opt/mssql/data/Sebo_Do_Joao_Teste.mdf',
     MOVE 'Sebo_do_Joao_log' TO '/var/opt/mssql/data/Sebo_Do_Joao_Teste.ldf';
GO


-- Compara o banco original com o restaurado: os dois números precisam ser iguais. --
SELECT
    (SELECT COUNT(*) FROM Sebo_Do_Joao.dbo.pedidos)       AS Pedidos_Original,
    (SELECT COUNT(*) FROM Sebo_Do_Joao_Teste.dbo.pedidos) AS Pedidos_Restaurado;
GO


-- Deleta o banco de dados restaurado sem gerar conflitos --
USE master;
GO

ALTER DATABASE Sebo_Do_Joao_Teste
SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
GO

DROP DATABASE Sebo_Do_Joao_Teste;
GO
