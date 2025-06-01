package org.vitor.appdistribuido.Presenca;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.vitor.appdistribuido.Alunos.Alunos;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "presencas")
public class Presenca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private Alunos aluno;

    @Column(nullable = false)
    private LocalDate dataChamada;

    @Column(nullable = false)
    private Boolean presente;

    public Presenca(Alunos aluno, LocalDate dataChamada, Boolean presente) {
        this.aluno = aluno;
        this.dataChamada = dataChamada;
        this.presente = presente;
    }
}