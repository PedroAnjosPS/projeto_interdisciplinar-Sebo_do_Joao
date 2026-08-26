package br.com.interdisciplinar.sebodojoao.model;

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
    private ArrayList<Pagamento> pagamentos = new ArrayList<>();
    private ArrayList<ItemPedido> itensPedido = new ArrayList<>();
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

    public ArrayList<ItemPedido> getItensPedido() {
        return itensPedido;
    }

    public Entrega getEntrega() {
        return entrega;
    }

    public void setEntrega(Entrega entrega) {
        this.entrega = entrega;
    }

    // Outros métodos
    public void adicionarPagamento(Pagamento pagamento) {
        pagamentos.add(pagamento);
    }

    public void removerPagamento(Pagamento pagamento) {
        pagamentos.remove(pagamento);
    }

    public void adicionarItemPedido(ItemPedido itemPedido) {
        itensPedido.add(itemPedido);
    }

    public void removerItemPedido(ItemPedido itemPedido) {
        itensPedido.remove(itemPedido);
    }

    public String mostrarPagamentos() {
        String dadosPagamentos = "";
        int i = 0;
        for (Pagamento pagamento : pagamentos) {
            if (i == 0) {
                dadosPagamentos += pagamento.toString() + ",";
            } else {
                dadosPagamentos += pagamento.toString();
            }

            i++;
        }

        return dadosPagamentos;
    }

    public String mostrarItensPedido() {
        String dadosItensPedido = "";
        int i = 0;
        for (ItemPedido itemPedido : itensPedido) {
            if (i == 0) {
                dadosItensPedido += itemPedido.toString() + ",";
            } else {
                dadosItensPedido += itemPedido.toString();
            }

            i++;
        }

        return dadosItensPedido;
    }

    // toString
    @Override
    public String toString() {
        return  "Id: " + id +
                "\nData: " + data +
                "\nTotal: R$" + String.format("%.2f", total) +
                "\nStatus: " + status +
                "\nCliente: " + cliente.getNome() + " - " + cliente.getEmail() +
                "\nFuncionário gerenciador: " + funcionario.getNome() + " - " + funcionario.getEmail() +
                "\nPagamentos:" + mostrarPagamentos() +
                "\nItens do pedido:" + mostrarItensPedido() +
                "\nEntrega:" + entrega;
    }
}
