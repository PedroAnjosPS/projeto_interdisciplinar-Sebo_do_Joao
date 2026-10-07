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
                .map(funcionario -> new FuncionarioResponseDTO(
                        funcionario.getId(),
                        funcionario.getNome(),
                        funcionario.getEmail(),
                        funcionario.getSenha(),
                        funcionario.getTelefone(),
                        funcionario.getBairro(),
                        funcionario.getLogradouro(),
                        funcionario.getCep() != null ? funcionario.getCep().getNr() : null,
                        funcionario.getStatus(),
                        funcionario.getCodigoFuncionario()
                ))
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
                funcionario.getCep() != null ? funcionario.getCep().getNr() : null,
                funcionario.getStatus(),
                funcionario.getCodigoFuncionario()
        );
    }

    public FuncionarioResponseDTO cadastrar(FuncionarioRequestDTO dto) {
        Cep cep = cepRepository
                .findById(dto.cepNr())
                .orElseThrow();

        Funcionario funcionario = new Funcionario();
        funcionario.setNome(dto.nome());
        funcionario.setEmail(dto.email());
        funcionario.setSenha(dto.senha());
        funcionario.setTelefone(dto.telefone());
        funcionario.setBairro(dto.bairro());
        funcionario.setLogradouro(dto.logradouro());
        funcionario.setCep(cep);
        funcionario.setStatus(dto.status());
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
                salvo.getCep() != null ? salvo.getCep().getNr() : null,
                salvo.getStatus(),
                salvo.getCodigoFuncionario()
        );
    }

    public FuncionarioResponseDTO atualizar(Long id, FuncionarioRequestDTO dto) {
        Funcionario funcionarioExistente = funcionarioRepository
                .findById(id)
                .orElseThrow();

        Cep cep = cepRepository
                .findById(dto.cepNr())
                .orElseThrow();

        funcionarioExistente.setNome(dto.nome());
        funcionarioExistente.setEmail(dto.email());
        funcionarioExistente.setSenha(dto.senha());
        funcionarioExistente.setTelefone(dto.telefone());
        funcionarioExistente.setBairro(dto.bairro());
        funcionarioExistente.setLogradouro(dto.logradouro());
        funcionarioExistente.setCep(cep);
        funcionarioExistente.setStatus(dto.status());
        funcionarioExistente.setCodigoFuncionario(dto.codigoFuncionario());

        Funcionario atualizado = funcionarioRepository.save(funcionarioExistente);

        return new FuncionarioResponseDTO(
                atualizado.getId(),
                atualizado.getNome(),
                atualizado.getEmail(),
                atualizado.getSenha(),
                atualizado.getTelefone(),
                atualizado.getBairro(),
                atualizado.getLogradouro(),
                atualizado.getCep() != null ? atualizado.getCep().getNr() : null,
                atualizado.getStatus(),
                atualizado.getCodigoFuncionario()
        );
    }

    public void excluir(Long id) {
        funcionarioRepository.deleteById(id);
    }
}