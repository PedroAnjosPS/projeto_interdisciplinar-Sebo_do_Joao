package br.com.interdisciplinar.sebodojoao.service;

import br.com.interdisciplinar.sebodojoao.dto.ProdutoRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.ProdutoResponseDTO;
import br.com.interdisciplinar.sebodojoao.model.Categoria;
import br.com.interdisciplinar.sebodojoao.model.Funcionario;
import br.com.interdisciplinar.sebodojoao.model.Genero;
import br.com.interdisciplinar.sebodojoao.model.ImagemProduto;
import br.com.interdisciplinar.sebodojoao.model.Produto;
import br.com.interdisciplinar.sebodojoao.model.StatusProduto;
import br.com.interdisciplinar.sebodojoao.repository.CategoriaRepository;
import br.com.interdisciplinar.sebodojoao.repository.FuncionarioRepository;
import br.com.interdisciplinar.sebodojoao.repository.GeneroRepository;
import br.com.interdisciplinar.sebodojoao.repository.ImagemProdutoRepository;
import br.com.interdisciplinar.sebodojoao.repository.ItemCarrinhoRepository;
import br.com.interdisciplinar.sebodojoao.repository.ItemPedidoRepository;
import br.com.interdisciplinar.sebodojoao.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {
    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final GeneroRepository generoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final ImagemProdutoRepository imagemProdutoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final ItemCarrinhoRepository itemCarrinhoRepository;

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
        // Confere os dados no servidor (o formulário do site pode ser contornado)
        String nome = conferirDados(dto);
        Categoria categoria = buscarCategoria(dto.categoriaId());
        Genero genero = buscarGenero(dto.generoId());
        Funcionario funcionario = buscarFuncionario(dto.funcionarioId());

        Produto produto = new Produto();

        produto.setNome(nome);
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

        String nome = conferirDados(dto);
        Categoria categoria = buscarCategoria(dto.categoriaId());
        Genero genero = buscarGenero(dto.generoId());
        Funcionario funcionario = buscarFuncionario(dto.funcionarioId());

        produtoExistente.setNome(nome);
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

    // Produto que já foi vendido não pode ser apagado: o histórico dos pedidos aponta para ele.
    // Nesse caso ele só fica INATIVO (some da loja). Produto que nunca foi vendido é apagado de vez,
    // junto com as imagens e com as vezes em que estava no carrinho de alguém.
    // Devolve true quando o produto foi apenas inativado.
    @Transactional
    public boolean excluir(Long id) {
        Produto produto = produtoRepository.findById(id).orElseThrow();

        if (itemPedidoRepository.existsByProduto_Id(id)) {
            produto.setStatus(StatusProduto.INATIVO);
            produtoRepository.save(produto);
            return true;
        }

        itemCarrinhoRepository.apagarDoProduto(id);
        imagemProdutoRepository.deleteAll(produto.getImagens());
        produtoRepository.delete(produto);
        produtoRepository.flush();
        return false;
    }

    // Confere os dados do produto e devolve o nome já sem espaços nas pontas
    private String conferirDados(ProdutoRequestDTO dto) {
        String nome = dto.nome() == null ? "" : dto.nome().trim();
        if (nome.isEmpty()) throw erro("Informe o nome do produto.");
        if (nome.length() > 100) throw erro("O nome pode ter no máximo 100 caracteres.");

        if (dto.preco() == null || dto.preco().signum() <= 0) throw erro("O preço precisa ser maior que zero.");
        if (dto.preco().scale() > 2) throw erro("O preço deve ter no máximo 2 casas decimais.");
        if (dto.preco().compareTo(new BigDecimal("99999999.99")) > 0) throw erro("O preço informado é alto demais.");

        if (dto.estoque() == null || dto.estoque() < 0) throw erro("O estoque não pode ser negativo.");
        if (dto.status() == null) throw erro("Escolha a situação do produto.");
        return nome;
    }

    private Categoria buscarCategoria(Long id) {
        return categoriaRepository.findById(id).orElseThrow(() -> erro("Categoria não encontrada."));
    }

    private Genero buscarGenero(Long id) {
        return generoRepository.findById(id).orElseThrow(() -> erro("Gênero não encontrado."));
    }

    private Funcionario buscarFuncionario(Long id) {
        return funcionarioRepository.findById(id).orElseThrow(() -> erro("Funcionário responsável não encontrado."));
    }

    private static ResponseStatusException erro(String mensagem) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
    }
}
