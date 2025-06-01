package org.vitor.appdistribuido.Presenca;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vitor.appdistribuido.Alunos.Alunos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositório para a entidade Presenca.
 * Fornece métodos para operações CRUD e consultas personalizadas.
 */
@Repository
public interface PresencaRepository extends JpaRepository<Presenca, Long> {
    /**
     * Encontra todos os registros de presença para um aluno específico.
     * @param aluno O objeto Alunos para o qual buscar as presenças.
     * @return Uma lista de objetos Presenca.
     */
    List<Presenca> findByAluno(Alunos aluno);

    /**
     * Encontra registros de presença para um aluno específico dentro de um intervalo de datas e horas.
     * @param aluno O objeto Alunos.
     * @param start O início do intervalo de data e hora (inclusive).
     * @param end O fim do intervalo de data e hora (inclusive).
     * @return Uma lista de objetos Presenca.
     */
    List<Presenca> findByAlunoAndDataHoraBetween(Alunos aluno, LocalDateTime start, LocalDateTime end);

    /**
     * Encontra um registro de presença específico para um aluno em uma determinada data e hora.
     * Útil para verificar se um aluno já registrou presença em um momento exato.
     * @param aluno O objeto Alunos.
     * @param dataHora A data e hora exata da presença.
     * @return Um Optional contendo o objeto Presenca, se encontrado.
     */
    Optional<Presenca> findByAlunoAndDataHora(Alunos aluno, LocalDateTime dataHora);
}
