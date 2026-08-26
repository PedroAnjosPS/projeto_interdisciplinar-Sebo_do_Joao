package br.com.interdisciplinar.sebodojoao.model;

import java.util.ArrayList;

public class Funcionario extends Usuario {
    // Atributos
    private String codigoFuncionario;
    private ArrayList<Pedido> pedidos;
    private ArrayList<Produto> produtos;

    // Construtores
    public Funcionario() {}

    public Funcionario(int id, String nome, String email, String senha, String telefone, String bairro, String logradouro, CEP cep, StatusUsuario status, String codigoFuncionario) {
        super(id, nome, email, senha, telefone, bairro, logradouro, cep, status);
        this.codigoFuncionario = codigoFuncionario;
    }

    // Métodos acessores e modificadores
    public String getCodigoFuncionario() {
        return codigoFuncionario;
    }

    public void setCodigoFuncionario(String codigoFuncionario) {
        this.codigoFuncionario = codigoFuncionario;
    }

    public ArrayList<Pedido> getPedidos() {
        return pedidos;
    }

    public ArrayList<Produto> getProdutos() {
        return produtos;
    }
}
