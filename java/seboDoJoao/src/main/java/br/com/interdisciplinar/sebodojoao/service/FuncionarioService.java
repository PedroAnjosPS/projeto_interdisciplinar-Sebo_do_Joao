package br.com.interdisciplinar.sebodojoao.service;

import br.com.interdisciplinar.sebodojoao.model.Funcionario;
import br.com.interdisciplinar.sebodojoao.repository.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FuncionarioService {
    private final FuncionarioRepository funcionarioRepository;

    public List<Funcionario> listarTodos() {
        return funcionarioRepository.findAll();
    }

    public Funcionario buscarPorId(Long id) {
        return funcionarioRepository.findById(id).orElseThrow();
    }

    public Funcionario cadastrar(Funcionario funcionario) {
        return funcionarioRepository.save(funcionario);
    }

    public Funcionario atualizar(Long id, @NonNull Funcionario funcionario) {
        Funcionario funcionarioExistente = funcionarioRepository.findById(id).orElseThrow();

        funcionarioExistente.setNome(funcionario.getNome());
        funcionarioExistente.setEmail(funcionario.getEmail());
        funcionarioExistente.setSenha(funcionario.getSenha());
        funcionarioExistente.setTelefone(funcionario.getTelefone());
        funcionarioExistente.setBairro(funcionario.getBairro());
        funcionarioExistente.setLogradouro(funcionario.getLogradouro());
        funcionarioExistente.setCep(funcionario.getCep());
        funcionarioExistente.setStatus(funcionario.getStatus());
        funcionarioExistente.setCodigoFuncionario(funcionario.getCodigoFuncionario());

        return funcionarioRepository.save(funcionarioExistente);
    }

    public void excluir(Long id) {
        funcionarioRepository.deleteById(id);
    }
}
