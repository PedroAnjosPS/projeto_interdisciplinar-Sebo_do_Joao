package model;

public class ImagemProduto {
    // Atributos
    private int id;
    private String caminho;
    private Produto produto;

    // Construtores
    public ImagemProduto() {}

    public ImagemProduto(int id, String caminho, Produto produto) {
        this.id = id;
        this.caminho = caminho;
        this.produto = produto;
    }

    // Métodos acessores e modificadores
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setUrl(String caminho) {
        this.caminho = caminho;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

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
