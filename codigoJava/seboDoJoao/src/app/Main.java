package app;

import model.*;

import java.time.LocalDate;

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
        Categoria categoria1 = new Categoria(1, "Livro");

        // Instanciando o gênero
        Genero genero1 = new Genero(1, "Aventura");

        // Instanciando o produto

    }
}