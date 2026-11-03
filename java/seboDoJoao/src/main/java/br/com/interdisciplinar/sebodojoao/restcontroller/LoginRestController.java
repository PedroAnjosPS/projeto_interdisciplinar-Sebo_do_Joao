package br.com.interdisciplinar.sebodojoao.restcontroller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.interdisciplinar.sebodojoao.dto.LoginRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.LoginResponseDTO;
import br.com.interdisciplinar.sebodojoao.model.Cliente;
import br.com.interdisciplinar.sebodojoao.model.Funcionario;
import br.com.interdisciplinar.sebodojoao.model.StatusUsuario;
import br.com.interdisciplinar.sebodojoao.repository.ClienteRepository;
import br.com.interdisciplinar.sebodojoao.repository.FuncionarioRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("/api/login")
@RequiredArgsConstructor 
public class LoginRestController {
    private final ClienteRepository clienteRepository;
    private final FuncionarioRepository funcionarioRepository;

    @PostMapping
    public ResponseEntity<LoginResponseDTO> fazerLogin(@RequestBody LoginRequestDTO dto, HttpSession sessao) {

        // 1-) Tenta achar como Cliente
        Optional<Cliente> clienteOpt = clienteRepository.findByEmailAndSenha(dto.email(), dto.senha());
        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();

            // Cliente que encerrou a conta (e tinha pedidos) fica inativo e não entra mais (erro 403)
            if (cliente.getStatus() == StatusUsuario.INATIVO) {
                return ResponseEntity.status(403).build();
            }

            // Guarda o perfil no servidor: é nele que as regras de compra vão confiar (ver ControleDeAcesso)
            ControleDeAcesso.registrarPerfil(sessao, ControleDeAcesso.PERFIL_CLIENTE);
            // Guarda também o ID do cliente na sessão (usado nas compras, no carrinho e em "Meus pedidos")
            ControleDeAcesso.registrarUsuario(sessao, cliente.getId());
            return ResponseEntity.ok(new LoginResponseDTO(cliente.getId(), cliente.getNome(),
                                                            cliente.getEmail(), ControleDeAcesso.PERFIL_CLIENTE,
                                                            cliente.getFotoPerfil()));
        }

        // 2-) Se não achou, tenta achar como Funcionário
        Optional<Funcionario> funcionarioOpt = funcionarioRepository.findByEmailAndSenha(dto.email(), dto.senha());
        if (funcionarioOpt.isPresent()) {
            Funcionario funcionario = funcionarioOpt.get();

            // Funcionário inativado pelo administrador não consegue mais entrar (erro 403)
            if (funcionario.getStatus() == StatusUsuario.INATIVO) {
                return ResponseEntity.status(403).build();
            }

            ControleDeAcesso.registrarPerfil(sessao, ControleDeAcesso.PERFIL_FUNCIONARIO);
            ControleDeAcesso.registrarAdministrador(sessao, Boolean.TRUE.equals(funcionario.getAdmin()));
            // O ID do funcionário também fica guardado na sessão
            ControleDeAcesso.registrarUsuario(sessao, funcionario.getId());
            return ResponseEntity.ok(new LoginResponseDTO(funcionario.getId(), funcionario.getNome(),
                                                            funcionario.getEmail(), ControleDeAcesso.PERFIL_FUNCIONARIO,
                                                            funcionario.getFotoPerfil()));
        }

        // Se não achou em nenhum lugar, retorna Erro 401 (Não autorizado)
        return ResponseEntity.status(401).build();
    }

    // Encerra a sessão no servidor. Sem isso, o botão "Sair" limparia só o navegador
    // e o perfil continuaria valendo no Java.
    @PostMapping("/sair")
    public void sair(HttpSession sessao) {
        sessao.invalidate();
    }
}
