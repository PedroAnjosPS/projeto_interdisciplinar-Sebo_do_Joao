package br.com.interdisciplinar.sebodojoao.model;

import java.time.LocalDateTime;

public class Entrega {
    // Atributos
    private int id;
    private String transportadora;
    private String linkRastreio;
    private String codigoRastreio;
    private LocalDateTime dataPostagem;
    private LocalDateTime dataEntrega;
    private LocalDateTime dataPrevisaoEntrega;
    private StatusEntrega status;

    // Construtores
    public Entrega() {}

    public Entrega(int id, String transportadora, String linkRastreio, String codigoRastreio, LocalDateTime dataPostagem, LocalDateTime dataEntrega, LocalDateTime dataPrevisaoEntrega, StatusEntrega status) {
        this.id = id;
        this.transportadora = transportadora;
        this.linkRastreio = linkRastreio;
        this.codigoRastreio = codigoRastreio;
        this.dataPostagem = dataPostagem;
        this.dataEntrega = dataEntrega;
        this.dataPrevisaoEntrega = dataPrevisaoEntrega;
        this.status = status;
    }

    // Métodos acessores e modificadores
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTransportadora() {
        return transportadora;
    }

    public void setTransportadora(String transportadora) {
        this.transportadora = transportadora;
    }

    public String getLinkRastreio() {
        return linkRastreio;
    }

    public void setLinkRastreio(String linkRastreio) {
        this.linkRastreio = linkRastreio;
    }

    public String getCodigoRastreio() {
        return codigoRastreio;
    }

    public void setCodigoRastreio(String codigoRastreio) {
        this.codigoRastreio = codigoRastreio;
    }

    public LocalDateTime getDataPostagem() {
        return dataPostagem;
    }

    public void setDataPostagem(LocalDateTime dataPostagem) {
        this.dataPostagem = dataPostagem;
    }

    public LocalDateTime getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(LocalDateTime dataEntrega) {
        this.dataEntrega = dataEntrega;
    }

    public LocalDateTime getDataPrevisaoEntrega() {
        return dataPrevisaoEntrega;
    }

    public void setDataPrevisaoEntrega(LocalDateTime dataPrevisaoEntrega) {
        this.dataPrevisaoEntrega = dataPrevisaoEntrega;
    }

    public StatusEntrega getStatus() {
        return status;
    }

    public void setStatus(StatusEntrega status) {
        this.status = status;
    }

    // toString
    @Override
    public String toString() {
        return "\n\t{" +
                "\n\t\tId: " + id
                + "\n\t\tTransportadora: " + transportadora
                + "\n\t\tLink de rastreio: " + linkRastreio
                + "\n\t\tCódigo de rastreio: " + codigoRastreio
                + "\n\t\tData de postagem: " + dataPostagem
                + "\n\t\tData de entrega: " + dataEntrega
                + "\n\t\tData de previsão de entrega: " + dataPrevisaoEntrega
                + "\n\t\tStatus: " + status
                + "\n\t}";
    }
}
