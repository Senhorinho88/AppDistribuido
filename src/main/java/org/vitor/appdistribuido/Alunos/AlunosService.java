package org.vitor.appdistribuido.Alunos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AlunosService {

    private final AlunosRepository alunosRepository;

    @Autowired
    public AlunosService(AlunosRepository alunosRepository) {
        this.alunosRepository = alunosRepository;
    }

    public List<Alunos> getAlunos() {
        return alunosRepository.findAll();
    }


    public List<Alunos> getAlunosSortedByName() {
        return alunosRepository.findAllByOrderByNameAsc();
    }

    public Alunos getAlunoById(Long id) {
        return alunosRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Aluno com id " + id + " não encontrado "
                ));
    }

    public void addNewAluno(Alunos aluno) {
        Optional<Alunos> alunoOptional = alunosRepository.findByNumber(aluno.getNumber());
        if (alunoOptional.isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Number " + aluno.getNumber() + " Já tomados"
            );
        }
        alunosRepository.save(aluno);
        System.out.println("Novo aluno adicionado : " + aluno);
    }

    public void deleteAluno(Long alunoId) {
        boolean exists = alunosRepository.existsById(alunoId);
        if (!exists) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Aluno with id " + alunoId + " Não existente "
            );
        }
        alunosRepository.deleteById(alunoId);
        System.out.println("Aluno deletado: " + alunoId);
    }

    @Transactional
    public void updateAluno(Long alunoId, String name, Integer number) {
        Alunos aluno = alunosRepository.findById(alunoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Aluno com id " + alunoId + " não foi encontrado"
                ));

        if (name != null && !name.isEmpty() && !Objects.equals(aluno.getName(), name)) {
            aluno.setName(name);
        }

        if (number != null && !Objects.equals(aluno.getNumber(), number)) {
            Optional<Alunos> alunoOptional = alunosRepository.findByNumber(number);
            if (alunoOptional.isPresent() && !Objects.equals(alunoOptional.get().getId(), alunoId)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "Número " + number + " Número em uso!"
                );
            }
            aluno.setNumber(number);
        }
    }
}