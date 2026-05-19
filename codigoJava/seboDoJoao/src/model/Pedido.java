package model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Pedido {
    // Atributos
    private int id;
    private LocalDateTime data;
    private double total;
    private StatusPedido status;
    private Cliente cliente;
    private Funcionario funcionario;
    private ArrayList<Pagamento> pagamentos;
    private ArrayList<ItemPedido> itemPedidos;
    private Entrega entrega;

    // Construtores
    public Pedido() {}

    public Pedido(int id, LocalDateTime data, double total, StatusPedido status, Cliente cliente, Funcionario funcionario, Entrega entrega) {
        this.id = id;
        this.data = data;
        this.total = total;
        this.status = status;
        this.cliente = cliente;
        this.funcionario = funcionario;
        this.entrega = entrega;
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

    public Entrega getEntrega() {
        return entrega;
    }

    public void setEntrega(Entrega entrega) {
        this.entrega = entrega;
    }
}
