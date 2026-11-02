package br.com.interdisciplinar.sebodojoao.restcontroller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.interdisciplinar.sebodojoao.dto.CartaoRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.CartaoResponseDTO;
import br.com.interdisciplinar.sebodojoao.service.CartaoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

// NOVO: rotas dos cartões salvos ("Meus cartões" e checkout). O cliente vem da SESSÃO de login.
// Nenhuma rota recebe ou devolve o número completo do cartão nem o CVV (eles não existem no banco).
@RestController
@RequestMapping("/api/cartoes")
@RequiredArgsConstructor
public class CartaoRestController {
    private final CartaoService cartaoService;

    @GetMapping
    public List<CartaoResponseDTO> listar(HttpSession sessao) {
        return cartaoService.listar(ControleDeAcesso.exigirCliente(sessao));
    }

    @PostMapping
    public CartaoResponseDTO salvar(@RequestBody CartaoRequestDTO dto, HttpSession sessao) {
        return cartaoService.salvar(ControleDeAcesso.exigirCliente(sessao), dto);
    }

    @PostMapping("/{id}/padrao")
    public CartaoResponseDTO definirPadrao(@PathVariable Long id, HttpSession sessao) {
        return cartaoService.definirPadrao(ControleDeAcesso.exigirCliente(sessao), id);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id, HttpSession sessao) {
        cartaoService.remover(ControleDeAcesso.exigirCliente(sessao), id);
    }
}
