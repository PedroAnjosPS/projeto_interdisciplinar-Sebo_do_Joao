package br.com.interdisciplinar.sebodojoao.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "imagem_produtos")
@Getter
@Setter
public class ImagemProduto {
    // Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "caminho", nullable = false)
    private String caminho;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    // Construtores
    public ImagemProduto() {}

    // toString
    @Override
    public String toString() {
        return "\n\t{"
        + "\n\t\tId: " + id
        + "\n\t\tCaminho: " + caminho
        + "\n\t\tId do produto: " + produto.getId()
        + "\n\t}";
    }
}
