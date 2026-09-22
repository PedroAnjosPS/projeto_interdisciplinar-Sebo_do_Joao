package br.com.interdisciplinar.sebodojoao.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "funcionarios")
@PrimaryKeyJoinColumn(name = "usuario_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Funcionario extends Usuario {
    @Column(name = "funcionario_cod", nullable = false, length = 50, unique = true)
    private String codigoFuncionario;

    @OneToMany(mappedBy = "funcionario")
    @JsonIgnore
    private List<Pedido> pedidos = new ArrayList<>();

    @OneToMany(mappedBy = "funcionario")
    @JsonIgnore
    private List<Produto> produtos = new ArrayList<>();
}