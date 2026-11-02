package br.com.interdisciplinar.sebodojoao.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "entregas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Entrega {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "transportadora", nullable = false)
    private String transportadora;

    @Column(name = "link_rastreio", nullable = false)
    private String linkRastreio;

    @Column(name = "codigo_rastreio", nullable = false)
    private String codigoRastreio;

    @Column(name = "data_postagem", nullable = false)
    private LocalDateTime dataPostagem;

    @Column(name = "data_previsao_entrega", nullable = false)
    private LocalDate dataPrevisaoEntrega;

    @Column(name = "data_entrega", nullable = false)
    private LocalDateTime dataEntrega;

    // NOVO: valor do frete desta entrega (pedidos.total = produtos + frete)
    @Column(name = "valor_frete", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorFrete = BigDecimal.ZERO;

    // NOVO: para onde a entrega vai (tabela enderecos)
    @ManyToOne
    @JoinColumn(name = "endereco_id", nullable = false)
    private Endereco endereco;

    @Convert(converter = StatusEntregaConverter.class)
    @Column(name = "status", nullable = false)
    private StatusEntrega status = StatusEntrega.PENDENTE;
}