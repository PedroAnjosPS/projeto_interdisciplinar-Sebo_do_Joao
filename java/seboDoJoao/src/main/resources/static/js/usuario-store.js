// ===== INÍCIO: usuario-store =====
// Contas de usuário e login (mesma ideia do ProdutosStore).
//
// Hoje tudo fica no localStorage do navegador: as contas criadas aqui só
// existem nesse computador. A senha nunca é guardada pura (vira um hash
// SHA-256 com "sal"), mas isso é só pra demonstração — num site de verdade
// quem guarda e confere a senha é o backend (Java + Spring Boot, com
// BCrypt, por exemplo), e estas funções viram chamadas à API
// (POST /usuarios, POST /login, GET /usuarios/me...).
//
// Existe um só login pra todo mundo. Cada conta tem um "perfil": CLIENTE (padrão)
// ou ADMIN. Quem entra com uma conta ADMIN vai pro painel do administrador e,
// pela loja, continua usando a conta normalmente (perfil, carrinho...).
// No backend Java isso vira um papel (role) do Spring Security.
//
// Conta do administrador (criada sozinha na primeira vez que o site é usado):
//     e-mail: sebodojoao@gmail.com   senha inicial: sebodojoao
// (a senha dá pra trocar em "Alterar senha" no painel do administrador)
const UsuarioStore = (() => {
    // A chave de sessão vai guardar quem está logado no momento
    const CHAVE_SESSAO = 'sebo_do_joao_sessao';
    const PERFIL_ADMIN = 'FUNCIONARIO';
    const PERFIL_CLIENTE = 'CLIENTE';
    
    // Salva os dados básicos do usuário logado no navegador para manter a sessão 
    function iniciarSessao(dadosUsuario) {
        localStorage.setItem(CHAVE_SESSAO, JSON.stringify(dadosUsuario));
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
                senha: dados.senha // O Java deve salvar e criptografar
            })
        });

        if (!resposta.ok) {
            throw new Error('Erro ao cadastrar. O e-mail pode já estar em uso.');
        }

        const clienteSalvo = await resposta.json();
        
        // Como acabou de se cadastrar, já fazemos o "login" automático
        const dadosSessao = { ...clienteSalvo, perfil: PERFIL_CLIENTE };
        iniciarSessao(dadosSessao);
        return dadosSessao;
    }

    // Chama uma rota unificada de login no Java (@PostMapping("/api/login"))
    async function entrar(email, senha) {
        const resposta = await fetch('/api/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, senha })
        });

        if (!resposta.ok) {
            throw new Error('E-mail ou senha incorretos.');
        }

        // O Java deve devolver os dados do usuário e qual é o perfil dele (CLIENTE ou FUNCIONARIO)
        const dadosUsuario = await resposta.json();
        iniciarSessao(dadosUsuario);
        return dadosUsuario;
    }

    function sair() {
        localStorage.removeItem(CHAVE_SESSAO);
    }

    // Retorna quem está logado agora
    async function obter() {
        return lerSessao();
    }

    // Chama o Java para atualizar os dados (@PutMapping("/api/clientes/{id}"))
    async function salvar(dados) {
        const usuarioLogado = lerSessao();
        if (!usuarioLogado) throw new Error('Você precisa estar logado.');

        // Se for funcionário, a rota seria /api/funcionarios (simplificado aqui para cliente)
        const rota = usuarioLogado.perfil === PERFIL_CLIENTE ? '/api/clientes' : '/api/funcionarios';

        const resposta = await fetch(`${rota}/${usuarioLogado.id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dados)
        });

        if (!resposta.ok) throw new Error('Erro ao atualizar os dados.');

        const usuarioAtualizado = await resposta.json();
        const novaSessao = { ...usuarioAtualizado, perfil: usuarioLogado.perfil };
        iniciarSessao(novaSessao);
        return novaSessao;
    }

    // Apenas exporta as funções
    return { PERFIL_ADMIN, cadastrar, entrar, sair, obter, salvar, ehAdmin };
    
    
    
    
    
    
    
    
    /* const CHAVE_CONTAS = 'sebo_do_joao_contas';
    const CHAVE_SESSAO = 'sebo_do_joao_sessao';
    const CHAVES = [CHAVE_CONTAS, CHAVE_SESSAO];

    const PERFIL_ADMIN = 'ADMIN';
    const PERFIL_CLIENTE = 'CLIENTE';
    const ADMIN_INICIAL = { nome: 'Administrador', email: 'sebodojoao@gmail.com', senha: 'sebodojoao' };
    // Versão anterior do site guardava o administrador separado, nessa chave
    const CHAVE_ADMIN_ANTIGA = 'sebo_do_joao_admin';

    // Campos que a pessoa pode editar no perfil
    const CAMPOS_EDITAVEIS = ['nome', 'email', 'cpf', 'telefone', 'nascimento', 'foto'];

    function lerContas() {
        try {
            const contas = JSON.parse(localStorage.getItem(CHAVE_CONTAS) || '[]');
            return Array.isArray(contas) ? contas : [];
        } catch (erro) {
            return [];
        }
    }

    function salvarContas(contas) {
        try {
            localStorage.setItem(CHAVE_CONTAS, JSON.stringify(contas));
        } catch (erro) {
            // Mesmo limite de espaço do localStorage dos produtos (as imagens
            // cadastradas também ocupam esse espaço)
            if (erro.name === 'QuotaExceededError' || erro.code === 22) {
                throw new Error('O navegador não tem mais espaço pra guardar esses dados. Tente uma foto menor ou remova imagens de produtos antigos.');
            }
            throw erro;
        }
    }

    function lerIdDaSessao() {
        try {
            const sessao = JSON.parse(localStorage.getItem(CHAVE_SESSAO) || 'null');
            return sessao && sessao.id != null ? sessao.id : null;
        } catch (erro) {
            return null;
        }
    }

    function normalizarEmail(email) {
        return String(email || '').trim().toLowerCase();
    }

    // Devolve a conta sem a senha (o resto do site nunca precisa dela)
    function semSenha(conta) {
        const { senhaHash, sal, ...resto } = conta;
        // Contas antigas, de antes de existir perfil, são clientes
        return { ...resto, perfil: resto.perfil || PERFIL_CLIENTE };
    }

    function ehAdmin(usuario) {
        return Boolean(usuario) && usuario.perfil === PERFIL_ADMIN;
    }

    function proximoIdDeConta(contas) {
        return contas.length ? Math.max(...contas.map((conta) => Number(conta.id) || 0)) + 1 : 1;
    }

    // Na primeira vez que o site é usado, cria a conta do administrador
    async function garantirContaAdmin() {
        const contas = lerContas();
        if (contas.some((conta) => conta.perfil === PERFIL_ADMIN)) return;

        // Se o administrador já tinha trocado a senha na versão anterior, mantém a senha nova
        let sal = null;
        let senhaHash = null;
        try {
            const antigo = JSON.parse(localStorage.getItem(CHAVE_ADMIN_ANTIGA) || 'null');
            if (antigo && antigo.sal && antigo.senhaHash) ({ sal, senhaHash } = antigo);
        } catch (erro) { /* sem versão antiga  }*/

        /* if (!senhaHash) {
            sal = gerarSal();
            senhaHash = await gerarHash(ADMIN_INICIAL.senha, sal);
        }

        contas.push({
            id: proximoIdDeConta(contas),
            perfil: PERFIL_ADMIN,
            nome: ADMIN_INICIAL.nome,
            email: ADMIN_INICIAL.email,
            cpf: '',
            telefone: '',
            nascimento: '',
            foto: '',
            sal,
            senhaHash
        });
        salvarContas(contas);
        localStorage.removeItem(CHAVE_ADMIN_ANTIGA);
    }

    function iniciarSessao(id) {
        localStorage.setItem(CHAVE_SESSAO, JSON.stringify({ id }));
    }

    // Cria a conta e já deixa a pessoa logada
    async function cadastrar({ nome, email, senha }) {
        const emailLimpo = normalizarEmail(email);
        await garantirContaAdmin(); // assim o e-mail do administrador já conta como "em uso"
        const contas = lerContas();

        if (contas.some((conta) => conta.email === emailLimpo)) {
            throw new Error('Já existe uma conta com esse e-mail. Tente fazer login.');
        }

        const sal = gerarSal();
        const novaConta = {
            id: proximoIdDeConta(contas),
            perfil: PERFIL_CLIENTE, // quem se cadastra pelo site sempre é cliente
            nome: String(nome || '').trim(),
            email: emailLimpo,
            cpf: '',
            telefone: '',
            nascimento: '', // formato AAAA-MM-DD (o mesmo do <input type="date">)
            foto: '',       // imagem em base64; vazio = sem foto
            sal,
            senhaHash: await gerarHash(senha, sal)
        };

        contas.push(novaConta);
        salvarContas(contas);
        iniciarSessao(novaConta.id);
        return semSenha(novaConta);
    }

    async function entrar(email, senha) {
        await garantirContaAdmin();
        const conta = lerContas().find((c) => c.email === normalizarEmail(email));

        // Mesma mensagem pra e-mail inexistente e senha errada (não entrega qual dos dois errou)
        const erroGenerico = new Error('E-mail ou senha incorretos.');
        if (!conta) throw erroGenerico;
        if (await gerarHash(senha, conta.sal) !== conta.senhaHash) throw erroGenerico;

        iniciarSessao(conta.id);
        return semSenha(conta);
    }

    function sair() {
        localStorage.removeItem(CHAVE_SESSAO);
    }

    // Quem está logado agora (ou null)
    async function obter() {
        const id = lerIdDaSessao();
        if (id == null) return null;

        const conta = lerContas().find((c) => String(c.id) === String(id));
        if (!conta) {
            sair(); // sessão apontando pra uma conta que não existe mais
            return null;
        }
        return semSenha(conta);
    }

    // Atualiza os dados da conta logada (perfil e foto)
    async function salvar(dados) {
        const id = lerIdDaSessao();
        const contas = lerContas();
        const indice = contas.findIndex((c) => String(c.id) === String(id));
        if (indice === -1) throw new Error('Você precisa estar logado pra alterar o perfil.');

        const alteracoes = {};
        CAMPOS_EDITAVEIS.forEach((campo) => {
            if (campo in dados) alteracoes[campo] = dados[campo];
        });

        // A tela já valida campo a campo, mas o Store também confere: no backend
        // Java a validação do servidor é a que vale de verdade.
        const validacoes = [
            ['nome', erroDoNome], ['email', erroDoEmail], ['cpf', erroDoCpf],
            ['telefone', erroDoTelefone], ['nascimento', erroDaDataNascimento]
        ];
        for (const [campo, validar] of validacoes) {
            if (!(campo in alteracoes)) continue;
            const erro = validar(alteracoes[campo]);
            if (erro) throw new Error(erro);
        }

        // CPF e telefone sempre ficam guardados no formato com máscara
        if ('cpf' in alteracoes) alteracoes.cpf = formatarCpf(alteracoes.cpf);
        if ('telefone' in alteracoes) alteracoes.telefone = formatarTelefone(alteracoes.telefone);

        if ('email' in alteracoes) {
            alteracoes.email = normalizarEmail(alteracoes.email);
            if (contas.some((c, i) => i !== indice && c.email === alteracoes.email)) {
                throw new Error('Já existe outra conta com esse e-mail.');
            }
        }

        contas[indice] = { ...contas[indice], ...alteracoes };
        salvarContas(contas);
        return semSenha(contas[indice]);
    }

    // Troca a senha da conta logada: confere a atual, valida a nova e guarda o novo hash
    async function alterarSenha(senhaAtual, novaSenha, confirmacao) {
        const id = lerIdDaSessao();
        const contas = lerContas();
        const indice = contas.findIndex((c) => String(c.id) === String(id));
        if (indice === -1) throw new Error('Você precisa estar logado para alterar a senha.');

        if (await gerarHash(String(senhaAtual ?? ''), contas[indice].sal) !== contas[indice].senhaHash) {
            throw new Error('A senha atual está incorreta.');
        }

        const erro = erroDaSenhaNova(novaSenha);
        if (erro) throw new Error(erro);
        if (novaSenha === senhaAtual) throw new Error('A nova senha precisa ser diferente da atual.');
        if (novaSenha !== confirmacao) throw new Error('A confirmação não é igual à nova senha.');

        const sal = gerarSal();
        contas[indice] = { ...contas[indice], sal, senhaHash: await gerarHash(novaSenha, sal) };
        salvarContas(contas);
    }

    return { CHAVES, PERFIL_ADMIN, cadastrar, entrar, sair, obter, salvar, alterarSenha, ehAdmin }; */
})();
// ===== FIM: usuario-store =====