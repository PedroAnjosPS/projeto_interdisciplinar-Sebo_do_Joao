package br.com.interdisciplinar.sebodojoao.service;

import br.com.interdisciplinar.sebodojoao.dto.CategoriaRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.CategoriaResponseDTO;
import br.com.interdisciplinar.sebodojoao.model.Categoria;
import br.com.interdisciplinar.sebodojoao.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;

    public List<CategoriaResponseDTO> listarTodos() {
        return categoriaRepository.findAll()
                .stream()
                .map(Categoria -> new CategoriaResponseDTO(
                        Categoria.getId(),
                        Categoria.getNome()
                ))
                .toList();
    }

    public CategoriaResponseDTO buscarPorId(Long id) {
        Categoria categoria = categoriaRepository
                .findById(id)
                .orElseThrow();

        return new CategoriaResponseDTO(
                categoria.getId(),
                categoria.getNome()
        );
    }

    public CategoriaResponseDTO cadastrar(CategoriaRequestDTO dto) {

        Categoria categoria = new Categoria();

        categoria.setNome(dto.nome());

        Categoria salvo = categoriaRepository.save(categoria);

        return new CategoriaResponseDTO(
                salvo.getId(),
                salvo.getNome()
        );
    }

    public CategoriaResponseDTO atualizar(Long id, CategoriaRequestDTO dto) {
        Categoria categoriaExistente = categoriaRepository
                                    .findById(id)
                                    .orElseThrow();

        categoriaExistente.setNome(dto.nome());

        Categoria atualizado = categoriaRepository.save(categoriaExistente);

        return new CategoriaResponseDTO(
                atualizado.getId(),
                atualizado.getNome()
        );
    }

    public void excluir(Long id) { categoriaRepository.deleteById(id); }
}
