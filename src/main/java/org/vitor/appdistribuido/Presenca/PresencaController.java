package org.vitor.appdistribuido.Presenca;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;


@RestController
@RequestMapping("/api/presencas")
public class PresencaController {

    private final PresencaService presencaService;

    @Autowired
    public PresencaController(PresencaService presencaService) {
        this.presencaService = presencaService;
    }

    /* ----------------------------------------------------------------
     * POST /marcar/{alunoId} – marca presença agora
     * -------------------------------------------------------------- */
    @PostMapping("/marcar/{alunoId}")
    public ResponseEntity<Presenca> marcarPresenca(@PathVariable Long alunoId) {
        try {
            Presenca novaPresenca = presencaService.marcarPresenca(alunoId);
            return new ResponseEntity<>(novaPresenca, HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /* ----------------------------------------------------------------
     * POST /marcar-data – marca presença em data/hora específica
     * -------------------------------------------------------------- */
    @PostMapping("/marcar-data")
    public ResponseEntity<Presenca> marcarPresencaComData(@RequestBody MarcarPresencaRequest request) {
        try {
            Presenca novaPresenca = presencaService.marcarPresenca(
                    request.getAlunoId(),
                    request.getDataHora());
            return new ResponseEntity<>(novaPresenca, HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {                     // erro de formato, etc.
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /* ----------------------------------------------------------------
     * GET /aluno/{id} – todas as presenças do aluno
     * -------------------------------------------------------------- */
    @GetMapping("/aluno/{alunoId}")
    public ResponseEntity<List<Presenca>> buscarPresencasPorAluno(@PathVariable Long alunoId) {
        try {
            List<Presenca> presencas = presencaService.buscarPresencasPorAluno(alunoId);
            return new ResponseEntity<>(presencas, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /* ----------------------------------------------------------------
     * GET /aluno/{id}/periodo?startDate=AAAA-MM-DD&endDate=AAAA-MM-DD
     * -------------------------------------------------------------- */
    @GetMapping("/aluno/{alunoId}/periodo")
    public ResponseEntity<List<Presenca>> buscarPresencasPorAlunoEPeriodo(
            @PathVariable Long alunoId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate")   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<Presenca> presencas = presencaService.buscarPresencasPorAlunoEPeriodo(alunoId, startDate, endDate);
            return new ResponseEntity<>(presencas, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /* ----------------------------------------------------------------
     * GET /verificar/{id}/{date} – presente nesse dia?
     * -------------------------------------------------------------- */
    @GetMapping("/verificar/{alunoId}/{date}")
    public ResponseEntity<Boolean> verificarPresencaNoDia(
            @PathVariable Long alunoId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            boolean presente = presencaService.verificarPresencaNoDia(alunoId, date);
            return new ResponseEntity<>(presente, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /* ----------------------------------------------------------------
     * GET /api/presencas – todas as presenças
     * -------------------------------------------------------------- */
    @GetMapping
    public ResponseEntity<List<Presenca>> buscarTodasPresencas() {
        List<Presenca> presencas = presencaService.buscarTodasPresencas();
        return new ResponseEntity<>(presencas, HttpStatus.OK);
    }

    /* ----------------------------------------------------------------
     * DELETE /{presencaId} – remove uma presença
     * -------------------------------------------------------------- */
    @DeleteMapping("/{presencaId}")
    public ResponseEntity<Void> deletarPresenca(@PathVariable Long presencaId) {
        try {
            presencaService.deletarPresenca(presencaId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /* ================================================================
     * DTO para o POST /marcar-data
     * ================================================================ */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class MarcarPresencaRequest {
        private Long alunoId;

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime dataHora;   // ISO-8601, ex.: 2025-06-01T14:30:00
    }
}
