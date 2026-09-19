package br.com.interdisciplinar.sebodojoao.restcontroller;

import br.com.interdisciplinar.sebodojoao.model.Cep;
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
    public List<Cep> listarTodos() {
        return cepService.listarTodos();
    }

    @GetMapping("/{nr}")
    public Cep buscarPorId(@PathVariable String nr) {
        return cepService.buscarPorId(nr);
    }

    @PostMapping
    public Cep cadastrar(@RequestBody Cep cep) {
        return cepService.cadastrar(cep);
    }

    @PutMapping("/{nr}")
    public Cep atualizar(
            @PathVariable String nr,
            @RequestBody Cep cep) {

        return cepService.atualizar(nr, cep);
    }

    @DeleteMapping("/{nr}")
    public void excluir(@PathVariable String nr) {
        cepService.excluir(nr);
    }
}
