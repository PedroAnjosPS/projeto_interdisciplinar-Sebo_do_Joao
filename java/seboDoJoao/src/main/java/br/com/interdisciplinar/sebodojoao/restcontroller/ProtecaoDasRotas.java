package br.com.interdisciplinar.sebodojoao.restcontroller;

import java.util.Set;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// Confere o nível de acesso das rotas da API antes de o controller ser chamado. As regras ficam
// todas aqui, num lugar só, para não repetir a conferência em cada controller:
//
//   - Tabelas de apoio e produtos: qualquer pessoa pode CONSULTAR (a vitrine precisa disso),
//     mas só funcionário cadastra, altera ou exclui.
//   - Pedidos, itens, pagamentos e entregas (CRUD direto): só funcionário consulta e só o
//     administrador altera ou exclui. O cliente compra pelas rotas de /api/compras.
//   - Funcionários: qualquer rota exige funcionário logado. O que é de cada conta fica no controller.
//   - Lista de clientes: só funcionário.
// As rotas de carrinho, endereços, cartões, compras, login e administração já se protegem sozinhas.
@Configuration
public class ProtecaoDasRotas implements WebMvcConfigurer, HandlerInterceptor {

    private static final Set<String> APOIO_PUBLICO_PARA_LEITURA = Set.of(
            "/api/produtos", "/api/categorias", "/api/generos", "/api/imagem-produtos",
            "/api/ufs", "/api/cidades", "/api/ceps");

    private static final Set<String> GESTAO_DE_PEDIDOS = Set.of(
            "/api/pedidos", "/api/item-pedidos", "/api/pagamentos", "/api/entregas");

    @Override
    public void addInterceptors(InterceptorRegistry registro) {
        registro.addInterceptor(this).addPathPatterns("/api/**");
    }

    @Override
    public boolean preHandle(HttpServletRequest requisicao, HttpServletResponse resposta, Object handler) {
        String caminho = requisicao.getRequestURI();
        String metodo = requisicao.getMethod();
        boolean leitura = metodo.equals("GET") || metodo.equals("HEAD") || metodo.equals("OPTIONS");

        if (faz(caminho, APOIO_PUBLICO_PARA_LEITURA)) {
            if (!leitura) ControleDeAcesso.exigirFuncionario(sessaoDe(requisicao));
        } else if (faz(caminho, GESTAO_DE_PEDIDOS)) {
            if (leitura) {
                ControleDeAcesso.exigirFuncionario(sessaoDe(requisicao));
            } else if (!metodo.equals("POST") || caminho.startsWith("/api/entregas")) {
                // criar pedido, item e pagamento já exige cliente dentro do controller
                ControleDeAcesso.exigirAdministrador(sessaoDe(requisicao));
            }
        } else if (caminho.equals("/api/funcionarios") || caminho.startsWith("/api/funcionarios/")) {
            ControleDeAcesso.exigirFuncionario(sessaoDe(requisicao));
        } else if (caminho.equals("/api/clientes") && leitura) {
            ControleDeAcesso.exigirFuncionario(sessaoDe(requisicao));
        }
        return true;
    }

    private static boolean faz(String caminho, Set<String> prefixos) {
        return prefixos.stream().anyMatch(p -> caminho.equals(p) || caminho.startsWith(p + "/"));
    }

    // Quem nunca fez login não tem sessão: nesse caso a resposta é "faça login" (401)
    private static HttpSession sessaoDe(HttpServletRequest requisicao) {
        HttpSession sessao = requisicao.getSession(false);
        if (sessao == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "É preciso fazer login.");
        }
        return sessao;
    }
}
