// ===== INÍCIO: usuario-store =====
// Contas de usuário e login (mesma ideia do ProdutosStore).
//
// As contas ficam no banco de dados e as funções daqui chamam a API Java
// (POST /api/clientes, POST /api/login, GET /api/clientes/{id}...). O que fica
// no localStorage do navegador é só uma cópia dos dados de quem está logado
// (sem a senha), usada para ajustar as telas.
//
// Existe um só login pra todo mundo. Cada conta tem um "perfil": CLIENTE ou
// FUNCIONARIO (guardado em PERFIL_ADMIN). Quem entra como funcionário vai pro
// painel dele e, pela loja, só visualiza os produtos: funcionário não usa carrinho
// nem compra. Dentro dos funcionários existe o administrador (campo "admin").
// O perfil guardado aqui serve só para ajustar as telas. Quem garante a regra é
// o Java, com o perfil da sessão (veja ControleDeAcesso.java).
//
// As contas de funcionário e de administrador vêm do banco
// (veja docs/sql/SeboDoJoao_INSERTS.sql).
const UsuarioStore = (() => {
    // As rotas da API devolvem {"mensagem": "..."} quando recusam algo; essa mensagem é mostrada à pessoa
    async function mensagemDoErro(resposta, padrao) {
        const corpo = await resposta.json().catch(() => ({}));
        return corpo.mensagem || padrao;
    }

    // A chave de sessão vai guardar quem está logado no momento
    const CHAVE_SESSAO = 'sebo_do_joao_sessao';
    const PERFIL_ADMIN = 'FUNCIONARIO';
    const PERFIL_CLIENTE = 'CLIENTE';
    const CHAVES = [CHAVE_SESSAO];
    
    // Salva os dados básicos do usuário logado no navegador para manter a sessão 
    // A API também devolve a senha, mas ela nunca deve ficar guardada no navegador.
    function iniciarSessao(dadosUsuario) {
        // A API devolve a senha junto com os dados do cliente; ela não precisa (nem deve) ficar guardada no navegador
        const { senha, ...semSenha } = dadosUsuario;
        localStorage.setItem(CHAVE_SESSAO, JSON.stringify(semSenha));
        // O carrinho do visitante não é juntado aqui, pois este método também roda ao atualizar o perfil.
        // A junção é uma chamada ao servidor e acontece só no login, na função entrar().
    }
    
    function lerSessao() {
        try {
            return JSON.parse(localStorage.getItem(CHAVE_SESSAO));
        } catch (erro) {
            return null;
        }
    }

    function ehAdmin(usuario) {
        return Boolean(usuario) && usuario.perfil === PERFIL_ADMIN;
    }

    // Chama o Java para registrar um novo Cliente (@PostMapping("/api/clientes"))
    async function cadastrar(dados) {
        const resposta = await fetch('/api/clientes', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                nome: dados.nome,
                email: dados.email,
                senha: dados.senha, // O Java deve salvar e criptografar
                telefone: dados.telefone,
                cpf: dados.cpf,
                dataNascimento: dados.dataNascimento // AAAA-MM-DD
            })
        });

        if (!resposta.ok) {
            throw new Error(await mensagemDoErro(resposta, 'Erro ao cadastrar. Tente novamente.'));
        }

        // Como acabou de se cadastrar, já fazemos o login automático. Ele passa pelo
        // /api/login para o Java também criar a sessão com o perfil do novo cliente.
        return entrar(dados.email, dados.senha);
    }

    // Chama uma rota unificada de login no Java (@PostMapping("/api/login"))
    async function entrar(email, senha) {
        const resposta = await fetch('/api/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, senha })
        });

        if (!resposta.ok) {
            // 403 = conta inativa (funcionário inativado pelo administrador ou cliente que encerrou a conta)
            throw new Error(resposta.status === 403
                ? 'Esta conta está inativa e não pode mais entrar. Em caso de dúvida, fale com a loja.'
                : 'E-mail ou senha incorretos.');
        }

        // O Java deve devolver os dados do usuário e qual é o perfil dele (CLIENTE ou FUNCIONARIO)
        let dadosUsuario = await resposta.json();

        // O /api/login só devolve id, nome, e-mail e perfil. Pro perfil, o checkout e o resto das
        // telas, buscamos o restante uma vez só, aqui: CPF, telefone, data de nascimento e foto
        // do cliente, ou o código do funcionário (o "número do crachá").
        try {
            const rotaCompleta = dadosUsuario.perfil === PERFIL_CLIENTE ? '/api/clientes' : '/api/funcionarios';
            const completa = await fetch(`${rotaCompleta}/${dadosUsuario.id}`);
            if (completa.ok) dadosUsuario = { ...(await completa.json()), perfil: dadosUsuario.perfil };
        } catch (erro) { /* sem os dados extras a pessoa entra do mesmo jeito */ }

        iniciarSessao(dadosUsuario);

        // O que a pessoa pôs no carrinho como visitante é enviado para o carrinho da conta, que fica no
        // banco de dados (CarrinhoStore fica em vitrine.js). Só vale para cliente; funcionário não usa carrinho.
        if (dadosUsuario.perfil === PERFIL_CLIENTE && typeof CarrinhoStore !== 'undefined' && dadosUsuario.id != null) {
            await CarrinhoStore.juntarVisitanteNaConta(dadosUsuario.id);
        }
        return dadosUsuario;
    }

    function sair() {
        localStorage.removeItem(CHAVE_SESSAO);
        // Encerra também a sessão no Java. O "keepalive" deixa a requisição terminar
        // mesmo com a tela mudando de página logo depois do clique em "Sair".
        fetch('/api/login/sair', { method: 'POST', keepalive: true }).catch(() => {});
    }

    // Retorna quem está logado agora
    async function obter() {
        return lerSessao();
    }

    // Busca no banco os dados completos de quem está logado. O login traz só o básico, então
    // a tela de perfil abria com CPF, telefone e nascimento vazios. Ao salvar, esses campos
    // vazios apagavam os dados reais ou davam erro. Sem conexão, segue com o que já está na sessão.
    async function recarregar() {
        const usuarioLogado = lerSessao();
        if (!usuarioLogado) return null;

        const rota = usuarioLogado.perfil === PERFIL_CLIENTE ? '/api/clientes' : '/api/funcionarios';

        try {
            const resposta = await fetch(`${rota}/${usuarioLogado.id}`);
            if (!resposta.ok) return usuarioLogado;

            iniciarSessao({ ...(await resposta.json()), perfil: usuarioLogado.perfil });
            return lerSessao();
        } catch (erro) {
            return usuarioLogado;
        }
    }

    // Chama o Java para atualizar os dados (@PutMapping("/api/clientes/{id}"))
    async function salvar(dados) {
        const usuarioLogado = lerSessao();
        if (!usuarioLogado) throw new Error('Você precisa estar logado.');

        // Se for funcionário, a rota seria /api/funcionarios
        const rota = usuarioLogado.perfil === PERFIL_CLIENTE ? '/api/clientes' : '/api/funcionarios';

        // Configurações base do fetch
        const opcoesFetch = {
            method: (dados instanceof FormData) ? 'POST' : 'PUT' // <-- A MÁGICA ESTÁ AQUI
        };

        if (dados instanceof FormData) {
            opcoesFetch.body = dados;
        } else {
            opcoesFetch.headers = { 'Content-Type': 'application/json' };
            opcoesFetch.body = JSON.stringify(dados);
        }

        const resposta = await fetch(`${rota}/${usuarioLogado.id}`, opcoesFetch);

        if (!resposta.ok) throw new Error(await mensagemDoErro(resposta, 'Erro ao atualizar os dados.'));

        const usuarioAtualizado = await resposta.json();
        iniciarSessao({ ...usuarioAtualizado, perfil: usuarioLogado.perfil });
        return lerSessao();
    }

    // Troca a senha da conta logada (cliente ou funcionário), cada um pela sua rota
    async function alterarSenha(senhaAtual, novaSenha, confirmacao) {
        const usuarioLogado = lerSessao();
        if (!usuarioLogado) throw new Error("Você precisa estar logado para alterar a senha!");

        // A senha atual é conferida no servidor (mesma rota do login), sem guardar senha no navegador.
        if (!(await senhaConfere(usuarioLogado.email, senhaAtual))) {
            throw new Error('A senha atual está incorreta.');
        }

        const erroSenha = erroDaSenhaNova(novaSenha);
        if (erroSenha) throw new Error(erroSenha);

        if (novaSenha === senhaAtual) {
            throw new Error('A nova senha precisa ser diferente da atual!');
        }

        if (novaSenha !== confirmacao) {
            throw new Error('A confirmação não é igual à nova senha!');
        }

        // Só a senha é enviada: os demais campos que não vierem no JSON são mantidos como estão no banco.
        const dadosAtualizados = { senha: novaSenha };

        const rota = usuarioLogado.perfil === PERFIL_CLIENTE ? '/api/clientes' : '/api/funcionarios';

        const resposta = await fetch(`${rota}/${usuarioLogado.id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dadosAtualizados)
        });

        if (!resposta.ok) throw new Error(await mensagemDoErro(resposta, 'Erro ao alterar a senha.'));

        const usuarioAtualizado = await resposta.json();
        const novaSessao = { ...usuarioAtualizado, perfil: usuarioLogado.perfil };
        iniciarSessao(novaSessao);
    }

    // ----- Funções das telas de Segurança e Privacidade e do cadastro em 2 etapas -----

    // Confere e-mail + senha no servidor (a mesma rota do login), sem alterar a sessão.
    async function senhaConfere(email, senha) {
        if (!senha) return false;
        const resposta = await fetch('/api/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, senha })
        });
        return resposta.ok;
    }

    // Diz se o e-mail ainda está livre (o cadastro usa isso já na 1ª etapa, pra pessoa não
    // preencher tudo e só descobrir no final que o e-mail está em uso).
    // A API tem uma rota só pra isso (a lista de contas não é aberta a quem não está logado).
    // Se a consulta falhar, devolve true e quem decide é o servidor, na hora de cadastrar.
    async function emailDisponivel(email, ignorarId = null) {
        const procurado = String(email || '').trim();
        try {
            let url = '/api/clientes/email-disponivel?email=' + encodeURIComponent(procurado);
            if (ignorarId != null) url += '&ignorarId=' + encodeURIComponent(ignorarId);
            const resposta = await fetch(url);
            if (!resposta.ok) return true;
            const resultado = await resposta.json();
            return resultado.disponivel !== false;
        } catch (erro) { /* deixa o servidor decidir */ }
        return true;
    }

    // Troca o e-mail de acesso (pede a senha). Devolve o usuário atualizado.
    async function alterarEmail(novoEmail, senha) {
        const erro = erroDoEmail(novoEmail);
        if (erro) throw new Error(erro);

        const usuarioLogado = lerSessao();
        if (!usuarioLogado) throw new Error('Você precisa estar logado para fazer isso.');
        if (!senha) throw new Error('Digite a sua senha para confirmar.');
        if (!(await senhaConfere(usuarioLogado.email, senha))) throw new Error('A senha está incorreta.');

        const email = String(novoEmail).trim().toLowerCase();
        if (email === String(usuarioLogado.email).toLowerCase()) throw new Error('Esse já é o e-mail da sua conta.');
        if (!(await emailDisponivel(email, usuarioLogado.id))) throw new Error('Já existe outra conta com esse e-mail.');

        // Cliente: rota de atualização do perfil (multipart). Funcionário: PUT com JSON.
        let resposta;
        if (usuarioLogado.perfil === PERFIL_CLIENTE) {
            const formData = new FormData();
            formData.append('email', email);
            resposta = await fetch(`/api/clientes/${usuarioLogado.id}`, { method: 'POST', body: formData });
        } else {
            resposta = await fetch(`/api/funcionarios/${usuarioLogado.id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email })
            });
        }
        if (!resposta.ok) throw new Error(await mensagemDoErro(resposta, 'Não foi possível trocar o e-mail agora. Tente novamente.'));

        const atualizado = await resposta.json();
        const novaSessao = { ...usuarioLogado, ...atualizado, perfil: usuarioLogado.perfil };
        iniciarSessao(novaSessao);
        return novaSessao;
    }

    // Encerra a conta logada (pede a senha). A conta do funcionário não pode. Sem pedidos a conta é
    // apagada; com pedidos ela só fica inativa. Devolve true nesse segundo caso.
    async function excluirConta(senha) {
        const usuarioLogado = lerSessao();
        if (!usuarioLogado) throw new Error('Você precisa estar logado para fazer isso.');
        if (usuarioLogado.perfil !== PERFIL_CLIENTE) throw new Error('A conta do administrador não pode ser excluída.');
        if (!senha) throw new Error('Digite a sua senha para confirmar.');
        if (!(await senhaConfere(usuarioLogado.email, senha))) throw new Error('A senha está incorreta.');

        const resposta = await fetch(`/api/clientes/${usuarioLogado.id}`, { method: 'DELETE' });
        if (!resposta.ok) throw new Error(await mensagemDoErro(resposta, 'Não foi possível excluir a conta agora. Tente novamente.'));

        const resultado = await resposta.json().catch(() => ({}));

        apagarDadosDaConta(usuarioLogado.id); // endereços, cartões, pedidos e carrinho da conta (vitrine.js)
        sair();
        return resultado.inativada === true;
    }

    // Apenas exporta as funções

    // - lerSessao: versão sem await de obter(), para quem monta HTML, como os cards de produto.
    // - CHAVES: o vitrine.js usa para acompanhar login e saída feitos em outra aba. Ela tinha
    //   ficado de fora e isso causava erro sempre que o localStorage mudava em outra aba.
    


    return { CHAVES, PERFIL_ADMIN, emailDisponivel, cadastrar, entrar, sair, obter, lerSessao, recarregar, salvar, alterarSenha, alterarEmail, excluirConta, ehAdmin };
})();
// ===== FIM: usuario-store =====
