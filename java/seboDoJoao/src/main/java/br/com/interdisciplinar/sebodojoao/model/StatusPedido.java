package br.com.interdisciplinar.sebodojoao.model;

import lombok.Getter;

@Getter
public enum StatusPedido {
    AGUARDANDO_PAGAMENTO(1),
    PROCESSANDO(2), // Pagamento foi aprovado, mas o pedido ainda não foi enviado
    CONCLUIDO(3), // Quando o pedido foi enviado e entregue com sucesso
    CANCELADO(4);

    private final int codigo;

    StatusPedido(int codigo) { this.codigo = codigo; }

    public static StatusPedido porCodigo(int codigo) {
        for (StatusPedido status : values()) {
            if (status.codigo == codigo) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status do pedido inválido: " + codigo);
    }
}
