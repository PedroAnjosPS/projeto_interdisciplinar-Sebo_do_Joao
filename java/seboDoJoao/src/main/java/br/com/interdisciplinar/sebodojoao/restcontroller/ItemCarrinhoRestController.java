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

import br.com.interdisciplinar.sebodojoao.dto.ItemCarrinhoDTO;
import br.com.interdisciplinar.sebodojoao.service.ItemCarrinhoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

// Rotas do carrinho de compras do cliente logado (tabela item_carrinhos).
// O cliente vem da SESSÃO de login: cada pessoa só enxerga e altera o próprio carrinho.
// Quem ainda não fez login usa um carrinho temporário no navegador (ver CarrinhoStore, em vitrine.js).
@RestController
@RequestMapping("/api/carrinho")
@RequiredArgsConstructor
public class ItemCarrinhoRestController {
    private final ItemCarrinhoService itemCarrinhoService;

    @GetMapping
    public List<ItemCarrinhoDTO> listar(HttpSession sessao) {
        return itemCarrinhoService.listar(ControleDeAcesso.exigirCliente(sessao));
    }

    // Coloca um produto no carrinho (soma à quantidade que já existir)
    @PostMapping
    public List<ItemCarrinhoDTO> adicionar(@RequestBody ItemCarrinhoDTO item, HttpSession sessao) {
        return itemCarrinhoService.adicionar(ControleDeAcesso.exigirCliente(sessao), item);
    }

    // Passa para o carrinho da conta os itens que a pessoa tinha como visitante (chamada no login)
    @PostMapping("/juntar")
    public List<ItemCarrinhoDTO> juntar(@RequestBody List<ItemCarrinhoDTO> itens, HttpSession sessao) {
        return itemCarrinhoService.juntar(ControleDeAcesso.exigirCliente(sessao), itens);
    }

    // Troca a quantidade de um produto (só o campo "quantidade" do corpo é usado)
    @PutMapping("/{produtoId}")
    public List<ItemCarrinhoDTO> definirQuantidade(@PathVariable Long produtoId, @RequestBody ItemCarrinhoDTO item, HttpSession sessao) {
        return itemCarrinhoService.definirQuantidade(ControleDeAcesso.exigirCliente(sessao), produtoId, item.quantidade());
    }

    @DeleteMapping("/{produtoId}")
    public List<ItemCarrinhoDTO> remover(@PathVariable Long produtoId, HttpSession sessao) {
        return itemCarrinhoService.remover(ControleDeAcesso.exigirCliente(sessao), produtoId);
    }

    // Esvazia o carrinho
    @DeleteMapping
    public void limpar(HttpSession sessao) {
        itemCarrinhoService.limpar(ControleDeAcesso.exigirCliente(sessao));
    }
}
