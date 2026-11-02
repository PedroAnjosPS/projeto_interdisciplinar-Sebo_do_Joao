package br.com.interdisciplinar.sebodojoao.restcontroller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.interdisciplinar.sebodojoao.dto.CarrinhoItemDTO;
import br.com.interdisciplinar.sebodojoao.service.CarrinhoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

// NOVO: rotas do carrinho de compras do cliente logado (tabela item_carrinhos).
// O cliente vem da SESSÃO de login: cada pessoa só enxerga e altera o próprio carrinho.
// Quem ainda não fez login usa um carrinho temporário no navegador (ver CarrinhoStore, em vitrine.js).
@RestController
@RequestMapping("/api/carrinho")
@RequiredArgsConstructor
public class CarrinhoRestController {
    private final CarrinhoService carrinhoService;

    @GetMapping
    public List<CarrinhoItemDTO> listar(HttpSession sessao) {
        return carrinhoService.listar(ControleDeAcesso.exigirCliente(sessao));
    }

    // Coloca um produto no carrinho (soma à quantidade que já existir)
    @PostMapping
    public List<CarrinhoItemDTO> adicionar(@RequestBody CarrinhoItemDTO item, HttpSession sessao) {
        return carrinhoService.adicionar(ControleDeAcesso.exigirCliente(sessao), item);
    }

    // Passa para o carrinho da conta os itens que a pessoa tinha como visitante (chamada no login)
    @PostMapping("/juntar")
    public List<CarrinhoItemDTO> juntar(@RequestBody List<CarrinhoItemDTO> itens, HttpSession sessao) {
        return carrinhoService.juntar(ControleDeAcesso.exigirCliente(sessao), itens);
    }

    // Troca a quantidade de um produto (só o campo "quantidade" do corpo é usado)
    @PutMapping("/{produtoId}")
    public List<CarrinhoItemDTO> definirQuantidade(@PathVariable Long produtoId, @RequestBody CarrinhoItemDTO item, HttpSession sessao) {
        return carrinhoService.definirQuantidade(ControleDeAcesso.exigirCliente(sessao), produtoId, item.quantidade());
    }

    @DeleteMapping("/{produtoId}")
    public List<CarrinhoItemDTO> remover(@PathVariable Long produtoId, HttpSession sessao) {
        return carrinhoService.remover(ControleDeAcesso.exigirCliente(sessao), produtoId);
    }

    // Esvazia o carrinho
    @DeleteMapping
    public void limpar(HttpSession sessao) {
        carrinhoService.limpar(ControleDeAcesso.exigirCliente(sessao));
    }
}
