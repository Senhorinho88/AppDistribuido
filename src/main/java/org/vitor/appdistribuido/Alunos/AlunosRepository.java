package org.vitor.appdistribuido.Alunos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlunosRepository extends JpaRepository<Alunos, Long> {

    Optional<Alunos> findByNumber(Integer number);

    boolean existsByNumber(Integer number);

    List<Alunos> findAllByOrderByNameAsc();
}
