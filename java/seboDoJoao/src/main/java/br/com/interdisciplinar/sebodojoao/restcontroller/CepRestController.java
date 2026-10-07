package br.com.interdisciplinar.sebodojoao.restcontroller;

import br.com.interdisciplinar.sebodojoao.dto.CepRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.CepResponseDTO;
import br.com.interdisciplinar.sebodojoao.service.CepService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ceps")
@RequiredArgsConstructor
public class CepRestController {
    private final CepService cepService;

    @GetMapping
    public List<CepResponseDTO> listarTodos() {
        return cepService.listarTodos();
    }

    @GetMapping("/{nr}")
    public CepResponseDTO buscarPorId(@PathVariable String nr) {
        return cepService.buscarPorId(nr);
    }

    @PostMapping
    public CepResponseDTO cadastrar(@RequestBody CepRequestDTO dto) {
        return cepService.cadastrar(dto);
    }

    @PutMapping("/{nr}")
    public CepResponseDTO atualizar(
            @PathVariable String nr,
            @RequestBody CepRequestDTO dto) {

        return cepService.atualizar(nr, dto);
    }

    @DeleteMapping("/{nr}")
    public void excluir(@PathVariable String nr) {
        cepService.excluir(nr);
    }
}