-----------------------------------------------------------------------
-- Criar o Banco de Dados
-----------------------------------------------------------------------
create database Sebo_do_Joao
go

-----------------------------------------------------------------------
-- Acessar o Banco de Dados
-----------------------------------------------------------------------
use Sebo_do_Joao
go

-- Desliga o cache de IDENTITY do SQL Server. Por padrão ele reserva blocos de 1000 números
-- em memória e, se o servidor reinicia sem desligar direito (ex.: o container do Docker),
-- o bloco é perdido e os ids pulam (ex.: de 12 para 1002). Desligado, os ids seguem em sequência.
alter database scoped configuration set identity_cache = off
go

-----------------------------------------------------------------------
-- Criar as tabelas do Banco de Dados
-----------------------------------------------------------------------

-----------------------------------------------------------------------
-- Tabela: ufs
-----------------------------------------------------------------------
create table ufs
(
	sigla				varchar(2)		not null		primary key,
	nome				varchar(100)	not null
)
go

-----------------------------------------------------------------------
-- Tabela: cidades
-----------------------------------------------------------------------
create table cidades
(
	id					int				not null		primary key		identity,
	nome				varchar(100)	not null,
	uf_sigla			varchar(2)		not null		references ufs(sigla)
)
go

-----------------------------------------------------------------------
-- Tabela: ceps
-----------------------------------------------------------------------
create table ceps
(
	numero_cep			varchar(10)		not null		primary key,
	cidade_id			int				not null		references cidades(id)
)
go

-----------------------------------------------------------------------
-- Tabela: usuarios
-----------------------------------------------------------------------
create table usuarios
(
	id					int				not null		primary key		identity,
	nome				varchar(100)	not null,
	email				varchar(100)	not null		unique,
	senha				varchar(255)	not null,
	-- status 1 - ativo; 2 - inativo
	status				int					null		default 1		check(status in (1,2)),
	telefone			varchar(20),
	data_cadastro		datetime						default getdate(),
	foto_perfil			varchar(255)		null
)
go

-----------------------------------------------------------------------
-- Tabela: clientes
-- chave primária que também é chave estrangeira, pois estamos fazendo
-- herança de usuarios
-----------------------------------------------------------------------
create table clientes
(
	usuario_id			int				not null		primary key		references usuarios(id),
	data_nascimento		date,
	cpf					varchar(14)						unique
)
go

-----------------------------------------------------------------------
-- Tabela: funcionarios
-- chave primária que também é chave estrangeira, pois estamos fazendo
-- herança de usuarios
-----------------------------------------------------------------------
create table funcionarios
(
	usuario_id			int				not null		primary key		references usuarios(id),
	codigo_funcionario	varchar(50)		not null		unique,
	-- admin 0 - funcionário comum (padrão); 1 - funcionário administrador
	admin				bit				not null		default 0
)
go

-----------------------------------------------------------------------
-- Tabela: enderecos
-- No front-end o endereço "excluído" fica com ativo = 0, para não
-- quebrar o histórico das entregas
-----------------------------------------------------------------------
create table enderecos
(
	id					int				not null		primary key		identity,
	logradouro			varchar(255)	not null,
	-- numero null - opção "Sem número" da tela
	numero				varchar(10)			null,
	complemento			varchar(100)		null,
	bairro				varchar(100)	not null,
	-- ativo 1 - ativo; 0 - o cliente deixou de usar este endereço
	ativo				bit				not null		default 1,
	-- nome dado pelo cliente ao endereço (ex.: "Casa", "Trabalho")
	apelido				varchar(30)		not null		default 'Endereço',
	-- padrao 1 - endereço já selecionado no checkout; 0 - não é o padrão
	-- só pode existir 1 endereço padrão por cliente (regra feita na aplicação)
	padrao				bit				not null		default 0,
	cep_numero			varchar(10)		not null		references ceps(numero_cep),
	cliente_id			int				not null		references clientes(usuario_id)
)
go

-----------------------------------------------------------------------
-- Tabela: cartoes
-- Nunca guardar o número completo nem o CVV: só os 4 últimos dígitos
-----------------------------------------------------------------------
create table cartoes
(
	id					int				not null		primary key		identity,
	bandeira			varchar(30)		not null,
	-- 4 últimos dígitos do cartão
	final_numero		char(4)			not null,
	titular				varchar(100)	not null,
	-- validade no formato MM/AA, igual à tela (mês de 01 a 12)
	validade			char(5)			not null		check(validade like '0[1-9]/[0-9][0-9]' or validade like '1[0-2]/[0-9][0-9]'),
	-- padrao 1 - cartão já selecionado no checkout; 0 - não é o padrão
	-- só pode existir 1 cartão padrão por cliente (regra feita na aplicação)
	padrao				bit				not null		default 0,
	cliente_id			int				not null		references clientes(usuario_id)
)
go

-----------------------------------------------------------------------
-- Tabela: categorias
-----------------------------------------------------------------------
create table categorias
(
	id					int				not null		primary key		identity,
	nome				varchar(100)	not null
)
go

-----------------------------------------------------------------------
-- Tabela: generos
-----------------------------------------------------------------------
create table generos
(
	id					int				not null		primary key		identity,
	nome				varchar(100)	not null
)
go

-----------------------------------------------------------------------
-- Tabela: produtos
-----------------------------------------------------------------------
create table produtos
(
	id					int				not null		primary key		identity,
	nome				varchar(100)	not null,
	descricao			varchar(max),
	estoque				int				not null		default 0		check(estoque >= 0),
	preco				decimal(10,2)	not null		check(preco > 0),
	-- status 1 - disponível; 2 - inativo
	status				int					null		default 1		check(status in (1,2)),
	categoria_id		int				not null		references categorias(id),
	genero_id			int				not null		references generos(id),
	funcionario_id		int				not null		references funcionarios(usuario_id)
)
go

-----------------------------------------------------------------------
-- Tabela: imagem_produtos
-----------------------------------------------------------------------
create table imagem_produtos
(
	id					int				not null		primary key		identity,
	caminho				varchar(255)	not null,
	produto_id			int				not null		references produtos(id)
)
go

-----------------------------------------------------------------------
-- Tabela: entregas
-- O frete fica na tabela de entregas (pedidos.total = produtos + frete)
-----------------------------------------------------------------------
create table entregas
(
	id					int				not null		primary key		identity,
	transportadora		varchar(100),
	-- status 1 - pendente; 2 - preparando; 3 - em trânsito; 4 - entregue; 5 - cancelada
	status				int								default 1		check(status in (1,2,3,4,5)),
	link_rastreio		varchar(255),
	codigo_rastreio		varchar(100),
	data_postagem		datetime						default getdate(),
	data_previsao_entrega	date,
	data_entrega		datetime,
	valor_frete			decimal(10,2)	not null		default 0		check(valor_frete >= 0),
	-- destino da entrega
	endereco_id			int				not null		references enderecos(id)
)
go

-----------------------------------------------------------------------
-- Tabela: pedidos
-----------------------------------------------------------------------
create table pedidos
(
	id					int				not null		primary key		identity,
	data				datetime		not null		default getdate(),
	total				decimal(10,2)	not null		default 0		check(total >= 0),
	-- status 1 - aguardando pagamento; 2 - processando; 3 - concluído; 4 - cancelado
	status				int					null		default 1		check(status in (1,2,3,4)),
	entrega_id			int								unique			references entregas(id),
	cliente_id			int				not null		references clientes(usuario_id),
	funcionario_id		int				    null		references funcionarios(usuario_id)
)
go

-----------------------------------------------------------------------
-- Tabela: item_pedidos
-- Tabela de relação NxN: chave primária é uma chave composta pelas
-- chaves estrangeiras das duas tabelas da relação NxN
-- Se o pedido for excluído, os itens dele também são
-----------------------------------------------------------------------
create table item_pedidos
(
	pedido_id			int				not null,
	produto_id			int				not null,
	quantidade			int				not null		check(quantidade > 0),
	preco_unitario		decimal(10,2)	not null		check(preco_unitario > 0),
	-- definição das chaves estrangeiras
	foreign key(pedido_id)				references		pedidos(id)		on delete cascade,
	foreign key(produto_id)				references		produtos(id),
	-- por ser uma relação NxN a chave primária é composta pelas duas chaves estrangeiras
	primary key(pedido_id, produto_id)
)
go

-----------------------------------------------------------------------
-- Tabela: pagamentos
-----------------------------------------------------------------------
create table pagamentos
(
	id					int				not null		primary key		identity,
	numero_parcela		int				not null,
	-- status 1 - pendente; 2 - aprovado; 3 - recusado; 4 - compra estornada (pedido cancelado depois de pago)
	status				int					null		default 1		check(status in (1,2,3,4)),
	valor				decimal(10,2)	not null		check(valor > 0),
	quantidade_parcelas	int				not null		check(quantidade_parcelas > 0),
	forma_pagamento		varchar(50)		not null,
	data_vencimento		date			not null,
	data_pagamento		date,
	-- cópia dos dados do cartão usado (bandeira e final_cartao), para o histórico não depender
	-- somente da tabela cartoes (o cliente pode pagar com cartão não salvo ou apagar o cartão
	-- depois). Fica null no PIX e no boleto
	bandeira			varchar(30)			null,
	final_cartao		char(4)				null,
	pedido_id			int				not null		references pedidos(id)
)
go

-----------------------------------------------------------------------
-- Tabela: item_carrinhos
-- O carrinho de compras do cliente é separado de item_pedidos: não baixa
-- estoque (a trigger é só de item_pedidos), guarda o preço do produto no
-- momento em que entrou no carrinho e não exige pedido
-----------------------------------------------------------------------
create table item_carrinhos
(
	cliente_id			int				not null,
	produto_id			int				not null,
	quantidade			int				not null		default 1		check(quantidade > 0),
	preco_unitario		decimal(10,2)	not null		check(preco_unitario > 0.00),
	-- definição das chaves estrangeiras (se o cliente for excluído, o carrinho dele também é)
	foreign key(cliente_id)				references		clientes(usuario_id)	on delete cascade,
	foreign key(produto_id)				references		produtos(id),
	primary key(cliente_id, produto_id)
)
go
