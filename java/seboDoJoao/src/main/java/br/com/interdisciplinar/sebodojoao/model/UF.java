package br.com.interdisciplinar.sebodojoao.model;
import java.util.ArrayList;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "ufs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UF {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sigla", nullable = false)
    private String sigla;

    @Column(name = "nome", nullable = false)
    private String nome;

    @OneToMany(mappedBy = "uf")
    private ArrayList<Cidade> cidades;
}