package br.com.interdisciplinar.sebodojoao.service;

import br.com.interdisciplinar.sebodojoao.dto.FuncionarioRequestDTO;
import br.com.interdisciplinar.sebodojoao.dto.FuncionarioResponseDTO;
import br.com.interdisciplinar.sebodojoao.model.Cep;
import br.com.interdisciplinar.sebodojoao.model.Funcionario;
import br.com.interdisciplinar.sebodojoao.repository.CepRepository;
import br.com.interdisciplinar.sebodojoao.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FuncionarioService {
    private final FuncionarioRepository funcionarioRepository;
    private final CepRepository cepRepository;

    public List<FuncionarioResponseDTO> listarTodos() {
        return funcionarioRepository.findAll()
                .stream()
                .map(funcionario -> {
                        // Pega o caminho da foto de perfil (caso exista) ou null caso não tenha imagem
                        String fotoPerfil = (funcionario.getFotoPerfil() != null && funcionario.getFotoPerfil().isEmpty()) ?
                                                        funcionario.getFotoPerfil() : null;
                        
                        return new FuncionarioResponseDTO(
                                funcionario.getId(),
                                funcionario.getNome(),
                                funcionario.getEmail(),
                                funcionario.getSenha(),
                                funcionario.getTelefone(),
                                funcionario.getBairro(),
                                funcionario.getLogradouro(),
                                funcionario.getCep() != null ? funcionario.getCep().getNumeroCep() : null,
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
                funcionario.getBairro(),
                funcionario.getLogradouro(),
                funcionario.getCep() != null ? funcionario.getCep().getNumeroCep() : null,
                funcionario.getStatus(),
                funcionario.getFotoPerfil(),
                funcionario.getCodigoFuncionario(),
                funcionario.getAdmin()
        );
    }

    public FuncionarioResponseDTO cadastrar(FuncionarioRequestDTO dto) {
        Cep cep = null;
        if (dto.cepNumero() != null && !dto.cepNumero().trim().isEmpty()) {
                cep = cepRepository
                        .findById(dto.cepNumero())
                        .orElseThrow();
        }  

        Funcionario funcionario = new Funcionario();
        funcionario.setNome(dto.nome());
        funcionario.setEmail(dto.email());
        funcionario.setSenha(dto.senha());
        funcionario.setTelefone(dto.telefone());
        funcionario.setBairro(dto.bairro());
        funcionario.setLogradouro(dto.logradouro());
        funcionario.setCep(cep);
        funcionario.setFotoPerfil(dto.fotoPerfil());

        if (dto.status() != null) {
                funcionario.setStatus(dto.status());
        }

        funcionario.setCodigoFuncionario(dto.codigoFuncionario());

        Funcionario salvo = funcionarioRepository.save(funcionario);

        return new FuncionarioResponseDTO(
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
                salvo.getCodigoFuncionario(),
                salvo.getAdmin()
        );
    }

    public FuncionarioResponseDTO atualizar(Long id, FuncionarioRequestDTO dto) {
        Funcionario funcionarioExistente = funcionarioRepository
                .findById(id)
                .orElseThrow();

        // Proteções contra atualização nula
        if (dto.nome() != null) funcionarioExistente.setNome(dto.nome());
        if (dto.email() != null) funcionarioExistente.setEmail(dto.email());
        if (dto.senha() != null) funcionarioExistente.setSenha(dto.senha());
        if (dto.telefone() != null) funcionarioExistente.setTelefone(dto.telefone());
        if (dto.bairro() != null) funcionarioExistente.setBairro(dto.bairro());
        if (dto.logradouro() != null) funcionarioExistente.setLogradouro(dto.logradouro());
        if (dto.fotoPerfil() != null) funcionarioExistente.setFotoPerfil(dto.fotoPerfil());
        if (dto.codigoFuncionario() != null) funcionarioExistente.setCodigoFuncionario(dto.codigoFuncionario());
        if (dto.admin() != null) funcionarioExistente.setAdmin(dto.admin());

        if (dto.cepNumero() != null && !dto.cepNumero().trim().isEmpty()) {
            Cep cep = cepRepository.findById(dto.cepNumero()).orElseThrow();
            funcionarioExistente.setCep(cep);
        }

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
                atualizado.getBairro(),
                atualizado.getLogradouro(),
                atualizado.getCep() != null ? atualizado.getCep().getNumeroCep() : null,
                atualizado.getStatus(),
                fotoPerfil,
                atualizado.getCodigoFuncionario(),
                atualizado.getAdmin()
        );
    }

    public void excluir(Long id) {
        funcionarioRepository.deleteById(id);
    }
}