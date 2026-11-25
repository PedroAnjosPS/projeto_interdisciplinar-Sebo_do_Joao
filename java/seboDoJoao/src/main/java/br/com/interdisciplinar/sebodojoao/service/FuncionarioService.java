package br.com.interdisciplinar.sebodojoao.service;

import br.com.interdisciplinar.sebodojoao.dto.FuncionarioRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.FuncionarioResponseDTO;
import br.com.interdisciplinar.sebodojoao.model.Funcionario;
import br.com.interdisciplinar.sebodojoao.model.StatusUsuario;
import br.com.interdisciplinar.sebodojoao.repository.FuncionarioRepository;
import br.com.interdisciplinar.sebodojoao.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FuncionarioService {
    private final FuncionarioRepository funcionarioRepository;
    private final UsuarioRepository usuarioRepository;
    // Usado para chamar a procedure sp_cadastrar_funcionario
    private final JdbcTemplate jdbc;

    public List<FuncionarioResponseDTO> listarTodos() {
        return funcionarioRepository.findAll()
                .stream()
                .map(funcionario -> {
                        // Pega o caminho da foto de perfil (caso exista) ou null caso não tenha imagem
                        String fotoPerfil = (funcionario.getFotoPerfil() != null && !funcionario.getFotoPerfil().isEmpty()) ?
                                                        funcionario.getFotoPerfil() : null;
                        
                        return new FuncionarioResponseDTO(
                                funcionario.getId(),
                                funcionario.getNome(),
                                funcionario.getEmail(),
                                funcionario.getSenha(),
                                funcionario.getTelefone(),
                                funcionario.getStatus(),
                                fotoPerfil,
                                funcionario.getCodigoFuncionario(),
                                funcionario.getAdmin()
                        );
                })
                .toList();
    }

    public FuncionarioResponseDTO buscarPorId(Long id) {
        Funcionario funcionario = funcionarioRepository
                .findById(id)
                .orElseThrow();

        return new FuncionarioResponseDTO(
                funcionario.getId(),
                funcionario.getNome(),
                funcionario.getEmail(),
                funcionario.getSenha(),
                funcionario.getTelefone(),
                funcionario.getStatus(),
                funcionario.getFotoPerfil(),
                funcionario.getCodigoFuncionario(),
                funcionario.getAdmin()
        );
    }

    public FuncionarioResponseDTO cadastrar(FuncionarioRequestDTO dto) {
        // Confere os dados no servidor (o formulário do site pode ser contornado)
        String nome = ValidacaoDeConta.nome(dto.nome());
        String email = ValidacaoDeConta.email(dto.email());
        String senha = ValidacaoDeConta.senha(dto.senha());
        String telefone = ValidacaoDeConta.telefone(dto.telefone());
        conferirEmailLivre(email, null);

        // Sem status informado, a conta nasce ativa
        int status = dto.status() != null ? dto.status().getCodigo() : StatusUsuario.ATIVO.getCodigo();

        // O código (FUNC-001, FUNC-002...) é sempre gerado aqui; o que vier no pedido é ignorado.
        // Se dois cadastros pegarem o mesmo número ao mesmo tempo, o banco recusa o segundo
        // (erro 50032 da procedure) e tentamos de novo com o número seguinte.
        Integer novoId = null;
        for (int tentativa = 1; novoId == null; tentativa++) {
            try {
                novoId = gravarFuncionario(nome, email, senha, telefone, proximoCodigo(), status, dto.fotoPerfil());
            } catch (UncategorizedSQLException erro) {
                boolean codigoRepetido = erro.getSQLException() != null && erro.getSQLException().getErrorCode() == 50032;
                if (!codigoRepetido || tentativa >= 3) throw erro;
            }
        }

        Funcionario salvo = funcionarioRepository.findById(novoId.longValue()).orElseThrow();

        return new FuncionarioResponseDTO(
                salvo.getId(),
                salvo.getNome(),
                salvo.getEmail(),
                salvo.getSenha(),
                salvo.getTelefone(),
                salvo.getStatus(),
                salvo.getFotoPerfil(),
                salvo.getCodigoFuncionario(),
                salvo.getAdmin()
        );
    }

    // Próximo código livre: o maior número já usado em FUNC-NNN, mais 1 (começa em FUNC-001).
    // Usa o maior número, e não a contagem, para nunca repetir o código de um funcionário que saiu.
    private String proximoCodigo() {
        Integer maior = jdbc.queryForObject(
                "SELECT MAX(TRY_CAST(SUBSTRING(codigo_funcionario, 6, 20) AS INT)) "
                + "FROM funcionarios WHERE codigo_funcionario LIKE 'FUNC-%'", Integer.class);
        return String.format("FUNC-%03d", (maior == null ? 0 : maior) + 1);
    }

    // A procedure sp_cadastrar_funcionario grava o usuário e o funcionário numa transação só
    // e devolve o id criado. Quem se cadastra por aqui nunca nasce administrador (admin = 0).
    private Integer gravarFuncionario(String nome, String email, String senha, String telefone,
                                      String codigo, int status, String fotoPerfil) {
        return jdbc.execute((Connection conexao) -> {
            try (CallableStatement chamada = conexao.prepareCall("{call dbo.sp_cadastrar_funcionario(?,?,?,?,?,?,?,?,?)}")) {
                chamada.setString(1, nome);
                chamada.setString(2, email);
                chamada.setString(3, senha);
                chamada.setString(4, telefone);
                chamada.setString(5, codigo);
                chamada.setBoolean(6, false);
                chamada.setInt(7, status);
                chamada.setString(8, fotoPerfil);   // null quando não há foto
                chamada.registerOutParameter(9, Types.INTEGER);
                chamada.execute();
                return chamada.getInt(9);
            }
        });
    }

    public FuncionarioResponseDTO atualizar(Long id, FuncionarioRequestDTO dto) {
        Funcionario funcionarioExistente = funcionarioRepository
                .findById(id)
                .orElseThrow();

        // Proteções contra atualização nula
        // Só o que foi enviado é conferido e alterado
        if (dto.nome() != null) funcionarioExistente.setNome(ValidacaoDeConta.nome(dto.nome()));
        if (dto.email() != null) {
            String email = ValidacaoDeConta.email(dto.email());
            conferirEmailLivre(email, id);
            funcionarioExistente.setEmail(email);
        }
        if (dto.senha() != null) funcionarioExistente.setSenha(ValidacaoDeConta.senha(dto.senha()));
        // Telefone vazio mantém o que já existe
        if (dto.telefone() != null && !dto.telefone().isBlank()) {
            funcionarioExistente.setTelefone(ValidacaoDeConta.telefone(dto.telefone()));
        }
        // Texto vazio é o pedido de remover a foto: a coluna volta a ser NULL
        if (dto.fotoPerfil() != null) funcionarioExistente.setFotoPerfil(dto.fotoPerfil().isEmpty() ? null : dto.fotoPerfil());
        if (dto.codigoFuncionario() != null) {
            funcionarioExistente.setCodigoFuncionario(ValidacaoDeConta.codigoDoFuncionario(dto.codigoFuncionario()));
        }
        if (dto.admin() != null) funcionarioExistente.setAdmin(dto.admin());

        if (dto.status() != null) {
            funcionarioExistente.setStatus(dto.status());
        }

        Funcionario atualizado = funcionarioRepository.save(funcionarioExistente);

        // Verifica se a foto de perfil foi atualizada ou não
        String fotoPerfil = (atualizado.getFotoPerfil() != null && !atualizado.getFotoPerfil().isEmpty()) ?
                atualizado.getFotoPerfil() : null;

        return new FuncionarioResponseDTO(
                atualizado.getId(),
                atualizado.getNome(),
                atualizado.getEmail(),
                atualizado.getSenha(),
                atualizado.getTelefone(),
                atualizado.getStatus(),
                fotoPerfil,
                atualizado.getCodigoFuncionario(),
                atualizado.getAdmin()
        );
    }

    // O funcionário que já cadastrou produtos ou cuidou de pedidos não pode ser apagado (o banco recusa).
    // Nesse caso o certo é inativar, pela tela de gerenciamento de funcionários. O administrador nunca é excluído.
    public void excluir(Long id) {
        Funcionario funcionario = funcionarioRepository.findById(id).orElseThrow();
        if (Boolean.TRUE.equals(funcionario.getAdmin())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "O funcionário administrador não pode ser excluído.");
        }
        funcionarioRepository.deleteById(id);
    }

    // O e-mail é único entre todas as contas (clientes e funcionários)
    private void conferirEmailLivre(String email, Long idDaPropriaConta) {
        boolean emUso = idDaPropriaConta == null
                ? usuarioRepository.existsByEmailIgnoreCase(email)
                : usuarioRepository.existsByEmailIgnoreCaseAndIdNot(email, idDaPropriaConta);
        if (emUso) throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma conta com esse e-mail.");
    }
}