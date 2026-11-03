package br.com.interdisciplinar.sebodojoao.repository;

import br.com.interdisciplinar.sebodojoao.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Usado na exclusão da conta: cliente com pedidos não pode ser apagado do banco
    boolean existsByCliente_Id(Long clienteId);
}
