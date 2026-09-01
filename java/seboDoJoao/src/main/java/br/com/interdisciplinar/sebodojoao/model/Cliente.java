package br.com.interdisciplinar.sebodojoao.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    @Id
    private String cpf;

    private LocalDate dataNascimento;
    private ArrayList<Pedido> pedidos = new ArrayList<>();
}

