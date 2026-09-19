package br.com.interdisciplinar.sebodojoao.service;

import br.com.interdisciplinar.sebodojoao.model.Cep;
import br.com.interdisciplinar.sebodojoao.repository.CepRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CepService {
    private final CepRepository cepRepository;

    public List<Cep> listarTodos() { return cepRepository.findAll(); }

    public Cep buscarPorId(String nr) { return cepRepository.findById(nr).orElseThrow(); }

    public Cep cadastrar(Cep cep) { return cepRepository.save(cep); }

    public Cep atualizar(String nr, @NonNull Cep cep) {
        Cep cepExistente = cepRepository.findById(nr).orElseThrow();

        cepExistente.setCidade(cep.getCidade());

        return cepRepository.save(cepExistente);
    }

    public void excluir(String nr) { cepRepository.deleteById(nr); }
}
