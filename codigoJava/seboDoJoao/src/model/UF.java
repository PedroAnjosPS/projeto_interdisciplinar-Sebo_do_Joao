package model;

public class UF {
    // Atributos
    private String sigla;
    private String nome;

    // Construtores
    public UF() {}

    public UF(String sigla, String nome) {
        this.sigla = sigla;
        this.nome = nome;
    }

    // Métodos acessores e modificadores
    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
