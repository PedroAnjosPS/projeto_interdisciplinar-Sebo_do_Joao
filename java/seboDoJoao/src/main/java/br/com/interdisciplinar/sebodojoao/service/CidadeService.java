package br.com.interdisciplinar.sebodojoao.service;

import br.com.interdisciplinar.sebodojoao.model.Cidade;
import br.com.interdisciplinar.sebodojoao.repository.CidadeRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CidadeService {
    private final CidadeRepository cidadeRepository;

    public List<Cidade> listarTodos() { return cidadeRepository.findAll(); }

    public Cidade buscarPorId(Long id) { return cidadeRepository.findById(id).orElseThrow(); }

    public Cidade cadastrar(Cidade cidade) { return cidadeRepository.save(cidade); }

    public Cidade atualizar(Long id, @NonNull Cidade cidade) {
        Cidade cidadeExistente = cidadeRepository.findById(id).orElseThrow();

        cidadeExistente.setNome(cidade.getNome());

        cidadeExistente.setUf(cidade.getUf());

        return cidadeRepository.save(cidadeExistente);
    }

    public void excluir(Long id) { cidadeRepository.deleteById(id); }
}
