package br.com.interdisciplinar.sebodojoao.service;

import br.com.interdisciplinar.sebodojoao.model.Produto;
import br.com.interdisciplinar.sebodojoao.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {
    private final ProdutoRepository produtoRepository;

    public List<Produto> listarTodos() { return produtoRepository.findAll(); }

    public Produto buscarPorId(Long id) { return produtoRepository.findById(id).orElseThrow(); }

    public Produto cadastrar(Produto produto) { return produtoRepository.save(produto); }

    public Produto atualizar(Long id, @NonNull Produto produto) {
        Produto produtoExistente = produtoRepository.findById(id).orElseThrow();

        produtoExistente.setNome(produto.getNome());
        produtoExistente.setDescricao(produto.getDescricao());
        produtoExistente.setPreco(produto.getPreco());
        produtoExistente.setEstoque(produto.getEstoque());
        produtoExistente.setCategoria(produto.getCategoria());
        produtoExistente.setGenero(produto.getGenero());
        produtoExistente.setFuncionario(produto.getFuncionario());
        produtoExistente.setStatus(produto.getStatus());

        return produtoRepository.save(produtoExistente);
    }

    public void excluir(Long id) { produtoRepository.deleteById(id); }
}
