package model;

public class CEP {
    // Atributos
    private int nr;
    private Cidade cidade;

    // Construtores
    public CEP() {}

    public CEP(int nr, Cidade cidade) {
        this.nr = nr;
        this.cidade = cidade;
    }

    // Métodos acessores e modificadores
    public int getNr() {
        return nr;
    }

    public void setNr(int nr) {
        this.nr = nr;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }
}
