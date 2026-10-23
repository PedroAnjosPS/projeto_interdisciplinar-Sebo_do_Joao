package br.com.interdisciplinar.sebodojoao.service;

import br.com.interdisciplinar.sebodojoao.dto.ProdutoRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.ProdutoResponseDTO;
import br.com.interdisciplinar.sebodojoao.model.Categoria;
import br.com.interdisciplinar.sebodojoao.model.Funcionario;
import br.com.interdisciplinar.sebodojoao.model.Genero;
import br.com.interdisciplinar.sebodojoao.model.ImagemProduto;
import br.com.interdisciplinar.sebodojoao.model.Produto;
import br.com.interdisciplinar.sebodojoao.repository.CategoriaRepository;
import br.com.interdisciplinar.sebodojoao.repository.FuncionarioRepository;
import br.com.interdisciplinar.sebodojoao.repository.GeneroRepository;
import br.com.interdisciplinar.sebodojoao.repository.ImagemProdutoRepository;
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
    private final ImagemProdutoRepository imagemProdutoRepository;

    public List<ProdutoResponseDTO> listarTodos() {
        return produtoRepository.findAll()
                .stream()
                .map(produto -> {
                        // Pega o caminho da primeira imagem da lista (caso exista) ou null caso não tenha imagem
                        String caminhoImagem = produto.getImagens().isEmpty() ?
                                                        null : produto.getImagens().get(0).getCaminho();

                        return new ProdutoResponseDTO(
                                produto.getId(),
                                produto.getNome(),
                                produto.getDescricao(),
                                produto.getPreco(),
                                produto.getEstoque(),
                                produto.getCategoria().getId(),
                                produto.getGenero().getId(),
                                produto.getFuncionario().getId(),
                                produto.getStatus(),
                                caminhoImagem
                        );
                })
                .toList();
    }

    public ProdutoResponseDTO buscarPorId(Long id) {
        Produto produto = produtoRepository
                .findById(id)
                .orElseThrow();

        String caminhoImagem = produto.getImagens().isEmpty() ?
                                                        null : produto.getImagens().get(0).getCaminho();

        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getEstoque(),
                produto.getCategoria().getId(),
                produto.getGenero().getId(),
                produto.getFuncionario().getId(),
                produto.getStatus(),
                caminhoImagem
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

        String caminhoImagem = null;

        // Se o front-end enviar uma imagem, salva na tabela imagem_produtos
        if (dto.imagem() != null && !dto.imagem().trim().isEmpty()) {
                ImagemProduto novaImagem = new ImagemProduto();
                novaImagem.setCaminho(dto.imagem());
                novaImagem.setProduto(salvo);
                imagemProdutoRepository.save(novaImagem);
                caminhoImagem = dto.imagem();
        }

        return new ProdutoResponseDTO(
                salvo.getId(),
                salvo.getNome(),
                salvo.getDescricao(),
                salvo.getPreco(),
                salvo.getEstoque(),
                salvo.getCategoria().getId(),
                salvo.getGenero().getId(),
                salvo.getFuncionario().getId(),
                salvo.getStatus(),
                caminhoImagem
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

        String caminhoImagem = null;

        if (dto.imagem() != null && !dto.imagem().trim().isEmpty()) {
                if (atualizado.getImagens().isEmpty()) {
                        ImagemProduto novaImagem = new ImagemProduto();
                        novaImagem.setCaminho(dto.imagem());
                        novaImagem.setProduto(atualizado);
                        imagemProdutoRepository.save(novaImagem);
                        caminhoImagem = dto.imagem();
                } else {
                        ImagemProduto imagemExistente = atualizado.getImagens().get(0);
                        imagemExistente.setCaminho(dto.imagem());
                        imagemProdutoRepository.save(imagemExistente);
                        caminhoImagem = dto.imagem();
                }
        }

        return new ProdutoResponseDTO(
                atualizado.getId(),
                atualizado.getNome(),
                atualizado.getDescricao(),
                atualizado.getPreco(),
                atualizado.getEstoque(),
                atualizado.getCategoria().getId(),
                atualizado.getGenero().getId(),
                atualizado.getFuncionario().getId(),
                atualizado.getStatus(),
                caminhoImagem
        );
    }

    public void excluir(Long id) { produtoRepository.deleteById(id); }
}
