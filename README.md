# Sebo do João — Sistema de E-commerce

Projeto Interdisciplinar desenvolvido na **Faculdade de Tecnologia de São José do Rio Preto (Fatec)**, com o objetivo de desenvolver uma plataforma de e-commerce para o Sebo do João, aproximando a loja física do ambiente digital e oferecendo recursos para consulta e gerenciamento de produtos e operações.

---

## Sobre o Sebo do João

O **Sebo do João** é um estabelecimento tradicional de São José do Rio Preto (SP), fundado há aproximadamente 30 anos por **João Luíz Meloze**. Desde 2016, suas filhas dão continuidade ao negócio, preservando seu legado e valorizando a cultura, a nostalgia e as memórias afetivas.

A loja trabalha com oito categorias de produtos:

- Discos de vinil;
- Livros;
- Histórias em quadrinhos (HQs);
- Mangás;
- CDs;
- DVDs;
- Blu-rays;
- Fitas cassete e VHS.

Os discos de vinil são um dos principais destaques do acervo. A loja também oferece o serviço de transferência de fitas VHS para mídia digital e se diferencia pela variedade de produtos e pelo atendimento próximo e acolhedor.

**Endereço:** Rua Conselheiro Saraiva, 163 — Vila Ercília, São José do Rio Preto — SP, 15013-090.  
**Telefone:** (17) 98105-8514.

---

## Objetivo do Projeto

Desenvolver um sistema de e-commerce que represente digitalmente as atividades do Sebo do João, permitindo a consulta e a comercialização simulada de seus produtos e apoiando o gerenciamento das informações da loja.

O projeto também tem como finalidade aplicar, na prática, os conhecimentos adquiridos durante o curso, integrando o desenvolvimento de interfaces web, a programação backend e a persistência de dados em um banco de dados relacional.

---

## Estado Atual do Desenvolvimento

O sistema encontra-se em sua **etapa final de desenvolvimento**, com as principais partes da aplicação implementadas e em preparação para a entrega acadêmica.

A aplicação conta com:

- Interface web desenvolvida com **HTML, CSS e JavaScript**;
- Backend desenvolvido em **Java 21 com Spring Boot**;
- Persistência e gerenciamento dos dados utilizando **Microsoft SQL Server**;
- Organização do backend em camadas e componentes, incluindo entidades, DTOs, repositórios, serviços e controladores REST;
- Scripts SQL para criação e manipulação da estrutura do banco de dados;
- Documentação e materiais do projeto organizados no diretório `/docs`.

### Limitações e simulações

Por se tratar de um projeto acadêmico, o sistema **não possui integração com plataformas reais de pagamento ou serviços de entrega**.

Dessa forma, as etapas relacionadas a pagamentos, entregas e demais operações que dependam desses serviços são representadas por simulações dentro da aplicação. Nenhuma transação financeira real é processada e nenhuma solicitação de entrega é efetivamente encaminhada a uma transportadora.

---

## Diagramas do Banco de Dados

Os diagramas abaixo representam a estrutura e a organização dos dados utilizados pelo sistema.

### DER — Diagrama de Entidade e Relacionamento

![DER (Diagrama de Entidade e Relacionamento)](docs/diagramas/DER-Sebo_do_Joao.png)

### Diagrama do Banco de Dados

![Diagrama do Banco de Dados](docs/diagramas/Diagrama_Banco_de_Dados.png)

---

## Interfaces do Sistema

A seguir, estão as telas da aplicação em funcionamento, organizadas por perfil de usuário.

### Área do cliente

#### Tela inicial

![Tela inicial](docs/interfaces/telas_do_site/index.png)

#### Informações do produto

![Tela de Informações do Produto](docs/interfaces/telas_do_site/informacoes_produto.png)

#### Carrinho de compras

![Tela de Carrinho de Compras](docs/interfaces/telas_do_site/carrinho_de_compra.png)

#### Login

![Tela de Login](docs/interfaces/telas_do_site/login.png)

#### Criar conta — etapa 1

![Tela de Criar Conta, etapa 1](docs/interfaces/telas_do_site/criar_conta_1.png)

#### Criar conta — etapa 2

![Tela de Criar Conta, etapa 2](docs/interfaces/telas_do_site/criar_conta_2.png)

#### Minha conta

![Tela Minha Conta do cliente](docs/interfaces/telas_do_site/minha_conta_cliente.png)

#### Informações do perfil

![Tela de Informações do Perfil do cliente](docs/interfaces/telas_do_site/informacoes_perfil_cliente.png)

#### Endereços

![Tela de Endereços](docs/interfaces/telas_do_site/enderecos.png)

#### Cartões

![Tela de Cartões](docs/interfaces/telas_do_site/cartoes.png)

#### Segurança e privacidade

![Tela de Segurança e Privacidade](docs/interfaces/telas_do_site/seguranca_privacidade_usuario.png)

#### Finalizar compra

![Tela de Finalizar Compra](docs/interfaces/telas_do_site/finalizar_compra.png)

#### Confirmação do pedido (Pix simulado)

![Tela de Confirmação de Pagamento do Pedido com Pix simulado](docs/interfaces/telas_do_site/confirmacao_pagamento_pedido_pix_simulado.png)

#### Meus pedidos

![Tela de Meus Pedidos](docs/interfaces/telas_do_site/meus_pedidos.png)

### Área do funcionário

#### Minha conta

![Tela Minha Conta do funcionário](docs/interfaces/telas_do_site/minha_conta_funcionario.png)

#### Cadastro de produto

![Tela de Cadastro de Produto](docs/interfaces/telas_do_site/cadastrar_produto.png)

#### Produtos cadastrados

![Tela de Produtos Cadastrados](docs/interfaces/telas_do_site/produtos_cadastrados_funcionario.png)

#### Gerenciamento de pedidos

![Tela de Gerenciamento de Pedidos](docs/interfaces/telas_do_site/gerenciamento_pedidos_funcionario.png)

### Área do administrador

#### Painel de dados (dashboard)

![Painel de dados do administrador](docs/interfaces/telas_do_site/administrador-dashboard.jpeg)

#### Gerenciamento de funcionários

![Tela de Gerenciamento de Funcionários](docs/interfaces/telas_do_site/gerenciamento_funcionarios_admin.png)

> Os protótipos desenhados no Figma antes do desenvolvimento estão na pasta [`docs/interfaces/prototipos`](docs/interfaces/prototipos).

---

## Tecnologias Utilizadas

As principais tecnologias utilizadas no desenvolvimento do sistema são:

| Tecnologia | Utilização |
|---|---|
| **HTML5** | Estruturação das páginas e elementos da interface web. |
| **CSS3** | Estilização e organização visual das interfaces. |
| **JavaScript** | Implementação de interações e comunicação do frontend com o backend. |
| **Java 21** | Linguagem utilizada no desenvolvimento da aplicação backend. |
| **Spring Boot** | Framework utilizado na construção da aplicação backend e dos controladores REST. |
| **Microsoft SQL Server** | Sistema gerenciador de banco de dados relacional utilizado para armazenar os dados da aplicação. |
| **Maven** | Gerenciamento de dependências e construção do projeto Java. |

---

## Estrutura do Repositório

Abaixo está uma visão geral da organização dos principais diretórios e arquivos do projeto:

```text
sebo-do-joao/
├── docs/
│   ├── diagramas/
│   │   ├── DER-Sebo_do_Joao.png
│   │   ├── Diagrama_Banco_de_Dados.png
│   │   └── ...
│   ├── interfaces/
│   │   ├── prototipos/
│   │   │   ├── Tela-principal-index.png
│   │   │   ├── Tela-cadastro-cliente.png
│   │   │   └── ...
│   │   └── telas_do_site/
│   │       └── ...
│   ├── metodos-estatisticos/
│   │   └── planilha-analise-estatistica-interdisciplinar.xlsx
│   ├── sql/
│   │   ├── SeboDoJoao.sql
│   │   ├── SeboDoJoao_INSERTS.sql
│   │   ├── SeboDoJoao_SELECTS.sql
│   │   ├── SeboDoJoao_VIEWS.sql
│   │   ├── SeboDoJoao_FUNCTIONS.sql
│   │   ├── SeboDoJoao_TRIGGERS.sql
│   │   ├── SeboDoJoao_PROCEDURES.sql
│   │   ├── SeboDoJoao_BACKUP-E-RESTORE.sql
│   │   ├── backup-sql/
│   │   │   └── Sebo_Do_Joao.bak
│   │   └── ...
│   └── testes/
│       ├── dados-testes.txt
│       └── Selects-de-testes.sql
│
├── java/
│   └── seboDoJoao/
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/br/com/interdisciplinar/sebodojoao/
│       │   │   │   ├── dto/
│       │   │   │   ├── model/
│       │   │   │   ├── repository/
│       │   │   │   ├── restcontroller/
│       │   │   │   ├── service/
│       │   │   │   └── SebodojoaoApplication.java
│       │   │   └── resources/
│       │   │       ├── static/
│       │   │       │   ├── css/
│       │   │       │   ├── data/
│       │   │       │   ├── imagens/
│       │   │       │   ├── js/
│       │   │       │   ├── index.html
│       │   │       │   └── ...
│       │   │       └── application.yaml
│       │   └── test/
│       │       └── ...
│       ├── mvnw
│       ├── pom.xml
│       └── ...
│
└── README.md
```

> A estrutura apresentada é uma visão geral do repositório. Alguns arquivos e diretórios foram omitidos para facilitar a leitura.

---

## Documentação

Os documentos, diagramas, scripts SQL, materiais de testes e demais arquivos de apoio estão organizados no diretório [`/docs`](docs) do repositório.

---

**Projeto Interdisciplinar — Fatec São José do Rio Preto**
