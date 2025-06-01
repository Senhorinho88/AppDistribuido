package org.vitor.appdistribuido.Alunos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity


public class Alunos {

    @SequenceGenerator(
            name = "alunos_sequence",
            sequenceName = "alunos_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "alunos_sequence"
    )
    private Long id;

    private String name;
    private Integer number;

    public Alunos(String name, Integer number) {
        this.name = name;
        this.number = number;
    }

}
