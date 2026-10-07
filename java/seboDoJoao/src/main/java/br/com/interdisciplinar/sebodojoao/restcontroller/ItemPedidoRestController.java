package br.com.interdisciplinar.sebodojoao.restcontroller;

import br.com.interdisciplinar.sebodojoao.dto.ItemPedidoIdDTO;
import br.com.interdisciplinar.sebodojoao.dto.ItemPedidoRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.ItemPedidoResponseDTO;
import br.com.interdisciplinar.sebodojoao.service.ItemPedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/item-pedidos")
@RequiredArgsConstructor
public class ItemPedidoRestController {
    private final ItemPedidoService itemPedidoService;

    @GetMapping
    public List<ItemPedidoResponseDTO> listarTodos() {
        return itemPedidoService.listarTodos();
    }

    @GetMapping("/{pedidoId}/{produtoId}")
    public ItemPedidoResponseDTO buscarPorId(
            @PathVariable Long pedidoId,
            @PathVariable Long produtoId) {
        ItemPedidoIdDTO idDto = new ItemPedidoIdDTO(pedidoId, produtoId);
        return itemPedidoService.buscarPorId(idDto);
    }

    @PostMapping
    public ItemPedidoResponseDTO cadastrar(@RequestBody ItemPedidoRequestDTO dto) {
        return itemPedidoService.cadastrar(dto);
    }

    @PutMapping("/{pedidoId}/{produtoId}")
    public ItemPedidoResponseDTO atualizar(
            @PathVariable Long pedidoId,
            @PathVariable Long produtoId,
            @RequestBody ItemPedidoRequestDTO dto) {
        ItemPedidoIdDTO idDto = new ItemPedidoIdDTO(pedidoId, produtoId);
        return itemPedidoService.atualizar(idDto, dto);
    }

    @DeleteMapping("/{pedidoId}/{produtoId}")
    public void excluir(
            @PathVariable Long pedidoId,
            @PathVariable Long produtoId) {
        ItemPedidoIdDTO idDto = new ItemPedidoIdDTO(pedidoId, produtoId);
        itemPedidoService.excluir(idDto);
    }
}