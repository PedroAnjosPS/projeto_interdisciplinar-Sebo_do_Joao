package br.com.interdisciplinar.sebodojoao.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime data;
    private double total;
    private StatusPedido status;
    private Cliente cliente;
    private Funcionario funcionario;
    private ArrayList<Pagamento> pagamentos = new ArrayList<>();
    private ArrayList<ItemPedido> itensPedido = new ArrayList<>();
    private Entrega entrega;

}