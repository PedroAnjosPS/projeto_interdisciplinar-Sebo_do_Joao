package br.com.interdisciplinar.sebodojoao.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "senha", nullable = false, length = 255)
    private String senha;

    @Column(name = "telefone", nullable = false, length = 20)
    private String telefone;

    @Column(name = "bairro", nullable = false, length = 100)
    private String bairro;

    @Column(name = "logradouro", nullable = false, length = 255)
    private String logradouro;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cep_nr", nullable = false)
    private Cep cep;

    @Convert(converter = StatusUsuarioConverter.class)
    @Column(name = "status", nullable = false)
    private StatusUsuario status = StatusUsuario.ATIVO;
}
