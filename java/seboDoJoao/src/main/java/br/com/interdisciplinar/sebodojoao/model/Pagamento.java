package br.com.interdisciplinar.sebodojoao.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos")
@Getter
@Setter
public class Pagamento {
    // Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "numero_parcela", nullable = false)
    private Integer numeroParcela;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false)
    private StatusPagamento status;

    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "quantidade_parcelas", nullable = false)
    private Integer quantidadeParcelas;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Column(name = "data_pagamento", nullable = false)
    private LocalDate dataPagamento;

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    // Construtores
    public Pagamento() {}

    // toString
    @Override
    public String toString() {
        return "\n\t{"
                + "\n\t\tId: " + id
                + "\n\t\tValor: R$" + String.format("%.2f", valor)
                + "\n\t\tNúmero da parcela: " + numeroParcela
                + "\n\t\tQuantidade de parcelas: " + quantidadeParcelas
                + "\n\t\tData de pagamento: " + dataPagamento
                + "\n\t\tData de vencimento: " + dataVencimento
                + "\n\t\tStatus: " + status
                + "\n\t\tId do pedido: " + pedido.getId()
                + "\n\t}";
    }
}
