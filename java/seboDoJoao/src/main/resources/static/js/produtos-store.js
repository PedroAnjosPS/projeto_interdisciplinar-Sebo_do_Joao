// ===== INÍCIO: js/produtos-store.js =====
// Camada de acesso a dados dos produtos do Sebo do João.
//
// A IDEIA: nenhuma outra parte do site (a vitrine, o cadastro...)
// deveria precisar saber ONDE os produtos estão guardados. Elas só
// chamam ProdutosStore.listar(), ProdutosStore.salvar(produto), etc.
//
// Hoje isso é salvo no localStorage do navegador. Quando o backend
// em Java + Spring Boot estiver pronto, a ideia é reescrever só as
// funções aqui dentro pra fazerem fetch() na API — o resto do código
// não precisa mudar.
const ProdutosStore = (() => {
    const CHAVE_LOCALSTORAGE = 'sebo_do_joao_produtos';
    // O seed fica em data/produtos.json. O caminho é calculado a partir do
    // lugar deste arquivo (js/vitrine.js), assim não depende da pasta da página.
    /* ATENÇÃO: Os códigos relacionados a seeds (para população e teste da aplicação) 
    estarão comentados quando o backend estiver pronto, mas não serão apagados, porque podem ser úteis para testes. */
    // const URL_SEED = new URL('data/produtos.json', CAMINHO_DESTE_SCRIPT).href;

    // URL base que aponta para o ProdutoRestController no Spring Boot.
    const URL_API = '/api/produtos';

    // ----- Nomes de categoria/gênero e ícone (a API devolve só os ids) -----
    // As telas da vitrine (busca, produto, carrinho, cards...) mostram o NOME da categoria
    // e do gênero e filtram por ele; a API manda só categoriaId/generoId. Por isso cada produto
    // que sai deste Store ganha também "categoria", "genero" e "icone" (os campos que a API
    // mandou continuam todos lá: nome, categoriaId, generoId, status "DISPONIVEL"/"INATIVO"...).
    // Os ids são os mesmos das <option> de cadastro-produto.html (e da tabela do banco).
    const NOMES_CATEGORIAS = {
        1: 'Livros', 2: 'HQs', 3: 'Discos de Vinil', 4: 'Blu-rays',
        5: 'Dvds', 6: 'Cds', 7: 'Fitas Cassetes'
    };
    const ICONES_CATEGORIAS = {
        1: 'fa-book-open', 2: 'fa-mask', 3: 'fa-record-vinyl', 4: 'fa-compact-disc',
        5: 'fa-film', 6: 'fa-compact-disc', 7: 'fa-cassette-tape'
    };
    const NOMES_GENEROS = {
        1: 'Ficção Científica', 2: 'Terror', 3: 'Suspense', 4: 'Rock', 5: 'Heavy Metal',
        6: 'Ação', 7: 'Aventura', 8: 'Rap', 9: 'Didático', 10: 'Biografia'
    };

    function completarProduto(produto) {
        if (!produto || typeof produto !== 'object') return produto;
        return {
            ...produto,
            categoria: NOMES_CATEGORIAS[produto.categoriaId] || '',
            genero: NOMES_GENEROS[produto.generoId] || '',
            icone: ICONES_CATEGORIAS[produto.categoriaId] || 'fa-box',
            preco: Number(produto.preco) || 0
        };
    }

    //let seedGarantido = null;

    /* function garantirSeed() {
        if (seedGarantido) return seedGarantido;

        seedGarantido = (async () => {
            const jaTemDados = localStorage.getItem(CHAVE_LOCALSTORAGE);
            if (jaTemDados) return;

            try {
                const resposta = await fetch(URL_SEED);
                if (!resposta.ok) throw new Error('produtos.json não encontrado');
                const produtosIniciais = await resposta.json();
                localStorage.setItem(CHAVE_LOCALSTORAGE, JSON.stringify(produtosIniciais));
            } catch (erro) {
                console.warn('Não achei o produtos.json original, começando com uma lista vazia.', erro);
                localStorage.setItem(CHAVE_LOCALSTORAGE, JSON.stringify([]));
            }
        })();

        return seedGarantido;
    } */

    // 1-) BUsca todos os produtos (Conecta com o @GetMapping)
    async function listar() {
        const resposta = await fetch(URL_API);
        if (!resposta.ok) throw new Error('Erro ao carregar produtos');
        
        return (await resposta.json()).map(completarProduto);
    }

    // 2-) Busca um produto pelo id (Conecta com @GetMapping("/{id}"))
    async function obterPorId(id) {
        const resposta = await fetch(`${URL_API}/${id}`);
        if (!resposta.ok) return null;

        return completarProduto(await resposta.json());
    }

    // 3-) Cadastra um novo produto (Conecta com @PostMapping)
    // 3-) Cadastra um novo produto (Conecta com @PostMapping)
    async function salvar(dadosProduto) {
        const opcoesFetch = {
            method: 'POST'
        };

        if (dadosProduto instanceof FormData) {
            opcoesFetch.body = dadosProduto;
        } else {
            opcoesFetch.headers = { 'Content-Type': 'application/json' };
            opcoesFetch.body = JSON.stringify(dadosProduto);
        }

        const resposta = await fetch(URL_API, opcoesFetch);

        if (!resposta.ok) throw new Error('Erro ao tentar salvar o produto');

        return await resposta.json();
    }

    // 4-) Atualiza um produto já existente no banco
    async function atualizar(id, dadosAtualizados) {
        const opcoesFetch = {
            // MUDANÇA: Usa POST se tiver arquivo, senão PUT
            method: (dadosAtualizados instanceof FormData) ? 'POST' : 'PUT'
        };

        if (dadosAtualizados instanceof FormData) {
            opcoesFetch.body = dadosAtualizados;
        } else {
            opcoesFetch.headers = { 'Content-Type': 'application/json' };
            opcoesFetch.body = JSON.stringify(dadosAtualizados);
        }

        const resposta = await fetch(`${URL_API}/${id}`, opcoesFetch);

        if (!resposta.ok) throw new Error('Erro ao tentar atualizar o produto');

        return await resposta.json();
    }

    // 5-) Deleta um produto do banco (Conecta com @DeleteMapping("/{id}"))
    async function remover(id) {
        const resposta = await fetch(`${URL_API}/${id}`, {
            method: 'DELETE'
        });

        if (!resposta.ok) throw new Error('Erro ao tentar remover o produto');
    }


    // Demais códigos anteriores relacionados a localStorage e seed, que não serão mais utilizados quando o backend estiver pronto, estão comentados abaixo: 
    /* function lerTudo() {
        return JSON.parse(localStorage.getItem(CHAVE_LOCALSTORAGE) || '[]');
    }

    function salvarTudo(listaDeProdutos) {
        try {
            localStorage.setItem(CHAVE_LOCALSTORAGE, JSON.stringify(listaDeProdutos));
        } catch (erro) {
            if (erro.name === 'QuotaExceededError' || erro.code === 22) {
                throw new Error('O navegador recusou salvar: os dados ficaram grandes demais pro localStorage (normalmente é a imagem). Tente uma imagem menor ou remova produtos antigos.');
            }
            throw erro;
        }
    }

    async function listar() {
        await garantirSeed();
        return lerTudo();
    }

    async function obterPorId(id) {
        const produtos = await listar();
        return produtos.find((produto) => String(produto.id) === String(id));
    }

    function calcularProximoId(produtos) {
        return produtos.length
            ? Math.max(...produtos.map((produto) => Number(produto.id) || 0)) + 1
            : 1;
    }

    // Mostra pra tela de cadastro qual vai ser o próximo id, antes
    // mesmo de salvar (só pra exibição — quem realmente define o id
    // definitivo é a função salvar() lá embaixo).
    async function proximoId() {
        await garantirSeed();
        return calcularProximoId(lerTudo());
    }

    async function salvar(dadosDoProduto) {
        await garantirSeed();
        const produtos = lerTudo();

        const novoProduto = {id: calcularProximoId(produtos), ...dadosDoProduto};
        produtos.push(novoProduto);
        salvarTudo(produtos);

        return novoProduto;
    }

    async function atualizar(id, dadosAtualizados) {
        const produtos = lerTudo();
        const indice = produtos.findIndex((produto) => String(produto.id) === String(id));
        if (indice === -1) return null;

        produtos[indice] = {...produtos[indice], ...dadosAtualizados, id: produtos[indice].id};
        salvarTudo(produtos);

        return produtos[indice];
    }

    async function remover(id) {
        const produtos = lerTudo().filter((produto) => String(produto.id) !== String(id));
        salvarTudo(produtos);
    }
 */

    // Expõe as funções para que o resto do site possa chamá-las
    return {listar, obterPorId, salvar, atualizar, remover, NOMES_CATEGORIAS};
})();
// ===== FIM: js/produtos-store.js =====