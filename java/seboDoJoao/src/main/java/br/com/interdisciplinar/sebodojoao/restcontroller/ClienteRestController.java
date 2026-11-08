package br.com.interdisciplinar.sebodojoao.restcontroller;

import br.com.interdisciplinar.sebodojoao.dto.ClienteRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.ClienteResponseDTO;
import br.com.interdisciplinar.sebodojoao.repository.UsuarioRepository;
import br.com.interdisciplinar.sebodojoao.service.ClienteService;
import lombok.RequiredArgsConstructor;

import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

// Quem pode o quê (o resto da conferência de acesso está em ProtecaoDasRotas):
//   - cadastrar uma conta nova: qualquer pessoa (é o "Criar conta" do site);
//   - ver os dados de uma conta: o próprio cliente ou um funcionário;
//   - alterar ou excluir a conta: somente o próprio cliente logado.
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteRestController {
    private final ClienteService clienteService;
    private final UsuarioRepository usuarioRepository;

    // Só funcionário chega aqui (ver ProtecaoDasRotas)
    @GetMapping
    public List<ClienteResponseDTO> listarTodos() {
        return clienteService.listarTodos();
    }

    // O cadastro confere se o e-mail já existe por aqui, sem precisar listar as contas de todo mundo
    @GetMapping("/email-disponivel")
    public Map<String, Boolean> emailDisponivel(@RequestParam String email,
                                                @RequestParam(required = false) Long ignorarId) {
        boolean emUso = ignorarId == null
                ? usuarioRepository.existsByEmailIgnoreCase(email.trim())
                : usuarioRepository.existsByEmailIgnoreCaseAndIdNot(email.trim(), ignorarId);
        return Map.of("disponivel", !emUso);
    }

    @GetMapping("/{id}")
    public ClienteResponseDTO buscarPorId(@PathVariable Long id, HttpSession sessao) {
        ControleDeAcesso.exigirDonoOuFuncionario(sessao, id);
        return clienteService.buscarPorId(id);
    }

    // Criar conta: o status não pode ser escolhido por quem se cadastra (a conta nasce ativa)
    @PostMapping
    public ClienteResponseDTO cadastrar(@RequestBody ClienteRequestDTO dto) {
        ClienteRequestDTO semStatus = new ClienteRequestDTO(
                dto.nome(), dto.email(), dto.senha(), dto.telefone(), null, null, dto.cpf(), dto.dataNascimento()
        );
        return clienteService.cadastrar(semStatus);
    }

    // MUDANÇA CRÍTICA: Usar POST porque o Tomcat barra pacotes multipart no PUT
    @PostMapping(value = "/{id}", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ClienteResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "cpf", required = false) String cpf,
            @RequestParam(value = "telefone", required = false) String telefone,
            @RequestParam(value = "dataNascimento", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataNascimento,
            @RequestParam(value = "foto", required = false) MultipartFile foto,
            @RequestParam(value = "removerFoto", defaultValue = "false") boolean removerFoto,
            HttpSession sessao) {

        exigirOProprioCliente(sessao, id);

        // null = manter a foto atual; "" = remover (o service grava NULL no banco).
        // A remoção precisa de um campo próprio: um "foto" de texto vazio nunca chegava
        // aqui, porque o Spring só preenche MultipartFile quando recebe um arquivo.
        String caminhoFoto = null;

        if (removerFoto) {
            FotoPerfilArquivo.apagar(id);
            caminhoFoto = "";
        } else if (foto != null && !foto.isEmpty()) {
            caminhoFoto = FotoPerfilArquivo.guardar(id, foto);
        }

        ClienteRequestDTO dto = new ClienteRequestDTO(
                nome, email, null, telefone, null, caminhoFoto, cpf, dataNascimento
        );

        return ResponseEntity.ok(clienteService.atualizar(id, dto));
    }

    // Atualiza dados da conta enviados em JSON (é a rota usada na troca de senha). Só o próprio
    // cliente logado pode alterar a sua conta; o id da URL precisa ser o mesmo da sessão.
    @PutMapping("/{id}")
    public ClienteResponseDTO atualizarPorJson(@PathVariable Long id, @RequestBody ClienteRequestDTO dto, HttpSession sessao) {
        exigirOProprioCliente(sessao, id);

        // O status (ativo ou inativo) não pode ser mudado pelo próprio cliente
        ClienteRequestDTO semStatus = new ClienteRequestDTO(
                dto.nome(), dto.email(), dto.senha(), dto.telefone(), null, dto.fotoPerfil(), dto.cpf(), dto.dataNascimento()
        );
        return clienteService.atualizar(id, semStatus);
    }

    @DeleteMapping("/{id}")
    public Map<String, Boolean> excluir(@PathVariable Long id, HttpSession sessao) {
        exigirOProprioCliente(sessao, id);
        // "inativada" = true quando a conta tinha pedidos e foi só inativada, não apagada
        return Map.of("inativada", clienteService.excluir(id));
    }

    private static void exigirOProprioCliente(HttpSession sessao, Long id) {
        Long clienteLogado = ControleDeAcesso.exigirCliente(sessao);

        if (!clienteLogado.equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você só pode alterar a sua própria conta.");
        }
    }
}
