package model;

import java.util.ArrayList;

public class Produto {
    // Atributos
    private int id;
    private String nome;
    private String descricao;
    private double preco;
    private int estoque;
    private String tipo;
    private Categoria categoria;
    private Genero genero;
    private ArrayList<ImagemProduto> imagens;
    private StatusProduto status;

    // Construtores
    public Produto() {}

    public Produto(int id, String nome, String descricao, double preco, int estoque, String tipo, Categoria categoria, Genero genero, StatusProduto status) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.estoque = estoque;
        this.tipo = tipo;
        this.categoria = categoria;
        this.genero = genero;
        this.status = status;
    }

    // Métodos acessores e modificadores
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public int getEstoque() {
        return estoque;
    }

    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Genero getGenero() {
        return genero;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    public ArrayList<ImagemProduto> getImagens() {
        return imagens;
    }

    public StatusProduto getStatus() {
        return status;
    }

    public void setStatus(StatusProduto status) {
        this.status = status;
    }

    public void adicionarImagem(ImagemProduto imagem) {
        imagens.add(imagem);
    }

    public void removerImagem(ImagemProduto imagem) {
        imagens.remove(imagem);
    }

    public void mostrarImagens() {
        for (ImagemProduto imagem : imagens) {
            System.out.println(imagem);
        }
    }

    @Override
    public String toString() {
        return "Id: " + id 
                + "\nNome: " + nome  
                + "\nDescrição: " + descricao  
                + "\nPreço: " + preco 
                + "\nEstoque: " + estoque 
                + "\nTipo: " + tipo 
                + "\nCategoria: " + categoria 
                + "\nGênero: " + genero 
                + "\nImagens: " + imagens
                + "\nStatus: " + status;
    }

    // toString
    
}
