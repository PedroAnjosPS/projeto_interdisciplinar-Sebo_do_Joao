package br.com.interdisciplinar.sebodojoao.restcontroller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

// PROBLEMA: quem estava logado só era conhecido pelo navegador (localStorage). Como a
// pessoa pode editar o localStorage ou chamar a API direto, o Java não tinha como saber
// se o pedido vinha de um Cliente ou de um Funcionário.
//
// SOLUÇÃO: no login, o perfil é guardado na sessão (HttpSession). Ela fica no servidor e o
// navegador só recebe um cookie com o número dela, então o usuário não consegue trocar o perfil.
// Esta classe concentra a regra para ela não ser repetida em cada controlador.
public final class ControleDeAcesso {
    public static final String PERFIL_CLIENTE = "CLIENTE";
    public static final String PERFIL_FUNCIONARIO = "FUNCIONARIO";

    private static final String ATRIBUTO_PERFIL = "perfilUsuario";
    // Nome do atributo da sessão que guarda o ID de quem fez login. Com ele, o Java sabe qual cliente
    // está comprando, sem precisar confiar em um ID enviado pelo navegador.
    private static final String ATRIBUTO_ID = "idUsuario";

    private ControleDeAcesso() { }

    public static void registrarPerfil(HttpSession sessao, String perfil) {
        sessao.setAttribute(ATRIBUTO_PERFIL, perfil);
    }

    // Guarda na sessão (no servidor) o ID do usuário que entrou.
    public static void registrarUsuario(HttpSession sessao, Long id) {
        sessao.setAttribute(ATRIBUTO_ID, id);
    }

    // Só Cliente logado pode comprar. A checagem libera um único perfil, em vez de barrar
    // apenas o Funcionário: assim, uma chamada sem login também é recusada.
    // Devolve o ID do cliente logado. Quem chama usa esse ID, e não um vindo do navegador,
    // para saber de quem é a compra.
    public static Long exigirCliente(HttpSession sessao) {
        Object perfil = sessao.getAttribute(ATRIBUTO_PERFIL);

        // As mensagens são genéricas porque esta mesma checagem protege a compra, o carrinho,
        // os endereços e os cartões.
        if (perfil == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "É preciso fazer login.");
        }

        if (!PERFIL_CLIENTE.equals(perfil)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Contas de funcionário não podem usar esta função (compras, carrinho, endereços e cartões são só para clientes).");
        }

        return lerId(sessao);
    }

    // O contrário de exigirCliente: usada nas telas de gestão de pedidos, que só o funcionário
    // pode usar (para fazer a entrega avançar, por exemplo).
    public static Long exigirFuncionario(HttpSession sessao) {
        Object perfil = sessao.getAttribute(ATRIBUTO_PERFIL);

        if (perfil == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "É preciso fazer login.");
        }

        if (!PERFIL_FUNCIONARIO.equals(perfil)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Somente funcionários podem fazer isso.");
        }

        return lerId(sessao);
    }

    // Lê o ID guardado na sessão. Se não existir (ex.: sessão antiga), pede um novo login.
    private static Long lerId(HttpSession sessao) {
        Object id = sessao.getAttribute(ATRIBUTO_ID);

        if (!(id instanceof Long)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sessão expirada. Faça login novamente.");
        }

        return (Long) id;
    }
}
