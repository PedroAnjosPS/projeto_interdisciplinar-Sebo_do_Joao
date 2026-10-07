package br.com.interdisciplinar.sebodojoao.service;

import br.com.interdisciplinar.sebodojoao.dto.GeneroRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.GeneroResponseDTO;
import br.com.interdisciplinar.sebodojoao.model.Genero;
import br.com.interdisciplinar.sebodojoao.repository.GeneroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GeneroService {
    private final GeneroRepository generoRepository;

    public List<GeneroResponseDTO> listarTodos() {
        return generoRepository.findAll()
                .stream()
                .map(genero -> new GeneroResponseDTO(
                        genero.getId(),
                        genero.getNome()
                ))
                .toList();
    }

    public GeneroResponseDTO buscarPorId(Long id) {
        Genero genero = generoRepository
                .findById(id)
                .orElseThrow();

        return new GeneroResponseDTO(
                genero.getId(),
                genero.getNome()
        );
    }

    public GeneroResponseDTO cadastrar(GeneroRequestDTO dto) {
        Genero genero = new Genero();
        genero.setNome(dto.nome());

        Genero salvo = generoRepository.save(genero);

        return new GeneroResponseDTO(
                salvo.getId(),
                salvo.getNome()
        );
    }

    public GeneroResponseDTO atualizar(Long id, GeneroRequestDTO dto) {
        Genero generoExistente = generoRepository
                .findById(id)
                .orElseThrow();

        generoExistente.setNome(dto.nome());

        Genero atualizado = generoRepository.save(generoExistente);

        return new GeneroResponseDTO(
                atualizado.getId(),
                atualizado.getNome()
        );
    }

    public void excluir(Long id) {
        generoRepository.deleteById(id);
    }
}