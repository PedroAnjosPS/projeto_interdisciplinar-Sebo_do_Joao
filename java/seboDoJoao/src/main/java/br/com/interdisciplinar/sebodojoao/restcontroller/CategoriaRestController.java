package br.com.interdisciplinar.sebodojoao.restcontroller;

import br.com.interdisciplinar.sebodojoao.dto.CategoriaRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.CategoriaResponseDTO;
import br.com.interdisciplinar.sebodojoao.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaRestController {
    private final CategoriaService categoriaService;

    @GetMapping
    public List<CategoriaResponseDTO> listarTodos() {
        return categoriaService.listarTodos();
    }

    @GetMapping("/{id}")
    public CategoriaResponseDTO buscarPorId(@PathVariable Long id) {
        return categoriaService.buscarPorId(id);
    }

    @PostMapping
    public CategoriaResponseDTO cadastrar(@RequestBody CategoriaRequestDTO dto) {
        return categoriaService.cadastrar(dto);
    }

    @PutMapping("/{id}")
    public CategoriaResponseDTO atualizar(
            @PathVariable Long id,
            @RequestBody CategoriaRequestDTO dto) {

        return categoriaService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        categoriaService.excluir(id);
    }
}
