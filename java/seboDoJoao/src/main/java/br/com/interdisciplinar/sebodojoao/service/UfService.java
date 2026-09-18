package br.com.interdisciplinar.sebodojoao.service;

import br.com.interdisciplinar.sebodojoao.model.Uf;
import br.com.interdisciplinar.sebodojoao.repository.UFRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UfService {
    private final UFRepository ufRepository;

    public List<Uf> listarTodos() {
        return ufRepository.findAll();
    }

    public Uf buscarPorId(String sigla) {
        return ufRepository.findById(sigla).orElseThrow();
    }

    public Uf cadastrar(Uf uf) {
        return ufRepository.save(uf);
    }

    public Uf atualizar(String sigla, Uf uf) {
        uf.setSigla(sigla);
        return ufRepository.save(uf);
    }

    public void excluir(String sigla) {
        ufRepository.deleteById(sigla);
    }
}
