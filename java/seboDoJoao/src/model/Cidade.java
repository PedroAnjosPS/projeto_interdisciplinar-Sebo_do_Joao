package model;

import java.util.ArrayList;

public class Cidade {
    // Atributos
    private int id;
    private String nome;
    private UF uf;
    private ArrayList<CEP> ceps;

    // Construtores
    public Cidade() {}

    public Cidade(int id, String nome, UF uf) {
        this.id = id;
        this.nome = nome;
        this.uf = uf;
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

    public UF getUf() {
        return uf;
    }

    public void setUf(UF uf) {
        this.uf = uf;
    }

    public ArrayList<CEP> getCeps() {
        return ceps;
    }
}
