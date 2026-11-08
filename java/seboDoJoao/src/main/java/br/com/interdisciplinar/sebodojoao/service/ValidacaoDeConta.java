package br.com.interdisciplinar.sebodojoao.service;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

// As regras dos dados de uma conta (cliente e funcionário). São as mesmas que o site confere
// enquanto a pessoa digita (vitrine.js), repetidas aqui no servidor porque o formulário pode ser
// contornado chamando a API direto. Cada método devolve o valor já limpo ou lança um erro 400
// com a mensagem que será mostrada na tela.
final class ValidacaoDeConta {

    private static final int TAMANHO_MINIMO_SENHA = 8;

    // DDDs que existem no Brasil
    private static final Set<Integer> DDDS_VALIDOS = Set.of(
            11, 12, 13, 14, 15, 16, 17, 18, 19, 21, 22, 24, 27, 28, 31, 32, 33, 34, 35, 37, 38,
            41, 42, 43, 44, 45, 46, 47, 48, 49, 51, 53, 54, 55, 61, 62, 63, 64, 65, 66, 67, 68, 69,
            71, 73, 74, 75, 77, 79, 81, 82, 83, 84, 85, 86, 87, 88, 89, 91, 92, 93, 94, 95, 96, 97, 98, 99);

    private ValidacaoDeConta() { }

    static String nome(String texto) {
        String nome = texto == null ? "" : texto.trim();
        if (nome.isEmpty()) throw erro("Informe o nome.");
        if (nome.length() < 2) throw erro("O nome precisa ter pelo menos 2 letras.");
        if (nome.length() > 100) throw erro("O nome pode ter no máximo 100 caracteres.");
        if (!nome.codePoints().anyMatch(Character::isLetter)) throw erro("O nome precisa ter letras.");
        return nome;
    }

    static String email(String texto) {
        String email = texto == null ? "" : texto.trim();
        if (email.isEmpty()) throw erro("Informe o e-mail.");
        if (email.length() > 100) throw erro("O e-mail pode ter no máximo 100 caracteres.");
        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]{2,}$")) throw erro("E-mail inválido. Ex.: nome@exemplo.com");
        return email;
    }

    static String senha(String texto) {
        if (texto == null || texto.isEmpty()) throw erro("Informe a senha.");
        if (texto.length() < TAMANHO_MINIMO_SENHA) {
            throw erro("A senha precisa ter pelo menos " + TAMANHO_MINIMO_SENHA + " caracteres.");
        }
        if (texto.length() > 255) throw erro("A senha pode ter no máximo 255 caracteres.");
        if (!texto.equals(texto.strip())) throw erro("A senha não pode começar nem terminar com espaço.");
        return texto;
    }

    // Telefone com DDD, ex.: (17) 99999-0000. Devolve o texto como foi digitado (com a máscara).
    static String telefone(String texto) {
        String original = texto == null ? "" : texto.trim();
        if (original.isEmpty()) throw erro("Informe o telefone.");

        String d = somenteDigitos(original);
        if (d.length() > 11 && d.startsWith("55")) d = d.substring(2);
        if (d.length() < 10) throw erro("Telefone incompleto. Digite o DDD e o número, ex.: (17) 99999-0000.");
        if (d.length() > 11) throw erro("Telefone com números demais.");
        if (!DDDS_VALIDOS.contains(Integer.parseInt(d.substring(0, 2)))) throw erro("DDD inválido.");
        if (d.length() == 11 && d.charAt(2) != '9') throw erro("Celular precisa começar com 9 depois do DDD.");
        if (d.length() == 10 && "2345".indexOf(d.charAt(2)) < 0) throw erro("Número de telefone fixo inválido.");
        if (original.length() > 20) throw erro("Telefone com caracteres demais.");
        return original;
    }

    // Devolve o CPF no formato 000.000.000-00 (é assim que fica gravado, para o CPF repetido ser sempre achado)
    static String cpf(String texto) {
        String d = somenteDigitos(texto == null ? "" : texto);
        if (d.isEmpty()) throw erro("Informe o CPF.");
        if (d.length() < 11) throw erro("CPF incompleto: são 11 números.");
        if (!cpfValido(d)) throw erro("CPF inválido. Confira os números digitados.");
        return d.substring(0, 3) + "." + d.substring(3, 6) + "." + d.substring(6, 9) + "-" + d.substring(9);
    }

    static String soDigitosDoCpf(String cpfFormatado) {
        return somenteDigitos(cpfFormatado);
    }

    static LocalDate dataDeNascimento(LocalDate data) {
        if (data == null) throw erro("Informe a data de nascimento.");
        if (data.getYear() < 1900) throw erro("Data muito antiga. Confira o ano.");
        if (data.isAfter(LocalDate.now())) throw erro("A data de nascimento não pode ser no futuro.");
        return data;
    }

    static String codigoDoFuncionario(String texto) {
        String codigo = texto == null ? "" : texto.trim();
        if (codigo.isEmpty()) throw erro("Informe o código do funcionário.");
        if (codigo.length() > 50) throw erro("O código pode ter no máximo 50 caracteres.");
        return codigo;
    }

    // Confere os dois dígitos verificadores do CPF
    private static boolean cpfValido(String d) {
        if (d.length() != 11 || d.chars().distinct().count() == 1) return false; // 111.111.111-11 etc. não existem
        return digitoVerificador(d, 9) == d.charAt(9) - '0' && digitoVerificador(d, 10) == d.charAt(10) - '0';
    }

    private static int digitoVerificador(String d, int quantidade) {
        int soma = 0;
        for (int i = 0; i < quantidade; i++) soma += (d.charAt(i) - '0') * (quantidade + 1 - i);
        int resto = (soma * 10) % 11;
        return resto == 10 ? 0 : resto;
    }

    private static String somenteDigitos(String texto) {
        return texto.replaceAll("\\D", "");
    }

    private static ResponseStatusException erro(String mensagem) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
    }
}
