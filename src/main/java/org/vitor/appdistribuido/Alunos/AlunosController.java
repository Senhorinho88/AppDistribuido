package org.vitor.appdistribuido.Alunos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/alunos")
public class AlunosController {

    private final AlunosService alunosService;

    @Autowired
    public AlunosController(AlunosService alunosService) {
        this.alunosService = alunosService;
    }


    @GetMapping
    public List<Alunos> getAlunos() {
        return alunosService.getAlunos();
    }


    @GetMapping(path = "/sortedByName")
    public List<Alunos> getAlunosSortedByName() {
        return alunosService.getAlunosSortedByName();
    }



    @GetMapping(path = "{alunoId}")
    public Alunos getAlunoById(@PathVariable("alunoId") Long alunoId) {
        return alunosService.getAlunoById(alunoId);
    }


    @PostMapping
    public ResponseEntity<String> registerNewAluno(@RequestBody Alunos aluno) {
        alunosService.addNewAluno(aluno);
        return new ResponseEntity<>("Aluno registrado com sucesso!", HttpStatus.CREATED);
    }


    @DeleteMapping(path = "{alunoId}")
    public ResponseEntity<String> deleteAluno(@PathVariable("alunoId") Long alunoId) {
        alunosService.deleteAluno(alunoId);
        return new ResponseEntity<>("Aluno deletado com sucesso!", HttpStatus.OK);
    }


    @PutMapping(path = "{alunoId}")
    public ResponseEntity<String> updateAluno(
            @PathVariable("alunoId") Long alunoId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer number) {
        alunosService.updateAluno(alunoId, name, number);
        return new ResponseEntity<>("Aluno atualizado com sucesso!", HttpStatus.OK);
    }
}