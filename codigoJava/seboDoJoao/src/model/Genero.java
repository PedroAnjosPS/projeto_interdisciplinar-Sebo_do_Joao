package model;

public class Genero {
    // Atributos
    private int id;
    private String nome;

    // Construtores
    public Genero() {}

    public Genero(int id, String nome) {
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
        return "Gênero {" +
                "\n\tid: " + id +
                "\n\tnome: " + nome +
                "\n}";
    }
}
