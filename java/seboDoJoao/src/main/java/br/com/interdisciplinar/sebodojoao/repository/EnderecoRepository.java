package br.com.interdisciplinar.sebodojoao.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.interdisciplinar.sebodojoao.model.Endereco;

// Consultas da tabela enderecos. O Spring monta o SQL a partir do nome do método
// (ex.: "findByCliente_IdAndAtivoTrue..." vira "WHERE cliente_id = ? AND ativo = 1").
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {

    // Endereços em uso do cliente: o padrão primeiro e, depois, do mais novo para o mais antigo
    List<Endereco> findByCliente_IdAndAtivoTrueOrderByPadraoDescIdDesc(Long clienteId);

    // Procura um endereço em uso E que seja deste cliente (impede mexer no endereço de outra pessoa)
    Optional<Endereco> findByIdAndCliente_IdAndAtivoTrue(Long id, Long clienteId);

    long countByCliente_IdAndAtivoTrue(Long clienteId);

    // Tira a marca de "padrão" de todos os endereços do cliente (usado antes de marcar um novo padrão)
    @Modifying
    @Query("UPDATE Endereco e SET e.padrao = false WHERE e.cliente.id = :clienteId")
    void limparPadrao(@Param("clienteId") Long clienteId);

    // Usado na exclusão da conta. Os endereços ligados a entregas não podem ser apagados (o banco recusa).
    List<Endereco> findByCliente_Id(Long clienteId);
}
