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
import br.com.interdisciplinar.sebodojoao.repository.ClienteRepository;
import br.com.interdisciplinar.sebodojoao.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("/api/login")
@RequiredArgsConstructor 
public class LoginRestController {
    private final ClienteRepository clienteRepository;
    private final FuncionarioRepository funcionarioRepository;

    @PostMapping
    public ResponseEntity<LoginResponseDTO> fazerLogin(@RequestBody LoginRequestDTO dto) {
        
        // 1-) Tenta achar como Cliente
        Optional<Cliente> clienteOpt = clienteRepository.findByEmailAndSenha(dto.email(), dto.senha());
        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            return ResponseEntity.ok(new LoginResponseDTO(cliente.getId(), cliente.getNome(), 
                                                            cliente.getEmail(), "CLIENTE"));
        }

        // 2-) Se não achou, tenta achar como Funcionário
        Optional<Funcionario> funcionarioOpt = funcionarioRepository.findByEmailAndSenha(dto.email(), dto.senha());
        if (funcionarioOpt.isPresent()) {
            Funcionario funcionario = funcionarioOpt.get();
            return ResponseEntity.ok(new LoginResponseDTO(funcionario.getId(), funcionario.getNome(), 
                                                            funcionario.getEmail(), "FUNCIONARIO"));
        }

        // Se não achou em nenhum lugar, retorna Erro 401 (Não autorizado)
        return ResponseEntity.status(401).build();
    } 
}
