package model;

public class Funcionario extends Usuario {
    // Atributos
    private String codigoFuncionario;

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
}
