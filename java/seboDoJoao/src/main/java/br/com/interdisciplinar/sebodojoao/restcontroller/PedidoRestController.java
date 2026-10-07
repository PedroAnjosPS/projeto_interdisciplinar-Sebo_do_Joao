package br.com.interdisciplinar.sebodojoao.restcontroller;

import br.com.interdisciplinar.sebodojoao.dto.PedidoRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.PedidoResponseDTO;
import br.com.interdisciplinar.sebodojoao.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoRestController {
    private final PedidoService pedidoService;

    @GetMapping
    public List<PedidoResponseDTO> listarTodos() {
        return pedidoService.listarTodos();
    }

    @GetMapping("/{id}")
    public PedidoResponseDTO buscarPorId(@PathVariable Long id) {
        return pedidoService.buscarPorId(id);
    }

    @PostMapping
    public PedidoResponseDTO cadastrar(@RequestBody PedidoRequestDTO dto) {
        return pedidoService.cadastrar(dto);
    }

    @PutMapping("/{id}")
    public PedidoResponseDTO atualizar(
            @PathVariable Long id,
            @RequestBody PedidoRequestDTO dto) {

        return pedidoService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        pedidoService.excluir(id);
    }
}