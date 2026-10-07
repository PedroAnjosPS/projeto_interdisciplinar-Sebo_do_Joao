package br.com.interdisciplinar.sebodojoao.restcontroller;

import br.com.interdisciplinar.sebodojoao.dto.UfRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.UfResponseDTO;
import br.com.interdisciplinar.sebodojoao.service.UfService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ufs")
@RequiredArgsConstructor
public class UfRestController {
    private final UfService ufService;

    @GetMapping
    public List<UfResponseDTO> listarTodos() {
        return ufService.listarTodos();
    }

    @GetMapping("/{sigla}")
    public UfResponseDTO buscarPorId(@PathVariable String sigla) {
        return ufService.buscarPorId(sigla);
    }

    @PostMapping
    public UfResponseDTO cadastrar(@RequestBody UfRequestDTO dto) {
        return ufService.cadastrar(dto);
    }

    @PutMapping("/{sigla}")
    public UfResponseDTO atualizar(
            @PathVariable String sigla,
            @RequestBody UfRequestDTO dto) {

        return ufService.atualizar(sigla, dto);
    }

    @DeleteMapping("/{sigla}")
    public void excluir(@PathVariable String sigla) {
        ufService.excluir(sigla);
    }
}