package br.com.interdisciplinar.sebodojoao.repository;

import br.com.interdisciplinar.sebodojoao.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
}
