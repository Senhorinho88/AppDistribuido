package org.vitor.appdistribuido.Presenca;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.vitor.appdistribuido.Alunos.Alunos;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PresencaRepository extends JpaRepository<Presenca, Long> {


    Optional<Presenca> findByAlunoAndDataChamada(Alunos aluno, LocalDate dataChamada);


    List<Presenca> findByDataChamada(LocalDate dataChamada);


    List<Presenca> findByAlunoOrderByDataChamadaAsc(Alunos aluno);
}