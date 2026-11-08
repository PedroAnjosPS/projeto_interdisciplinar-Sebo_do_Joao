package br.com.interdisciplinar.sebodojoao.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.interdisciplinar.sebodojoao.dto.ItemCarrinhoDTO;
import br.com.interdisciplinar.sebodojoao.model.ItemCarrinho;
import br.com.interdisciplinar.sebodojoao.model.ItemCarrinhoId;
import br.com.interdisciplinar.sebodojoao.model.Produto;
import br.com.interdisciplinar.sebodojoao.model.StatusProduto;
import br.com.interdisciplinar.sebodojoao.repository.ClienteRepository;
import br.com.interdisciplinar.sebodojoao.repository.ItemCarrinhoRepository;
import br.com.interdisciplinar.sebodojoao.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;

// O carrinho de compras do cliente logado, guardado na tabela item_carrinhos.
//
// O que o carrinho NÃO faz: ele não baixa o estoque (isso só acontece na compra, pela trigger do banco).
// Ele guarda o preço do produto no momento em que o item entra. Mesmo assim, ele confere o estoque ao
// adicionar, para a pessoa não montar um carrinho com mais unidades do que existem.
// Quem é o cliente vem da SESSÃO de login: cada cliente só enxerga e altera o próprio carrinho.
@Service
@RequiredArgsConstructor
public class ItemCarrinhoService {
    private final ItemCarrinhoRepository itemCarrinhoRepository;
    private final ProdutoRepository produtoRepository;
    private final ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<ItemCarrinhoDTO> listar(Long clienteId) {
        return itemCarrinhoRepository.findByCliente_IdOrderByProduto_IdAsc(clienteId)
                .stream()
                .map(i -> new ItemCarrinhoDTO(i.getProduto().getId(), i.getQuantidade()))
                .toList();
    }

    // Soma "quantidade" unidades do produto ao que já existe no carrinho
    @Transactional
    public List<ItemCarrinhoDTO> adicionar(Long clienteId, ItemCarrinhoDTO dto) {
        if (dto == null || dto.produtoId() == null || dto.quantidade() == null || dto.quantidade() < 1) {
            throw erro(HttpStatus.BAD_REQUEST, "Item do carrinho inválido.");
        }
        Produto produto = produtoDisponivel(dto.produtoId());

        ItemCarrinho item = itemCarrinhoRepository.findById(new ItemCarrinhoId(clienteId, produto.getId())).orElse(null);
        int total = (item == null ? 0 : item.getQuantidade()) + dto.quantidade();
        if (total > produto.getEstoque()) {
            throw erro(HttpStatus.CONFLICT, "Não há estoque suficiente (disponível: " + produto.getEstoque() + ").");
        }

        gravar(clienteId, produto, item, total);
        return listar(clienteId);
    }

    // Troca a quantidade de um produto que já está no carrinho
    @Transactional
    public List<ItemCarrinhoDTO> definirQuantidade(Long clienteId, Long produtoId, Integer quantidade) {
        if (quantidade == null || quantidade < 1) throw erro(HttpStatus.BAD_REQUEST, "Quantidade inválida.");

        ItemCarrinho item = itemCarrinhoRepository.findById(new ItemCarrinhoId(clienteId, produtoId))
                .orElseThrow(() -> erro(HttpStatus.NOT_FOUND, "Esse produto não está no seu carrinho."));
        Produto produto = produtoDisponivel(produtoId);
        if (quantidade > produto.getEstoque()) {
            throw erro(HttpStatus.CONFLICT, "Não há estoque suficiente (disponível: " + produto.getEstoque() + ").");
        }

        gravar(clienteId, produto, item, quantidade);
        return listar(clienteId);
    }

    @Transactional
    public List<ItemCarrinhoDTO> remover(Long clienteId, Long produtoId) {
        itemCarrinhoRepository.deleteById(new ItemCarrinhoId(clienteId, produtoId));
        return listar(clienteId);
    }

    @Transactional
    public void limpar(Long clienteId) {
        itemCarrinhoRepository.apagarDoCliente(clienteId);
    }

    // Usado no login: o que a pessoa colocou no carrinho como visitante (guardado no navegador) é somado
    // ao carrinho da conta. Produto que não existe mais, está inativo ou sem estoque é ignorado, e a
    // quantidade nunca passa do estoque.
    @Transactional
    public List<ItemCarrinhoDTO> juntar(Long clienteId, List<ItemCarrinhoDTO> itensDoVisitante) {
        if (itensDoVisitante != null) {
            for (ItemCarrinhoDTO dto : itensDoVisitante) {
                if (dto == null || dto.produtoId() == null || dto.quantidade() == null || dto.quantidade() < 1) continue;

                Produto produto = produtoRepository.findById(dto.produtoId()).orElse(null);
                if (produto == null || produto.getStatus() != StatusProduto.DISPONIVEL || produto.getEstoque() < 1) continue;

                ItemCarrinho item = itemCarrinhoRepository.findById(new ItemCarrinhoId(clienteId, produto.getId())).orElse(null);
                int total = Math.min(produto.getEstoque(), (item == null ? 0 : item.getQuantidade()) + dto.quantidade());
                gravar(clienteId, produto, item, total);
            }
        }
        return listar(clienteId);
    }

    // Cria o item (se ainda não existe) ou atualiza a quantidade
    private void gravar(Long clienteId, Produto produto, ItemCarrinho item, int quantidade) {
        if (item == null) {
            item = new ItemCarrinho();
            item.setId(new ItemCarrinhoId(clienteId, produto.getId()));
            item.setCliente(clienteRepository.getReferenceById(clienteId));
            item.setProduto(produto);
        }
        item.setQuantidade(quantidade);
        item.setPrecoUnitario(produto.getPreco());
        itemCarrinhoRepository.save(item);
    }

    // Só dá para comprar produto que existe e está ativo (DISPONIVEL)
    private Produto produtoDisponivel(Long produtoId) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> erro(HttpStatus.NOT_FOUND, "Produto não encontrado."));
        if (produto.getStatus() != StatusProduto.DISPONIVEL || produto.getEstoque() < 1) {
            throw erro(HttpStatus.CONFLICT, "Esse produto não está disponível no momento.");
        }
        return produto;
    }

    private static ResponseStatusException erro(HttpStatus status, String mensagem) {
        return new ResponseStatusException(status, mensagem);
    }
}
