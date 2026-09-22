package br.com.interdisciplinar.sebodojoao.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "data", nullable = false)
    private LocalDateTime data;

    @Column(name = "total", nullable = false)
    private double total;

    @Convert(converter = StatusPedidoConverter.class)
    @Column(name = "status", nullable = false)
    private StatusPedido status;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonIgnore
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "funcionario_id", nullable = false)
    @JsonIgnore
    private Funcionario funcionario;

    @OneToMany(mappedBy = "pedido")
    private List<Pagamento> pagamentos = new ArrayList<>();

    @OneToMany(mappedBy = "pedido")
    private List<ItemPedido> itensPedido = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "entrega_id", unique = true, nullable = false)
    private Entrega entrega;
}