package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "entregas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Entrega {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String transportadora;
    private String linkRastreio;
    private String codigoRastreio;
    private LocalDateTime dataPostagem;
    private LocalDateTime dataEntrega;
    private LocalDateTime dataPrevisaoEntrega;
    private StatusEntrega status;
}
