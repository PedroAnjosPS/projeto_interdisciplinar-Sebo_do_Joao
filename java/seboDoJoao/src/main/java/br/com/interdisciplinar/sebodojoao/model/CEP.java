package br.com.interdisciplinar.sebodojoao.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ceps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CEP {

    @Id
    private String nr;
    private Cidade cidade;
}
