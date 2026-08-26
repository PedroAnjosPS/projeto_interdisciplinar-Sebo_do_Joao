package br.com.interdisciplinar.sebodojoao.model;

public class Categoria {
    // Atributos
    private int id;
    private String nome;

    // Construtores
    public Categoria() {}

    public Categoria(int id, String nome) {
        this.id = id;
        this.nome = nome;
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

    // toString
    @Override
    public String toString() {
        return "\n\t{" +
                "\n\t\tid: " + id +
                "\n\t\tnome:" + nome +
                "\n\t}";
    }
}
