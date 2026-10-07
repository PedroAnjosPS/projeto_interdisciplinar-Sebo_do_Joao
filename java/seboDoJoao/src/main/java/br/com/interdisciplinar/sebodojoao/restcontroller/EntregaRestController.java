package br.com.interdisciplinar.sebodojoao.restcontroller;

import br.com.interdisciplinar.sebodojoao.dto.EntregaRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.EntregaResponseDTO;
import br.com.interdisciplinar.sebodojoao.service.EntregaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entregas")
@RequiredArgsConstructor
public class EntregaRestController {
    private final EntregaService entregaService;

    @GetMapping
    public List<EntregaResponseDTO> listarTodos() {
        return entregaService.listarTodos();
    }

    @GetMapping("/{id}")
    public EntregaResponseDTO buscarPorId(@PathVariable Long id) {
        return entregaService.buscarPorId(id);
    }

    @PostMapping
    public EntregaResponseDTO cadastrar(@RequestBody EntregaRequestDTO dto) {
        return entregaService.cadastrar(dto);
    }

    @PutMapping("/{id}")
    public EntregaResponseDTO atualizar(
            @PathVariable Long id,
            @RequestBody EntregaRequestDTO dto) {

        return entregaService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        entregaService.excluir(id);
    }
}