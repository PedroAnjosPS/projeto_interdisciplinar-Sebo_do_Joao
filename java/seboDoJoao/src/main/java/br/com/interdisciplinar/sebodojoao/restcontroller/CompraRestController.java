package br.com.interdisciplinar.sebodojoao.restcontroller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.interdisciplinar.sebodojoao.dto.CompraRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.CompraResponseDTO;
import br.com.interdisciplinar.sebodojoao.service.CompraService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

// Rotas da compra, usadas pelo checkout, por "Meus pedidos" e pelo painel do funcionário.
// Elas existem separadas da /api/pedidos porque aquela rota grava só a tabela pedidos, enquanto uma
// compra mexe em 4 tabelas ao mesmo tempo (entrega, pedido, itens e pagamentos) e no estoque. Tudo
// isso é feito de uma vez pela procedure sp_finalizar_compra.
//
// Em TODAS as rotas, quem é a pessoa vem da SESSÃO de login (ControleDeAcesso), nunca do navegador.
@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
public class CompraRestController {
    private final CompraService compraService;

    // ----- Cliente -----

    // Confirma a compra (botão "Confirmar a compra" do checkout)
    @PostMapping
    public CompraResponseDTO comprar(@RequestBody CompraRequestDTO dto, HttpSession sessao) {
        Long clienteId = ControleDeAcesso.exigirCliente(sessao);
        return compraService.finalizar(clienteId, dto);
    }

    // "Meus pedidos": só os pedidos do cliente logado
    @GetMapping
    public List<CompraResponseDTO> meusPedidos(HttpSession sessao) {
        return compraService.listarDoCliente(ControleDeAcesso.exigirCliente(sessao));
    }

    // Detalhe de um pedido (só se for do cliente logado)
    @GetMapping("/{id}")
    public CompraResponseDTO detalhe(@PathVariable Long id, HttpSession sessao) {
        return compraService.buscarDoCliente(id, ControleDeAcesso.exigirCliente(sessao));
    }

    // Botão "Simular pagamento" do Pix (demonstração)
    @PostMapping("/{id}/simular-pagamento")
    public CompraResponseDTO simularPagamento(@PathVariable Long id, HttpSession sessao) {
        return compraService.aprovarPagamento(id, ControleDeAcesso.exigirCliente(sessao));
    }

    // Botão "Cancelar pedido" (procedure sp_cancelar_pedido)
    @PostMapping("/{id}/cancelar")
    public CompraResponseDTO cancelar(@PathVariable Long id, HttpSession sessao) {
        return compraService.cancelar(id, ControleDeAcesso.exigirCliente(sessao));
    }

    // ----- Funcionário (painel de pedidos) -----

    @GetMapping("/gestao")
    public List<CompraResponseDTO> todosOsPedidos(HttpSession sessao) {
        ControleDeAcesso.exigirFuncionario(sessao);
        return compraService.listarTodos();
    }

    // Anda um passo na entrega (procedure sp_avancar_entrega)
    @PostMapping("/gestao/{id}/avancar-entrega")
    public CompraResponseDTO avancarEntrega(@PathVariable Long id, HttpSession sessao) {
        Long funcionarioId = ControleDeAcesso.exigirFuncionario(sessao);
        return compraService.avancarEntrega(id, funcionarioId);
    }

    // Os erros desta classe (sem login, estoque insuficiente, parcelas inválidas...) saem como
    // {"mensagem": "..."} com o código HTTP certo. O site lê "mensagem" e mostra para a pessoa.
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> tratarErro(ResponseStatusException erro) {
        return ResponseEntity.status(erro.getStatusCode()).body(Map.of("mensagem", String.valueOf(erro.getReason())));
    }
}
