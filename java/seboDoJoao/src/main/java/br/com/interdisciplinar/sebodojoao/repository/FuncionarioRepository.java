package br.com.interdisciplinar.sebodojoao.repository;

import br.com.interdisciplinar.sebodojoao.model.Funcionario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {
    // O Spring cria o comando SQL: "SELECT * FROM funcionarios WHERE email = ? AND senha = ?"
    Optional<Funcionario> findByEmailAndSenha(String email, String senha);
}
