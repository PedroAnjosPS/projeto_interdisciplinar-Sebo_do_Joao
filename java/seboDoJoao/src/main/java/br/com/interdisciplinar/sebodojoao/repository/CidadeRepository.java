package br.com.interdisciplinar.sebodojoao.repository;

import br.com.interdisciplinar.sebodojoao.model.Cidade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CidadeRepository extends JpaRepository<Cidade, Integer> {
}
