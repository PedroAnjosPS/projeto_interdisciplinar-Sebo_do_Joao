package br.com.interdisciplinar.sebodojoao.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
public class Pedido {
    // Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "data", nullable = false)
    private LocalDateTime data;

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false)
    private StatusPedido status;

    @OneToOne
    @JoinColumn(name = "entrega_id")
    private Entrega entrega;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    @OneToMany(mappedBy = "pedido")
    private List<Pagamento> pagamentos = new ArrayList<>();

    @OneToMany(mappedBy = "pedido")
    private ArrayList<ItemPedido> itensPedido = new ArrayList<>();

    // Construtores
    public Pedido() {}

    // toString
    @Override
    public String toString() {
        return  "Id: " + id +
                "\nData: " + data +
                "\nTotal: R$" + String.format("%.2f", total) +
                "\nStatus: " + status +
                "\nCliente: " + cliente.getNome() + " - " + cliente.getEmail() +
                "\nFuncionário gerenciador: " + funcionario.getNome() + " - " + funcionario.getEmail() +
                "\nPagamentos:" + pagamentos +
                "\nItens do pedido:" + itensPedido +
                "\nEntrega:" + entrega;
    }
}
