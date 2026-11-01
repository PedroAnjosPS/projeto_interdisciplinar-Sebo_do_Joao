-- BACKUP E RESTORE DO BANCO DE DADOS - SEBO DO JOÃO --

-- === BACKUP === --
-- O Backup do Banco de dados é feito pelo PowerShell ou terminal Linux, --
-- devido ao fato de o nosso BD do projeto rodar via Docker. --
/*
	docker exec -u root meu_sql_server sh -c "mkdir -p /var/opt/mssql/backup && chown mssql /var/opt/mssql/backup"
	docker exec meu_sql_server /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "Fatec@2026" -C -Q "BACKUP DATABASE Sebo_Do_Joao TO DISK = N'/var/opt/mssql/backup/Sebo_Do_Joao.bak' WITH FORMAT, INIT, NAME = N'Backup completo - Sebo do Joao', STATS = 50"
	docker cp meu_sql_server:/var/opt/mssql/backup/Sebo_Do_Joao.bak .\Sebo_Do_Joao.bak

*/

-- === RESTORE === --
RESTORE DATABASE Sebo_Do_Joao_Restaurado
FROM DISK = N'/var/opt/mssql/backup/Sebo_Do_Joao.bak'
WITH MOVE 'Sebo_do_Joao'     TO '/var/opt/mssql/data/Sebo_Do_Joao_Restaurado.mdf',
     MOVE 'Sebo_do_Joao_log' TO '/var/opt/mssql/data/Sebo_Do_Joao_Restaurado.ldf';
