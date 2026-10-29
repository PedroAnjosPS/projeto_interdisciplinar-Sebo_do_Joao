// =====================================================================
// vitrine.js — código compartilhado de todas as telas do Sebo do João
//
// Antes cada .html trazia uma cópia disto dentro de um <script>. Agora fica
// tudo aqui e as telas só fazem:
//     <script src="../../js/vitrine.js"></script>
// (o caminho é relativo à pasta assets/css/pages/, onde ficam os .html)
//
// O que tem aqui:
//   1. Máscaras e validações (CPF, telefone, nome, e-mail, data de nascimento)
//   2. ProdutosStore  — produtos (cadastro, vitrine, busca...)
//   3. CarrinhoStore  — carrinho de compras
//   4. UsuarioStore   — contas, login, perfil (CLIENTE ou ADMIN) e troca de senha
//   5. Funções de apoio (preço, badge do carrinho, cabeçalho logado/deslogado)
//   6. Aviso rápido (toast) e card de produto
//   7. Área do administrador (exigirAdmin, sair, mostrar senha)
//
// Hoje os dados ficam no localStorage do navegador. Quando o backend
// Java + Spring Boot (SQL Server) estiver pronto, só as funções dos "Stores"
// precisam virar chamadas fetch() à API — as telas continuam iguais.
// =====================================================================

// Lugar deste arquivo na URL (precisa ser lido aqui no topo, enquanto o
// script está sendo carregado) — usado pra achar o assets/data/produtos.json
const CAMINHO_DESTE_SCRIPT = document.currentScript
    ? document.currentScript.src
    : new URL('js/vitrine.js', location.href).href;

// ===== INÍCIO: mascaras-e-validacoes =====
// Máscaras (formatam enquanto a pessoa digita) e validações dos campos do perfil.
// Ficam aqui, e não dentro de uma tela, porque a mesma regra vale pra qualquer
// formulário (perfil, cadastro, finalizar compra...) — e o backend Java vai
// precisar repetir essas mesmas regras (ex.: Bean Validation no Spring Boot).
//
// Padrão: cada função "erroDo..." recebe o texto do campo e devolve a mensagem
// de erro, ou '' (texto vazio) quando está tudo certo.

function somenteDigitos(texto) {
    return String(texto ?? '').replace(/\D/g, '');
}

// "12345678909" -> "123.456.789-09" (vai montando conforme digita)
function formatarCpf(texto) {
    const d = somenteDigitos(texto).slice(0, 11);
    if (d.length <= 3) return d;
    if (d.length <= 6) return `${d.slice(0, 3)}.${d.slice(3)}`;
    if (d.length <= 9) return `${d.slice(0, 3)}.${d.slice(3, 6)}.${d.slice(6)}`;
    return `${d.slice(0, 3)}.${d.slice(3, 6)}.${d.slice(6, 9)}-${d.slice(9)}`;
}

// "17999990000" -> "(17) 99999-0000"  |  "1733334444" -> "(17) 3333-4444"
function formatarTelefone(texto) {
    let d = somenteDigitos(texto);
    // Número colado com o código do país (+55 17 99999-0000)
    if (d.length > 11 && d.startsWith('55')) d = d.slice(2);
    d = d.slice(0, 11);

    if (d.length === 0) return '';
    if (d.length <= 2) return `(${d}`;

    const ddd = d.slice(0, 2);
    const numero = d.slice(2);
    // Celular (11 dígitos) = 5 + 4. Fixo (10 dígitos) = 4 + 4.
    // Enquanto ainda está digitando, usa 4 + resto.
    const corte = d.length === 11 ? 5 : 4;
    return numero.length <= corte
        ? `(${ddd}) ${numero}`
        : `(${ddd}) ${numero.slice(0, corte)}-${numero.slice(corte)}`;
}

// Liga a máscara num <input>: reformata a cada tecla e mantém o cursor no lugar
// certo (senão, ao editar o meio do número, o cursor pularia pro final).
function ligarMascara(input, formatar) {
    input.addEventListener('input', () => {
        const digitosAntesDoCursor = somenteDigitos(input.value.slice(0, input.selectionStart)).length;
        const formatado = formatar(input.value);
        input.value = formatado;

        let posicao = formatado.length;
        if (digitosAntesDoCursor === 0) {
            posicao = 0;
        } else {
            let contados = 0;
            for (let i = 0; i < formatado.length; i++) {
                if (/\d/.test(formatado[i])) contados++;
                if (contados === digitosAntesDoCursor) { posicao = i + 1; break; }
            }
        }
        input.setSelectionRange(posicao, posicao);
    });
}

// CPF: 11 números e os 2 dígitos verificadores corretos
function cpfValido(texto) {
    const d = somenteDigitos(texto);
    if (d.length !== 11 || /^(\d)\1{10}$/.test(d)) return false; // 111.111.111-11 etc. não existem

    const digitoVerificador = (quantidade) => {
        let soma = 0;
        for (let i = 0; i < quantidade; i++) soma += Number(d[i]) * (quantidade + 1 - i);
        const resto = (soma * 10) % 11;
        return resto === 10 ? 0 : resto;
    };
    return digitoVerificador(9) === Number(d[9]) && digitoVerificador(10) === Number(d[10]);
}

function erroDoCpf(texto) {
    const d = somenteDigitos(texto);
    if (!d) return ''; // campo opcional
    if (d.length < 11) return 'CPF incompleto: são 11 números.';
    return cpfValido(d) ? '' : 'CPF inválido. Confira os números digitados.';
}

// DDDs que existem no Brasil
const DDDS_VALIDOS = new Set([
    11, 12, 13, 14, 15, 16, 17, 18, 19, 21, 22, 24, 27, 28, 31, 32, 33, 34, 35, 37, 38,
    41, 42, 43, 44, 45, 46, 47, 48, 49, 51, 53, 54, 55, 61, 62, 63, 64, 65, 66, 67, 68, 69,
    71, 73, 74, 75, 77, 79, 81, 82, 83, 84, 85, 86, 87, 88, 89, 91, 92, 93, 94, 95, 96, 97, 98, 99
]);

function erroDoTelefone(texto) {
    let d = somenteDigitos(texto);
    if (!d) return ''; // campo opcional
    if (d.length > 11 && d.startsWith('55')) d = d.slice(2);

    if (d.length < 10) return 'Telefone incompleto. Digite o DDD e o número, ex.: (17) 99999-0000.';
    if (d.length > 11) return 'Telefone com números demais.';
    if (!DDDS_VALIDOS.has(Number(d.slice(0, 2)))) return 'DDD inválido.';
    if (d.length === 11 && d[2] !== '9') return 'Celular precisa começar com 9 depois do DDD.';
    if (d.length === 10 && !/[2-5]/.test(d[2])) return 'Número de telefone fixo inválido.';
    return '';
}

function erroDoNome(texto) {
    const nome = String(texto ?? '').trim();
    if (!nome) return 'Informe o seu nome.';
    if (nome.length < 2) return 'O nome precisa ter pelo menos 2 letras.';
    if (!/\p{L}/u.test(nome)) return 'O nome precisa ter letras.';
    return '';
}

function erroDoEmail(texto) {
    const email = String(texto ?? '').trim();
    if (!email) return 'Informe o seu e-mail.';
    return /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(email) ? '' : 'E-mail inválido. Ex.: nome@exemplo.com';
}

// Data no formato AAAA-MM-DD (o mesmo do <input type="date">)
function erroDaDataNascimento(dataISO) {
    if (!dataISO) return ''; // campo opcional
    const partes = /^(\d{4})-(\d{2})-(\d{2})$/.exec(dataISO);
    if (!partes) return 'Data inválida.';

    const [ano, mes, dia] = partes.slice(1).map(Number);
    const data = new Date(ano, mes - 1, dia);
    const existe = data.getFullYear() === ano && data.getMonth() === mes - 1 && data.getDate() === dia;
    if (!existe) return 'Essa data não existe.';
    if (ano < 1900) return 'Data muito antiga. Confira o ano.';
    if (data > new Date()) return 'A data de nascimento não pode ser no futuro.';
    return '';
}
// Senha nova (troca de senha)
const TAMANHO_MINIMO_SENHA = 8;

function erroDaSenhaNova(senha) {
    if (!senha) return 'Digite a nova senha.';
    if (senha.length < TAMANHO_MINIMO_SENHA) {
        return `A senha precisa ter pelo menos ${TAMANHO_MINIMO_SENHA} caracteres.`;
    }
    if (/^\s|\s$/.test(senha)) return 'A senha não pode começar nem terminar com espaço.';
    return '';
}
// ===== FIM: mascaras-e-validacoes =====



// ===== INÍCIO: carrinho-store =====
// Camada de acesso ao carrinho de compras (mesma ideia do ProdutosStore).
//
// Só guardamos { id, quantidade } de cada item. Nome, preço e imagem vêm
// do ProdutosStore na hora de mostrar a tela, assim o preço nunca fica
// desatualizado. Hoje fica no localStorage; com o backend Java + Spring
// Boot essas funções viram chamadas à API e as telas não precisam mudar.
const CarrinhoStore = (() => {
    const CHAVE_LOCALSTORAGE = 'sebo_do_joao_carrinho';

    function lerTudo() {
        try {
            const itens = JSON.parse(localStorage.getItem(CHAVE_LOCALSTORAGE) || '[]');
            return Array.isArray(itens) ? itens : [];
        } catch (erro) {
            return [];
        }
    }

    function salvarTudo(itens) {
        localStorage.setItem(CHAVE_LOCALSTORAGE, JSON.stringify(itens));
    }

    // Quantas unidades dá pra comprar no máximo. Produtos antigos, sem o
    // campo "estoque", ficam sem limite.
    function limiteDeEstoque(produto) {
        return typeof produto.estoque === 'number' ? produto.estoque : Infinity;
    }

    // Só dá pra comprar produto "DISPONIVEL" (ou sem status) e com estoque
    function disponivel(produto) {
        const statusOk = !produto.status || produto.status === 'DISPONIVEL';
        return statusOk && limiteDeEstoque(produto) > 0;
    }

    async function listar() {
        return lerTudo();
    }

    async function contarItens() {
        return lerTudo().reduce((total, item) => total + (Number(item.quantidade) || 0), 0);
    }

    async function adicionar(produto, quantidade = 1) {
        if (!disponivel(produto)) return { ok: false, motivo: 'indisponivel' };

        const itens = lerTudo();
        const item = itens.find((i) => String(i.id) === String(produto.id));
        const quantidadeAtual = item ? Number(item.quantidade) || 0 : 0;

        if (quantidadeAtual + quantidade > limiteDeEstoque(produto)) {
            return { ok: false, motivo: 'estoque' };
        }

        if (item) {
            item.quantidade = quantidadeAtual + quantidade;
        } else {
            itens.push({ id: produto.id, quantidade });
        }

        salvarTudo(itens);
        return { ok: true };
    }

    async function definirQuantidade(id, quantidade) {
        const itens = lerTudo();
        const item = itens.find((i) => String(i.id) === String(id));
        if (!item) return;

        item.quantidade = Math.max(1, Math.floor(quantidade));
        salvarTudo(itens);
    }

    async function remover(id) {
        salvarTudo(lerTudo().filter((i) => String(i.id) !== String(id)));
    }

    async function limpar() {
        salvarTudo([]);
    }

    return {
        CHAVE_LOCALSTORAGE, listar, contarItens, adicionar,
        definirQuantidade, remover, limpar, disponivel, limiteDeEstoque
    };
})();
// ===== FIM: carrinho-store =====

// ===== INÍCIO: seguranca =====
// Guardar senha: nunca a senha em si, só um "resumo" (hash SHA-256) misturado com
// um sal aleatório. É o que o UsuarioStore usa (clientes e administrador).
// DEMONSTRAÇÃO: com o backend Java + Spring Boot isso vira BCrypt no servidor.
function garantirCriptografia() {
    if (!window.crypto || !window.crypto.subtle) {
        throw new Error('Seu navegador não deixa guardar a senha com segurança nesse endereço. Abra o site por http://localhost ou https.');
    }
}

function gerarSal() {
    garantirCriptografia();
    const bytes = crypto.getRandomValues(new Uint8Array(16));
    return Array.from(bytes).map((byte) => byte.toString(16).padStart(2, '0')).join('');
}

async function gerarHash(senha, sal) {
    garantirCriptografia();
    const resumo = await crypto.subtle.digest('SHA-256', new TextEncoder().encode(`${sal}:${senha}`));
    return Array.from(new Uint8Array(resumo)).map((byte) => byte.toString(16).padStart(2, '0')).join('');
}
// ===== FIM: seguranca =====


// ===== Funções de apoio (usadas em várias telas) =====

// Evita que um nome de produto com "<" ou aspas quebre o HTML da tela
function escaparHtml(texto) {
    const substituicoes = { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' };
    return String(texto ?? '').replace(/[&<>"']/g, (caractere) => substituicoes[caractere]);
}

function formatarPreco(valor) {
    return (Number(valor) || 0).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
}

// Bolinha com a quantidade de itens em cima do ícone do carrinho
async function atualizarBadgeCarrinho() {
    const total = await CarrinhoStore.contarItens();
    document.querySelectorAll('[data-ec-badge-carrinho]').forEach((badge) => {
        badge.textContent = total > 99 ? '99+' : total;
        badge.classList.toggle('d-none', total === 0);
    });
}

// Se o carrinho mudar em outra aba, a bolinha acompanha
window.addEventListener('storage', (evento) => {
    if (evento.key === CarrinhoStore.CHAVE_LOCALSTORAGE) atualizarBadgeCarrinho();
});

// ===== Conta no cabeçalho e login =====

// Mostra no cabeçalho "Fazer Login" ou o menu da conta (com a foto do usuário,
// se ele tiver uma), conforme a pessoa esteja logada ou não
async function atualizarCabecalhoConta() {
    const usuario = await UsuarioStore.obter();

    document.querySelectorAll('[data-ec-deslogado]').forEach((elemento) => {
        elemento.classList.toggle('d-none', Boolean(usuario));
    });
    document.querySelectorAll('[data-ec-logado]').forEach((elemento) => {
        elemento.classList.toggle('d-none', !usuario);
    });
    // Itens que só o administrador vê (ex.: "Painel do administrador" no menu da conta)
    document.querySelectorAll('[data-ec-so-admin]').forEach((elemento) => {
        elemento.classList.toggle('d-none', !UsuarioStore.ehAdmin(usuario));
    });
    document.querySelectorAll('[data-ec-avatar]').forEach((elemento) => {
        elemento.innerHTML = usuario && usuario.fotoPerfil
            ? `<img src="${escaparHtml(usuario.fotoPerfil)}" alt="Foto de ${escaparHtml(usuario.nome)}" class="ec-header-avatar-img">`
            : '<i class="fas fa-user-circle fs-3"></i>';
    });
}

// Telas de conta só abrem com login: se não tiver, manda pro login e, depois
// de entrar, a pessoa volta pra cá. Devolve o usuário (ou null se redirecionou).
async function exigirLogin() {
    const usuario = await UsuarioStore.obter();
    if (usuario) return usuario;

    const paginaAtual = location.pathname.split('/').pop() + location.search;
    location.replace('login.html?voltar=' + encodeURIComponent(paginaAtual));
    return null;
}

// Botão "Sair" do menu da conta
document.addEventListener('click', (evento) => {
    if (!evento.target.closest('[data-ec-sair]')) return;
    evento.preventDefault();
    UsuarioStore.sair();
    location.href = 'index.html';
});

// Se entrar ou sair em outra aba, o cabeçalho acompanha
window.addEventListener('storage', (evento) => {
    if (UsuarioStore.CHAVES.includes(evento.key)) atualizarCabecalhoConta();
});

// Aviso rápido (toast) no canto inferior esquerdo
// (só funciona nas telas que têm o HTML do toast, o #avisoToast)
let avisoToast = null;

function avisar(texto, mostrarLinkCarrinho) {
    const elementoToast = document.getElementById('avisoToast');
    if (!elementoToast) return;
    if (!avisoToast) avisoToast = new bootstrap.Toast(elementoToast, { delay: 3500 });

    document.getElementById('avisoToastTexto').textContent = texto;
    document.getElementById('avisoToastLink').classList.toggle('d-none', !mostrarLinkCarrinho);
    avisoToast.show();
}

// Adiciona 1 unidade do produto ao carrinho e avisa o resultado.
// Devolve true se deu certo.
async function comprarProduto(id) {
    const produto = await ProdutosStore.obterPorId(id);
    if (!produto) {
        avisar('Esse produto não está mais disponível.', false);
        return false;
    }

    const resultado = await CarrinhoStore.adicionar(produto, 1);
    if (resultado.ok) {
        avisar(`"${produto.titulo}" foi adicionado ao carrinho.`, true);
        atualizarBadgeCarrinho();
        return true;
    }

    if (resultado.motivo === 'estoque') {
        avisar('Você já tem todas as unidades disponíveis desse produto no carrinho.', true);
    } else {
        avisar('Esse produto está indisponível no momento.', false);
    }
    return false;
}

// Um único "ouvinte" no container cuida de todos os botões Comprar dos cards
function ligarBotoesComprar(container) {
    container.addEventListener('click', (evento) => {
        const botao = evento.target.closest('[data-comprar]');
        if (botao) comprarProduto(botao.dataset.comprar);
    });
}

// Card de produto da vitrine/busca (mesmo visual do index.html)
function montarCardProduto(produto) {
    const titulo = escaparHtml(produto.nome);
    const link = `produto.html?id=${encodeURIComponent(produto.id)}`;
    const disponivel = CarrinhoStore.disponivel(produto);

    const conteudoImagem = produto.imagem
        ? `<img src="${escaparHtml(produto.imagem)}" alt="${titulo}" class="w-100 h-100 object-fit-cover">`
        : `<i class="fas ${escaparHtml(produto.icone || 'fa-box')} fa-4x opacity-50"></i>`;

    const botao = disponivel
        ? `<button type="button" class="btn ec-btn-outline-primary ec-btn-pill w-100" data-comprar="${escaparHtml(produto.id)}">
                <i class="fas fa-cart-plus me-2"></i>Comprar
           </button>`
        : `<button type="button" class="btn ec-btn-outline-primary ec-btn-pill w-100" disabled>Esgotado</button>`;

    const selo = disponivel
        ? ''
        : '<span class="position-absolute top-0 end-0 m-3 pe-none ec-admin-status-badge ec-admin-status-esgotado">Esgotado</span>';

    return `
        <div class="col">
            <div class="ec-card ec-product-card h-100 d-flex flex-column ec-card-shadow">
                <div class="ec-product-img-wrapper position-relative">
                    <a href="${link}" class="ec-product-link" aria-label="Ver ${titulo}">
                        <div class="ec-product-placeholder">
                            ${conteudoImagem}
                        </div>
                    </a>
                    <span class="position-absolute top-0 start-0 m-3 pe-none badge bg-dark rounded-pill">${escaparHtml(produto.categoria || '')}</span>
                    ${selo}
                </div>
                <div class="ec-product-body d-flex flex-column flex-grow-1 p-4">
                    <h5 class="ec-product-title mb-2"><a href="${link}" class="ec-product-link">${titulo}</a></h5>
                    <div class="mt-auto">
                        <p class="ec-product-price mb-3">${formatarPreco(produto.preco)}</p>
                        ${botao}
                    </div>
                </div>
            </div>
        </div>
    `;
}


// ===== Área do administrador =====

// Telas do administrador só abrem pra conta com perfil ADMIN:
//  - sem login        -> vai pro login e volta pra cá depois de entrar
//  - cliente comum    -> volta pra loja
// As telas começam com <body hidden> e só aparecem depois dessa conferência.
// Devolve true se pode ficar.
async function exigirAdmin() {
    const usuario = await UsuarioStore.obter();

    if (UsuarioStore.ehAdmin(usuario)) {
        document.body.hidden = false;
        return true;
    }

    if (usuario) {
        location.replace('index.html');
    } else {
        const paginaAtual = location.pathname.split('/').pop() + location.search;
        location.replace('login.html?voltar=' + encodeURIComponent(paginaAtual));
    }
    return false;
}

// Botão "Sair" do cabeçalho do administrador
document.addEventListener('click', (evento) => {
    if (!evento.target.closest('[data-ec-sair-admin]')) return;
    evento.preventDefault();
    UsuarioStore.sair();
    location.href = 'login.html';
});

// Botão de olhinho: <button data-ec-mostrar-senha="id-do-input"> alterna entre
// esconder e mostrar a senha digitada
document.addEventListener('click', (evento) => {
    const botao = evento.target.closest('[data-ec-mostrar-senha]');
    if (!botao) return;

    const campo = document.getElementById(botao.dataset.ecMostrarSenha);
    const mostrar = campo.type === 'password';
    campo.type = mostrar ? 'text' : 'password';
    botao.setAttribute('aria-label', mostrar ? 'Esconder senha' : 'Mostrar senha');
    botao.querySelector('i').className = mostrar ? 'fas fa-eye-slash' : 'fas fa-eye';
});

// Se a conta entrar ou sair em outra aba, as telas do administrador acompanham
window.addEventListener('storage', async (evento) => {
    if (document.body.dataset.ecAdmin === undefined || !UsuarioStore.CHAVES.includes(evento.key)) return;
    if (!UsuarioStore.ehAdmin(await UsuarioStore.obter())) location.reload();
});
