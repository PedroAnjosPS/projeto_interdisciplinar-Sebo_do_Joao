package br.com.interdisciplinar.sebodojoao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.interdisciplinar.sebodojoao.model.ItemCarrinho;
import br.com.interdisciplinar.sebodojoao.model.ItemCarrinhoId;

// Consultas da tabela item_carrinhos (o carrinho de cada cliente).
public interface ItemCarrinhoRepository extends JpaRepository<ItemCarrinho, ItemCarrinhoId> {

    // O carrinho do cliente, na ordem em que os itens foram sendo adicionados
    List<ItemCarrinho> findByCliente_IdOrderByProduto_IdAsc(Long clienteId);

    // Esvazia o carrinho do cliente (apaga todas as linhas dele)
    @Modifying
    @Query("DELETE FROM ItemCarrinho i WHERE i.cliente.id = :clienteId")
    void apagarDoCliente(@Param("clienteId") Long clienteId);

    // Tira o produto do carrinho de todo mundo (usado quando o produto é excluído)
    @Modifying
    @Query("DELETE FROM ItemCarrinho i WHERE i.produto.id = :produtoId")
    void apagarDoProduto(@Param("produtoId") Long produtoId);
}
