package br.com.interdisciplinar.sebodojoao.restcontroller;

import br.com.interdisciplinar.sebodojoao.model.Cidade;
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
    public List<Cidade> listarTodos() {
        return cidadeService.listarTodos();
    }

    @GetMapping("/{id}")
    public Cidade buscarPorId(@PathVariable Long id) {
        return cidadeService.buscarPorId(id);
    }

    @PostMapping
    public Cidade cadastrar(@RequestBody Cidade cidade) {
        return cidadeService.cadastrar(cidade);
    }

    @PutMapping("/{id}")
    public Cidade atualizar(
            @PathVariable Long id,
            @RequestBody Cidade cidade) {

        return cidadeService.atualizar(id, cidade);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        cidadeService.excluir(id);
    }
}
