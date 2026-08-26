package br.com.interdisciplinar.sebodojoao.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Produtos")
public class Produto {
    // Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "preco")
    private BigDecimal preco;

    @Column(name = "estoque")
    private int estoque;

    @Column(name = "tipo")
    private String tipo;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name = "genero_id")
    private Genero genero;

    @OneToMany(mappedBy = "produtos")
    private List<ImagemProduto> imagens = new ArrayList<>();

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status")
    private StatusProduto status;

    // Construtores
    public Produto() {}

    // toString
    @Override
    public String toString() {
        return "Id: " + id 
                + "\nNome: " + nome  
                + "\nDescrição: " + descricao  
                + "\nPreço: " + "R$" +String.format("%.2f", preco)
                + "\nEstoque: " + estoque 
                + "\nTipo: " + tipo 
                + "\nCategoria: " + categoria 
                + "\nGênero: " + genero 
                + "\nImagens do produto: "
                + "\nStatus: " + status;
    }
}
