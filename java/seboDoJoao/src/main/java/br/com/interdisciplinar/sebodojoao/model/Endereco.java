package br.com.interdisciplinar.sebodojoao.model;

import jakarta.persistence.*;
import lombok.*;

// Representa a tabela "enderecos" do banco: um endereço de entrega de um cliente.
// Um cliente pode ter vários endereços. A cidade e a UF não ficam aqui: ficam nas tabelas
// ceps -> cidades -> ufs (o endereço aponta para o CEP, e o CEP aponta para a cidade).
@Entity
@Table(name = "enderecos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Endereco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    // Rua ou avenida
    @Column(name = "logradouro", nullable = false, length = 255)
    private String logradouro;

    // null = a pessoa marcou "Sem número"
    @Column(name = "numero", length = 10)
    private String numero;

    @Column(name = "complemento", length = 100)
    private String complemento;

    @Column(name = "bairro", nullable = false, length = 100)
    private String bairro;

    // true = em uso. "Excluir" um endereço no site só muda para false (a linha fica no banco),
    // porque as entregas antigas continuam apontando para ele (histórico).
    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    // Nome dado pela pessoa ("Casa", "Trabalho"...)
    @Column(name = "apelido", nullable = false, length = 30)
    private String apelido = "Endereço";

    // true = endereço que já vem escolhido no checkout (só um por cliente)
    @Column(name = "padrao", nullable = false)
    private Boolean padrao = false;

    // Quem recebe a entrega é o próprio cliente: o nome e o telefone ficam na conta dele (tabela usuarios).

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cep_numero", nullable = false)
    private Cep cep;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
}
