package model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Cliente extends Usuario {
    // Atributos
    private String cpf;
    private LocalDateTime dataNascimento;
    private ArrayList<Pedido> pedidos;

    // Construtores
    public Cliente() {}

    public Cliente(int id, String nome, String email, String senha, String telefone, String bairro, String logradouro, CEP cep, StatusUsuario status, String cpf, LocalDateTime dataNascimento) {
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

    public LocalDateTime getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDateTime dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public ArrayList<Pedido> getPedidos() {
        return pedidos;
    }
}
