package br.com.interdisciplinar.sebodojoao.model;

import jakarta.persistence.*;
import lombok.*;

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
    private Integer id;

    @Column(name = "transportadora", nullable = false)
    private String transportadora;

    @Column(name = "link_rastreio", nullable = false)
    private String linkRastreio;

    @Column(name = "codigo_rastreio", nullable = false)
    private String codigoRastreio;

    @Column(name = "data_postagem", nullable = false)
    private LocalDateTime dataPostagem;

    @Column(name = "data_entrega", nullable = false)
    private LocalDateTime dataEntrega;

    @Column(name = "data_previsao_entrega", nullable = false)
    private LocalDateTime dataPrevisaoEntrega;

    @Column(name = "status", nullable = false)
    private StatusEntrega status;
}