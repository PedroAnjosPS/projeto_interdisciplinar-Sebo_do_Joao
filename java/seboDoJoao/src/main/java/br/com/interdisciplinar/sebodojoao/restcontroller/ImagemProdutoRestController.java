package br.com.interdisciplinar.sebodojoao.restcontroller;

import br.com.interdisciplinar.sebodojoao.dto.ImagemProdutoRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.ImagemProdutoResponseDTO;
import br.com.interdisciplinar.sebodojoao.service.ImagemProdutoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/imagem-produtos")
@RequiredArgsConstructor
public class ImagemProdutoRestController {
    private final ImagemProdutoService imagemProdutoService;

    @GetMapping
    public List<ImagemProdutoResponseDTO> listarTodos() {
        return imagemProdutoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ImagemProdutoResponseDTO buscarPorId(@PathVariable Long id) {
        return imagemProdutoService.buscarPorId(id);
    }

    @PostMapping
    public ImagemProdutoResponseDTO cadastrar(@RequestBody ImagemProdutoRequestDTO dto) {
        return imagemProdutoService.cadastrar(dto);
    }

    @PutMapping("/{id}")
    public ImagemProdutoResponseDTO atualizar(
            @PathVariable Long id,
            @RequestBody ImagemProdutoRequestDTO dto) {

        return imagemProdutoService.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        imagemProdutoService.excluir(id);
    }
}