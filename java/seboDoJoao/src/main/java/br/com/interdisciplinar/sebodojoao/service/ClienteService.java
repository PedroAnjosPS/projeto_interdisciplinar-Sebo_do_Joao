package br.com.interdisciplinar.sebodojoao.service;

import br.com.interdisciplinar.sebodojoao.dto.ClienteRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.ClienteResponseDTO;
import br.com.interdisciplinar.sebodojoao.model.Cliente;
import br.com.interdisciplinar.sebodojoao.repository.CartaoRepository;
import br.com.interdisciplinar.sebodojoao.repository.ClienteRepository;
import br.com.interdisciplinar.sebodojoao.repository.EnderecoRepository;
import br.com.interdisciplinar.sebodojoao.repository.ItemCarrinhoRepository;
import br.com.interdisciplinar.sebodojoao.repository.PedidoRepository;
import br.com.interdisciplinar.sebodojoao.repository.UsuarioRepository;
import br.com.interdisciplinar.sebodojoao.model.StatusUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.time.LocalDate;
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
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    // Usado para chamar a procedure sp_cadastrar_cliente
    private final JdbcTemplate jdbc;

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
        // Confere os dados no servidor (o formulário do site pode ser contornado)
        String nome = ValidacaoDeConta.nome(dto.nome());
        String email = ValidacaoDeConta.email(dto.email());
        String senha = ValidacaoDeConta.senha(dto.senha());
        String telefone = ValidacaoDeConta.telefone(dto.telefone());
        String cpf = ValidacaoDeConta.cpf(dto.cpf());
        LocalDate nascimento = ValidacaoDeConta.dataDeNascimento(dto.dataNascimento());
        conferirEmailLivre(email, null);
        conferirCpfLivre(cpf, -1L);

        // A procedure sp_cadastrar_cliente grava o usuário e o cliente numa transação só
        // (a conta nasce ativa, com o status padrão do banco) e devolve o id criado
        Integer novoId = jdbc.execute((Connection conexao) -> {
            try (CallableStatement chamada = conexao.prepareCall("{call dbo.sp_cadastrar_cliente(?,?,?,?,?,?,?,?)}")) {
                chamada.setString(1, nome);
                chamada.setString(2, email);
                chamada.setString(3, senha);
                chamada.setString(4, telefone);
                chamada.setString(5, cpf);
                chamada.setDate(6, java.sql.Date.valueOf(nascimento));
                chamada.setString(7, dto.fotoPerfil());   // null quando não há foto
                chamada.registerOutParameter(8, Types.INTEGER);
                chamada.execute();
                return chamada.getInt(8);
            }
        });

        Cliente salvo = clienteRepository.findById(novoId.longValue()).orElseThrow();

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
        // Só o que foi enviado é conferido e alterado
        if (dto.nome() != null) clienteExistente.setNome(ValidacaoDeConta.nome(dto.nome()));
        if (dto.email() != null) {
            String email = ValidacaoDeConta.email(dto.email());
            conferirEmailLivre(email, id);
            clienteExistente.setEmail(email);
        }
        if (dto.senha() != null) clienteExistente.setSenha(ValidacaoDeConta.senha(dto.senha()));
        // Telefone vazio mantém o que já existe, como o CPF logo abaixo
        if (dto.telefone() != null && !dto.telefone().isBlank()) {
            clienteExistente.setTelefone(ValidacaoDeConta.telefone(dto.telefone()));
        }
        // O CPF é único no banco. Se dois clientes salvassem o campo vazio, o segundo receberia
        // erro e nada do perfil seria gravado, por isso o vazio mantém o valor que já existe.
        if (dto.cpf() != null && !dto.cpf().isBlank()) {
            String cpf = ValidacaoDeConta.cpf(dto.cpf());
            conferirCpfLivre(cpf, id);
            clienteExistente.setCpf(cpf);
        }
        if (dto.dataNascimento() != null) {
            clienteExistente.setDataNascimento(ValidacaoDeConta.dataDeNascimento(dto.dataNascimento()));
        }
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

    // O e-mail é único entre todas as contas (clientes e funcionários)
    private void conferirEmailLivre(String email, Long idDaPropriaConta) {
        boolean emUso = idDaPropriaConta == null
                ? usuarioRepository.existsByEmailIgnoreCase(email)
                : usuarioRepository.existsByEmailIgnoreCaseAndIdNot(email, idDaPropriaConta);
        if (emUso) throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma conta com esse e-mail.");
    }

    private void conferirCpfLivre(String cpf, Long idDaPropriaConta) {
        if (clienteRepository.existeOutroComCpf(ValidacaoDeConta.soDigitosDoCpf(cpf), idDaPropriaConta)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um cliente com esse CPF.");
        }
    }

    // Encerra a conta do cliente, tudo numa transação só. Devolve true se a conta foi apenas
    // inativada e false se foi apagada de vez.
    //  - Sem pedidos: apaga o cliente e o que pertence a ele (carrinho, cartões e endereços).
    //  - Com pedidos: o histórico de compras precisa ser mantido, então a conta fica INATIVA.
    //    Carrinho e cartões são apagados; os endereços ficam, porque as entregas apontam para eles.
    @Transactional
    public boolean excluir(Long id) {
        Cliente cliente = clienteRepository.findById(id).orElseThrow();
        itemCarrinhoRepository.apagarDoCliente(id);
        cartaoRepository.deleteAll(cartaoRepository.findByCliente_Id(id));

        if (pedidoRepository.existsByCliente_Id(id)) {
            cliente.setStatus(StatusUsuario.INATIVO);
            clienteRepository.save(cliente);
            return true;
        }

        enderecoRepository.deleteAll(enderecoRepository.findByCliente_Id(id));
        clienteRepository.deleteById(id);
        // flush: força o envio dos comandos ao banco agora, para o erro (se houver) aparecer aqui
        clienteRepository.flush();
        return false;
    }
}