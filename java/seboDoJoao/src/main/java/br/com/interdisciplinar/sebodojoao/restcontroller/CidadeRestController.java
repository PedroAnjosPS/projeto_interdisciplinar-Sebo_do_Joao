package br.com.interdisciplinar.sebodojoao.restcontroller;

import br.com.interdisciplinar.sebodojoao.dto.CidadeRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.CidadeResponseDTO;
import br.com.interdisciplinar.sebodojoao.service.CidadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cidades")
@RequiredArgsConstructor
public class CidadeRestController {
    private final CidadeService cidadeService;

    @GetMapping
    public List<CidadeResponseDTO> listarTodos() {
        return cidadeService.listarTodos();
    }

    @GetMapping("/{id}")
    public CidadeResponseDTO buscarPorId(@PathVariable Long id) {
        return cidadeService.buscarPorId(id);
    }

    @PostMapping
    public CidadeResponseDTO cadastrar(@RequestBody CidadeRequestDTO dto) {
        return cidadeService.cadastrar(dto);
    }

    @PutMapping("/{id}")
    public CidadeResponseDTO atualizar(
            @PathVariable Long id,
            @RequestBody CidadeRequestDTO dto) {

        return cidadeService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        cidadeService.excluir(id);
    }
}