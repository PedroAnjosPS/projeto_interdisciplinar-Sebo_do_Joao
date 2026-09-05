package br.com.interdisciplinar.sebodojoao.model;

import java.util.List;

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
    // Atributos
    @Column(name = "funcionario_cod", nullable = false)
    private String codigoFuncionario;

    @OneToMany(mappedBy = "funcionario")
    private List<Pedido> pedidos;

    @OneToMany(mappedBy = "funcionario")
    private List<Produto> produtos;
}