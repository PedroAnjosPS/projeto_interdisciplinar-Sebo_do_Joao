package br.com.interdisciplinar.sebodojoao.service;

import br.com.interdisciplinar.sebodojoao.dto.ItemPedidoIdDTO;
import br.com.interdisciplinar.sebodojoao.dto.ItemPedidoRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.ItemPedidoResponseDTO;
import br.com.interdisciplinar.sebodojoao.model.ItemPedido;
import br.com.interdisciplinar.sebodojoao.model.ItemPedidoId;
import br.com.interdisciplinar.sebodojoao.model.Pedido;
import br.com.interdisciplinar.sebodojoao.model.Produto;
import br.com.interdisciplinar.sebodojoao.repository.ItemPedidoRepository;
import br.com.interdisciplinar.sebodojoao.repository.PedidoRepository;
import br.com.interdisciplinar.sebodojoao.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemPedidoService {
    private final ItemPedidoRepository itemPedidoRepository;
    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;

    public List<ItemPedidoResponseDTO> listarTodos() {
        return itemPedidoRepository.findAll()
                .stream()
                .map(item -> new ItemPedidoResponseDTO(
                        new ItemPedidoIdDTO(item.getId().getPedidoId(), item.getId().getProdutoId()),
                        item.getPedido().getId(),
                        item.getProduto().getId(),
                        item.getQuantidade(),
                        item.getPrecoUnitario()
                ))
                .toList();
    }

    public ItemPedidoResponseDTO buscarPorId(ItemPedidoIdDTO idDto) {
        ItemPedidoId id = new ItemPedidoId(idDto.pedidoId(), idDto.produtoId());
        ItemPedido item = itemPedidoRepository
                .findById(id)
                .orElseThrow();

        return new ItemPedidoResponseDTO(
                new ItemPedidoIdDTO(item.getId().getPedidoId(), item.getId().getProdutoId()),
                item.getPedido().getId(),
                item.getProduto().getId(),
                item.getQuantidade(),
                item.getPrecoUnitario()
        );
    }

    public ItemPedidoResponseDTO cadastrar(ItemPedidoRequestDTO dto) {
        Pedido pedido = pedidoRepository
                .findById(dto.pedidoId())
                .orElseThrow();

        Produto produto = produtoRepository
                .findById(dto.produtoId())
                .orElseThrow();

        ItemPedidoId itemId = new ItemPedidoId(dto.pedidoId(), dto.produtoId());

        ItemPedido item = new ItemPedido();
        item.setId(itemId);
        item.setPedido(pedido);
        item.setProduto(produto);
        item.setQuantidade(dto.quantidade());
        item.setPrecoUnitario(dto.precoUnitario());

        ItemPedido salvo = itemPedidoRepository.save(item);

        return new ItemPedidoResponseDTO(
                new ItemPedidoIdDTO(salvo.getId().getPedidoId(), salvo.getId().getProdutoId()),
                salvo.getPedido().getId(),
                salvo.getProduto().getId(),
                salvo.getQuantidade(),
                salvo.getPrecoUnitario()
        );
    }

    public ItemPedidoResponseDTO atualizar(ItemPedidoIdDTO idDto, ItemPedidoRequestDTO dto) {
        ItemPedidoId id = new ItemPedidoId(idDto.pedidoId(), idDto.produtoId());
        ItemPedido itemExistente = itemPedidoRepository
                .findById(id)
                .orElseThrow();

        Pedido pedido = pedidoRepository
                .findById(dto.pedidoId())
                .orElseThrow();

        Produto produto = produtoRepository
                .findById(dto.produtoId())
                .orElseThrow();

        itemExistente.setPedido(pedido);
        itemExistente.setProduto(produto);
        itemExistente.setQuantidade(dto.quantidade());
        itemExistente.setPrecoUnitario(dto.precoUnitario());

        ItemPedido atualizado = itemPedidoRepository.save(itemExistente);

        return new ItemPedidoResponseDTO(
                new ItemPedidoIdDTO(atualizado.getId().getPedidoId(), atualizado.getId().getProdutoId()),
                atualizado.getPedido().getId(),
                atualizado.getProduto().getId(),
                atualizado.getQuantidade(),
                atualizado.getPrecoUnitario()
        );
    }

    public void excluir(ItemPedidoIdDTO idDto) {
        ItemPedidoId id = new ItemPedidoId(idDto.pedidoId(), idDto.produtoId());
        itemPedidoRepository.deleteById(id);
    }
}