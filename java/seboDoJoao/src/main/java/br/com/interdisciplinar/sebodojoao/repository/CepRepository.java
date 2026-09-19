package br.com.interdisciplinar.sebodojoao.repository;

import br.com.interdisciplinar.sebodojoao.model.Cep;
import br.com.interdisciplinar.sebodojoao.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CepRepository extends JpaRepository<Cep, String> {
}
