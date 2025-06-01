package org.vitor.appdistribuido.Alunos;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor      // injecção via construtor
public class AlunosService {

    private final AlunosRepository alunosRepository;

    /* ------------------ READ ------------------ */
    public List<Alunos> getAlunos() {
        return alunosRepository.findAll();
    }

    public List<Alunos> getAlunosSortedByName() {
        return alunosRepository.findAllByOrderByNameAsc();
    }

    public Alunos getAlunoById(Long id) {
        return alunosRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Aluno com id %d não encontrado".formatted(id)));
    }

    /* ------------------ CREATE ------------------ */
    @Transactional
    public void addNewAluno(Alunos aluno) {
        if (alunosRepository.existsByNumber(aluno.getNumber())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Número %d já está em uso".formatted(aluno.getNumber()));
        }
        alunosRepository.save(aluno);
        log.info("Novo aluno adicionado: {}", aluno);
    }

    /* ------------------ DELETE ------------------ */
    @Transactional
    public void deleteAluno(Long alunoId) {
        if (!alunosRepository.existsById(alunoId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Aluno com id %d não existe".formatted(alunoId));
        }
        alunosRepository.deleteById(alunoId);
        log.info("Aluno deletado: {}", alunoId);
    }

    /* ------------------ UPDATE ------------------ */
    @Transactional
    public void updateAluno(Long alunoId, String name, Integer number) {
        Alunos aluno = getAlunoById(alunoId); // lança 404 se não existir

        if (name != null && !name.isBlank() && !Objects.equals(aluno.getName(), name)) {
            aluno.setName(name);
        }

        if (number != null && !Objects.equals(aluno.getNumber(), number)) {
            if (alunosRepository.existsByNumber(number)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "Número %d já está em uso".formatted(number));
            }
            aluno.setNumber(number);
        }
    }
}
