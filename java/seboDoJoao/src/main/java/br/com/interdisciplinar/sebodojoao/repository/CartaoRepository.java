package br.com.interdisciplinar.sebodojoao.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.interdisciplinar.sebodojoao.model.Cartao;

// Consultas da tabela cartoes.
public interface CartaoRepository extends JpaRepository<Cartao, Long> {

    // Cartões do cliente: o padrão primeiro e, depois, do mais novo para o mais antigo
    List<Cartao> findByCliente_IdOrderByPadraoDescIdDesc(Long clienteId);

    // Procura o cartão só se ele for deste cliente (impede mexer no cartão de outra pessoa)
    Optional<Cartao> findByIdAndCliente_Id(Long id, Long clienteId);

    long countByCliente_Id(Long clienteId);

    // O mesmo cartão (bandeira + final + validade) não pode ser salvo duas vezes pelo mesmo cliente
    boolean existsByCliente_IdAndBandeiraAndFinalNumeroAndValidade(Long clienteId, String bandeira,
                                                                    String finalNumero, String validade);

    @Modifying
    @Query("UPDATE Cartao c SET c.padrao = false WHERE c.cliente.id = :clienteId")
    void limparPadrao(@Param("clienteId") Long clienteId);

    List<Cartao> findByCliente_Id(Long clienteId);
}
