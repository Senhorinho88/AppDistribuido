package org.vitor.appdistribuido.Presenca;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.vitor.appdistribuido.Alunos.Alunos; // Importa a entidade Alunos

import java.time.LocalDateTime;

/**
 * Entidade que representa um registro de presença de um aluno.
 * Utiliza Lombok para reduzir o código boilerplate.
 */
@Entity
@Table(name = "presencas") // Define o nome da tabela no banco de dados
@Data // Gera automaticamente getters, setters, toString, equals e hashCode
@NoArgsConstructor // Gera um construtor sem argumentos
@AllArgsConstructor // Gera um construtor com todos os campos
public class Presenca {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // Define uma relação de muitos para um (muitas presenças para um aluno)
    @JoinColumn(name = "aluno_id", nullable = false) // Define a coluna da chave estrangeira e que não pode ser nula
    private Alunos aluno;

    private LocalDateTime dataHora; // Data e hora em que a presença foi registrada
    private Boolean presente; // Indica se o aluno estava presente (true) ou ausente (false)
}