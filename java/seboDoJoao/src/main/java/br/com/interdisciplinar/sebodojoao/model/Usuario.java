package br.com.interdisciplinar.sebodojoao.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private String bairro;
    private String logradouro;
    private CEP cep;
    private StatusUsuario status;


}
