-- BACKUP E RESTORE DO BANCO DE DADOS - SEBO DO JOÃO --

-- === BACKUP === --
-- Consulta o diretório padrão de backups do SQL Server. --
SELECT
    SERVERPROPERTY('InstanceDefaultBackupPath') AS Diretorio_Backup;


-- O Backup do Banco de dados é feito pelo PowerShell ou terminal Linux, --
-- devido ao fato de o nosso BD do projeto rodar via Docker. --
/*
	docker exec -u root meu_sql_server sh -c "mkdir -p /var/opt/mssql/backup && chown mssql /var/opt/mssql/backup"
	docker exec meu_sql_server /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "Fatec@2026" -C -Q "BACKUP DATABASE Sebo_Do_Joao TO DISK = N'/var/opt/mssql/backup/Sebo_Do_Joao.bak' WITH FORMAT, INIT, NAME = N'Backup completo - Sebo do Joao', STATS = 50"
	docker cp meu_sql_server:/var/opt/mssql/backup/Sebo_Do_Joao.bak .\Sebo_Do_Joao.bak

	Para localizar o backup no PowerShell : docker exec -it NOME_DO_CONTAINER ls -lh /var/opt/mssql/data/

*/

-- Comando convencional do Sql Server para fazer o backup (sem PowerShell) --
BACKUP DATABASE Sebo_Do_Joao
TO DISK = 
'/var/opt/mssql/data/Sebo_Do_Joao_Teste.bak'
-- * WITH FORMAT, INIT: Cria um novo conjunto de mídia e sobrescreve arquivos de backup anteriores no mesmo caminho. --
-- * COMPRESSION: Compacta o arquivo de backup para economizar espaço em disco (disponível nas edições compatíveis do SQL Server). --
-- * STATS = 10: Exibe o progresso da operação a cada 10% concluídos. --
WITH FORMAT, INIT, COMPRESSION, STATS = 10;


-- === RESTORE === --
-- Faz a consulta do arquivo backup. --
RESTORE FILELISTONLY
FROM DISK = N'/var/opt/mssql/backup/Sebo_Do_Joao.bak';


-- Confirmação para ver se já não existe um banco de dados restaurado --
SELECT name
FROM sys.databases
WHERE name = 'Sebo_Do_Joao_Restaurado';


-- Faz o restore do banco de dados. --
RESTORE DATABASE Sebo_Do_Joao_Restaurado
FROM DISK = N'/var/opt/mssql/backup/Sebo_Do_Joao.bak'
WITH MOVE 'Sebo_do_Joao'     TO '/var/opt/mssql/data/Sebo_Do_Joao_Restaurado.mdf',
     MOVE 'Sebo_do_Joao_log' TO '/var/opt/mssql/data/Sebo_Do_Joao_Restaurado.ldf';


-- Deleta o banco de dados restaurado sem gerar conflitos --
USE master;
GO

ALTER DATABASE Sebo_Do_Joao_Restaurado
SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
GO

DROP DATABASE Sebo_Do_Joao_Restaurado;
GO