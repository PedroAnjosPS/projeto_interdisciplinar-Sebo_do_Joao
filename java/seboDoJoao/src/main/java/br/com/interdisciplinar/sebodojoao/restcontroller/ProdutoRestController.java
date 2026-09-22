package br.com.interdisciplinar.sebodojoao.restcontroller;

import br.com.interdisciplinar.sebodojoao.dto.ProdutoRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.ProdutoResponseDTO;
import br.com.interdisciplinar.sebodojoao.service.ProdutoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
public class ProdutoRestController {
    private final ProdutoService produtoService;

    @GetMapping
    public List<ProdutoResponseDTO> listarTodos() {
        return produtoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ProdutoResponseDTO buscarPorId(@PathVariable Long id) {
        return produtoService.buscarPorId(id);
    }

    @PostMapping
    public ProdutoResponseDTO cadastrar(@RequestBody ProdutoRequestDTO dto) {
        return produtoService.cadastrar(dto);
    }

    @PutMapping("/{id}")
    public ProdutoResponseDTO atualizar(
            @PathVariable Long id,
            @RequestBody ProdutoRequestDTO dto) {

        return produtoService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        produtoService.excluir(id);
    }
}
