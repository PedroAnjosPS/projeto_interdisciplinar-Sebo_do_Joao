package br.com.interdisciplinar.sebodojoao.service;

import br.com.interdisciplinar.sebodojoao.dto.ClienteRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.ClienteResponseDTO;
import br.com.interdisciplinar.sebodojoao.model.Cliente;
import br.com.interdisciplinar.sebodojoao.repository.CartaoRepository;
import br.com.interdisciplinar.sebodojoao.repository.ClienteRepository;
import br.com.interdisciplinar.sebodojoao.repository.EnderecoRepository;
import br.com.interdisciplinar.sebodojoao.repository.ItemCarrinhoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {
    private final ClienteRepository clienteRepository;
    // Estes três repositórios são usados na exclusão da conta: o cliente tem endereços, cartões
    // e carrinho, que precisam ser apagados junto com ele.
    private final EnderecoRepository enderecoRepository;
    private final CartaoRepository cartaoRepository;
    private final ItemCarrinhoRepository itemCarrinhoRepository;

    public List<ClienteResponseDTO> listarTodos() {
        return clienteRepository.findAll()
                .stream()
                .map(cliente -> {
                        // Pega o caminho da foto de perfil (caso exista) ou null caso não tenha imagem
                        String fotoPerfil = (cliente.getFotoPerfil() != null && !cliente.getFotoPerfil().isEmpty()) ?
                                                        cliente.getFotoPerfil() : null;

                        return new ClienteResponseDTO(
                                cliente.getId(),
                                cliente.getNome(),
                                cliente.getEmail(),
                                cliente.getSenha(),
                                cliente.getTelefone(),
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
                cliente.getStatus(),
                cliente.getFotoPerfil(),
                cliente.getCpf(),
                cliente.getDataNascimento()
        );
    }

    public ClienteResponseDTO cadastrar(ClienteRequestDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setNome(dto.nome());
        cliente.setEmail(dto.email());
        cliente.setSenha(dto.senha());
        cliente.setTelefone(dto.telefone());
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
        // O CPF é único no banco. Se dois clientes salvassem o campo vazio, o segundo receberia
        // erro e nada do perfil seria gravado, por isso o vazio mantém o valor que já existe.
        if (dto.cpf() != null && !dto.cpf().isBlank()) clienteExistente.setCpf(dto.cpf());
        if (dto.dataNascimento() != null) clienteExistente.setDataNascimento(dto.dataNascimento());
        // Texto vazio é o pedido de remover a foto: a coluna volta a ser NULL
        if (dto.fotoPerfil() != null) clienteExistente.setFotoPerfil(dto.fotoPerfil().isEmpty() ? null : dto.fotoPerfil());

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
                atualizado.getStatus(),
                atualizado.getFotoPerfil(),
                atualizado.getCpf(),
                atualizado.getDataNascimento()
        );
    }

    // Exclui o cliente e também o que pertence a ele (carrinho, cartões e endereços), tudo numa transação
    // só: se algo falhar, nada é apagado. Se o cliente já tiver pedidos, o banco recusa a exclusão
    // (o histórico de compras precisa ser mantido) e a transação inteira é desfeita.
    @Transactional
    public void excluir(Long id) {
        itemCarrinhoRepository.apagarDoCliente(id);
        cartaoRepository.deleteAll(cartaoRepository.findByCliente_Id(id));
        enderecoRepository.deleteAll(enderecoRepository.findByCliente_Id(id));
        clienteRepository.deleteById(id);
        // flush: força o envio dos comandos ao banco agora, para o erro (se houver) aparecer aqui
        clienteRepository.flush();
    }
}