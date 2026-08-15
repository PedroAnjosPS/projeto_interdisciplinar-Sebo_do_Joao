# Documentação de Arquitetura CSS - Front-end (E-commerce)

## 📌 Visão Geral
Este documento estabelece o padrão arquitetural para as folhas de estilo (CSS) do projeto. O objetivo desta estrutura é manter o código organizado, escalável e de fácil manutenção para todos os membros da equipe.

Nossa abordagem utiliza o **Bootstrap** como framework base, complementado por estilos customizados estritamente separados por responsabilidade. Isso garante que o trabalho não precise ser refeito e que os componentes possam ser reutilizados em diferentes telas do e-commerce.

---

## 📂 Estrutura de Diretórios

A estrutura de estilos está localizada no diretório `assets/css/` e adota uma metodologia modular (inspirada no modelo 7-1 do SASS e no padrão BEM/SMACSS adaptado):

```text
raiz-do-projeto/
└── assets/
    └── css/
        ├── base/
        │   ├── _variables.css     # (Cores, fontes, sombras padronizadas)
        │   └── _reset.css         # (Configurações globais e body)
        ├── components/
        │   ├── _buttons.css       # (Estilos de botões padronizados)
        │   ├── _cards.css         # (Containers, painéis e cards)
        │   └── _inputs.css        # (Formulários e campos de texto)
        ├── pages/
        │   └── _login.css         # (Estilos EXCLUSIVOS da tela de login)
        └── style.css              # (Arquivo principal de importação/agrupamento)
```

---

## 🏗️ Responsabilidade de Cada Pasta

### 1. `base/`
Contém as regras fundamentais do projeto. Nenhum componente visual específico deve ser criado aqui.
* **O que colocar:** Variáveis de cor (extraídas da identidade visual), tipografia global, variáveis de espaçamento, estilos aplicados diretamente a tags HTML (como `body`, `h1`, `a`).
* **Exemplo:** A definição das cores principais (ex: `--ec-color-accent`) fica nesta pasta.

### 2. `components/`
O coração da nossa padronização. Esta pasta abriga blocos de interface reutilizáveis.
* **O que colocar:** Botões customizados, modais, cards de produtos, barras de navegação, estilos padronizados de formulário.
* **Regra de Ouro:** Se um elemento (como o botão de "Entrar") pode ser usado em outra página (como na tela de "Finalizar Compra"), ele DEVE estar na pasta `components/`.

### 3. `pages/`
Estilos altamente específicos que pertencem a uma única tela e que não farão sentido em nenhum outro lugar do sistema.
* **O que colocar:** Ajustes finos de posicionamento (grids, layouts de uma página específica) ou sobrescritas necessárias apenas para aquela view.
* **Regra de Ouro:** Mantenha os arquivos desta pasta o mais vazios possível. Se um estilo está aqui, pergunte-se: *"Isso não deveria ser um componente?"*

### 4. `style.css` (Root)
Este é o arquivo central que o HTML irá carregar (`<link rel="stylesheet" href="assets/css/style.css">`).
* **Função:** Ele serve apenas para unificar/importar os outros arquivos menores, criando um único pacote de estilos para o navegador.

---

## ✍️ Boas Práticas e Padrões para a Equipe

1. **Prefixo de Classes:**
   Todas as classes customizadas da nossa aplicação devem utilizar o prefixo `ec-` (E-Commerce) para evitar conflitos com as classes nativas do Bootstrap.
   * ❌ *Incorreto:* `.btn-custom { ... }`
   * ✅ *Correto:* `.ec-btn-outline-primary { ... }`

2. **Evite IDs para estilização:**
   Utilize apenas classes (`.minha-classe`) para aplicar estilos. IDs (`#meu-id`) têm um peso de especificidade muito alto e dificultam a sobrescrita e manutenção futura.

3. **Responsividade:**
   Sempre utilize as classes de grid e os breakpoints utilitários do Bootstrap (`col-md-6`, `d-none`, `d-lg-block`) antes de criar `media queries` customizadas no CSS. O CSS customizado deve ser o último recurso.

4. **Uso de Variáveis:**
   Nunca insira cores hexadecimais soltas (ex: `#4D443B`) diretamente nas propriedades CSS dos componentes. Sempre utilize as variáveis declaradas no arquivo `base/` (ex: `var(--ec-color-card-dark-panel)`).

---
*Documentação gerada para padronização técnica da equipe de Front-end.*
