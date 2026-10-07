package br.com.interdisciplinar.sebodojoao.restcontroller;

import br.com.interdisciplinar.sebodojoao.dto.PagamentoRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.PagamentoResponseDTO;
import br.com.interdisciplinar.sebodojoao.service.PagamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagamentos")
@RequiredArgsConstructor
public class PagamentoRestController {
    private final PagamentoService pagamentoService;

    @GetMapping
    public List<PagamentoResponseDTO> listarTodos() {
        return pagamentoService.listarTodos();
    }

    @GetMapping("/{id}")
    public PagamentoResponseDTO buscarPorId(@PathVariable Long id) {
        return pagamentoService.buscarPorId(id);
    }

    @PostMapping
    public PagamentoResponseDTO cadastrar(@RequestBody PagamentoRequestDTO dto) {
        return pagamentoService.cadastrar(dto);
    }

    @PutMapping("/{id}")
    public PagamentoResponseDTO atualizar(
            @PathVariable Long id,
            @RequestBody PagamentoRequestDTO dto) {

        return pagamentoService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        pagamentoService.excluir(id);
    }
}