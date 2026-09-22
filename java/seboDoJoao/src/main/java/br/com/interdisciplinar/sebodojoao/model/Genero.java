package br.com.interdisciplinar.sebodojoao.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "generos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Genero {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @OneToMany(mappedBy = "genero")
    @JsonIgnore
    private List<Produto> produtos = new ArrayList<>();
}
