package org.vitor.appdistribuido.Presenca;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.vitor.appdistribuido.Alunos.Alunos; // Importa a entidade Alunos

import java.time.LocalDateTime;

@Entity
@Table(name = "presencas")
@Data
@NoArgsConstructor
public class Presenca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private Alunos aluno;

    private LocalDateTime dataHora;
    private Boolean presente;

    // >>> novo construtor
    public Presenca(Alunos aluno, LocalDateTime dataHora, Boolean presente) {
        this.aluno    = aluno;
        this.dataHora = dataHora;
        this.presente = presente;
    }
}