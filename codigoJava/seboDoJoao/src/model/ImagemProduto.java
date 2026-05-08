package model;

public class ImagemProduto {
    // Atributos
    private int id;
    private String url;
    private Produto produto;

    // Construtores
    public ImagemProduto() {}

    public ImagemProduto(int id, String url, Produto produto) {
        this.id = id;
        this.url = url;
        this.produto = produto;
    }

    // Métodos acessores e modificadores
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }
}
