package br.com.interdisciplinar.sebodojoao.repository;

import br.com.interdisciplinar.sebodojoao.model.Cidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CidadeRepository extends JpaRepository<Cidade, Long> {
    // Procura uma cidade pelo nome (sem diferenciar maiúsculas) dentro de uma UF.
    // Usado ao salvar um endereço: se a cidade já existe, reaproveita; se não, ela é criada.
    Optional<Cidade> findFirstByNomeIgnoreCaseAndUf_Sigla(String nome, String ufSigla);
}
