package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;

@Entity
@Table(name = "ufs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UF {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String sigla;

    private String nome;
    private ArrayList<Cidade> cidades;
}
