package br.com.interdisciplinar.sebodojoao.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "ceps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cep {
    @Id
    @Column(name = "nr", nullable = false)
    private String nr;

    @ManyToOne
    @JoinColumn(name = "cidade_id", nullable = false)
    private Cidade cidade;

    @OneToMany(mappedBy = "cep")
    @JsonIgnore
    private List<Usuario> usuarios;
}
