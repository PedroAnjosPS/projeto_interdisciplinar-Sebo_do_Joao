package br.com.interdisciplinar.sebodojoao.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    @Column(name = "numero_cep", length = 10, nullable = false)
    private String numeroCep;

    @ManyToOne
    @JoinColumn(name = "cidade_id", nullable = false)
    private Cidade cidade;

    @OneToMany(mappedBy = "cep")
    @JsonIgnore
    private List<Usuario> usuarios;
}
