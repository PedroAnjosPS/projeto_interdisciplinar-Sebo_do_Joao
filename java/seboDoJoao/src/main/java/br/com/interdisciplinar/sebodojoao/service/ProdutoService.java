package br.com.interdisciplinar.sebodojoao.service;

import br.com.interdisciplinar.sebodojoao.dto.ProdutoRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.ProdutoResponseDTO;
import br.com.interdisciplinar.sebodojoao.model.Categoria;
import br.com.interdisciplinar.sebodojoao.model.Funcionario;
import br.com.interdisciplinar.sebodojoao.model.Genero;
import br.com.interdisciplinar.sebodojoao.model.Produto;
import br.com.interdisciplinar.sebodojoao.repository.CategoriaRepository;
import br.com.interdisciplinar.sebodojoao.repository.FuncionarioRepository;
import br.com.interdisciplinar.sebodojoao.repository.GeneroRepository;
import br.com.interdisciplinar.sebodojoao.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {
    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final GeneroRepository generoRepository;
    private final FuncionarioRepository funcionarioRepository;

    public List<ProdutoResponseDTO> listarTodos() {
        return produtoRepository.findAll()
                .stream()
                .map(produto -> new ProdutoResponseDTO(
                        produto.getId(),
                        produto.getNome(),
                        produto.getDescricao(),
                        produto.getPreco(),
                        produto.getEstoque(),
                        produto.getCategoria().getId(),
                        produto.getGenero().getId(),
                        produto.getFuncionario().getId(),
                        produto.getStatus()
                ))
                .toList();
    }

    public ProdutoResponseDTO buscarPorId(Long id) {
        Produto produto = produtoRepository
                .findById(id)
                .orElseThrow();

        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getEstoque(),
                produto.getCategoria().getId(),
                produto.getGenero().getId(),
                produto.getFuncionario().getId(),
                produto.getStatus()
        );
    }

    public ProdutoResponseDTO cadastrar(ProdutoRequestDTO dto) {
        Categoria categoria = categoriaRepository
                              .findById(dto.categoriaId())
                              .orElseThrow();

        Genero genero = generoRepository
                        .findById(dto.generoId())
                        .orElseThrow();

        Funcionario funcionario = funcionarioRepository
                                  .findById(dto.funcionarioId())
                                  .orElseThrow();

        Produto produto = new Produto();

        produto.setNome(dto.nome());
        produto.setDescricao(dto.descricao());
        produto.setPreco(dto.preco());
        produto.setEstoque(dto.estoque());

        produto.setCategoria(categoria);
        produto.setGenero(genero);
        produto.setFuncionario(funcionario);

        produto.setStatus(dto.status());

        Produto salvo = produtoRepository.save(produto);

        return new ProdutoResponseDTO(
                salvo.getId(),
                salvo.getNome(),
                salvo.getDescricao(),
                salvo.getPreco(),
                salvo.getEstoque(),
                salvo.getCategoria().getId(),
                salvo.getGenero().getId(),
                salvo.getFuncionario().getId(),
                salvo.getStatus()
        );
    }

    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto) {
        Produto produtoExistente = produtoRepository
                                    .findById(id)
                                    .orElseThrow();

        Categoria categoria = categoriaRepository
                                .findById(dto.categoriaId())
                                .orElseThrow();

        Genero genero = generoRepository
                        .findById(dto.generoId())
                        .orElseThrow();

        Funcionario funcionario = funcionarioRepository
                                    .findById(dto.funcionarioId())
                                    .orElseThrow();

        produtoExistente.setNome(dto.nome());
        produtoExistente.setDescricao(dto.descricao());
        produtoExistente.setPreco(dto.preco());
        produtoExistente.setEstoque(dto.estoque());

        produtoExistente.setCategoria(categoria);
        produtoExistente.setGenero(genero);
        produtoExistente.setFuncionario(funcionario);

        produtoExistente.setStatus(dto.status());

        Produto atualizado = produtoRepository.save(produtoExistente);

        return new ProdutoResponseDTO(
                atualizado.getId(),
                atualizado.getNome(),
                atualizado.getDescricao(),
                atualizado.getPreco(),
                atualizado.getEstoque(),
                atualizado.getCategoria().getId(),
                atualizado.getGenero().getId(),
                atualizado.getFuncionario().getId(),
                atualizado.getStatus()
        );
    }

    public void excluir(Long id) { produtoRepository.deleteById(id); }
}
