package model;

public class Usuario {
    // Atributos
    private int id;
    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private String bairro;
    private String logradouro;
    private CEP cep;
    private StatusUsuario status;

    // Construtores
    public Usuario() {}

    public Usuario(int id, String nome, String email, String senha, String telefone, String bairro, String logradouro, CEP cep, StatusUsuario status) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.bairro = bairro;
        this.logradouro = logradouro;
        this.cep = cep;
        this.status = status;
    }

    // Métodos acessores e modificadores
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public CEP getCep() {
        return cep;
    }

    public void setCep(CEP cep) {
        this.cep = cep;
    }

    public StatusUsuario getStatus() {
        return status;
    }

    public void setStatus(StatusUsuario status) {
        this.status = status;
    }
}
