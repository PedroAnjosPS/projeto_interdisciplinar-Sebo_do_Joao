package br.com.interdisciplinar.sebodojoao.repository;

import br.com.interdisciplinar.sebodojoao.model.ItemPedido;
import br.com.interdisciplinar.sebodojoao.model.ItemPedidoId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, ItemPedidoId> {

    // Diz se o produto já foi vendido (aparece em algum pedido)
    boolean existsByProduto_Id(Long produtoId);
}
