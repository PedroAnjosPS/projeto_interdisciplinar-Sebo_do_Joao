package br.com.interdisciplinar.sebodojoao.restcontroller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

// O "tratador de erros" dos controllers de endereços, cartões e carrinho.
// Quando uma regra é quebrada (sem login, endereço inexistente, limite de cartões, estoque insuficiente...),
// o serviço lança um ResponseStatusException. Este tratador transforma o erro em {"mensagem": "..."} com o
// código HTTP certo; o site lê "mensagem" e mostra para a pessoa. (O CompraRestController já faz o mesmo
// por conta própria; aqui evitamos repetir o mesmo trecho em cada controller novo.)
@RestControllerAdvice(assignableTypes = { EnderecoRestController.class, CartaoRestController.class, ItemCarrinhoRestController.class, AdministracaoRestController.class })
public class TratadorDeErrosDaLoja {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> tratarErro(ResponseStatusException erro) {
        return ResponseEntity.status(erro.getStatusCode()).body(Map.of("mensagem", String.valueOf(erro.getReason())));
    }
}
