package model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Pedido {
    // Atributos
    private int id;
    private LocalDateTime data;
    private double total;
    private int numeroRastreio;
    private StatusPedido status;
    private Cliente cliente;
    private Funcionario funcionario;
    private ArrayList<Pagamento> pagamentos;
    private ArrayList<ItemPedido> itemPedidos;

    // Construtores
    public Pedido() {}

    public Pedido(int id, LocalDateTime data, double total, int numeroRastreio, StatusPedido status, Cliente cliente, Funcionario funcionario) {
        this.id = id;
        this.data = data;
        this.total = total;
        this.numeroRastreio = numeroRastreio;
        this.status = status;
        this.cliente = cliente;
        this.funcionario = funcionario;
    }

    // Métodos acessores e modificadores
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getNumeroRastreio() {
        return numeroRastreio;
    }

    public void setNumeroRastreio(int numeroRastreio) {
        this.numeroRastreio = numeroRastreio;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    public ArrayList<Pagamento> getPagamentos() {
        return pagamentos;
    }

    public ArrayList<ItemPedido> getItemPedidos() {
        return itemPedidos;
    }
}
