package br.com.interdisciplinar.sebodojoao.restcontroller;

import br.com.interdisciplinar.sebodojoao.dto.GeneroRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.GeneroResponseDTO;
import br.com.interdisciplinar.sebodojoao.service.GeneroService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/generos")
@RequiredArgsConstructor
public class GeneroRestController {
    private final GeneroService generoService;

    @GetMapping
    public List<GeneroResponseDTO> listarTodos() {
        return generoService.listarTodos();
    }

    @GetMapping("/{id}")
    public GeneroResponseDTO buscarPorId(@PathVariable Long id) {
        return generoService.buscarPorId(id);
    }

    @PostMapping
    public GeneroResponseDTO cadastrar(@RequestBody GeneroRequestDTO dto) {
        return generoService.cadastrar(dto);
    }

    @PutMapping("/{id}")
    public GeneroResponseDTO atualizar(
            @PathVariable Long id,
            @RequestBody GeneroRequestDTO dto) {

        return generoService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        generoService.excluir(id);
    }
}