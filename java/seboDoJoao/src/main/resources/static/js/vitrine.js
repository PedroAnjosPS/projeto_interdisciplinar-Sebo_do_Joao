// =====================================================================
// vitrine.js — código compartilhado de todas as telas do Sebo do João
//
// As telas carregam, nesta ordem:
//     <script src="js/produtos-store.js"></script>   (produtos, via API Java)
//     <script src="js/usuario-store.js"></script>    (login, conta e perfil, via API Java)
//     <script src="js/vitrine.js"></script>          (este arquivo)
//     <script src="js/formularios-conta.js"></script> (só as telas de conta/compra)
//
// O que tem aqui:
//   1. Máscaras e validações (CPF, telefone, nome, e-mail, nascimento, CEP, endereço, cartão)
//   2. CarrinhoStore  — carrinho de compras (no banco para cliente logado; no navegador para visitante)
//   3. EnderecosStore, CartoesStore e PedidosStore — endereços, cartões e pedidos da conta
//      (ALTERADO: agora todos conversam com a API Java e ficam gravados no banco de dados)
//   4. Funções de apoio (preço, badge do carrinho, cabeçalho logado/deslogado)
//   5. Aviso rápido (toast) e card de produto
//   6. Área do administrador (exigirAdmin, sair, mostrar senha)
// =====================================================================

// Lugar deste arquivo na URL (precisa ser lido aqui no topo, enquanto o
// script está sendo carregado) — usado pra achar o assets/data/produtos.json
const CAMINHO_DESTE_SCRIPT = document.currentScript
    ? document.currentScript.src
    : new URL('../../js/vitrine.js', location.href).href;

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
        const cursorNoFim = input.selectionStart === input.value.length;
        const digitosAntesDoCursor = somenteDigitos(input.value.slice(0, input.selectionStart)).length;
        const formatado = formatar(input.value);
        input.value = formatado;

        let posicao = formatado.length;
        if (cursorNoFim) {
            // digitando no fim: o cursor fica no fim (a máscara pode ter acrescentado "0", "/", " "...)
        } else if (digitosAntesDoCursor === 0) {
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
// ----- Endereço -----

// "17500000" -> "17500-000"
function formatarCep(texto) {
    const d = somenteDigitos(texto).slice(0, 8);
    return d.length > 5 ? `${d.slice(0, 5)}-${d.slice(5)}` : d;
}

function erroDoCep(texto) {
    const d = somenteDigitos(texto);
    if (!d) return 'Informe o CEP.';
    if (d.length < 8) return 'CEP incompleto: são 8 números.';
    if (/^(\d)\1{7}$/.test(d)) return 'CEP inválido.';
    return '';
}

const UFS = [
    'AC', 'AL', 'AP', 'AM', 'BA', 'CE', 'DF', 'ES', 'GO', 'MA', 'MT', 'MS', 'MG', 'PA', 'PB', 'PR', 'PE',
    'PI', 'RJ', 'RN', 'RS', 'RO', 'RR', 'SC', 'SP', 'SE', 'TO'
];

// Confere o endereço inteiro e devolve { campo: mensagem } só com os campos que têm erro
// (objeto vazio = tudo certo). Quem usa mostra cada mensagem embaixo do seu campo.
function errosDoEndereco(e) {
    const erros = {};
    const texto = (valor) => String(valor ?? '').trim();
    const obrigatorio = (campo, mensagem, max = 100, min = 2) => {
        const valor = texto(e[campo]);
        if (!valor) erros[campo] = mensagem;
        else if (valor.length < min) erros[campo] = 'Esse campo está muito curto.';
        else if (valor.length > max) erros[campo] = `Use no máximo ${max} caracteres.`;
    };

    if (texto(e.apelido).length > 30) erros.apelido = 'Use no máximo 30 caracteres.';
    // ALTERADO: a conferência de "destinatário" e "telefone" foi removida: o endereço não tem mais esses campos
    // (quem recebe é a pessoa da conta, com o nome e o telefone do cadastro).

    const erroCep = erroDoCep(e.cep);
    if (erroCep) erros.cep = erroCep;

    obrigatorio('rua', 'Informe a rua ou avenida.');
    if (!e.semNumero) obrigatorio('numero', 'Informe o número (ou marque "Sem número").', 10, 1);
    if (texto(e.complemento).length > 60) erros.complemento = 'Use no máximo 60 caracteres.';
    obrigatorio('bairro', 'Informe o bairro.');
    obrigatorio('cidade', 'Informe a cidade.');
    if (!UFS.includes(texto(e.uf).toUpperCase())) erros.uf = 'Escolha o estado.';
    return erros;
}

// "Rua X, 123 - Apto 4" / "Bairro - Cidade/UF" / "CEP 00000-000" (pra mostrar o endereço em texto)
function linhasDoEndereco(e) {
    const numero = e.semNumero || !e.numero ? 's/n' : e.numero;
    const primeira = `${e.rua}, ${numero}${e.complemento ? ' - ' + e.complemento : ''}`;
    return [primeira, `${e.bairro} - ${e.cidade}/${e.uf}`, `CEP ${e.cep}`];
}

// ----- Cartão -----
// ATENÇÃO: o número completo e o CVV NUNCA são guardados. Aqui só tem máscara e
// conferência; quem guarda (CartoesStore) fica só com bandeira + últimos 4 dígitos.
// No backend de verdade o pagamento passa por um gateway (Mercado Pago, Stripe...),
// que devolve um "token" no lugar do número do cartão.

function bandeiraDoCartao(texto) {
    const d = somenteDigitos(texto);
    if (!d) return '';
    if (/^(4011|4312|4389|4514|4576|5041|5066|5067|509\d|6277|6362|6363|650\d|6516|6550)/.test(d)) return 'elo';
    if (/^(606282|3841)/.test(d)) return 'hipercard';
    if (/^3[47]/.test(d)) return 'amex';
    if (/^3(0[0-5]|[68])/.test(d)) return 'diners';
    if (/^4/.test(d)) return 'visa';
    if (/^(5[1-5]|2(22[1-9]|2[3-9]\d|[3-6]\d\d|7[01]\d|720))/.test(d)) return 'mastercard';
    return '';
}

const NOMES_DAS_BANDEIRAS = {
    visa: 'Visa', mastercard: 'Mastercard', amex: 'American Express', elo: 'Elo',
    hipercard: 'Hipercard', diners: 'Diners Club', '': 'Cartão'
};

// "4111111111111111" -> "4111 1111 1111 1111"  (Amex: 4-6-5)
function formatarNumeroCartao(texto) {
    const bandeira = bandeiraDoCartao(texto);
    const d = somenteDigitos(texto).slice(0, bandeira === 'amex' ? 15 : 16);
    if (bandeira === 'amex') {
        return [d.slice(0, 4), d.slice(4, 10), d.slice(10)].filter(Boolean).join(' ');
    }
    return (d.match(/.{1,4}/g) || []).join(' ');
}

// Confere o último dígito do número (algoritmo de Luhn, usado por todos os cartões)
function cartaoValidoPorLuhn(texto) {
    const d = somenteDigitos(texto);
    let soma = 0;
    for (let i = 0; i < d.length; i++) {
        let digito = Number(d[d.length - 1 - i]);
        if (i % 2 === 1) {
            digito *= 2;
            if (digito > 9) digito -= 9;
        }
        soma += digito;
    }
    return d.length > 0 && soma % 10 === 0;
}

function erroDoNumeroCartao(texto) {
    const d = somenteDigitos(texto);
    if (!d) return 'Informe o número do cartão.';
    const tamanho = bandeiraDoCartao(d) === 'amex' ? 15 : 16;
    if (d.length < tamanho) return 'Número do cartão incompleto.';
    return cartaoValidoPorLuhn(d) ? '' : 'Número de cartão inválido. Confira os números.';
}

// "1" -> "01/"  |  "1228" -> "12/28"
function formatarValidade(texto) {
    let d = somenteDigitos(texto);
    if (d.length === 1 && d > '1') d = '0' + d; // 2..9 viram 02..09
    d = d.slice(0, 4);
    return d.length > 2 ? `${d.slice(0, 2)}/${d.slice(2)}` : d;
}

function erroDaValidade(texto) {
    const d = somenteDigitos(texto);
    if (!d) return 'Informe a validade.';
    if (d.length < 4) return 'Validade incompleta. Use MM/AA.';
    const mes = Number(d.slice(0, 2));
    const ano = 2000 + Number(d.slice(2));
    if (mes < 1 || mes > 12) return 'Mês inválido.';
    const hoje = new Date();
    // o cartão vale até o último dia do mês indicado
    if (ano < hoje.getFullYear() || (ano === hoje.getFullYear() && mes < hoje.getMonth() + 1)) return 'Cartão vencido.';
    if (ano > hoje.getFullYear() + 20) return 'Validade inválida.';
    return '';
}

function validadeJaVenceu(validade) {
    const d = somenteDigitos(validade);
    return d.length < 4 || erroDaValidade(d) === 'Cartão vencido.';
}

function erroDoCvv(texto, bandeira) {
    const d = somenteDigitos(texto);
    const tamanho = bandeira === 'amex' ? 4 : 3;
    if (!d) return 'Informe o código de segurança.';
    return d.length === tamanho ? '' : `O código de segurança tem ${tamanho} números.`;
}

function erroDoTitularDoCartao(texto) {
    const nome = String(texto ?? '').trim();
    if (!nome) return 'Informe o nome como está no cartão.';
    if (nome.length < 3 || !/\p{L}/u.test(nome)) return 'Nome inválido.';
    return '';
}
// ===== FIM: mascaras-e-validacoes =====

// ProdutosStore agora fica em js/produtos-store.js (conversa com a API /api/produtos).

// ===== INÍCIO: carrinho-store =====
// Camada de acesso ao carrinho de compras (mesma ideia do ProdutosStore).
//
// ALTERADO (bloco inteiro): antes o carrinho de TODO mundo ficava só no localStorage do navegador.
// Agora existem dois carrinhos, e este arquivo escolhe o certo sozinho:
//   - CLIENTE LOGADO  -> o carrinho fica no BANCO DE DADOS (tabela item_carrinhos), pela API /api/carrinho.
//                        Assim ele acompanha a conta em qualquer computador ou navegador.
//   - VISITANTE (sem login) -> carrinho temporário no navegador (localStorage), porque ainda não há conta
//                        para guardá-lo no banco. Ao fazer login, o que estava nele é passado para a conta
//                        (função juntarVisitanteNaConta) e o carrinho temporário é esvaziado.
//   - FUNCIONÁRIO     -> não usa carrinho (não compra).
// Só guardamos { id, quantidade } de cada item. Nome, preço e imagem vêm do ProdutosStore na hora de
// mostrar a tela, assim o preço nunca fica desatualizado.
// As telas continuam chamando as mesmas funções de antes (listar, adicionar, remover...): não mudou nada para elas.
const CarrinhoStore = (() => {
    const PREFIXO = 'sebo_do_joao_carrinho';
    const CHAVE_LOCALSTORAGE = PREFIXO; // carrinho do visitante (nome antigo, mantido)

    // true quando quem está usando o site é um CLIENTE logado (o carrinho dele fica no banco)
    function ehClienteLogado() {
        const sessao = UsuarioStore.lerSessao();
        return Boolean(sessao) && !UsuarioStore.ehAdmin(sessao);
    }

    // ----- Carrinho do VISITANTE (localStorage) -----
    function lerDe(chave) {
        try {
            const itens = JSON.parse(localStorage.getItem(chave) || '[]');
            return Array.isArray(itens) ? itens : [];
        } catch (erro) {
            return [];
        }
    }

    function lerVisitante() {
        return lerDe(PREFIXO);
    }

    function salvarVisitante(itens) {
        localStorage.setItem(PREFIXO, JSON.stringify(itens));
    }

    // ----- Carrinho do CLIENTE (banco de dados, via API) -----
    // A API devolve [{ produtoId, quantidade }]; aqui viram { id, quantidade }, o formato que as telas usam.
    function deApi(itens) {
        return (itens || []).map((i) => ({ id: i.produtoId, quantidade: i.quantidade }));
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
        const sessao = UsuarioStore.lerSessao();
        if (!sessao) return lerVisitante();
        if (UsuarioStore.ehAdmin(sessao)) return [];
        try {
            return deApi(await chamarApi('/api/carrinho'));
        } catch (erro) {
            // Sem conexão ou sessão expirada: mostra o carrinho vazio em vez de quebrar a tela
            return [];
        }
    }

    async function contarItens() {
        return (await listar()).reduce((total, item) => total + (Number(item.quantidade) || 0), 0);
    }

    async function adicionar(produto, quantidade = 1) {
        // Funcionário não compra. A regra fica aqui porque todo caminho que coloca
        // produto no carrinho passa por esta função, e assim ela não se repete em cada tela.
        if (UsuarioStore.ehAdmin(UsuarioStore.lerSessao())) return { ok: false, motivo: 'funcionario' };

        if (!disponivel(produto)) return { ok: false, motivo: 'indisponivel' };

        const itens = await listar();
        const item = itens.find((i) => String(i.id) === String(produto.id));
        const quantidadeAtual = item ? Number(item.quantidade) || 0 : 0;

        if (quantidadeAtual + quantidade > limiteDeEstoque(produto)) {
            return { ok: false, motivo: 'estoque' };
        }

        // Visitante: guarda no navegador
        if (!ehClienteLogado()) {
            if (item) item.quantidade = quantidadeAtual + quantidade;
            else itens.push({ id: produto.id, quantidade });
            salvarVisitante(itens);
            return { ok: true };
        }

        // Cliente logado: grava no banco (o Java confere de novo o estoque, por segurança)
        try {
            await chamarApi('/api/carrinho', {
                method: 'POST',
                body: JSON.stringify({ produtoId: produto.id, quantidade })
            });
            return { ok: true };
        } catch (erro) {
            if (erro.status === 409 && /estoque/i.test(erro.message)) return { ok: false, motivo: 'estoque' };
            if (erro.status === undefined) return { ok: false, motivo: 'sessao', mensagem: erro.message };
            return { ok: false, motivo: 'indisponivel' };
        }
    }

    async function definirQuantidade(id, quantidade) {
        const nova = Math.max(1, Math.floor(quantidade));

        if (!ehClienteLogado()) {
            const itens = lerVisitante();
            const item = itens.find((i) => String(i.id) === String(id));
            if (!item) return;
            item.quantidade = nova;
            salvarVisitante(itens);
            return;
        }

        try {
            await chamarApi(`/api/carrinho/${encodeURIComponent(id)}`, {
                method: 'PUT',
                body: JSON.stringify({ produtoId: id, quantidade: nova })
            });
        } catch (erro) { /* sem estoque ou item que já saiu do carrinho: a tela recarrega e mostra a situação real */ }
    }

    async function remover(id) {
        if (!ehClienteLogado()) {
            salvarVisitante(lerVisitante().filter((i) => String(i.id) !== String(id)));
            return;
        }
        try {
            await chamarApi(`/api/carrinho/${encodeURIComponent(id)}`, { method: 'DELETE' });
        } catch (erro) { /* a tela recarrega e mostra o carrinho real */ }
    }

    async function limpar() {
        if (!ehClienteLogado()) {
            salvarVisitante([]);
            return;
        }
        try {
            await chamarApi('/api/carrinho', { method: 'DELETE' });
        } catch (erro) { /* a tela recarrega e mostra o carrinho real */ }
    }

    // Chamado quando a pessoa entra numa conta: o que ela pôs no carrinho como visitante é enviado ao banco
    // (somando quantidades, sem passar do estoque) e o carrinho temporário do navegador é esvaziado.
    // Também aproveita para trazer o carrinho que versões antigas do site guardavam no navegador por conta
    // (chave "sebo_do_joao_carrinho_<id>"), que depois deixa de existir.
    async function juntarVisitanteNaConta(idDaConta) {
        const chaveAntigaDaConta = `${PREFIXO}_${idDaConta}`;
        const itens = [...lerVisitante(), ...lerDe(chaveAntigaDaConta)]
            .map((i) => ({ produtoId: i.id, quantidade: Number(i.quantidade) || 1 }));
        if (!itens.length) return;

        try {
            await chamarApi('/api/carrinho/juntar', { method: 'POST', body: JSON.stringify(itens) });
            localStorage.removeItem(PREFIXO);
            localStorage.removeItem(chaveAntigaDaConta);
        } catch (erro) {
            // Se falhar (ex.: servidor fora do ar), os itens continuam no navegador para tentar de novo no próximo login
        }
    }

    // Ao excluir a conta, o Java já apaga o carrinho do banco; aqui só sobra limpar o carrinho antigo do navegador
    function apagarDaConta(idDaConta) {
        localStorage.removeItem(`${PREFIXO}_${idDaConta}`);
    }

    function ehChaveDeCarrinho(chave) {
        return typeof chave === 'string' && chave.startsWith(PREFIXO);
    }

    return {
        CHAVE_LOCALSTORAGE, listar, contarItens, adicionar,
        definirQuantidade, remover, limpar, disponivel, limiteDeEstoque,
        juntarVisitanteNaConta, apagarDaConta, ehChaveDeCarrinho
    };
})();
// ===== FIM: carrinho-store =====


// UsuarioStore agora fica em js/usuario-store.js (conversa com a API /api/login e /api/clientes).

// ===== INÍCIO: enderecos-cartoes-pedidos =====
// Endereços, cartões e pedidos da conta logada. Mesma ideia dos outros Stores: as telas só
// chamam estas funções, e cada uma faz uma chamada à API Java (/api/enderecos, /api/cartoes,
// /api/compras), que grava no SQL Server. ALTERADO: antes endereços e cartões ficavam no
// localStorage; agora tudo está no banco, e o Java usa o login da sessão para saber quem é o
// dono (cada conta só enxerga os próprios registros).

// Devolve a conta logada ou avisa que precisa entrar
async function usuarioLogadoOuErro() {
    const usuario = await UsuarioStore.obter();
    if (!usuario) throw new Error('Você precisa estar logado pra fazer isso.');
    return usuario;
}

// ----- Endereços -----
// ALTERADO (bloco inteiro): antes os endereços ficavam só no localStorage do navegador. Agora são gravados
// no BANCO (tabela enderecos), pela API /api/enderecos. Quem é o dono vem do login (o Java usa a sessão),
// então cada conta só enxerga os próprios endereços. As funções são as mesmas de antes (listar, obter,
// salvar, definirPadrao, remover), por isso as telas quase não mudaram.
//
// Dois "formatos" de endereço convivem:
//   - o do BANCO/Java:  { logradouro, numero (null = sem número), complemento, ... }
//   - o das TELAS:      { rua, numero ('' quando sem número), semNumero (true/false), complemento, ... }
// As funções deApi() e paraApi() só traduzem de um para o outro.
const EnderecosStore = (() => {
    const LIMITE = 10; // o Java confere o mesmo limite (EnderecoService.LIMITE)

    function deApi(e) {
        return {
            id: e.id,
            apelido: e.apelido,
            // ALTERADO: o banco não guarda mais destinatário e telefone no endereço. Para as telas que ainda mostram
            // "nome · telefone" junto do endereço, usamos o nome e o telefone da conta logada (cadastro).
            destinatario: (UsuarioStore.lerSessao() || {}).nome || '',
            telefone: (UsuarioStore.lerSessao() || {}).telefone || '',
            cep: e.cep,
            rua: e.logradouro,
            numero: e.numero || '',
            semNumero: e.numero == null,
            complemento: e.complemento || '',
            bairro: e.bairro,
            cidade: e.cidade,
            uf: e.uf,
            padrao: e.padrao === true
        };
    }

    // Só clientes logados têm endereços (visitante e funcionário: lista vazia)
    function contaComEnderecos(usuario) {
        return Boolean(usuario) && !UsuarioStore.ehAdmin(usuario);
    }

    // O Java já devolve o padrão primeiro e, depois, do mais novo pro mais antigo
    async function listar() {
        const usuario = await UsuarioStore.obter();
        if (!contaComEnderecos(usuario)) return [];
        return (await chamarApi('/api/enderecos')).map(deApi);
    }

    async function obter(id) {
        const usuario = await UsuarioStore.obter();
        if (!contaComEnderecos(usuario) || id == null) return null;
        try {
            return deApi(await chamarApi(`/api/enderecos/${encodeURIComponent(id)}`));
        } catch (erro) {
            if (erro.status === 404) return null; // não existe, foi excluído ou é de outra pessoa
            throw erro;
        }
    }

    // Cria (sem id) ou atualiza (com id). O primeiro endereço da conta já vira o padrão.
    async function salvar(dados) {
        await usuarioLogadoOuErro();
        // A tela confere os campos para avisar rápido; o Java confere tudo de novo (e é ele quem decide)
        const erros = errosDoEndereco(dados);
        const primeiroErro = Object.values(erros)[0];
        if (primeiroErro) throw new Error(primeiroErro);

        const corpo = {
            apelido: String(dados.apelido ?? '').trim(),
            cep: formatarCep(dados.cep),
            logradouro: String(dados.rua).trim(),
            numero: dados.semNumero ? '' : String(dados.numero).trim(),
            semNumero: Boolean(dados.semNumero),
            complemento: String(dados.complemento ?? '').trim(),
            bairro: String(dados.bairro).trim(),
            cidade: String(dados.cidade).trim(),
            uf: String(dados.uf).trim().toUpperCase(),
            padrao: Boolean(dados.padrao)
        };

        const editando = dados.id != null;
        const salvo = await chamarApi(editando ? `/api/enderecos/${encodeURIComponent(dados.id)}` : '/api/enderecos', {
            method: editando ? 'PUT' : 'POST',
            body: JSON.stringify(corpo)
        });
        return deApi(salvo);
    }

    async function definirPadrao(id) {
        await usuarioLogadoOuErro();
        await chamarApi(`/api/enderecos/${encodeURIComponent(id)}/padrao`, { method: 'POST' });
    }

    // "Exclui" o endereço: no banco ele só é marcado como inativo (as entregas antigas continuam usando)
    async function remover(id) {
        await usuarioLogadoOuErro();
        await chamarApi(`/api/enderecos/${encodeURIComponent(id)}`, { method: 'DELETE' });
    }

    return { LIMITE, listar, obter, salvar, definirPadrao, remover };
})();

// ----- Cartões -----
// ALTERADO (bloco inteiro): antes os cartões ficavam só no localStorage do navegador. Agora são gravados no
// BANCO (tabela cartoes), pela API /api/cartoes. Continua valendo a regra de segurança: o número completo e o
// CVV são conferidos aqui e JOGADOS FORA na hora; só a bandeira, os 4 últimos dígitos, o nome e a validade
// seguem para o Java e para o banco.
const CartoesStore = (() => {
    const LIMITE = 6; // o Java confere o mesmo limite (CartaoService.LIMITE)

    // O banco chama de "finalNumero"; as telas usam "ultimos4"
    function deApi(c) {
        return {
            id: c.id,
            bandeira: c.bandeira,
            ultimos4: c.finalNumero,
            titular: c.titular,
            validade: c.validade,
            padrao: c.padrao === true
        };
    }

    function contaComCartoes(usuario) {
        return Boolean(usuario) && !UsuarioStore.ehAdmin(usuario);
    }

    // O Java já devolve o padrão primeiro e, depois, do mais novo pro mais antigo
    async function listar() {
        const usuario = await UsuarioStore.obter();
        if (!contaComCartoes(usuario)) return [];
        return (await chamarApi('/api/cartoes')).map(deApi);
    }

    async function obter(id) {
        return (await listar()).find((c) => String(c.id) === String(id)) || null;
    }

    // Confere os dados do cartão (número, nome e validade) e devolve o que pode ser guardado
    function conferirCartao({ numero, titular, validade }) {
        const erro = erroDoNumeroCartao(numero) || erroDoTitularDoCartao(titular) || erroDaValidade(validade);
        if (erro) throw new Error(erro);
        const d = somenteDigitos(numero);
        return {
            bandeira: bandeiraDoCartao(d),
            ultimos4: d.slice(-4),
            titular: String(titular).trim().replace(/\s+/g, ' ').toUpperCase(),
            validade: formatarValidade(validade)
        };
    }

    async function salvar(dados) {
        await usuarioLogadoOuErro();
        const limpo = conferirCartao(dados); // o número completo para por aqui

        const salvo = await chamarApi('/api/cartoes', {
            method: 'POST',
            body: JSON.stringify({
                bandeira: limpo.bandeira,
                finalNumero: limpo.ultimos4,
                titular: limpo.titular,
                validade: limpo.validade,
                padrao: Boolean(dados.padrao)
            })
        });
        return deApi(salvo);
    }

    async function definirPadrao(id) {
        await usuarioLogadoOuErro();
        await chamarApi(`/api/cartoes/${encodeURIComponent(id)}/padrao`, { method: 'POST' });
    }

    async function remover(id) {
        await usuarioLogadoOuErro();
        await chamarApi(`/api/cartoes/${encodeURIComponent(id)}`, { method: 'DELETE' });
    }

    return { LIMITE, listar, obter, conferirCartao, salvar, definirPadrao, remover };
})();

// ----- Frete e parcelas (simulados) -----
// Valores e prazos de exemplo, calculados pelo CEP. No backend isso vira uma consulta
// aos Correios / transportadora.
const VALOR_PARA_FRETE_GRATIS = 150;

function somarDiasUteis(inicio, dias) {
    const data = new Date(inicio);
    let faltam = dias;
    while (faltam > 0) {
        data.setDate(data.getDate() + 1);
        if (data.getDay() !== 0 && data.getDay() !== 6) faltam--;
    }
    return data;
}

function formatarDataCurta(data) {
    // "seg., 10 de nov." -> "seg, 10 nov"
    return data.toLocaleDateString('pt-BR', { weekday: 'short', day: 'numeric', month: 'short' })
        .replace(/\./g, '').replace(' de ', ' ');
}

function calcularOpcoesDeFrete(cep, subtotal) {
    const primeiro = somenteDigitos(cep)[0];
    // zona pelo 1º dígito do CEP: 0-1 = São Paulo, 2,3,8,9 = Sudeste/Sul, resto = demais regiões
    const zona = '01'.includes(primeiro) ? 'sp' : '2389'.includes(primeiro) ? 'sul-sudeste' : 'demais';
    const tabela = {
        sp: { eco: [12.9, 4, 6], exp: [24.9, 1, 2] },
        'sul-sudeste': { eco: [18.9, 6, 9], exp: [34.9, 2, 3] },
        demais: { eco: [27.9, 9, 14], exp: [49.9, 3, 5] }
    }[zona];

    const hoje = new Date();
    const prazo = (min, max) =>
        `Chegará entre ${formatarDataCurta(somarDiasUteis(hoje, min))} e ${formatarDataCurta(somarDiasUteis(hoje, max))}`;
    const gratis = subtotal >= VALOR_PARA_FRETE_GRATIS;

    // ALTERADO: cada opção ganhou "prazoDias" (o prazo máximo, em dias úteis). Ele é enviado ao Java na
    // compra e o banco usa para calcular a data de previsão de entrega (entregas.data_previsao_entrega).
    return [
        {
            id: 'economico', nome: 'Econômico', preco: gratis ? 0 : tabela.eco[0],
            prazoTexto: prazo(tabela.eco[1], tabela.eco[2]), prazoDias: tabela.eco[2],
            observacao: gratis ? `Frete grátis em compras a partir de ${formatarPreco(VALOR_PARA_FRETE_GRATIS)}` : ''
        },
        { id: 'expresso', nome: 'Expresso', preco: tabela.exp[0], prazoTexto: prazo(tabela.exp[1], tabela.exp[2]), prazoDias: tabela.exp[2], observacao: '' },
        {
            id: 'retirada', nome: 'Retirar com o vendedor', preco: 0, prazoDias: 0,
            prazoTexto: 'Combine a retirada com o Sebo do João depois da compra', observacao: ''
        }
    ];
}

// Parcelas sem juros: até 3x, com parcela mínima de R$ 20
// ALTERADO: o limite era 6 e agora é 3, igual à regra do banco (@max_parcelas na procedure
// sp_finalizar_compra). Se a regra de negócio mudar, altere os dois lugares.
const MAX_PARCELAS = 3;
function calcularParcelas(total) {
    const maximo = Math.max(1, Math.min(MAX_PARCELAS, Math.floor(total / 20)));
    return Array.from({ length: maximo }, (_, i) => ({ quantidade: i + 1, valor: total / (i + 1) }));
}

// ----- Pedidos -----
// ALTERADO (bloco inteiro): antes o pedido era montado e guardado só no localStorage do navegador
// (nada chegava ao banco e o estoque era baixado "na mão"). Agora o pedido é enviado ao Java
// (POST /api/compras), que chama a procedure sp_finalizar_compra e grava de uma vez as tabelas
// entregas, pedidos, item_pedidos e pagamentos. O estoque é baixado pelo próprio banco (trigger).
// Os nomes das funções continuam os mesmos (listar, obter, criar...), por isso as telas mudam pouco.

// Fala com o Java e trata os erros de um jeito só (usado por pedidos, entregas e painel do funcionário).
//  - O Java responde erros como {"mensagem": "..."}; essa mensagem é mostrada para a pessoa.
//  - Erro 401 = o Java não reconhece o login (por exemplo, o servidor foi reiniciado). Nesse caso a
//    conta é desconectada aqui também, para a pessoa entrar de novo em vez de ficar "meio logada".
async function chamarApi(url, opcoes = {}) {
    let resposta;
    try {
        resposta = await fetch(url, { headers: { 'Content-Type': 'application/json' }, ...opcoes });
    } catch (erro) {
        throw new Error('Não foi possível falar com o servidor. Confira a conexão e tente novamente.');
    }

    let corpo = null;
    try { corpo = await resposta.json(); } catch (erro) { /* resposta sem corpo */ }

    if (resposta.status === 401) {
        UsuarioStore.sair();
        throw new Error('Sua sessão expirou. Entre novamente para continuar.');
    }
    if (!resposta.ok) {
        const erro = new Error((corpo && corpo.mensagem) || 'Não foi possível concluir a operação. Tente novamente.');
        erro.status = resposta.status;
        throw erro;
    }
    return corpo;
}

const PedidosStore = (() => {
    // Status que o banco devolve em pedidos.status (via view vw_pedidos_detalhados)
    const STATUS_AGUARDANDO = 'Aguardando pagamento';
    const STATUS_PROCESSANDO = 'Processando';
    const STATUS_CONCLUIDO = 'Concluido';
    const STATUS_CANCELADO = 'Cancelado';

    // ALTERADO: antes o endereço de entrega e a bandeira/final do cartão ficavam anotados no navegador
    // ("extras"), porque o banco não tinha onde guardá-los. Agora eles têm lugar próprio (entregas.endereco_id
    // e pagamentos.bandeira/final_cartao), então tudo vem do banco e as anotações locais foram REMOVIDAS.
    // Esta chave só continua aqui para apagar as anotações que versões antigas deixaram no navegador.
    const CHAVE_EXTRAS = 'sebo_do_joao_pedidos_extras';

    // Ícone de cada categoria (o banco devolve só o nome da categoria)
    const ICONES_POR_CATEGORIA = {
        'livros': 'fa-book-open', 'hqs': 'fa-mask', 'discos de vinil': 'fa-record-vinyl',
        'blu-rays': 'fa-compact-disc', 'dvds': 'fa-film', 'cds': 'fa-compact-disc', 'fitas cassetes': 'fa-cassette-tape'
    };

    // ALTERADO: lerExtras() e guardarExtras() foram REMOVIDAS (não há mais o que anotar no navegador).
    // No lugar, as anotações antigas são apagadas uma vez, quando este arquivo é carregado.
    try { localStorage.removeItem(CHAVE_EXTRAS); } catch (erro) { /* navegador sem localStorage: nada a limpar */ }

    // Código "copia e cola" de exemplo (parece um Pix de verdade, mas é fictício).
    // ALTERADO: antes usava números aleatórios e o código ficava guardado no pedido. Agora o pedido vem
    // do banco (que não guarda o código), então ele é calculado a partir do código do pedido: o mesmo
    // pedido sempre mostra o mesmo código, mesmo se a página for aberta de novo.
    function gerarCodigoPix(total, codigoDoPedido) {
        const valor = Number(total).toFixed(2);
        let h = 2166136261;
        let aleatorio = '';
        for (let n = 0; n < 8; n++) {
            for (const c of codigoDoPedido + n) h = Math.imul(h ^ c.charCodeAt(0), 16777619) >>> 0;
            aleatorio += (h & 255).toString(16).padStart(2, '0');
        }
        return '00020126580014br.gov.bcb.pix0136' + aleatorio + '-sebo-do-joao-demo' +
            '5204000053039865406' + valor.replace('.', '') + '5802BR5913SEBO DO JOAO6009SAO PAULO' +
            '62' + String(4 + codigoDoPedido.length).padStart(2, '0') + '05' + String(codigoDoPedido.length).padStart(2, '0') + codigoDoPedido + '6304DEMO';
    }

    // Converte o pedido que veio do Java (CompraResponseDTO) para o formato que as telas já usam.
    function converter(dto) {
        const entrega = dto.entrega || {};
        const pg = dto.pagamento || {};
        const ehPix = pg.forma === 'PIX';

        // ALTERADO: o endereço de entrega agora vem do banco (dto.endereco). Aqui ele é traduzido para o
        // mesmo formato que as telas já usam (rua, numero, semNumero...), igual ao EnderecosStore.
        const end = dto.endereco;
        const endereco = end && end.logradouro ? {
            destinatario: end.destinatario, telefone: end.telefone, cep: end.cep,
            rua: end.logradouro, numero: end.numero || '', semNumero: end.numero == null,
            complemento: end.complemento || '', bairro: end.bairro, cidade: end.cidade, uf: end.uf
        } : null;

        // "2026-11-07" -> "07/11/2026"
        const previsao = entrega.previsao ? entrega.previsao.split('-').reverse().join('/') : '';

        return {
            id: dto.id,
            codigo: dto.codigo,
            criadoEm: dto.data,
            status: dto.statusPedido,
            cliente: dto.cliente,
            funcionario: dto.funcionario,
            itens: dto.itens.map((i) => ({
                id: i.produtoId, nome: i.produto, preco: Number(i.precoUnitario), quantidade: i.quantidade,
                categoria: i.categoria, icone: ICONES_POR_CATEGORIA[String(i.categoria).toLowerCase()] || 'fa-box'
            })),
            subtotal: Number(dto.subtotalProdutos),
            frete: {
                nome: entrega.transportadora || 'Entrega', preco: Number(dto.valorFrete),
                prazoTexto: previsao ? `Previsão de entrega: ${previsao}` : ''
            },
            total: Number(dto.total),
            endereco,
            entrega: { status: entrega.statusEntrega, codigoRastreio: entrega.codigoRastreio },
            pagamento: {
                tipo: ehPix ? 'pix' : 'cartao',
                status: pg.statusPagamento,
                parcelas: pg.parcelas || 1,
                valorParcela: Number(pg.valorParcela),
                // ALTERADO: bandeira e final do cartão agora vêm do banco (pagamentos.bandeira / final_cartao)
                bandeira: pg.bandeira || '',
                ultimos4: pg.finalCartao || '',
                pixCodigo: ehPix ? gerarCodigoPix(dto.total, dto.codigo) : undefined
            },
            podeCancelar: dto.podeCancelar,
            podePagar: dto.podePagar,
            podeAvancarEntrega: dto.podeAvancarEntrega
        };
    }

    // Pedidos da conta logada (o Java usa o login da sessão para saber de quem são)
    async function listar() {
        const usuario = await UsuarioStore.obter();
        if (!usuario) return [];
        return (await chamarApi('/api/compras')).map(converter);
    }

    async function obter(id) {
        try {
            return converter(await chamarApi(`/api/compras/${encodeURIComponent(id)}`));
        } catch (erro) {
            if (erro.status === 404) return null; // pedido inexistente ou de outra pessoa
            throw erro;
        }
    }

    // Monta o pedido a partir do carrinho. A tela e este código conferem o que dá (estoque, cartão,
    // parcelas...) para avisar rápido, mas quem decide de verdade é o banco: ele usa o preço
    // cadastrado do produto, confere o estoque e desfaz tudo se algo falhar.
    //   pagamento: { tipo: 'cartao', cartaoId | cartaoNovo: { numero, titular, validade, salvar }, cvv, parcelas }
    //              ou { tipo: 'pix' }
    async function criar({ enderecoId, freteId, pagamento }) {
        const usuario = await usuarioLogadoOuErro();

        const itensCarrinho = await CarrinhoStore.listar();
        if (!itensCarrinho.length) throw new Error('Seu carrinho está vazio.');

        const itens = [];
        for (const item of itensCarrinho) {
            const produto = await ProdutosStore.obterPorId(item.id);
            if (!produto || !CarrinhoStore.disponivel(produto)) {
                throw new Error(`"${produto ? produto.nome : 'Um item'}" não está mais disponível. Remova do carrinho pra continuar.`);
            }
            const limite = CarrinhoStore.limiteDeEstoque(produto);
            if (item.quantidade > limite) {
                throw new Error(`"${produto.nome}": só ${limite === 1 ? 'resta 1 unidade' : `restam ${limite} unidades`}. Ajuste o carrinho.`);
            }
            itens.push({ produto, quantidade: item.quantidade });
        }
        const subtotal = itens.reduce((soma, { produto, quantidade }) => soma + produto.preco * quantidade, 0);

        const endereco = await EnderecosStore.obter(enderecoId);
        if (!endereco) throw new Error('Escolha um endereço de entrega.');

        const frete = calcularOpcoesDeFrete(endereco.cep, subtotal).find((o) => o.id === freteId);
        if (!frete) throw new Error('Escolha uma forma de entrega.');
        const total = subtotal + frete.preco;

        // ----- pagamento -----
        let formaPagamento;   // o nome que o banco entende: 'PIX' ou 'CARTAO_CREDITO'
        let parcelasEscolhidas = 1;
        let cartaoUsado = null;
        if (pagamento && pagamento.tipo === 'cartao') {
            let cartao;
            if (pagamento.cartaoId != null) {
                cartao = await CartoesStore.obter(pagamento.cartaoId);
                if (!cartao) throw new Error('Escolha um cartão.');
                if (validadeJaVenceu(cartao.validade)) throw new Error('Esse cartão está vencido. Escolha outro.');
            } else if (pagamento.cartaoNovo) {
                cartao = CartoesStore.conferirCartao(pagamento.cartaoNovo); // valida, ainda sem salvar
            } else {
                throw new Error('Escolha um cartão.');
            }

            const erroCvv = erroDoCvv(pagamento.cvv, cartao.bandeira);
            if (erroCvv) throw new Error(erroCvv);

            const parcelas = calcularParcelas(total).find((p) => p.quantidade === Number(pagamento.parcelas));
            if (!parcelas) throw new Error('Escolha o número de parcelas.');

            formaPagamento = 'CARTAO_CREDITO';
            parcelasEscolhidas = parcelas.quantidade;
            cartaoUsado = { bandeira: cartao.bandeira, ultimos4: cartao.ultimos4 };
        } else if (pagamento && pagamento.tipo === 'pix') {
            formaPagamento = 'PIX';
        } else {
            throw new Error('Escolha uma forma de pagamento.');
        }

        // ----- envia a compra ao Java (que grava no banco) -----
        const pedidoCriado = await chamarApi('/api/compras', {
            method: 'POST',
            body: JSON.stringify({
                itens: itens.map(({ produto, quantidade }) => ({ produtoId: produto.id, quantidade })),
                formaPagamento,
                parcelas: parcelasEscolhidas,
                transportadora: frete.nome,   // vai para entregas.transportadora
                prazoDias: frete.prazoDias,   // o banco calcula a data de previsão de entrega
                valorFrete: frete.preco,      // entra no total do pedido e das parcelas (e fica em entregas.valor_frete)
                // NOVO: o endereço escolhido (o banco confere se é deste cliente) e, no cartão, só a bandeira e os
                // 4 últimos dígitos (vão para pagamentos.bandeira e pagamentos.final_cartao). O número e o CVV NÃO são enviados.
                enderecoId: endereco.id,
                bandeira: cartaoUsado ? cartaoUsado.bandeira : null,
                finalCartao: cartaoUsado ? cartaoUsado.ultimos4 : null
            })
        });

        // Só depois de o banco confirmar é que o cartão novo é salvo (se a pessoa quis) e o carrinho esvazia
        if (pagamento.cartaoNovo && pagamento.cartaoNovo.salvar) {
            try { await CartoesStore.salvar(pagamento.cartaoNovo); } catch (erro) { /* já salvo ou limite: a compra segue */ }
        }
        // ALTERADO: não é mais preciso "anotar" o endereço e o cartão no navegador (guardarExtras foi removida):
        // o endereço e o cartão do pedido já ficaram gravados no banco e voltam na resposta.

        // O carrinho (que agora é do banco, para quem está logado) é esvaziado
        await CarrinhoStore.limpar();
        return converter(pedidoCriado);
    }

    // Só pra demonstração: "paga" um Pix pendente (no mundo real quem avisa é o banco/gateway).
    // ALTERADO: agora o Java marca as parcelas como aprovadas no banco e a trigger muda o pedido para "Processando".
    async function simularPagamentoDoPix(id) {
        await usuarioLogadoOuErro();
        return converter(await chamarApi(`/api/compras/${encodeURIComponent(id)}/simular-pagamento`, { method: 'POST' }));
    }

    // NOVO: cancela o pedido (procedure sp_cancelar_pedido). O estoque volta pela trigger.
    async function cancelar(id) {
        await usuarioLogadoOuErro();
        return converter(await chamarApi(`/api/compras/${encodeURIComponent(id)}/cancelar`, { method: 'POST' }));
    }

    // NOVO: usadas só pelo painel do funcionário (admin-pedidos.html)
    async function listarTodos() {
        return (await chamarApi('/api/compras/gestao')).map(converter);
    }
    async function avancarEntrega(id) {
        return converter(await chamarApi(`/api/compras/gestao/${encodeURIComponent(id)}/avancar-entrega`, { method: 'POST' }));
    }
    async function aprovarPagamento(id) {
        return converter(await chamarApi(`/api/compras/gestao/${encodeURIComponent(id)}/aprovar-pagamento`, { method: 'POST' }));
    }

    // ALTERADO: removerDaConta() foi removida daqui (não há mais anotações locais de pedidos).
    return {
        CHAVE_EXTRAS, STATUS_AGUARDANDO, STATUS_PROCESSANDO, STATUS_CONCLUIDO, STATUS_CANCELADO,
        listar, obter, criar, simularPagamentoDoPix, cancelar, listarTodos, avancarEntrega, aprovarPagamento
    };
})();

// Tudo que pertence a uma conta que está sendo excluída.
// ALTERADO: endereços, cartões, carrinho e pedidos agora ficam no banco, e quem apaga o que é da conta
// (endereços, cartões e carrinho) é o Java, na hora de excluir o cliente. Aqui sobra só limpar o que ainda
// possa ter ficado neste navegador (o carrinho antigo de visitante/conta).
function apagarDadosDaConta(usuarioId) {
    CarrinhoStore.apagarDaConta(usuarioId);
}
// ===== FIM: enderecos-cartoes-pedidos =====

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
    if (CarrinhoStore.ehChaveDeCarrinho(evento.key) || evento.key === 'sebo_do_joao_sessao') atualizarBadgeCarrinho();
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
    // Itens ligados a compras (ex.: "Meus pedidos") somem para o Funcionário, que não compra.
    // Basta marcar o item no HTML com data-ec-so-cliente: a regra fica aqui, num lugar só.
    document.querySelectorAll('[data-ec-so-cliente]').forEach((elemento) => {
        elemento.classList.toggle('d-none', UsuarioStore.ehAdmin(usuario));
    });
    // Funcionário não compra, então os ícones do carrinho (cabeçalho e botão flutuante) somem
    document.querySelectorAll('.ec-cart-link, .ec-carrinho-flutuante').forEach((elemento) => {
        elemento.classList.toggle('d-none', UsuarioStore.ehAdmin(usuario));
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

// Monta os links de categoria com os nomes do banco. A busca filtra comparando o nome que vem
// no link com o nome gravado na tabela. Como os links eram escritos à mão em cada tela, qualquer
// diferença entre os dois (ex.: "Disco" e "Discos") fazia a categoria mostrar 0 produtos.
// Se a API falhar, o menu continua com os links que já estão no HTML.
async function atualizarMenuCategorias() {
    const menus = document.querySelectorAll('.ec-nav-categories');
    if (!menus.length) return;

    try {
        const resposta = await fetch('/api/categorias');
        if (!resposta.ok) return;

        const categorias = await resposta.json();
        const categoriaAtual = new URLSearchParams(location.search).get('categoria');

        menus.forEach((menu) => {
            menu.querySelectorAll('a[href*="categoria="]').forEach((link) => link.remove());

            categorias.forEach((categoria) => {
                const link = document.createElement('a');
                link.className = 'nav-link';
                link.href = 'busca.html?categoria=' + encodeURIComponent(categoria.nome);
                link.textContent = categoria.nome;
                link.classList.toggle('ec-ativo', categoria.nome === categoriaAtual);
                menu.appendChild(link);
            });
        });
    } catch (erro) {
        console.error('Erro ao carregar as categorias:', erro);
    }
}

document.addEventListener('DOMContentLoaded', atualizarMenuCategorias);

// Botão "Sair" do menu da conta
document.addEventListener('click', (evento) => {
    if (!evento.target.closest('[data-ec-sair]')) return;
    evento.preventDefault();
    UsuarioStore.sair();
    location.href = 'index.html'; // Redireciona corretamente para a loja (index.html)
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
        avisar(`"${produto.nome}" foi adicionado ao carrinho.`, true);
        atualizarBadgeCarrinho();
        return true;
    }

    if (resultado.motivo === 'funcionario') {
        avisar('Contas de funcionário não podem realizar compras.', false);
    } else if (resultado.motivo === 'estoque') {
        avisar('Você já tem todas as unidades disponíveis desse produto no carrinho.', true);
    } else if (resultado.motivo === 'sessao') {
        // NOVO: o carrinho de quem está logado fica no banco; se o servidor não reconhece o login (por exemplo,
        // foi reiniciado) ou está fora do ar, a pessoa recebe o motivo em vez de "produto indisponível".
        avisar(resultado.mensagem || 'Não foi possível adicionar ao carrinho. Entre novamente e tente de novo.', false);
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

    // 1. Verifica se quem está vendo a vitrine é um funcionário. Toda tela que usa este
    // card precisa carregar o usuario-store.js: sem ele, o UsuarioStore não existe, a
    // função quebra e nenhum produto aparece.
    const ehFuncionario = UsuarioStore.ehAdmin(UsuarioStore.lerSessao());

    const conteudoImagem = produto.imagem
        ? `<img src="${escaparHtml(produto.imagem)}" alt="${titulo}" class="w-100 h-100 object-fit-cover">`
        : `<i class="fas ${escaparHtml(produto.icone || 'fa-box')} fa-4x opacity-50"></i>`;

    // 2. Se for funcionário, a variável 'botao' fica vazia (nenhum botão "Comprar" aparece).
    // Se for cliente/visitante, gera o botão normalmente.
    let botao = '';
    if (!ehFuncionario) {
        botao = disponivel
            ? `<button type="button" class="btn ec-btn-outline-primary ec-btn-pill w-100" data-comprar="${escaparHtml(produto.id)}">
                    <i class="fas fa-cart-plus me-2"></i>Comprar
               </button>`
            : `<button type="button" class="btn ec-btn-outline-primary ec-btn-pill w-100" disabled>Esgotado</button>`;
    }

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


// ===== Privacidade =====

// "Apagar cookies": limpa o que o site guardou de temporário neste navegador — cookies,
// dados da sessão (sessionStorage) e o carrinho. NÃO apaga a conta nem encerra o login.
// Devolve quantos itens havia no carrinho.
async function apagarDadosDeNavegacao() {
    const itensNoCarrinho = await CarrinhoStore.contarItens();
    await CarrinhoStore.limpar();

    // Cookies do site (hoje o site quase não usa, mas se algum for criado, sai aqui)
    document.cookie.split(';').forEach((cookie) => {
        const nome = cookie.split('=')[0].trim();
        if (!nome) return;
        const expirado = `${nome}=; expires=Thu, 01 Jan 1970 00:00:00 GMT; max-age=0`;
        document.cookie = `${expirado}; path=/`;
        document.cookie = expirado; // caminho da página atual
    });

    try {
        sessionStorage.clear();
    } catch (erro) { /* navegador sem sessionStorage: nada a limpar */ }

    atualizarBadgeCarrinho();
    return itensNoCarrinho;
}

// Aviso que uma tela deixa pra próxima (ex.: "Sua conta foi excluída." na loja,
// depois do redirecionamento). Aparece uma vez só.
const CHAVE_AVISO_PENDENTE = 'sebo_do_joao_aviso';

function deixarAvisoParaProximaTela(texto) {
    try {
        sessionStorage.setItem(CHAVE_AVISO_PENDENTE, texto);
    } catch (erro) { /* sem aviso, sem problema */ }
}

document.addEventListener('DOMContentLoaded', () => {
    try {
        const aviso = sessionStorage.getItem(CHAVE_AVISO_PENDENTE);
        if (!aviso) return;
        sessionStorage.removeItem(CHAVE_AVISO_PENDENTE);
        avisar(aviso, false);
    } catch (erro) { /* sem aviso, sem problema */ }
});

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
