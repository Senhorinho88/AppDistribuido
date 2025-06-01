package org.vitor.appdistribuido.Alunos;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/alunos")
@RequiredArgsConstructor
public class AlunosController {

    private final AlunosService alunosService;

    /* ------------------ LISTAR ------------------ */
    @GetMapping
    public List<Alunos> getAlunos() {
        return alunosService.getAlunos();
    }

    /* Ordenado por nome (poderia usar ?sort=name,asc) */
    @GetMapping("/sortedByName")
    public List<Alunos> getAlunosSortedByName() {
        return alunosService.getAlunosSortedByName();
    }

    /* ------------------ BUSCAR POR ID ------------------ */
    @GetMapping("{alunoId}")
    public Alunos getAlunoById(@PathVariable Long alunoId) {
        return alunosService.getAlunoById(alunoId);
    }

    /* ------------------ CRIAR ------------------ */
    @PostMapping
    public ResponseEntity<Alunos> registerNewAluno(@Valid @RequestBody Alunos aluno) {
        alunosService.addNewAluno(aluno);
        return ResponseEntity.status(HttpStatus.CREATED).body(aluno);
    }

    /* ------------------ DELETAR ------------------ */
    @DeleteMapping("{alunoId}")
    public ResponseEntity<Void> deleteAluno(@PathVariable Long alunoId) {
        alunosService.deleteAluno(alunoId);
        return ResponseEntity.ok().build();
    }

    /* ------------------ ATUALIZAR ------------------ */
    @PutMapping("{alunoId}")
    public ResponseEntity<Void> updateAluno(
            @PathVariable Long alunoId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer number) {
        alunosService.updateAluno(alunoId, name, number);
        return ResponseEntity.ok().build();
    }
}
