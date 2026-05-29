package app;

import java.time.LocalDate;
import model.*;

public class Main {
    public static void main(String[] args) {
        // Instanciando a uf
        UF uf = new UF("SP", "São Paulo");

        // Instanciando a cidade
        Cidade cidade = new Cidade(1, "São José do Rio Preto", uf);

        // Instanciando o cep
        CEP cep1 = new CEP("16042-891", cidade);
        CEP cep2 = new CEP("89013-445", cidade);

        // Instanciando o cliente
        Usuario cliente1 = new Cliente(1, "Marcelo", "marcelo@gmail.com", "marc123", "(11)11111-1111", "bairro Maria Gabriela", "rua José Fagundes", cep1, StatusUsuario.ATIVO, "222.222.222-22", LocalDate.of(2000, 10, 3));

        System.out.println("Cliente cadastrado:\n" + cliente1);

        // Instanciando o funcionário
        Usuario funcionario1 = new Funcionario(1, "Natália", "natalia@gmail.com", "nat456", "(22)22222-2222", "bairro do Limoeiro", "rua Daiana Monique", cep2, StatusUsuario.ATIVO, "8906");

        System.out.println("\nFuncionário cadastrado:\n" + funcionario1);

        // Instanciando a categoria
        Categoria categoria1 = new Categoria(1, "Livros");
        Categoria categoria2 = new Categoria(2, "HQs Ocidentais");
        Categoria categoria3 = new Categoria(3, "Discos de Vinil");
        Categoria categoria4 = new Categoria(4, "CDs");

        // Instanciando o gênero
        Genero genero1 = new Genero(1, "Aventura");
        Genero genero2 = new Genero(2, "Romance");
        Genero genero3 = new Genero(3, "Suspense");
        Genero genero4 = new Genero(4, "Terror");
        Genero genero5 = new Genero(5, "Rock");
        Genero genero6 = new Genero(6, "Heavy Metal");
        Genero genero7 = new Genero(7, "Rap");

        // Instanciando o produto
        Produto produto1 = new Produto(1, "Harry Potter e a Ordem da Fênix", "Primeiro livro da saga britânica infanto-juvenil, Harry Potter", 45.00, 100, "Novo", categoria1, genero1, StatusProduto.DISPONIVEL);

        // Instanciando imagens do produto
        ImagemProduto imagem1 = new ImagemProduto(1, "assets/images/imagem1.png", produto1);
        ImagemProduto imagem2 = new ImagemProduto(2, "assets/images/imagem2.png", produto1);

        // Adicionando as imagens ao produto cadastrado
        produto1.adicionarImagem(imagem1);
        produto1.adicionarImagem(imagem2);

        System.out.println("\nProduto cadastrado:\n" + produto1);
    }
}