package br.com.interdisciplinar.sebodojoao.service;

import br.com.interdisciplinar.sebodojoao.dto.UfRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.UfResponseDTO;
import br.com.interdisciplinar.sebodojoao.model.Uf;
import br.com.interdisciplinar.sebodojoao.repository.UFRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UfService {
    private final UFRepository ufRepository;

    public List<UfResponseDTO> listarTodos() {
        return ufRepository.findAll()
                .stream()
                .map(Uf -> new UfResponseDTO(
                        Uf.getSigla(),
                        Uf.getNome()
                ))
                .toList();
    }

    public UfResponseDTO buscarPorId(String sigla) {
        Uf uf = ufRepository.findById(sigla).orElseThrow();

        return new UfResponseDTO(
                uf.getSigla(),
                uf.getNome()
        );
    }

    public UfResponseDTO cadastrar(UfRequestDTO dto) {
        Uf uf = new Uf();

        uf.setSigla(dto.sigla());
        uf.setNome(dto.nome());

        Uf salvo = ufRepository.save(uf);

        return new UfResponseDTO(
                salvo.getSigla(),
                salvo.getNome()
        );
    }

    public UfResponseDTO atualizar(String sigla, UfRequestDTO dto) {
        Uf ufExistente = ufRepository.findById(sigla).orElseThrow();

        ufExistente.setSigla(dto.sigla());
        ufExistente.setNome(dto.nome());

        Uf atualizado = ufRepository.save(ufExistente);

        return new UfResponseDTO(
                atualizado.getSigla(),
                atualizado.getNome()
        );
    }

    public void excluir(String sigla) {
        ufRepository.deleteById(sigla);
    }
}
