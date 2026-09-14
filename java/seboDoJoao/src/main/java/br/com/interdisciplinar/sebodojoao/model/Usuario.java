package br.com.interdisciplinar.sebodojoao.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "senha", nullable = false)
    private String senha;

    @Column(name = "telefone", nullable = false)
    private String telefone;

    @Column(name = "bairro", nullable = false)
    private String bairro;

    @Column(name = "logradouro", nullable = false)
    private String logradouro;

    @ManyToOne()
    @JoinColumn(name = "cep_nr", nullable = false)
    private CEP cep;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false)
    private StatusUsuario status;
}
