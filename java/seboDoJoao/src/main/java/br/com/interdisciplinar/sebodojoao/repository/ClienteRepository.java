package br.com.interdisciplinar.sebodojoao.repository;

import br.com.interdisciplinar.sebodojoao.model.Cliente;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // O Spring cria o comando SQL: "SELECT * FROM clientes WHERE email = ? AND senha = ?"
    Optional<Cliente> findByEmailAndSenha(String email, String senha);

    // Diz se algum OUTRO cliente já tem esse CPF. Compara só os números, então "111.222.333-96"
    // e "11122233396" são o mesmo CPF.
    @Query("select count(c) > 0 from Cliente c "
         + "where replace(replace(c.cpf, '.', ''), '-', '') = :digitos and c.id <> :id")
    boolean existeOutroComCpf(@Param("digitos") String digitos, @Param("id") Long id);
}
