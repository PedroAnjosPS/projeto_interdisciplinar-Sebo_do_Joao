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
//   2. CarrinhoStore  — carrinho de compras (um por conta)
//   3. EnderecosStore, CartoesStore e PedidosStore — endereços, cartões e pedidos da conta
//      (por enquanto no localStorage; a API ainda não tem essas rotas)
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
    obrigatorio('destinatario', 'Informe o nome de quem vai receber.');
    if (!somenteDigitos(e.telefone)) erros.telefone = 'Informe um telefone para contato.';
    else if (erroDoTelefone(e.telefone)) erros.telefone = erroDoTelefone(e.telefone);

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
// Só guardamos { id, quantidade } de cada item. Nome, preço e imagem vêm
// do ProdutosStore na hora de mostrar a tela, assim o preço nunca fica
// desatualizado. Hoje fica no localStorage; com o backend Java + Spring
// Boot essas funções viram chamadas à API e as telas não precisam mudar.
const CarrinhoStore = (() => {
    // Cada conta tem o seu carrinho: quem não entrou ainda usa o carrinho de "visitante"
    // (que é juntado ao da conta quando a pessoa faz login). No backend Java isso é só
    // buscar os itens do usuário logado (GET /carrinho).
    const PREFIXO = 'sebo_do_joao_carrinho';
    const CHAVE_SESSAO = 'sebo_do_joao_sessao';
    const CHAVE_LOCALSTORAGE = PREFIXO; // carrinho do visitante (nome antigo, mantido)

    function idDaContaLogada() {
        try {
            const sessao = JSON.parse(localStorage.getItem(CHAVE_SESSAO) || 'null');
            return sessao && sessao.id != null ? sessao.id : null;
        } catch (erro) {
            return null;
        }
    }

    function chaveDe(idDaConta) {
        return idDaConta == null ? PREFIXO : `${PREFIXO}_${idDaConta}`;
    }

    function lerDe(chave) {
        try {
            const itens = JSON.parse(localStorage.getItem(chave) || '[]');
            return Array.isArray(itens) ? itens : [];
        } catch (erro) {
            return [];
        }
    }

    function lerTudo() {
        return lerDe(chaveDe(idDaContaLogada()));
    }

    function salvarTudo(itens) {
        localStorage.setItem(chaveDe(idDaContaLogada()), JSON.stringify(itens));
    }

    // Chamado quando a pessoa entra numa conta: o que ela pôs no carrinho como visitante
    // vai pro carrinho da conta (somando quantidades) e o carrinho de visitante esvazia.
    function juntarVisitanteNaConta(idDaConta) {
        const doVisitante = lerDe(PREFIXO);
        if (!doVisitante.length) return;
        const daConta = lerDe(chaveDe(idDaConta));
        doVisitante.forEach((item) => {
            const existente = daConta.find((i) => String(i.id) === String(item.id));
            if (existente) existente.quantidade = (Number(existente.quantidade) || 0) + (Number(item.quantidade) || 0);
            else daConta.push({ id: item.id, quantidade: Number(item.quantidade) || 1 });
        });
        localStorage.setItem(chaveDe(idDaConta), JSON.stringify(daConta));
        localStorage.removeItem(PREFIXO);
    }

    function apagarDaConta(idDaConta) {
        localStorage.removeItem(chaveDe(idDaConta));
    }

    function ehChaveDeCarrinho(chave) {
        return typeof chave === 'string' && chave.startsWith(PREFIXO);
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
        definirQuantidade, remover, limpar, disponivel, limiteDeEstoque,
        juntarVisitanteNaConta, apagarDaConta, ehChaveDeCarrinho
    };
})();
// ===== FIM: carrinho-store =====


// UsuarioStore agora fica em js/usuario-store.js (conversa com a API /api/login e /api/clientes).

// ===== INÍCIO: enderecos-cartoes-pedidos =====
// Endereços, cartões e pedidos da conta logada. Mesma ideia dos outros Stores: as telas só
// chamam estas funções e, no backend Java + Spring Boot (SQL Server), cada uma vira uma
// chamada à API (GET/POST /enderecos, /cartoes, /pedidos...). Cada registro guarda o
// "usuarioId" do dono, e todas as funções só enxergam os registros da conta logada.

// Devolve a conta logada ou avisa que precisa entrar
async function usuarioLogadoOuErro() {
    const usuario = await UsuarioStore.obter();
    if (!usuario) throw new Error('Você precisa estar logado pra fazer isso.');
    return usuario;
}

function lerLista(chave) {
    try {
        const lista = JSON.parse(localStorage.getItem(chave) || '[]');
        return Array.isArray(lista) ? lista : [];
    } catch (erro) {
        return [];
    }
}

function salvarLista(chave, lista) {
    try {
        localStorage.setItem(chave, JSON.stringify(lista));
    } catch (erro) {
        if (erro.name === 'QuotaExceededError' || erro.code === 22) {
            throw new Error('O navegador não tem mais espaço pra guardar esses dados.');
        }
        throw erro;
    }
}

function proximoIdDaLista(lista) {
    return lista.length ? Math.max(...lista.map((item) => Number(item.id) || 0)) + 1 : 1;
}

// ----- Endereços -----
const EnderecosStore = (() => {
    const CHAVE = 'sebo_do_joao_enderecos';
    const LIMITE = 10;

    // Padrão primeiro, depois do mais novo pro mais antigo
    async function listar() {
        const usuario = await UsuarioStore.obter();
        if (!usuario) return [];
        return lerLista(CHAVE)
            .filter((e) => String(e.usuarioId) === String(usuario.id))
            .sort((a, b) => (b.padrao === true) - (a.padrao === true) || b.id - a.id);
    }

    async function obter(id) {
        return (await listar()).find((e) => String(e.id) === String(id)) || null;
    }

    // Cria (sem id) ou atualiza (com id). O primeiro endereço da conta já vira o padrão.
    async function salvar(dados) {
        const usuario = await usuarioLogadoOuErro();
        const erros = errosDoEndereco(dados);
        const primeiroErro = Object.values(erros)[0];
        if (primeiroErro) throw new Error(primeiroErro);

        const todos = lerLista(CHAVE);
        const meus = todos.filter((e) => String(e.usuarioId) === String(usuario.id));

        const limpo = {
            apelido: String(dados.apelido ?? '').trim() || 'Endereço',
            destinatario: String(dados.destinatario).trim(),
            telefone: formatarTelefone(dados.telefone),
            cep: formatarCep(dados.cep),
            rua: String(dados.rua).trim(),
            numero: dados.semNumero ? '' : String(dados.numero).trim(),
            semNumero: Boolean(dados.semNumero),
            complemento: String(dados.complemento ?? '').trim(),
            bairro: String(dados.bairro).trim(),
            cidade: String(dados.cidade).trim(),
            uf: String(dados.uf).trim().toUpperCase()
        };

        let registro;
        if (dados.id != null) {
            registro = todos.find((e) => String(e.id) === String(dados.id) && String(e.usuarioId) === String(usuario.id));
            if (!registro) throw new Error('Endereço não encontrado.');
            Object.assign(registro, limpo);
        } else {
            if (meus.length >= LIMITE) throw new Error(`Você pode guardar até ${LIMITE} endereços. Exclua algum pra adicionar outro.`);
            registro = { id: proximoIdDaLista(todos), usuarioId: usuario.id, padrao: meus.length === 0, ...limpo };
            todos.push(registro);
        }

        if (dados.padrao) {
            todos.forEach((e) => { if (String(e.usuarioId) === String(usuario.id)) e.padrao = e === registro; });
        }
        salvarLista(CHAVE, todos);
        return registro;
    }

    async function definirPadrao(id) {
        const usuario = await usuarioLogadoOuErro();
        const todos = lerLista(CHAVE);
        if (!todos.some((e) => String(e.id) === String(id) && String(e.usuarioId) === String(usuario.id))) return;
        todos.forEach((e) => {
            if (String(e.usuarioId) === String(usuario.id)) e.padrao = String(e.id) === String(id);
        });
        salvarLista(CHAVE, todos);
    }

    async function remover(id) {
        const usuario = await usuarioLogadoOuErro();
        let todos = lerLista(CHAVE);
        const removido = todos.find((e) => String(e.id) === String(id) && String(e.usuarioId) === String(usuario.id));
        if (!removido) return;
        todos = todos.filter((e) => e !== removido);

        // Se apagou o padrão, o mais novo que sobrou assume
        if (removido.padrao) {
            const sobra = todos.filter((e) => String(e.usuarioId) === String(usuario.id)).sort((a, b) => b.id - a.id)[0];
            if (sobra) sobra.padrao = true;
        }
        salvarLista(CHAVE, todos);
    }

    function removerDaConta(usuarioId) {
        salvarLista(CHAVE, lerLista(CHAVE).filter((e) => String(e.usuarioId) !== String(usuarioId)));
    }

    return { CHAVE, LIMITE, listar, obter, salvar, definirPadrao, remover, removerDaConta };
})();

// ----- Cartões -----
// Só guarda bandeira, últimos 4 dígitos, nome e validade. O número inteiro e o CVV
// são conferidos e jogados fora na hora (nunca vão pro localStorage).
const CartoesStore = (() => {
    const CHAVE = 'sebo_do_joao_cartoes';
    const LIMITE = 6;

    async function listar() {
        const usuario = await UsuarioStore.obter();
        if (!usuario) return [];
        return lerLista(CHAVE)
            .filter((c) => String(c.usuarioId) === String(usuario.id))
            .sort((a, b) => (b.padrao === true) - (a.padrao === true) || b.id - a.id);
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
        const usuario = await usuarioLogadoOuErro();
        const limpo = conferirCartao(dados);

        const todos = lerLista(CHAVE);
        const meus = todos.filter((c) => String(c.usuarioId) === String(usuario.id));
        if (meus.some((c) => c.bandeira === limpo.bandeira && c.ultimos4 === limpo.ultimos4 && c.validade === limpo.validade)) {
            throw new Error('Esse cartão já está salvo na sua conta.');
        }
        if (meus.length >= LIMITE) throw new Error(`Você pode guardar até ${LIMITE} cartões. Exclua algum pra adicionar outro.`);

        const registro = { id: proximoIdDaLista(todos), usuarioId: usuario.id, padrao: meus.length === 0, ...limpo };
        todos.push(registro);
        if (dados.padrao) {
            todos.forEach((c) => { if (String(c.usuarioId) === String(usuario.id)) c.padrao = c === registro; });
        }
        salvarLista(CHAVE, todos);
        return registro;
    }

    async function definirPadrao(id) {
        const usuario = await usuarioLogadoOuErro();
        const todos = lerLista(CHAVE);
        if (!todos.some((c) => String(c.id) === String(id) && String(c.usuarioId) === String(usuario.id))) return;
        todos.forEach((c) => {
            if (String(c.usuarioId) === String(usuario.id)) c.padrao = String(c.id) === String(id);
        });
        salvarLista(CHAVE, todos);
    }

    async function remover(id) {
        const usuario = await usuarioLogadoOuErro();
        let todos = lerLista(CHAVE);
        const removido = todos.find((c) => String(c.id) === String(id) && String(c.usuarioId) === String(usuario.id));
        if (!removido) return;
        todos = todos.filter((c) => c !== removido);
        if (removido.padrao) {
            const sobra = todos.filter((c) => String(c.usuarioId) === String(usuario.id)).sort((a, b) => b.id - a.id)[0];
            if (sobra) sobra.padrao = true;
        }
        salvarLista(CHAVE, todos);
    }

    function removerDaConta(usuarioId) {
        salvarLista(CHAVE, lerLista(CHAVE).filter((c) => String(c.usuarioId) !== String(usuarioId)));
    }

    return { CHAVE, LIMITE, listar, obter, conferirCartao, salvar, definirPadrao, remover, removerDaConta };
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

    return [
        {
            id: 'economico', nome: 'Econômico', preco: gratis ? 0 : tabela.eco[0],
            prazoTexto: prazo(tabela.eco[1], tabela.eco[2]),
            observacao: gratis ? `Frete grátis em compras a partir de ${formatarPreco(VALOR_PARA_FRETE_GRATIS)}` : ''
        },
        { id: 'expresso', nome: 'Expresso', preco: tabela.exp[0], prazoTexto: prazo(tabela.exp[1], tabela.exp[2]), observacao: '' },
        {
            id: 'retirada', nome: 'Retirar com o vendedor', preco: 0,
            prazoTexto: 'Combine a retirada com o Sebo do João depois da compra', observacao: ''
        }
    ];
}

// Parcelas sem juros: até 6x, com parcela mínima de R$ 20
function calcularParcelas(total) {
    const maximo = Math.max(1, Math.min(6, Math.floor(total / 20)));
    return Array.from({ length: maximo }, (_, i) => ({ quantidade: i + 1, valor: total / (i + 1) }));
}

// ----- Pedidos -----
const PedidosStore = (() => {
    const CHAVE = 'sebo_do_joao_pedidos';
    const STATUS_AGUARDANDO = 'Aguardando pagamento';
    const STATUS_APROVADO = 'Pagamento aprovado';

    async function listar() {
        const usuario = await UsuarioStore.obter();
        if (!usuario) return [];
        return lerLista(CHAVE)
            .filter((p) => String(p.usuarioId) === String(usuario.id))
            .sort((a, b) => b.id - a.id);
    }

    async function obter(id) {
        return (await listar()).find((p) => String(p.id) === String(id)) || null;
    }

    // Código "copia e cola" de exemplo (parece um Pix de verdade, mas é fictício)
    function gerarCodigoPix(total, codigoDoPedido) {
        const valor = total.toFixed(2);
        const aleatorio = Array.from(crypto.getRandomValues(new Uint8Array(8)))
            .map((b) => b.toString(16).padStart(2, '0')).join('');
        return '00020126580014br.gov.bcb.pix0136' + aleatorio + '-sebo-do-joao-demo' +
            '5204000053039865406' + valor.replace('.', '') + '5802BR5913SEBO DO JOAO6009SAO PAULO' +
            '62' + String(4 + codigoDoPedido.length).padStart(2, '0') + '05' + String(codigoDoPedido.length).padStart(2, '0') + codigoDoPedido + '6304DEMO';
    }

    // Monta o pedido a partir do carrinho. TUDO é conferido de novo aqui (preços, estoque,
    // frete, parcelas...) em vez de confiar na tela: no backend é o servidor que faz isso.
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
        let pagamentoFinal;
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

            if (pagamento.cartaoNovo && pagamento.cartaoNovo.salvar) {
                try { await CartoesStore.salvar(pagamento.cartaoNovo); } catch (erro) { /* já salvo ou limite: a compra segue */ }
            }
            pagamentoFinal = {
                tipo: 'cartao', bandeira: cartao.bandeira, ultimos4: cartao.ultimos4,
                parcelas: parcelas.quantidade, valorParcela: parcelas.valor
            };
        } else if (pagamento && pagamento.tipo === 'pix') {
            pagamentoFinal = { tipo: 'pix' };
        } else {
            throw new Error('Escolha uma forma de pagamento.');
        }

        // ----- grava o pedido e dá baixa no estoque -----
        const todos = lerLista(CHAVE);
        const id = proximoIdDaLista(todos);
        const codigo = 'SJ-' + String(id).padStart(5, '0');
        if (pagamentoFinal.tipo === 'pix') pagamentoFinal.pixCodigo = gerarCodigoPix(total, codigo);

        const pedido = {
            id, codigo, usuarioId: usuario.id, criadoEm: new Date().toISOString(),
            status: pagamentoFinal.tipo === 'pix' ? STATUS_AGUARDANDO : STATUS_APROVADO,
            itens: itens.map(({ produto, quantidade }) => ({
                id: produto.id, nome: produto.nome, preco: produto.preco, quantidade,
                categoria: produto.categoria || '', icone: produto.icone || 'fa-box'
            })),
            subtotal,
            frete: { id: frete.id, nome: frete.nome, preco: frete.preco, prazoTexto: frete.prazoTexto },
            total,
            endereco: { ...endereco, id: undefined, usuarioId: undefined, padrao: undefined },
            pagamento: pagamentoFinal
        };
        todos.push(pedido);
        salvarLista(CHAVE, todos);

        for (const { produto, quantidade } of itens) {
            if (typeof produto.estoque === 'number') await darBaixaNoEstoque(produto, quantidade);
        }
        await CarrinhoStore.limpar();
        return pedido;
    }

    // Dá baixa no estoque pela API de produtos (a mesma rota de edição que o administrador usa:
    // POST multipart /api/produtos/{id}, que pede todos os campos). Se a baixa falhar, a compra já
    // foi registrada e segue; o ideal é a baixa ficar com o backend, dentro do POST /api/pedidos.
    async function darBaixaNoEstoque(produto, quantidade) {
        try {
            const dados = new FormData();
            dados.append('nome', produto.nome);
            dados.append('categoriaId', produto.categoriaId);
            dados.append('generoId', produto.generoId);
            dados.append('funcionarioId', produto.funcionarioId);
            dados.append('preco', produto.preco);
            dados.append('estoque', Math.max(0, produto.estoque - quantidade));
            dados.append('status', produto.status || 'DISPONIVEL');
            dados.append('descricao', produto.descricao || '');
            await ProdutosStore.atualizar(produto.id, dados);
        } catch (erro) {
            console.warn(`Não consegui dar baixa no estoque do produto ${produto.id}.`, erro);
        }
    }

    // Só pra demonstração: "paga" um Pix pendente (no mundo real quem avisa é o banco/gateway)
    async function simularPagamentoDoPix(id) {
        const usuario = await usuarioLogadoOuErro();
        const todos = lerLista(CHAVE);
        const pedido = todos.find((p) => String(p.id) === String(id) && String(p.usuarioId) === String(usuario.id));
        if (!pedido || pedido.status !== STATUS_AGUARDANDO) return pedido || null;
        pedido.status = STATUS_APROVADO;
        salvarLista(CHAVE, todos);
        return pedido;
    }

    function removerDaConta(usuarioId) {
        salvarLista(CHAVE, lerLista(CHAVE).filter((p) => String(p.usuarioId) !== String(usuarioId)));
    }

    return { CHAVE, STATUS_AGUARDANDO, STATUS_APROVADO, listar, obter, criar, simularPagamentoDoPix, removerDaConta };
})();

// Tudo que pertence a uma conta que está sendo excluída
function apagarDadosDaConta(usuarioId) {
    CarrinhoStore.apagarDaConta(usuarioId);
    EnderecosStore.removerDaConta(usuarioId);
    CartoesStore.removerDaConta(usuarioId);
    PedidosStore.removerDaConta(usuarioId);
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
    // CORREÇÃO: Usar produto.nome para ler o atributo correto do Java
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
