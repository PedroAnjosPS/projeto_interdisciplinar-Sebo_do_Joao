package model;

public class CEP {
    // Atributos
    private String nr;
    private Cidade cidade;

    // Construtores
    public CEP() {}

    public CEP(String nr, Cidade cidade) {
        this.nr = nr;
        this.cidade = cidade;
    }

    // Métodos acessores e modificadores
    public String getNr() {
        return nr;
    }

    public void setNr(String nr) {
        this.nr = nr;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    // toString

    @Override
    public String toString() {
        return cidade.getNome() + "/" + cidade.getUf().getSigla() + ", " + nr;
    }
}
