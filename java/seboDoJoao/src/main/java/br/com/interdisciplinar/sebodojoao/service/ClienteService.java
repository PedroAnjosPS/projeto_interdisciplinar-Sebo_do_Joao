package br.com.interdisciplinar.sebodojoao.service;

import br.com.interdisciplinar.sebodojoao.dto.ClienteRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.ClienteResponseDTO;
import br.com.interdisciplinar.sebodojoao.model.Cep;
import br.com.interdisciplinar.sebodojoao.model.Cliente;
import br.com.interdisciplinar.sebodojoao.repository.CepRepository;
import br.com.interdisciplinar.sebodojoao.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {
    private final ClienteRepository clienteRepository;
    private final CepRepository cepRepository;

    public List<ClienteResponseDTO> listarTodos() {
        return clienteRepository.findAll()
                .stream()
                .map(cliente -> new ClienteResponseDTO(
                        cliente.getId(),
                        cliente.getNome(),
                        cliente.getEmail(),
                        cliente.getSenha(),
                        cliente.getTelefone(),
                        cliente.getBairro(),
                        cliente.getLogradouro(),
                        cliente.getCep() != null ? cliente.getCep().getNr() : null, // Ajuste conforme o tipo do ID da classe Cep
                        cliente.getStatus(),
                        cliente.getCpf(),
                        cliente.getDataNascimento()
                ))
                .toList();
    }

    public ClienteResponseDTO buscarPorId(Long id) {
        Cliente cliente = clienteRepository
                .findById(id)
                .orElseThrow();

        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getSenha(),
                cliente.getTelefone(),
                cliente.getBairro(),
                cliente.getLogradouro(),
                cliente.getCep() != null ? cliente.getCep().getNr() : null,
                cliente.getStatus(),
                cliente.getCpf(),
                cliente.getDataNascimento()
        );
    }

    public ClienteResponseDTO cadastrar(ClienteRequestDTO dto) {
        Cep cep = cepRepository
                .findById(dto.cepNr())
                .orElseThrow();

        Cliente cliente = new Cliente();
        cliente.setNome(dto.nome());
        cliente.setEmail(dto.email());
        cliente.setSenha(dto.senha());
        cliente.setTelefone(dto.telefone());
        cliente.setBairro(dto.bairro());
        cliente.setLogradouro(dto.logradouro());
        cliente.setCep(cep);
        cliente.setStatus(dto.status());
        cliente.setCpf(dto.cpf());
        cliente.setDataNascimento(dto.dataNascimento());

        Cliente salvo = clienteRepository.save(cliente);

        return new ClienteResponseDTO(
                salvo.getId(),
                salvo.getNome(),
                salvo.getEmail(),
                salvo.getSenha(),
                salvo.getTelefone(),
                salvo.getBairro(),
                salvo.getLogradouro(),
                salvo.getCep() != null ? salvo.getCep().getNr() : null,
                salvo.getStatus(),
                salvo.getCpf(),
                salvo.getDataNascimento()
        );
    }

    public ClienteResponseDTO atualizar(Long id, ClienteRequestDTO dto) {
        Cliente clienteExistente = clienteRepository
                .findById(id)
                .orElseThrow();

        Cep cep = cepRepository
                .findById(dto.cepNr())
                .orElseThrow();

        clienteExistente.setNome(dto.nome());
        clienteExistente.setEmail(dto.email());
        clienteExistente.setSenha(dto.senha());
        clienteExistente.setTelefone(dto.telefone());
        clienteExistente.setBairro(dto.bairro());
        clienteExistente.setLogradouro(dto.logradouro());
        clienteExistente.setCep(cep);
        clienteExistente.setStatus(dto.status());
        clienteExistente.setCpf(dto.cpf());
        clienteExistente.setDataNascimento(dto.dataNascimento());

        Cliente atualizado = clienteRepository.save(clienteExistente);

        return new ClienteResponseDTO(
                atualizado.getId(),
                atualizado.getNome(),
                atualizado.getEmail(),
                atualizado.getSenha(),
                atualizado.getTelefone(),
                atualizado.getBairro(),
                atualizado.getLogradouro(),
                atualizado.getCep() != null ? atualizado.getCep().getNr() : null,
                atualizado.getStatus(),
                atualizado.getCpf(),
                atualizado.getDataNascimento()
        );
    }

    public void excluir(Long id) {
        clienteRepository.deleteById(id);
    }
}