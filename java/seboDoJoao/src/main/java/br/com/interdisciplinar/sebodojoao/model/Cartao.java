package br.com.interdisciplinar.sebodojoao.model;

import jakarta.persistence.*;
import lombok.*;

// Representa a tabela "cartoes": um cartão salvo por um cliente.
// ATENÇÃO À SEGURANÇA: o número completo e o código de segurança (CVV) NUNCA são guardados.
// Só ficam a bandeira, os 4 últimos dígitos, o nome do titular e a validade.
@Entity
@Table(name = "cartoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cartao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    // visa, mastercard, amex, elo, hipercard ou diners
    @Column(name = "bandeira", nullable = false, length = 30)
    private String bandeira;

    // Os 4 últimos dígitos do cartão
    @Column(name = "final_numero", nullable = false, length = 4)
    private String finalNumero;

    // Nome impresso no cartão (em maiúsculas)
    @Column(name = "titular", nullable = false, length = 100)
    private String titular;

    // Formato MM/AA (ex.: "08/29")
    @Column(name = "validade", nullable = false, length = 5)
    private String validade;

    // true = cartão que já vem escolhido no checkout (só um por cliente)
    @Column(name = "padrao", nullable = false)
    private Boolean padrao = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
}
