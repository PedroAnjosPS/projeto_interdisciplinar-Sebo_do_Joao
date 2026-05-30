package model;

import java.time.LocalDate;
import java.util.ArrayList;

public class Cliente extends Usuario {
    // Atributos
    private String cpf;
    private LocalDate dataNascimento;
    private ArrayList<Pedido> pedidos = new ArrayList<>();

    // Construtores
    public Cliente() {}

    public Cliente(int id, String nome, String email, String senha, String telefone, String bairro, String logradouro, CEP cep, StatusUsuario status, String cpf, LocalDate dataNascimento) {
        super(id, nome, email, senha, telefone, bairro, logradouro, cep, status);
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
    }

    // Métodos acessores e modificadores
    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public ArrayList<Pedido> getPedidos() {
        return pedidos;
    }

    public void adicionarPedido(Pedido pedido) {
        pedidos.add(pedido);
    }

    public void removerPedido(Pedido pedido) {
        pedidos.remove(pedido);
    }

    // toString

    @Override
    public String toString() {
        return super.toString()
                + "\nCPF: " + cpf
                + "\nData de Nascimento: " + dataNascimento;
    }
}
