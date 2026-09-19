package br.com.interdisciplinar.sebodojoao.model;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "ufs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Uf {
    @Id
    @Column(name = "sigla", nullable = false)
    private String sigla;

    @Column(name = "nome", nullable = false)
    private String nome;

    @OneToMany(mappedBy = "uf")
    @JsonIgnore
    private List<Cidade> cidades = new ArrayList<>();
}