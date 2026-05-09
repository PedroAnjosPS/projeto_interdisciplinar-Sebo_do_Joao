package model;

import java.time.LocalDateTime;

public class Pagamento {
    // Atributos
    private int id;
    private double valor;
    private int numeroParcela;
    private int quantidadeParcelas;
    private LocalDateTime dataPagamento;
    private LocalDateTime dataVencimento;
    private StatusPagamento status;
    private Pedido pedido;

    // Construtores
    public Pagamento() {}

    public Pagamento(int id, double valor, int numeroParcela, int quantidadeParcelas, LocalDateTime dataPagamento, LocalDateTime dataVencimento, StatusPagamento status, Pedido pedido) {
        this.id = id;
        this.valor = valor;
        this.numeroParcela = numeroParcela;
        this.quantidadeParcelas = quantidadeParcelas;
        this.dataPagamento = dataPagamento;
        this.dataVencimento = dataVencimento;
        this.status = status;
        this.pedido = pedido;
    }

    // Métodos acessores e modificadores
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public int getNumeroParcela() {
        return numeroParcela;
    }

    public void setNumeroParcela(int numeroParcela) {
        this.numeroParcela = numeroParcela;
    }

    public int getQuantidadeParcelas() {
        return quantidadeParcelas;
    }

    public void setQuantidadeParcelas(int quantidadeParcelas) {
        this.quantidadeParcelas = quantidadeParcelas;
    }

    public LocalDateTime getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDateTime dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public LocalDateTime getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDateTime dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public StatusPagamento getStatus() {
        return status;
    }

    public void setStatus(StatusPagamento status) {
        this.status = status;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }
}
