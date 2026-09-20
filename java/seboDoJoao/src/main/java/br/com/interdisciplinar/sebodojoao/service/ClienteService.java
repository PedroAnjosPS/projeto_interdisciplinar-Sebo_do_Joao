package br.com.interdisciplinar.sebodojoao.service;

import br.com.interdisciplinar.sebodojoao.model.Cliente;
import br.com.interdisciplinar.sebodojoao.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {
    private final ClienteRepository clienteRepository;

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id).orElseThrow();
    }

    public Cliente cadastrar(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public Cliente atualizar(Long id, @NonNull Cliente cliente) {
        Cliente clienteExistente = clienteRepository.findById(id).orElseThrow();

        clienteExistente.setNome(cliente.getNome());
        clienteExistente.setEmail(cliente.getEmail());
        clienteExistente.setSenha(cliente.getSenha());
        clienteExistente.setTelefone(cliente.getTelefone());
        clienteExistente.setBairro(cliente.getBairro());
        clienteExistente.setLogradouro(cliente.getLogradouro());
        clienteExistente.setCep(cliente.getCep());
        clienteExistente.setStatus(cliente.getStatus());
        clienteExistente.setCpf(cliente.getCpf());
        clienteExistente.setDataNascimento(cliente.getDataNascimento());

        return clienteRepository.save(clienteExistente);
    }

    public void excluir(Long id) {
        clienteRepository.deleteById(id);
    }
}
