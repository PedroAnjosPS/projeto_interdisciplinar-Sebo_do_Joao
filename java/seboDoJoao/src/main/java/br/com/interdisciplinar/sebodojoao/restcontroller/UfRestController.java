package br.com.interdisciplinar.sebodojoao.restcontroller;

import br.com.interdisciplinar.sebodojoao.model.Uf;
import br.com.interdisciplinar.sebodojoao.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ufs")
@RequiredArgsConstructor
public class UfRestController {
    private final UfService ufService;

    @GetMapping
    public List<Uf> listarTodos() {
        return ufService.listarTodos();
    }

    @GetMapping("/{sigla}")
    public Uf buscarPorId(@PathVariable String sigla) {
        return ufService.buscarPorId(sigla);
    }

    @PostMapping
    public Uf cadastrar(@RequestBody Uf uf) {
        return ufService.cadastrar(uf);
    }

    @PutMapping("/{sigla}")
    public Uf atualizar(
            @PathVariable String sigla,
            @RequestBody Uf uf) {

        return ufService.atualizar(sigla, uf);
    }

    @DeleteMapping("/{sigla}")
    public void excluir(@PathVariable String sigla) {
        ufService.excluir(sigla);
    }
}
