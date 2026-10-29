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
                .map(cliente -> {
                        // Pega o caminho da foto de perfil (caso exista) ou null caso não tenha imagem
                        String fotoPerfil = (cliente.getFotoPerfil() != null && cliente.getFotoPerfil().isEmpty()) ?
                                                        cliente.getFotoPerfil() : null;

                        return new ClienteResponseDTO(
                                cliente.getId(),
                                cliente.getNome(),
                                cliente.getEmail(),
                                cliente.getSenha(),
                                cliente.getTelefone(),
                                cliente.getBairro(),
                                cliente.getLogradouro(),
                                cliente.getCep() != null ? cliente.getCep().getNumeroCep() : null, // Ajuste conforme o tipo do ID da classe Cep
                                cliente.getStatus(),
                                fotoPerfil,
                                cliente.getCpf(),
                                cliente.getDataNascimento()
                        );
                })
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
                cliente.getCep() != null ? cliente.getCep().getNumeroCep() : null,
                cliente.getStatus(),
                cliente.getFotoPerfil(),
                cliente.getCpf(),
                cliente.getDataNascimento()
        );
    }

    public ClienteResponseDTO cadastrar(ClienteRequestDTO dto) {
        // Só busca o CEP se o front-end enviar algum número de CEP. Caso contrário, o CEP será nulo.
        Cep cep = null;
        if (dto.cepNumero() != null && !dto.cepNumero().trim().isEmpty()) {
                cep = cepRepository
                        .findById(dto.cepNumero())
                        .orElseThrow();
        }                

        Cliente cliente = new Cliente();
        cliente.setNome(dto.nome());
        cliente.setEmail(dto.email());
        cliente.setSenha(dto.senha());
        cliente.setTelefone(dto.telefone());
        cliente.setBairro(dto.bairro());
        cliente.setLogradouro(dto.logradouro());
        cliente.setCep(cep);
        cliente.setFotoPerfil(dto.fotoPerfil());

        // Se vier null, o padrão do banco (1) será assumido (ou você pode setar manualmente aqui)
        if (dto.status() != null) {
                cliente.setStatus(dto.status());
        }
        
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
                salvo.getCep() != null ? salvo.getCep().getNumeroCep() : null,
                salvo.getStatus(),
                salvo.getFotoPerfil(),
                salvo.getCpf(),
                salvo.getDataNascimento()
        );
    }

    public ClienteResponseDTO atualizar(Long id, ClienteRequestDTO dto) {
        Cliente clienteExistente = clienteRepository
                .findById(id)
                .orElseThrow();

        // Checa se os campos não são nulos antes de atualizar
        if (dto.nome() != null) clienteExistente.setNome(dto.nome());
        if (dto.email() != null) clienteExistente.setEmail(dto.email());
        if (dto.senha() != null) clienteExistente.setSenha(dto.senha());
        if (dto.telefone() != null) clienteExistente.setTelefone(dto.telefone());
        if (dto.bairro() != null) clienteExistente.setBairro(dto.bairro());
        if (dto.logradouro() != null) clienteExistente.setLogradouro(dto.logradouro());
        if (dto.cpf() != null) clienteExistente.setCpf(dto.cpf());
        if (dto.dataNascimento() != null) clienteExistente.setDataNascimento(dto.dataNascimento());
        if (dto.fotoPerfil() != null) clienteExistente.setFotoPerfil(dto.fotoPerfil());

        if (dto.cepNumero() != null && !dto.cepNumero().trim().isEmpty()) {
            Cep cep = cepRepository.findById(dto.cepNumero()).orElseThrow();
            clienteExistente.setCep(cep);
        }

        if (dto.status() != null) {
            clienteExistente.setStatus(dto.status());
        }

        Cliente atualizado = clienteRepository.save(clienteExistente);

        return new ClienteResponseDTO(
                atualizado.getId(),
                atualizado.getNome(),
                atualizado.getEmail(),
                atualizado.getSenha(),
                atualizado.getTelefone(),
                atualizado.getBairro(),
                atualizado.getLogradouro(),
                atualizado.getCep() != null ? atualizado.getCep().getNumeroCep() : null,
                atualizado.getStatus(),
                atualizado.getFotoPerfil(),
                atualizado.getCpf(),
                atualizado.getDataNascimento()
        );
    }

    public void excluir(Long id) {
        clienteRepository.deleteById(id);
    }
}