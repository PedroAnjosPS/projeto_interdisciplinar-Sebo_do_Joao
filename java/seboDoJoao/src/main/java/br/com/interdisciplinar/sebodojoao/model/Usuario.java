package br.com.interdisciplinar.sebodojoao.model;


import java.time.LocalDateTime;

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

    @Convert(converter = StatusUsuarioConverter.class)
    @Column(name = "status", nullable = false)
    private StatusUsuario status = StatusUsuario.ATIVO;

    @Column(name = "logradouro", nullable = false, length = 255)
    private String logradouro;

    @Column(name = "bairro", nullable = false, length = 100)
    private String bairro;
    
    @Column(name = "telefone", nullable = false, length = 20)
    private String telefone;

    @Column(name = "foto_perfil")
    private String fotoPerfil;

    @Column(name = "data_cadastro", nullable = false, insertable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cep_numero", nullable = false)
    private Cep cep;
}
