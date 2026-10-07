package br.com.interdisciplinar.sebodojoao.service;

import br.com.interdisciplinar.sebodojoao.dto.ImagemProdutoRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.ImagemProdutoResponseDTO;
import br.com.interdisciplinar.sebodojoao.model.ImagemProduto;
import br.com.interdisciplinar.sebodojoao.model.Produto;
import br.com.interdisciplinar.sebodojoao.repository.ImagemProdutoRepository;
import br.com.interdisciplinar.sebodojoao.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImagemProdutoService {
    private final ImagemProdutoRepository imagemProdutoRepository;
    private final ProdutoRepository produtoRepository;

    public List<ImagemProdutoResponseDTO> listarTodos() {
        return imagemProdutoRepository.findAll()
                .stream()
                .map(imagem -> new ImagemProdutoResponseDTO(
                        imagem.getId(),
                        imagem.getCaminho(),
                        imagem.getProduto().getId()
                ))
                .toList();
    }

    public ImagemProdutoResponseDTO buscarPorId(Long id) {
        ImagemProduto imagem = imagemProdutoRepository
                .findById(id)
                .orElseThrow();

        return new ImagemProdutoResponseDTO(
                imagem.getId(),
                imagem.getCaminho(),
                imagem.getProduto().getId()
        );
    }

    public ImagemProdutoResponseDTO cadastrar(ImagemProdutoRequestDTO dto) {
        Produto produto = produtoRepository
                .findById(dto.produtoId())
                .orElseThrow();

        ImagemProduto imagem = new ImagemProduto();
        imagem.setCaminho(dto.caminho());
        imagem.setProduto(produto);

        ImagemProduto salva = imagemProdutoRepository.save(imagem);

        return new ImagemProdutoResponseDTO(
                salva.getId(),
                salva.getCaminho(),
                salva.getProduto().getId()
        );
    }

    public ImagemProdutoResponseDTO atualizar(Long id, ImagemProdutoRequestDTO dto) {
        ImagemProduto imagemExistente = imagemProdutoRepository
                .findById(id)
                .orElseThrow();

        Produto produto = produtoRepository
                .findById(dto.produtoId())
                .orElseThrow();

        imagemExistente.setCaminho(dto.caminho());
        imagemExistente.setProduto(produto);

        ImagemProduto atualizada = imagemProdutoRepository.save(imagemExistente);

        return new ImagemProdutoResponseDTO(
                atualizada.getId(),
                atualizada.getCaminho(),
                atualizada.getProduto().getId()
        );
    }

    public void excluir(Long id) {
        imagemProdutoRepository.deleteById(id);
    }
}