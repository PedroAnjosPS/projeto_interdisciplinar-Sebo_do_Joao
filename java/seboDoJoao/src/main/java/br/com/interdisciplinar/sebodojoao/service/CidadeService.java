package br.com.interdisciplinar.sebodojoao.service;

import br.com.interdisciplinar.sebodojoao.dto.CidadeRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.CidadeResponseDTO;
import br.com.interdisciplinar.sebodojoao.model.Cidade;
import br.com.interdisciplinar.sebodojoao.model.Uf;
import br.com.interdisciplinar.sebodojoao.repository.CidadeRepository;
import br.com.interdisciplinar.sebodojoao.repository.UFRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CidadeService {
    private final CidadeRepository cidadeRepository;
    private final UFRepository ufRepository;

    public List<CidadeResponseDTO> listarTodos() {
        return cidadeRepository.findAll()
                .stream()
                .map(Cidade -> new CidadeResponseDTO(
                        Cidade.getId(),
                        Cidade.getNome(),
                        Cidade.getUf().getSigla()
                ))
                .toList();
    }

    public CidadeResponseDTO buscarPorId(Long id) {
        Cidade cidade = cidadeRepository.findById(id).orElseThrow();

        return new CidadeResponseDTO(
                cidade.getId(),
                cidade.getNome(),
                cidade.getUf().getSigla()
        );
    }

    public CidadeResponseDTO cadastrar(CidadeRequestDTO dto) {
        Cidade cidade = new Cidade();

        Uf uf = ufRepository.findById(dto.ufSigla()).orElseThrow();

        cidade.setNome(dto.nome());
        cidade.setUf(uf);

        Cidade salvo = cidadeRepository.save(cidade);

        return new CidadeResponseDTO(
                salvo.getId(),
                salvo.getNome(),
                salvo.getUf().getSigla()
        );
    }

    public CidadeResponseDTO atualizar(Long id, CidadeRequestDTO dto) {
        Cidade cidadeExistente = cidadeRepository.findById(id).orElseThrow();

        Uf uf = ufRepository.findById(dto.ufSigla()).orElseThrow();

        cidadeExistente.setNome(dto.nome());
        cidadeExistente.setUf(uf);

        Cidade atualizado = cidadeRepository.save(cidadeExistente);

        return new CidadeResponseDTO(
                atualizado.getId(),
                atualizado.getNome(),
                atualizado.getUf().getSigla()
        );
    }

    public void excluir(Long id) { cidadeRepository.deleteById(id); }
}
