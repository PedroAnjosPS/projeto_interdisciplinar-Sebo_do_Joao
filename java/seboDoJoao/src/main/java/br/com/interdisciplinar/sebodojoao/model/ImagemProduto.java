package br.com.interdisciplinar.sebodojoao.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "imagem_produtos")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
