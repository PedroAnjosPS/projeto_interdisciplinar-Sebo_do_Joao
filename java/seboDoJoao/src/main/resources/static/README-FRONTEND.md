# Front-end do Sebo do João

Este documento explica como o front-end do projeto está organizado hoje: as pastas, as telas, os arquivos
JavaScript e CSS, quem pode abrir cada tela e como o site conversa com o back-end em Java.

---

## 1. Visão geral

- **HTML, CSS e JavaScript puros.** Não há framework de JavaScript nem etapa de compilação do front-end.
- **Bibliotecas por CDN** (precisam de internet):
  - Bootstrap 5.3.0 (layout, grid, modais, botões e alertas);
  - Font Awesome 6.4.0 (ícones);
  - Chart.js 4.4.1 (só os gráficos de `administrador-dashboard.html`).
- **Servido pelo próprio Spring Boot.** Os arquivos ficam em `src/main/resources/static`, então o site abre em
  `http://localhost:8080/index.html` com o projeto rodando.
- **Os dados ficam no banco.** O JavaScript chama a API Java com `fetch()` (rotas `/api/...`). No navegador
  ficam guardados apenas o carrinho do visitante e uma cópia dos dados do login, usada para ajustar as telas.
- **A regra de verdade está no Java.** Esconder um botão ou redirecionar uma tela no front-end é só conforto
  para quem usa. Quem decide se a ação é permitida é o back-end, pela sessão de login (`ControleDeAcesso.java`).

---

## 2. Estrutura de pastas

```text
static/
├── index.html, busca.html, produto.html ...   (as telas; todas na raiz da pasta)
├── css/
│   └── style.css                  # único arquivo de estilos próprios
├── js/
│   ├── produtos-store.js          # produtos (API /api/produtos)
│   ├── usuario-store.js           # login, conta e sessão
│   ├── vitrine.js                 # código compartilhado por todas as telas
│   ├── formularios-conta.js       # janelas de endereço, cartão e confirmação
│   └── perfil.js                  # formulário antigo de perfil (ver seção 5)
├── imagens/
│   ├── sebo.png                   # logo
│   ├── produtos/                  # fotos enviadas no cadastro de produtos
│   └── usuarios/                  # fotos de perfil
├── data/
│   └── produtos.json              # dados de exemplo antigos (não é mais lido pelo site)
└── README-FRONTEND.md             # este arquivo
```

---

## 3. Telas

Os nomes dos arquivos seguem quem usa a tela: `funcionario-...` é para o funcionário comum (e também para o
administrador), e `administrador-...` é só para o funcionário administrador (`funcionarios.admin = 1`).

### Loja (qualquer pessoa)

| Arquivo | Para que serve |
|---|---|
| `index.html` | Página inicial com carrossel de destaque e produtos |
| `busca.html` | Lista produtos filtrando por categoria (`?categoria=Livros`) e por texto (procura no nome, categoria, gênero e descrição) |
| `produto.html` | Detalhe de um produto |
| `login.html` | Login e cadastro, no mesmo card com painel deslizante |
| `signup.html` | Cadastro em 2 etapas |

### Cliente

| Arquivo | Para que serve |
|---|---|
| `carrinho.html` | Carrinho de compras (pede login) |
| `checkout.html` | Finalizar a compra: endereço, entrega, pagamento e resumo (pede login) |
| `pedido-confirmado.html` | Detalhe do pedido e sua situação |
| `pedidos.html` | Lista de "Meus pedidos", com cancelamento |
| `enderecos.html` | Endereços de entrega (somente cliente) |
| `cartoes.html` | Cartões salvos (somente cliente) |

### Conta (cliente e funcionário)

| Arquivo | Para que serve |
|---|---|
| `menu-conta.html` | Atalhos da conta ("Endereços" e "Cartões" só aparecem para cliente) |
| `perfil.html` | Dados pessoais e foto |
| `seguranca.html` | E-mail, senha, cookies e encerrar a conta (a exclusão só aparece para cliente) |

### Funcionário (comum e administrador)

| Arquivo | Para que serve |
|---|---|
| `funcionario.html` | Painel com os atalhos do funcionário |
| `cadastro-produto.html` | Cadastrar e editar produtos |
| `produtos-cadastrados.html` | Lista de produtos, com edição e remoção |
| `funcionario-pedidos.html` | Pedidos já pagos da loja: avançar a entrega |
| `funcionario-senha.html` | Alterar a senha do funcionário |
| `funcionario-login.html` | Só redireciona para `login.html` (endereço antigo do login) |

### Somente o funcionário administrador

| Arquivo | Para que serve |
|---|---|
| `administrador-funcionarios.html` | Tabela dos funcionários comuns, com inativar e reativar |
| `administrador-dashboard.html` | "Informações do Sebo": números e gráficos de vendas, clientes e estoque |

---

## 4. Perfis e acesso às telas

O site tem quatro situações de uso:

| Perfil | O que pode |
|---|---|
| Visitante | Ver a loja e montar um carrinho temporário |
| Cliente | Comprar, ver pedidos, gerenciar endereços e cartões |
| Funcionário | Cadastrar produtos e cuidar dos pedidos. Não compra, então não tem carrinho nem pedidos próprios |
| Funcionário administrador | Tudo do funcionário, mais gerenciar funcionários e ver a dashboard |

Cada tela protegida chama uma função do `vitrine.js` logo que carrega:

| Função | Quem passa | Quem é desviado |
|---|---|---|
| `exigirLogin()` | Qualquer pessoa logada | Visitante vai para o login |
| `exigirCliente()` | Cliente | Funcionário volta para o painel; visitante vai para o login |
| `exigirAdmin()` | Funcionário (comum ou administrador) | Cliente volta para a loja; visitante vai para o login |
| `exigirAdministrador()` | Funcionário administrador | Funcionário comum volta para `funcionario.html` |

As telas de funcionário começam com `<body hidden>` e só aparecem depois dessa conferência.

Outros pontos que dependem do perfil:

- **Atributos `data-ec-...` no HTML** (a regra fica no `vitrine.js`, num lugar só):
  - `data-ec-logado` / `data-ec-deslogado`: mostra o item só para quem está logado ou deslogado;
  - `data-ec-so-cliente`: some para o funcionário (por exemplo, "Meus pedidos" e o carrinho);
  - `data-ec-so-admin`: aparece só para funcionário (o link para o painel);
  - `data-ec-papel="Painel do {papel}"`: o texto vira "funcionário" ou "administrador" conforme o perfil;
  - `data-ec-badge-carrinho`, `data-ec-avatar`, `data-ec-sair`: bolinha do carrinho, foto do usuário e botão sair.
- Funcionário inativado não consegue mais entrar. O login mostra a mensagem de conta inativa.

---

## 5. JavaScript

### Ordem de carregamento

As telas carregam os scripts nesta ordem, no fim do `<body>`, depois do Bootstrap:

```html
<script src="js/produtos-store.js"></script>
<script src="js/usuario-store.js"></script>
<script src="js/vitrine.js"></script>
<script src="js/formularios-conta.js"></script>   <!-- só onde há endereço, cartão ou confirmação -->
```

O `vitrine.js` depende dos dois stores, e o `formularios-conta.js` depende do `vitrine.js`.

### Arquivos

| Arquivo | O que tem |
|---|---|
| `produtos-store.js` | `ProdutosStore`: listar, buscar, salvar, atualizar e remover produtos pela API. Também completa cada produto com o nome da categoria, o nome do gênero e o ícone, pois a API devolve só os ids |
| `usuario-store.js` | `UsuarioStore`: cadastro, login, saída, troca de senha e e-mail, exclusão da conta. Guarda a sessão em `localStorage` (`sebo_do_joao_sessao`) |
| `vitrine.js` | Máscaras e validações de campos; `CarrinhoStore`, `EnderecosStore`, `CartoesStore` e `PedidosStore`; cabeçalho logado/deslogado; aviso rápido (toast); card de produto; funções `exigir...`; mensagens que somem sozinhas |
| `formularios-conta.js` | Janelas (modais) de endereço, cartão e `confirmarAcao()`, que devolve uma Promise |
| `perfil.js` | Formulário de perfil mais antigo. Só faz algo na tela que tem o formulário `formPerfil` |

### Como funcionam os "stores"

Cada store é um objeto que esconde de onde vêm os dados. As telas só chamam `CarrinhoStore.adicionar(...)` ou
`PedidosStore.listar()`, sem saber se o dado está na API ou no navegador.

- **Carrinho:** o cliente logado usa o carrinho do banco (`/api/carrinho`). O visitante usa o `localStorage`
  (`sebo_do_joao_carrinho`), e ao fazer login esses itens são somados ao carrinho da conta.
- **Sessão:** o login cria a sessão no servidor (cookie). O front guarda uma cópia dos dados em `localStorage`
  só para ajustar as telas. Se a API responder 401, o front encerra a sessão local.

### Mensagens que somem sozinhas

Todo alerta do Bootstrap (`.alert`) que aparece depois da página carregada some com um efeito de fade:
sucesso em 5 segundos e erro ou aviso em 8. Passar o mouse em cima pausa a contagem. Para um alerta ficar fixo,
coloque `data-ec-fixo` nele. Os erros embaixo de cada campo (`.ec-form-erro`) não somem até a pessoa corrigir.

---

## 6. API usada pelo front-end

| Rota | Uso |
|---|---|
| `POST /api/login`, `POST /api/login/sair` | Entrar e sair |
| `/api/clientes`, `/api/funcionarios` | Cadastro e dados da conta |
| `/api/produtos`, `/api/categorias` | Produtos e categorias |
| `/api/carrinho` (e `/api/carrinho/juntar`) | Carrinho do cliente |
| `/api/enderecos`, `/api/cartoes` | Endereços e cartões |
| `/api/compras` | Finalizar compra, listar, cancelar e simular pagamento |
| `/api/compras/gestao` | Pedidos já pagos da loja: avançar a entrega |
| `/api/administracao/funcionarios`, `/api/administracao/painel` | Gerenciamento de funcionários e dashboard |

Os erros voltam como `{"mensagem": "..."}`, e o front mostra essa mensagem na tela.

Quem pode chamar cada rota é conferido no servidor, em `ProtecaoDasRotas.java` e nos controllers:

- produtos, categorias, gêneros e tabelas de apoio: qualquer pessoa consulta, só funcionário altera;
- cadastro de cliente e `GET /api/clientes/email-disponivel`: abertos (é o "Criar conta");
- dados de uma conta: só o dono (ou um funcionário, no caso de cliente); excluir conta: só o dono;
- lista de clientes: funcionário; lista, cadastro e exclusão de funcionários: administrador;
- pedidos, itens, pagamentos e entregas pelo CRUD direto: funcionário consulta e só o administrador altera.

O servidor também repete as validações dos formulários (nome, e-mail, senha, CPF, telefone, produto...) e traduz
os erros do banco (e-mail repetido, excluir algo em uso) para mensagens em português.

---

## 7. CSS

Todos os estilos próprios estão em `css/style.css`, carregado depois do Bootstrap. O arquivo é dividido em
seções numeradas, na ordem em que aparecem:

| Seção | Conteúdo |
|---|---|
| 1 a 7 | Paleta de cores, base e tipografia, cards, ícones, formulários e inputs, botões e rodapé |
| 8 e 11 | Vitrine e página inicial (cabeçalho, carrossel, cards de produto) |
| 9 | Login e cadastro com painel deslizante |
| 10 | Telas de produto do funcionário (cadastro e listagem) |
| 12 | Telas da loja: busca, produto, carrinho, conta e perfil |
| 13 | Validação de formulários (mensagens nos campos) |
| 14 | Área do funcionário: painel, troca de senha e campo de senha com olhinho |
| 15 | Segurança e privacidade |
| 16 | Cadastro em 2 etapas e e-mail somente leitura no perfil |
| 17 | Endereços, cartões, finalizar compra e pedidos |
| Final | Efeito de fade dos alertas |

As telas `administrador-funcionarios.html` e `administrador-dashboard.html` têm um pequeno `<style>` próprio, só
com o que é específico delas, e reaproveitam o resto do `style.css`.

### Paleta

As cores são variáveis da seção 1 (`:root`), com nomes baseados na cor real: `--ec-color-white`, `cream`,
`latte`, `sand`, `taupe`, `mocha` e `coffee`. Também há as variáveis de borda arredondada e de sombra.

---

## 8. Boas práticas para mexer no front-end

1. **Prefixo `ec-` nas classes próprias.** Evita conflito com as classes do Bootstrap.
   Correto: `.ec-btn-outline-primary`. Incorreto: `.btn-custom`.
2. **Cores só por variável.** Não coloque cores hexadecimais soltas no CSS dos componentes; use as variáveis
   do `:root` (por exemplo, `var(--ec-color-coffee)`).
3. **Estilo por classe, não por id.** IDs ficam para o JavaScript (`getElementById`) e para ligar `label` e
   campo.
4. **Responsividade primeiro pelo Bootstrap.** Use `col-md-6`, `d-none`, `d-lg-block` e afins antes de criar
   `@media` próprio.
5. **Regras de perfil num lugar só.** Para esconder algo de cliente ou funcionário, use os atributos
   `data-ec-...` (seção 4) em vez de repetir a conferência em cada tela.
6. **Mensagens de erro vêm da API.** Mostre `erro.message` e não escreva textos que repitam a regra do banco.
7. **Nome de tela nova segue o perfil:** `funcionario-...` para o funcionário e `administrador-...` para o
   administrador.

### Para criar uma tela nova

1. Copie uma tela parecida (cabeçalho, links do Bootstrap e do `style.css` já vêm prontos).
2. Carregue os scripts na ordem da seção 5.
3. Se a tela for protegida, chame a função `exigir...` certa no `DOMContentLoaded` e deixe o `<body hidden>` quando
   for tela de funcionário.
4. Busque os dados pelos stores ou com `fetch('/api/...')`.

---

## 9. Para rodar e testar

1. Suba o SQL Server (Docker) e crie o banco com os scripts de `docs/sql`, na ordem: `SeboDoJoao.sql`,
   `SeboDoJoao_INSERTS.sql`, `SeboDoJoao_FUNCTIONS.sql`, `SeboDoJoao_VIEWS.sql`, `SeboDoJoao_TRIGGERS.sql` e
   `SeboDoJoao_PROCEDURES.sql`.
2. Rode o projeto Java (`./mvnw spring-boot:run`) e abra `http://localhost:8080/index.html`.
3. Depois de mudar um arquivo `.js` ou `.css`, atualize a página com Ctrl+F5 para o navegador não usar o antigo.

---

## 10. Pontos conhecidos

- O hash das senhas (BCrypt) ficou para a etapa de hospedagem. Até lá as senhas ficam em texto no banco e a API
  ainda as devolve nas respostas de cliente e funcionário.
- `data/produtos.json` e o código de "seed" em `produtos-store.js` estão comentados e só ficam como exemplo.
- Os nomes das categorias e dos gêneros estão escritos em `produtos-store.js` e em `cadastro-produto.html`, com os
  mesmos ids da tabela do banco. Ao incluir uma categoria ou gênero novo, atualize os dois lugares.
