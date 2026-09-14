package br.com.interdisciplinar.sebodojoao.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "categorias")
public class Categoria {

    // 1. O id é apenas a Chave Primária e Auto Incremento (IDENTITY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // (Pode ser Long também)

    // 2. Coluna nome
    @Column(nullable = false, length = 100)
    private String nome;

    // 3. SE você quiser listar os produtos dessa categoria, AQUI entra o @OneToMany
    // Note que é uma Lista, e não o id.
    @OneToMany(mappedBy = "categoria")
    private List<Produto> produtos;


    // Construtores, Getters e Setters abaixo...

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Produto> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<Produto> produtos) {
        this.produtos = produtos;
    }
}