package br.com.interdisciplinar.sebodojoao.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "categorias")
@Getter
@Setter
public class Categoria {
    // Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @OneToMany
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nome", nullable = false)
    private String nome;

    // Construtores
    public Categoria() {}

    // toString
    @Override
    public String toString() {
        return "\n\t{" +
                "\n\t\tid: " + id +
                "\n\t\tnome:" + nome +
                "\n\t}";
    }
}
