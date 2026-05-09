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
        CEP cep = new CEP("16042-891", cidade);

        // Instanciando o cliente
        Usuario cliente1 = new Cliente(1, "Marcelo", "marcelo@gmail.com", "marc123", "(11) 11111-111", "bairro Maria Gabriela", "rua José Fagundes", cep, StatusUsuario.ATIVO, "222.222.222-22", LocalDate.of(2000, 10, 3));

        System.out.println("Cliente cadastrado:\n" + cliente1);
    }
}