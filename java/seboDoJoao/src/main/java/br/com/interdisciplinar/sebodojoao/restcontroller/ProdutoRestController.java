package br.com.interdisciplinar.sebodojoao.restcontroller;

import br.com.interdisciplinar.sebodojoao.dto.ProdutoRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.ProdutoResponseDTO;
import br.com.interdisciplinar.sebodojoao.model.StatusProduto;
import br.com.interdisciplinar.sebodojoao.service.ProdutoService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
public class ProdutoRestController {
    private final ProdutoService produtoService;

    @GetMapping
    public List<ProdutoResponseDTO> listarTodos() {
        return produtoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ProdutoResponseDTO buscarPorId(@PathVariable Long id) {
        return produtoService.buscarPorId(id);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProdutoResponseDTO> cadastrar(
                    @RequestParam("nome") String nome,
                    @RequestParam("categoriaId") Long categoriaId,
                    @RequestParam("generoId") Long generoId,
                    @RequestParam("funcionarioId") Long funcionarioId,
                    @RequestParam("preco") BigDecimal preco,
                    @RequestParam("estoque") Integer estoque,
                    @RequestParam("status") StatusProduto status,
                    @RequestParam("descricao") String descricao,
                    @RequestParam(value = "imagem", required = false) MultipartFile imagemFisica) {
        String caminhoImagemDb = null;

    if (imagemFisica != null && !imagemFisica.isEmpty()) {
        try {
            // Diretório para as imagens dos produtos
            String diretorio = "src/main/resources/static/imagens/produtos/";
            Path caminhoDiretorio = Paths.get(diretorio);
            
            if (!Files.exists(caminhoDiretorio)) {
                Files.createDirectories(caminhoDiretorio);
            }

            // Usa o nome original do ficheiro para simplificar (ou gere um UUID)
            String nomeArquivo = System.currentTimeMillis() + "_" + imagemFisica.getOriginalFilename();
            java.nio.file.Path caminhoArquivo = caminhoDiretorio.resolve(nomeArquivo);

            java.nio.file.Files.copy(imagemFisica.getInputStream(), caminhoArquivo, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            caminhoImagemDb = "/imagens/produtos/" + nomeArquivo;
        } catch (java.io.IOException e) {
            throw new RuntimeException("Erro ao guardar a imagem do produto.", e);
        }
    }

    // Cria o DTO de requisição
    ProdutoRequestDTO dto = new ProdutoRequestDTO(
            nome, descricao, preco, estoque, categoriaId, generoId, funcionarioId, status, caminhoImagemDb
    );

    return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.cadastrar(dto));
}

    @PostMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProdutoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestParam("nome") String nome,
            @RequestParam("categoriaId") Long categoriaId,
            @RequestParam("generoId") Long generoId,
            @RequestParam("funcionarioId") Long funcionarioId,
            @RequestParam("preco") BigDecimal preco,
            @RequestParam("estoque") Integer estoque,
            @RequestParam("status") StatusProduto status,
            @RequestParam("descricao") String descricao,
            @RequestParam(value = "imagem", required = false) MultipartFile imagemFisica) {

        String caminhoImagemProduto = null;

        // Só processa e guarda a imagem se o administrador tiver selecionado um novo ficheiro
        if (imagemFisica != null && !imagemFisica.isEmpty()) {
            try {
                String diretorio = "src/main/resources/static/imagens/produtos/";
                Path caminhoDiretorio = Paths.get(diretorio);

                if (!Files.exists(caminhoDiretorio)) {
                    Files.createDirectories(caminhoDiretorio);
                }

                String nomeArquivo = System.currentTimeMillis() + "_" + imagemFisica.getOriginalFilename();
                Path caminhoArquivo = caminhoDiretorio.resolve(nomeArquivo);

                Files.copy(imagemFisica.getInputStream(), caminhoArquivo, StandardCopyOption.REPLACE_EXISTING);

                caminhoImagemProduto = "/imagens/produtos/" + nomeArquivo;
            } catch (IOException error) {
                throw new RuntimeException("Erro ao atualizar a imagem do produto! Erro: " + error);
            }
        }

        ProdutoRequestDTO dto = new ProdutoRequestDTO(
            nome, descricao, preco, estoque, categoriaId, generoId, funcionarioId, status, caminhoImagemProduto
        );

        return ResponseEntity.ok(produtoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        produtoService.excluir(id);
    }
}
