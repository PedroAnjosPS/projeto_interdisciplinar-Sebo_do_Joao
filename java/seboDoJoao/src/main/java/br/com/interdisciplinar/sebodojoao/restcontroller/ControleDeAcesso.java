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

    private ControleDeAcesso() { }

    public static void registrarPerfil(HttpSession sessao, String perfil) {
        sessao.setAttribute(ATRIBUTO_PERFIL, perfil);
    }

    // Só Cliente logado pode comprar. A checagem libera um único perfil, em vez de barrar
    // apenas o Funcionário: assim, uma chamada sem login também é recusada.
    public static void exigirCliente(HttpSession sessao) {
        Object perfil = sessao.getAttribute(ATRIBUTO_PERFIL);

        if (perfil == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "É preciso fazer login para comprar.");
        }

        if (!PERFIL_CLIENTE.equals(perfil)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Contas de funcionário não podem realizar compras.");
        }
    }
}
