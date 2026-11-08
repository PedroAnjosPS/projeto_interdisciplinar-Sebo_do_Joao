package br.com.interdisciplinar.sebodojoao.restcontroller;

import java.sql.SQLException;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

// O "tratador de erros" de toda a API.
// Quando uma regra é quebrada (sem login, endereço inexistente, limite de cartões, estoque insuficiente...),
// o serviço lança um ResponseStatusException. Este tratador transforma o erro em {"mensagem": "..."} com o
// código HTTP certo; o site lê "mensagem" e mostra para a pessoa. Também traduz os erros que vêm do
// próprio banco (e-mail repetido, excluir algo que está em uso...) para mensagens em português.
@RestControllerAdvice
public class TratadorDeErrosDaLoja {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> tratarErro(ResponseStatusException erro) {
        return ResponseEntity.status(erro.getStatusCode()).body(Map.of("mensagem", String.valueOf(erro.getReason())));
    }

    // O banco recusou o comando: valor repetido (UNIQUE), registro em uso (chave estrangeira), valor proibido (CHECK)...
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> tratarErroDoBanco(DataIntegrityViolationException erro) {
        String detalhe = String.valueOf(NestedExceptionUtils.getMostSpecificCause(erro).getMessage());
        String mensagem;

        if (detalhe.contains("UNIQUE KEY") || detalhe.contains("duplicate key")) {
            if (detalhe.contains("dbo.usuarios")) mensagem = "Já existe uma conta com esse e-mail.";
            else if (detalhe.contains("dbo.clientes")) mensagem = "Já existe um cliente com esse CPF.";
            else if (detalhe.contains("dbo.funcionarios")) mensagem = "Já existe um funcionário com esse código.";
            else mensagem = "Já existe um registro com esses dados.";
        } else if (detalhe.contains("REFERENCE constraint")) {
            mensagem = "Não é possível excluir: este registro está sendo usado em outros cadastros "
                    + "(por exemplo, produtos, pedidos ou entregas).";
        } else if (detalhe.contains("FOREIGN KEY constraint")) {
            mensagem = "Um dos dados informados (categoria, gênero, cidade...) não existe.";
        } else if (detalhe.contains("CHECK constraint")) {
            mensagem = "Algum valor está fora do permitido (preço, estoque, status...).";
        } else {
            mensagem = "Não foi possível salvar: os dados entram em conflito com o banco de dados.";
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensagem", mensagem));
    }

    // Erro lançado de propósito por uma procedure (THROW 50030, por exemplo): a mensagem já está em português
    @ExceptionHandler(UncategorizedSQLException.class)
    public ResponseEntity<Map<String, String>> tratarErroDeProcedure(UncategorizedSQLException erro) {
        SQLException sql = erro.getSQLException();
        if (sql != null && sql.getErrorCode() >= 50000 && sql.getErrorCode() < 51000) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("mensagem", sql.getMessage()));
        }
        erro.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensagem", "Não foi possível concluir a operação agora. Tente novamente."));
    }

    // Os serviços usam orElseThrow() quando o id não existe
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> tratarNaoEncontrado(NoSuchElementException erro) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensagem", "Registro não encontrado."));
    }

    // Campo que faltou, número escrito errado, JSON quebrado...
    @ExceptionHandler({ MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class })
    public ResponseEntity<Map<String, String>> tratarDadosInvalidos(Exception erro) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensagem", "Dados inválidos ou faltando."));
    }
}
