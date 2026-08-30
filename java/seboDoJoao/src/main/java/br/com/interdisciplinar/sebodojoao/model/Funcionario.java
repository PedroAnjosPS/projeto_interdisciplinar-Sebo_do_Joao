package br.com.interdisciplinar.sebodojoao.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "funcionarios")
@Getter
@Setter
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Funcionario extends Usuario {
    // Atributos
    @Column(name = "funcionario_cod", nullable = false)
    private String codigoFuncionario;

    @OneToMany(mappedBy = "funcionario")
    private List<Pedido> pedidos;

    @OneToMany(mappedBy = "funcionario")
    private List<Produto> produtos;

    // Construtores
    public Funcionario() {}
}
