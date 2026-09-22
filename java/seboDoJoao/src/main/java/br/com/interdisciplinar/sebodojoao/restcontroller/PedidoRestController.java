package br.com.interdisciplinar.sebodojoao.restcontroller;

import br.com.interdisciplinar.sebodojoao.model.Pedido;
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
    public List<Pedido> listarTodos() {
        return pedidoService.listarTodos();
    }

    @GetMapping("/{id}")
    public Pedido buscarPorId(@PathVariable Long id) {
        return pedidoService.buscarPorId(id);
    }

    @PostMapping
    public Pedido cadastrar(@RequestBody Pedido pedido) {
        return pedidoService.cadastrar(pedido);
    }

    @PutMapping("/{id}")
    public Pedido atualizar(
            @PathVariable Long id,
            @RequestBody Pedido pedido) {

        return pedidoService.atualizar(id, pedido);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        pedidoService.excluir(id);
    }
}
