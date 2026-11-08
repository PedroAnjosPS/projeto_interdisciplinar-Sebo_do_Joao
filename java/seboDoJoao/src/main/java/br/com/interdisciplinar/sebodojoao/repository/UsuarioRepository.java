package br.com.interdisciplinar.sebodojoao.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.interdisciplinar.sebodojoao.model.Usuario;

// Consultas na tabela usuarios, que vale para cliente e funcionário juntos (o e-mail é único nas duas contas).
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
