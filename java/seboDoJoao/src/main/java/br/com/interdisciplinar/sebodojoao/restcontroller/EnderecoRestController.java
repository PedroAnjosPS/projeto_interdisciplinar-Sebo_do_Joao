package br.com.interdisciplinar.sebodojoao.restcontroller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.interdisciplinar.sebodojoao.dto.EnderecoRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.EnderecoResponseDTO;
import br.com.interdisciplinar.sebodojoao.service.EnderecoService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

// NOVO: rotas dos endereços de entrega ("Meus endereços" e checkout).
// Em TODAS as rotas, o cliente vem da SESSÃO de login (ControleDeAcesso), nunca do navegador:
// cada cliente só enxerga e altera os próprios endereços.
@RestController
@RequestMapping("/api/enderecos")
@RequiredArgsConstructor
public class EnderecoRestController {
    private final EnderecoService enderecoService;

    // Lista os endereços do cliente logado (o padrão vem primeiro)
    @GetMapping
    public List<EnderecoResponseDTO> listar(HttpSession sessao) {
        return enderecoService.listar(ControleDeAcesso.exigirCliente(sessao));
    }

    @GetMapping("/{id}")
    public EnderecoResponseDTO buscar(@PathVariable Long id, HttpSession sessao) {
        return enderecoService.buscar(ControleDeAcesso.exigirCliente(sessao), id);
    }

    // Cadastra um endereço novo
    @PostMapping
    public EnderecoResponseDTO criar(@RequestBody EnderecoRequestDTO dto, HttpSession sessao) {
        return enderecoService.salvar(ControleDeAcesso.exigirCliente(sessao), null, dto);
    }

    // Edita um endereço existente
    @PutMapping("/{id}")
    public EnderecoResponseDTO editar(@PathVariable Long id, @RequestBody EnderecoRequestDTO dto, HttpSession sessao) {
        return enderecoService.salvar(ControleDeAcesso.exigirCliente(sessao), id, dto);
    }

    // Marca o endereço como padrão
    @PostMapping("/{id}/padrao")
    public EnderecoResponseDTO definirPadrao(@PathVariable Long id, HttpSession sessao) {
        return enderecoService.definirPadrao(ControleDeAcesso.exigirCliente(sessao), id);
    }

    // "Exclui" o endereço (na verdade o marca como inativo, para preservar o histórico das entregas)
    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id, HttpSession sessao) {
        enderecoService.remover(ControleDeAcesso.exigirCliente(sessao), id);
    }
}
