package br.com.interdisciplinar.sebodojoao.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "produtos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Produto {
    // Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "descricao", nullable = false)
    private String descricao;

    @Column(name = "preco", nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(name = "estoque", nullable = false)
    private Integer estoque;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "genero_id", nullable = false)
    private Genero genero;

    @ManyToOne
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    @OneToMany(mappedBy = "produto")
    private List<ImagemProduto> imagens = new ArrayList<>();

    @Convert(converter = StatusProdutoConverter.class)
    @Column(name = "status", nullable = false)
    private StatusProduto status = StatusProduto.DISPONIVEL;
}
