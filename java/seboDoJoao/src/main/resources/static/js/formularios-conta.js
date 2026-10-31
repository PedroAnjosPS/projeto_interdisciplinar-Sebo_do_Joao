// =====================================================================
// formularios-conta.js — janelas (modais) de endereço, cartão e confirmação
//
// Usadas pelas telas Endereços, Cartões e Finalizar compra. Depende do
// vitrine.js (carregue ele antes) e do Bootstrap JS:
//     <script src="../../js/vitrine.js"></script>
//     <script src="../../js/formularios-conta.js"></script>
//
// Cada função abre a janela e devolve uma Promise:
//     const endereco = await abrirModalEndereco();      // endereço salvo, ou null se cancelou
//     const cartao   = await abrirModalCartao();        // cartão salvo, ou null
//     const sim      = await confirmarAcao({...});      // true / false
// =====================================================================

// Quando a pessoa clica num botão ou caixinha (Salvar, "Sem número"...) com um campo ainda em foco, o campo perde o foco,
// a mensagem de erro aparece e empurra o botão pra baixo ANTES do clique terminar: o clique se perde.
// Por isso, enquanto o mouse/dedo está apertando um deles, os campos não mostram erro ao perder o foco
// (o envio do formulário confere tudo do mesmo jeito).
let apertandoBotao = false;
document.addEventListener('pointerdown', (evento) => {
    apertandoBotao = Boolean(evento.target.closest && evento.target.closest("button, label, a, input[type='checkbox'], input[type='radio']"));
}, true);
['pointerup', 'pointercancel', 'click'].forEach((nome) => document.addEventListener(nome, () => {
    setTimeout(() => { apertandoBotao = false; }, 0);
}, true));

// Mostra (ou limpa) o erro embaixo de um campo e pinta a borda de vermelho
function mostrarErroDoCampo(id, mensagem) {
    const campo = document.getElementById(id);
    const area = document.getElementById(`erro-${id}`);
    if (campo) {
        campo.classList.toggle('is-invalid', Boolean(mensagem));
        campo.setAttribute('aria-invalid', mensagem ? 'true' : 'false');
    }
    if (area) area.textContent = mensagem || '';
}

// <label> + <input> + área do erro, no padrão visual dos formulários do site
function campoDeFormulario({ id, rotulo, tipo = 'text', placeholder = '', atributos = '', classe = 'col-12' }) {
    return `
        <div class="${classe}">
            <label class="ec-admin-label" for="${id}">${rotulo}</label>
            <input id="${id}" type="${tipo}" class="form-control ec-admin-input" placeholder="${placeholder}"
                aria-describedby="erro-${id}" ${atributos}>
            <div id="erro-${id}" class="ec-form-erro" role="alert"></div>
        </div>`;
}

// Ícone da bandeira do cartão (Font Awesome tem Visa, Mastercard, Amex e Diners)
function iconeDaBandeira(bandeira) {
    const icones = { visa: 'fa-cc-visa', mastercard: 'fa-cc-mastercard', amex: 'fa-cc-amex', diners: 'fa-cc-diners-club' };
    return icones[bandeira]
        ? `<i class="fab ${icones[bandeira]} ec-bandeira-icone" title="${NOMES_DAS_BANDEIRAS[bandeira]}"></i>`
        : `<i class="fas fa-credit-card ec-bandeira-icone" title="${NOMES_DAS_BANDEIRAS[bandeira] || 'Cartão'}"></i>`;
}

// Cria a janela, mostra e devolve uma Promise que só resolve quando ela fecha.
// "montar(elemento, fechar)" liga os botões; chame fechar(valor) pra devolver um resultado.
function abrirJanela(htmlDoDialogo, montar, opcoes = {}) {
    return new Promise((resolver) => {
        const elemento = document.createElement('div');
        elemento.className = 'modal fade';
        elemento.tabIndex = -1;
        elemento.setAttribute('aria-hidden', 'true');
        elemento.innerHTML = `<div class="modal-dialog modal-dialog-centered ${opcoes.tamanho || ''}">${htmlDoDialogo}</div>`;
        document.body.appendChild(elemento);

        const modal = new bootstrap.Modal(elemento, { backdrop: 'static' });
        let resultado = null;

        elemento.addEventListener('hidden.bs.modal', () => {
            modal.dispose();
            elemento.remove();
            resolver(resultado);
        });
        elemento.addEventListener('shown.bs.modal', () => {
            const primeiro = elemento.querySelector('[data-foco]');
            if (primeiro) primeiro.focus();
        });

        montar(elemento, (valor) => { resultado = valor === undefined ? null : valor; modal.hide(); });
        modal.show();
    });
}

// ----- Confirmação (excluir endereço, cartão...) -----
function confirmarAcao({ titulo, texto, botao = 'Excluir' }) {
    const html = `
        <div class="modal-content ec-card ec-card-shadow border-0">
            <div class="modal-body text-center p-4 p-md-5">
                <i class="fas fa-triangle-exclamation fs-1 mb-3 ec-icone-perigo"></i>
                <h5 class="ec-heading-dark fw-bold mb-2">${escaparHtml(titulo)}</h5>
                <p class="mb-4 ec-texto-suave">${escaparHtml(texto)}</p>
                <div class="d-flex justify-content-center gap-2 flex-wrap">
                    <button type="button" class="btn ec-btn-outline-primary ec-btn-pill px-4" data-acao="cancelar">Cancelar</button>
                    <button type="button" class="btn ec-btn-perigo ec-btn-pill px-4" data-acao="confirmar" data-foco>${escaparHtml(botao)}</button>
                </div>
            </div>
        </div>`;
    return abrirJanela(html, (el, fechar) => {
        el.querySelector('[data-acao="cancelar"]').addEventListener('click', () => fechar(false));
        el.querySelector('[data-acao="confirmar"]').addEventListener('click', () => fechar(true));
    }, { tamanho: 'modal-sm' }).then((r) => r === true);
}

// ----- Endereço -----

// Procura o CEP no ViaCEP (serviço gratuito). Se falhar (sem internet, CEP novo...),
// devolve null e a pessoa preenche na mão.
async function buscarCepNoViaCep(cep) {
    const digitos = somenteDigitos(cep);
    if (digitos.length !== 8) return null;
    const controle = new AbortController();
    const limite = setTimeout(() => controle.abort(), 5000);
    try {
        const resposta = await fetch(`https://viacep.com.br/ws/${digitos}/json/`, { signal: controle.signal });
        if (!resposta.ok) return null;
        const dados = await resposta.json();
        return dados && !dados.erro ? dados : null;
    } catch (erro) {
        return null;
    } finally {
        clearTimeout(limite);
    }
}

function abrirModalEndereco(existente = null) {
    const editando = Boolean(existente);
    const opcoesUf = ['<option value="">UF</option>']
        .concat(UFS.map((uf) => `<option value="${uf}">${uf}</option>`)).join('');

    const html = `
        <div class="modal-content ec-card ec-card-shadow border-0">
            <form id="form-endereco" novalidate>
                <div class="modal-body p-4 p-md-5">
                    <h5 class="ec-heading-dark fw-bold mb-1">${editando ? 'Editar endereço' : 'Novo endereço'}</h5>
                    <p class="ec-texto-suave mb-4">Digite o CEP que a gente preenche o resto pra você.</p>

                    <div class="row g-3">
                        ${campoDeFormulario({ id: 'end-cep', rotulo: 'CEP', placeholder: '00000-000', classe: 'col-sm-5',
                            atributos: 'inputmode="numeric" maxlength="9" autocomplete="postal-code" data-foco' })}
                        <div class="col-sm-7 d-flex align-items-end">
                            <div id="end-cep-aviso" class="ec-form-dica mb-3" role="status"></div>
                        </div>

                        ${campoDeFormulario({ id: 'end-rua', rotulo: 'Rua ou avenida', atributos: 'maxlength="100" autocomplete="address-line1"' })}

                        <div class="col-sm-4">
                            <label class="ec-admin-label" for="end-numero">Número</label>
                            <input id="end-numero" type="text" class="form-control ec-admin-input" maxlength="10"
                                aria-describedby="erro-end-numero" inputmode="numeric">
                            <div id="erro-end-numero" class="ec-form-erro" role="alert"></div>
                            <div class="form-check mt-1">
                                <input class="form-check-input" type="checkbox" id="end-sem-numero">
                                <label class="form-check-label ec-texto-suave" for="end-sem-numero">Sem número</label>
                            </div>
                        </div>
                        ${campoDeFormulario({ id: 'end-complemento', rotulo: 'Complemento (opcional)', classe: 'col-sm-8',
                            placeholder: 'Apto, bloco, referência...', atributos: 'maxlength="60"' })}

                        ${campoDeFormulario({ id: 'end-bairro', rotulo: 'Bairro', classe: 'col-sm-5', atributos: 'maxlength="100"' })}
                        ${campoDeFormulario({ id: 'end-cidade', rotulo: 'Cidade', classe: 'col-sm-4', atributos: 'maxlength="100"' })}
                        <div class="col-sm-3">
                            <label class="ec-admin-label" for="end-uf">Estado</label>
                            <select id="end-uf" class="form-select ec-admin-input" aria-describedby="erro-end-uf">${opcoesUf}</select>
                            <div id="erro-end-uf" class="ec-form-erro" role="alert"></div>
                        </div>

                        ${campoDeFormulario({ id: 'end-destinatario', rotulo: 'Quem vai receber', atributos: 'maxlength="100" autocomplete="name"' })}
                        ${campoDeFormulario({ id: 'end-telefone', rotulo: 'Telefone de contato', tipo: 'tel', classe: 'col-sm-6',
                            placeholder: '(00) 00000-0000', atributos: 'inputmode="numeric" autocomplete="tel"' })}
                        ${campoDeFormulario({ id: 'end-apelido', rotulo: 'Nome do endereço (opcional)', classe: 'col-sm-6',
                            placeholder: 'Casa, Trabalho...', atributos: 'maxlength="30"' })}

                        <div class="col-12">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" id="end-padrao">
                                <label class="form-check-label" for="end-padrao">Usar como meu endereço padrão</label>
                            </div>
                        </div>
                    </div>

                    <div id="end-mensagem" class="mt-3" role="status"></div>

                    <div class="d-flex justify-content-end gap-2 flex-wrap mt-4">
                        <button type="button" class="btn ec-btn-outline-primary ec-btn-pill px-4" data-acao="cancelar">Cancelar</button>
                        <button type="submit" class="btn ec-btn-dark ec-btn-pill px-4">Salvar endereço</button>
                    </div>
                </div>
            </form>
        </div>`;

    return abrirJanela(html, async (el, fechar) => {
        const $ = (id) => el.querySelector('#' + id);
        const campos = ['cep', 'rua', 'numero', 'complemento', 'bairro', 'cidade', 'uf', 'destinatario', 'telefone', 'apelido'];

        // Preenche (edição) ou sugere nome e telefone da conta (novo endereço)
        const usuario = await UsuarioStore.obter();
        const base = existente || { destinatario: usuario ? usuario.nome : '', telefone: usuario ? usuario.telefone : '' };
        campos.forEach((campo) => { if (base[campo] != null) $('end-' + campo).value = base[campo]; });
        $('end-sem-numero').checked = Boolean(existente && existente.semNumero);
        $('end-numero').disabled = $('end-sem-numero').checked;
        const ehOUnico = (await EnderecosStore.listar()).length === 0;
        $('end-padrao').checked = Boolean(existente ? existente.padrao : ehOUnico);
        $('end-padrao').disabled = Boolean(existente ? existente.padrao : ehOUnico); // o único (ou o atual padrão) já é padrão

        // Máscaras
        ligarMascara($('end-cep'), formatarCep);
        ligarMascara($('end-telefone'), formatarTelefone);

        // CEP completo -> busca o endereço
        let buscaAtual = 0;
        $('end-cep').addEventListener('input', async () => {
            const aviso = $('end-cep-aviso');
            if (somenteDigitos($('end-cep').value).length !== 8) { aviso.textContent = ''; return; }
            const numeroDaBusca = ++buscaAtual;
            aviso.textContent = 'Buscando endereço...';
            const dados = await buscarCepNoViaCep($('end-cep').value);
            if (numeroDaBusca !== buscaAtual) return; // a pessoa mudou o CEP no meio da busca
            if (!dados) {
                aviso.textContent = 'Não achamos esse CEP. Preencha o endereço abaixo.';
                return;
            }
            aviso.textContent = 'Endereço encontrado!';
            const preencher = (id, valor) => { if (valor) { $(id).value = valor; mostrarErroDoCampo(id, ''); } };
            preencher('end-rua', dados.logradouro);
            preencher('end-bairro', dados.bairro);
            preencher('end-cidade', dados.localidade);
            preencher('end-uf', dados.uf);
            mostrarErroDoCampo('end-cep', '');
            ($('end-rua').value ? $('end-numero') : $('end-rua')).focus();
        });

        $('end-sem-numero').addEventListener('change', () => {
            $('end-numero').disabled = $('end-sem-numero').checked;
            if ($('end-sem-numero').checked) { $('end-numero').value = ''; mostrarErroDoCampo('end-numero', ''); }
        });

        // Valida um campo ao sair dele (e vai corrigindo enquanto digita, se já estava com erro)
        const lerDados = () => ({
            cep: $('end-cep').value, rua: $('end-rua').value, numero: $('end-numero').value,
            semNumero: $('end-sem-numero').checked, complemento: $('end-complemento').value,
            bairro: $('end-bairro').value, cidade: $('end-cidade').value, uf: $('end-uf').value,
            destinatario: $('end-destinatario').value, telefone: $('end-telefone').value, apelido: $('end-apelido').value
        });
        campos.forEach((campo) => {
            const input = $('end-' + campo);
            const conferir = () => mostrarErroDoCampo('end-' + campo, errosDoEndereco(lerDados())[campo] || '');
            input.addEventListener('blur', () => { if (!apertandoBotao) conferir(); });
            input.addEventListener(input.tagName === 'SELECT' ? 'change' : 'input', () => {
                if (input.classList.contains('is-invalid')) conferir();
            });
        });

        el.querySelector('[data-acao="cancelar"]').addEventListener('click', () => fechar(null));

        $('form-endereco').addEventListener('submit', async (evento) => {
            evento.preventDefault();
            $('end-mensagem').innerHTML = '';

            const dados = lerDados();
            const erros = errosDoEndereco(dados);
            campos.forEach((campo) => mostrarErroDoCampo('end-' + campo, erros[campo] || ''));
            const primeiroComErro = campos.find((campo) => erros[campo]);
            if (primeiroComErro) { $('end-' + primeiroComErro).focus(); return; }

            const botao = $('form-endereco').querySelector('[type="submit"]');
            botao.disabled = true;
            try {
                const salvo = await EnderecosStore.salvar({
                    ...dados, id: existente ? existente.id : undefined, padrao: $('end-padrao').checked
                });
                fechar(salvo);
            } catch (erro) {
                $('end-mensagem').innerHTML = `<div class="alert alert-danger py-2 mb-0 small">${escaparHtml(erro.message)}</div>`;
                botao.disabled = false;
            }
        });
    }, { tamanho: 'modal-lg' });
}

// ----- Cartão -----

function abrirModalCartao() {
    const html = `
        <div class="modal-content ec-card ec-card-shadow border-0">
            <form id="form-cartao" novalidate>
                <div class="modal-body p-4 p-md-5">
                    <h5 class="ec-heading-dark fw-bold mb-1">Novo cartão</h5>
                    <p class="ec-texto-suave mb-4">
                        <i class="fas fa-lock me-1"></i>Guardamos só a bandeira e os 4 últimos números.
                        O número completo e o código de segurança não ficam salvos.
                    </p>

                    <div class="row g-3">
                        <div class="col-12">
                            <label class="ec-admin-label" for="cartao-numero">Número do cartão</label>
                            <div class="ec-campo-senha">
                                <input id="cartao-numero" type="text" class="form-control ec-admin-input" inputmode="numeric"
                                    autocomplete="cc-number" placeholder="0000 0000 0000 0000" maxlength="19"
                                    aria-describedby="erro-cartao-numero" data-foco>
                                <span id="cartao-bandeira" class="ec-bandeira-no-campo" aria-hidden="true"></span>
                            </div>
                            <div id="erro-cartao-numero" class="ec-form-erro" role="alert"></div>
                        </div>
                        ${campoDeFormulario({ id: 'cartao-titular', rotulo: 'Nome impresso no cartão',
                            atributos: 'maxlength="60" autocomplete="cc-name" style="text-transform: uppercase"' })}
                        ${campoDeFormulario({ id: 'cartao-validade', rotulo: 'Validade', classe: 'col-6',
                            placeholder: 'MM/AA', atributos: 'inputmode="numeric" maxlength="5" autocomplete="cc-exp"' })}
                        <div class="col-6 d-flex align-items-end">
                            <div class="form-check mb-3">
                                <input class="form-check-input" type="checkbox" id="cartao-padrao">
                                <label class="form-check-label" for="cartao-padrao">Cartão padrão</label>
                            </div>
                        </div>
                    </div>

                    <div id="cartao-mensagem" class="mt-2" role="status"></div>

                    <div class="d-flex justify-content-end gap-2 flex-wrap mt-4">
                        <button type="button" class="btn ec-btn-outline-primary ec-btn-pill px-4" data-acao="cancelar">Cancelar</button>
                        <button type="submit" class="btn ec-btn-dark ec-btn-pill px-4">Salvar cartão</button>
                    </div>
                </div>
            </form>
        </div>`;

    return abrirJanela(html, (el, fechar) => {
        const $ = (id) => el.querySelector('#' + id);
        const verificadores = {
            'cartao-numero': () => erroDoNumeroCartao($('cartao-numero').value),
            'cartao-titular': () => erroDoTitularDoCartao($('cartao-titular').value),
            'cartao-validade': () => erroDaValidade($('cartao-validade').value)
        };

        ligarMascara($('cartao-numero'), formatarNumeroCartao);
        ligarMascara($('cartao-validade'), formatarValidade);
        $('cartao-numero').addEventListener('input', () => {
            $('cartao-bandeira').innerHTML = bandeiraDoCartao($('cartao-numero').value)
                ? iconeDaBandeira(bandeiraDoCartao($('cartao-numero').value)) : '';
        });

        Object.keys(verificadores).forEach((id) => {
            const input = $(id);
            input.addEventListener('blur', () => { if (!apertandoBotao && input.value.trim()) mostrarErroDoCampo(id, verificadores[id]()); });
            input.addEventListener('input', () => { if (input.classList.contains('is-invalid')) mostrarErroDoCampo(id, verificadores[id]()); });
        });

        el.querySelector('[data-acao="cancelar"]').addEventListener('click', () => fechar(null));

        $('form-cartao').addEventListener('submit', async (evento) => {
            evento.preventDefault();
            $('cartao-mensagem').innerHTML = '';

            const ids = Object.keys(verificadores);
            ids.forEach((id) => mostrarErroDoCampo(id, verificadores[id]()));
            const primeiro = ids.find((id) => verificadores[id]());
            if (primeiro) { $(primeiro).focus(); return; }

            const botao = $('form-cartao').querySelector('[type="submit"]');
            botao.disabled = true;
            try {
                fechar(await CartoesStore.salvar({
                    numero: $('cartao-numero').value, titular: $('cartao-titular').value,
                    validade: $('cartao-validade').value, padrao: $('cartao-padrao').checked
                }));
            } catch (erro) {
                $('cartao-mensagem').innerHTML = `<div class="alert alert-danger py-2 mb-0 small">${escaparHtml(erro.message)}</div>`;
                botao.disabled = false;
            }
        });
    });
}
