package org.vitor.appdistribuido.Presenca;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Controlador REST para gerenciar operações relacionadas à presença.
 * Expõe endpoints para marcar, buscar e deletar registros de presença.
 */
@RestController
@RequestMapping("/api/presencas") // Define o caminho base para todos os endpoints neste controlador
public class PresencaController {

    private final PresencaService presencaService;

    /**
     * Construtor para injeção de dependência do PresencaService.
     * @param presencaService O serviço de presença a ser utilizado.
     */
    @Autowired
    public PresencaController(PresencaService presencaService) {
        this.presencaService = presencaService;
    }

    /**
     * Endpoint para marcar a presença de um aluno no momento atual.
     * Exemplo de requisição: POST /api/presencas/marcar/{alunoId}
     *
     * @param alunoId O ID do aluno cuja presença será marcada.
     * @return ResponseEntity com o objeto Presenca salvo e status HTTP 201 (Created),
     * ou status 404 (Not Found) se o aluno não for encontrado.
     */
    @PostMapping("/marcar/{alunoId}")
    public ResponseEntity<Presenca> marcarPresenca(@PathVariable Long alunoId) {
        try {
            Presenca novaPresenca = presencaService.marcarPresenca(alunoId);
            return new ResponseEntity<>(novaPresenca, HttpStatus.CREATED); // Retorna 201 Created
        } catch (NoSuchElementException e) {
            // Se o aluno não for encontrado, retorna 404 Not Found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint para marcar a presença de um aluno em uma data e hora específicas.
     * Exemplo de requisição: POST /api/presencas/marcar-data
     * Corpo da requisição (JSON):
     * {
     * "alunoId": 1,
     * "dataHora": "2023-10-26T10:00:00"
     * }
     *
     * @param request Um mapa contendo "alunoId" e "dataHora" (string no formato ISO 8601).
     * @return ResponseEntity com o objeto Presenca salvo e status HTTP 201 (Created),
     * ou status 404 (Not Found) se o aluno não for encontrado,
     * ou status 400 (Bad Request) se o formato da data/hora estiver incorreto.
     */
    @PostMapping("/marcar-data")
    public ResponseEntity<Presenca> marcarPresencaComData(@RequestBody MarcarPresencaRequest request) {
        try {
            // Converte a string dataHora para LocalDateTime
            LocalDateTime dataHora = LocalDateTime.parse(request.getDataHora());
            Presenca novaPresenca = presencaService.marcarPresenca(request.getAlunoId(), dataHora);
            return new ResponseEntity<>(novaPresenca, HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // Captura outras exceções, como erro de parsing de data/hora
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Endpoint para buscar todos os registros de presença de um aluno.
     * Exemplo de requisição: GET /api/presencas/aluno/{alunoId}
     *
     * @param alunoId O ID do aluno.
     * @return ResponseEntity com uma lista de objetos Presenca e status HTTP 200 (OK),
     * ou status 404 (Not Found) se o aluno não for encontrado.
     */
    @GetMapping("/aluno/{alunoId}")
    public ResponseEntity<List<Presenca>> buscarPresencasPorAluno(@PathVariable Long alunoId) {
        try {
            List<Presenca> presencas = presencaService.buscarPresencasPorAluno(alunoId);
            return new ResponseEntity<>(presencas, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint para buscar registros de presença de um aluno em um período específico.
     * Exemplo de requisição: GET /api/presencas/aluno/{alunoId}/periodo?startDate=2023-01-01&endDate=2023-01-31
     *
     * @param alunoId O ID do aluno.
     * @param startDate A data de início do período (formato YYYY-MM-DD).
     * @param endDate A data de fim do período (formato YYYY-MM-DD).
     * @return ResponseEntity com uma lista de objetos Presenca e status HTTP 200 (OK),
     * ou status 404 (Not Found) se o aluno não for encontrado.
     */
    @GetMapping("/aluno/{alunoId}/periodo")
    public ResponseEntity<List<Presenca>> buscarPresencasPorAlunoEPeriodo(
            @PathVariable Long alunoId,
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate) {
        try {
            List<Presenca> presencas = presencaService.buscarPresencasPorAlunoEPeriodo(alunoId, startDate, endDate);
            return new ResponseEntity<>(presencas, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint para verificar se um aluno esteve presente em uma data específica.
     * Exemplo de requisição: GET /api/presencas/verificar/{alunoId}/{date}
     *
     * @param alunoId O ID do aluno.
     * @param date A data a ser verificada (formato YYYY-MM-DD).
     * @return ResponseEntity com um Boolean indicando a presença e status HTTP 200 (OK),
     * ou status 404 (Not Found) se o aluno não for encontrado.
     */
    @GetMapping("/verificar/{alunoId}/{date}")
    public ResponseEntity<Boolean> verificarPresencaNoDia(
            @PathVariable Long alunoId,
            @PathVariable LocalDate date) {
        try {
            boolean presente = presencaService.verificarPresencaNoDia(alunoId, date);
            return new ResponseEntity<>(presente, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Endpoint para buscar todos os registros de presença no sistema.
     * Exemplo de requisição: GET /api/presencas
     *
     * @return ResponseEntity com uma lista de todos os objetos Presenca e status HTTP 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<Presenca>> buscarTodasPresencas() {
        List<Presenca> presencas = presencaService.buscarTodasPresencas();
        return new ResponseEntity<>(presencas, HttpStatus.OK);
    }

    /**
     * Endpoint para deletar um registro de presença pelo seu ID.
     * Exemplo de requisição: DELETE /api/presencas/{presencaId}
     *
     * @param presencaId O ID do registro de presença a ser deletado.
     * @return ResponseEntity com status HTTP 204 (No Content) se a exclusão for bem-sucedida,
     * ou status 404 (Not Found) se o registro não for encontrado.
     */
    @DeleteMapping("/{presencaId}")
    public ResponseEntity<Void> deletarPresenca(@PathVariable Long presencaId) {
        try {
            presencaService.deletarPresenca(presencaId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Retorna 204 No Content
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Classe auxiliar para o corpo da requisição POST de marcar presença com data e hora específicas.
     */
    @Data // Lombok para getters e setters
    @NoArgsConstructor
    @AllArgsConstructor
    static class MarcarPresencaRequest {
        private Long alunoId;
        private String dataHora; // String para facilitar o parsing de LocalDateTime
    }
}