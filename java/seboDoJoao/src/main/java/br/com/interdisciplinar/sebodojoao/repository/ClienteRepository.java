package br.com.interdisciplinar.sebodojoao.repository;

import br.com.interdisciplinar.sebodojoao.model.Cliente;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // O Spring cria o comando SQL: "SELECT * FROM clientes WHERE email = ? AND senha = ?"
    Optional<Cliente> findByEmailAndSenha(String email, String senha);
}
