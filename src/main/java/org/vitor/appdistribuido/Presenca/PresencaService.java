package org.vitor.appdistribuido.Presenca;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vitor.appdistribuido.Alunos.Alunos;
import org.vitor.appdistribuido.Alunos.AlunosRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Serviço responsável pela lógica de negócios relacionada à presença dos alunos.
 * Gerencia operações como marcar presença, buscar registros de presença e verificar status.
 */
@Service
public class PresencaService {

    private final PresencaRepository presencaRepository;
    private final AlunosRepository alunosRepository; // Usado para buscar informações do aluno

    /**
     * Construtor para injeção de dependências.
     * @param presencaRepository O repositório para operações de presença.
     * @param alunosRepository O repositório para operações de aluno.
     */
    @Autowired
    public PresencaService(PresencaRepository presencaRepository, AlunosRepository alunosRepository) {
        this.presencaRepository = presencaRepository;
        this.alunosRepository = alunosRepository;
    }

    /**
     * Marca a presença de um aluno no momento atual.
     *
     * @param alunoId O ID do aluno a ser marcado como presente.
     * @return O objeto Presenca salvo.
     * @throws NoSuchElementException se o aluno com o ID fornecido não for encontrado.
     */
    public Presenca marcarPresenca(Long alunoId) {
        // Busca o aluno pelo ID, lançando uma exceção se não for encontrado
        Alunos aluno = alunosRepository.findById(alunoId)
                .orElseThrow(() -> new NoSuchElementException("Aluno não encontrado com ID: " + alunoId));

        // Cria um novo registro de presença com a data/hora atual e status 'presente'
        Presenca presenca = new Presenca(aluno, LocalDateTime.now(), true);
        return presencaRepository.save(presenca); // Salva o registro no banco de dados
    }

    /**
     * Marca a presença de um aluno em uma data e hora específicas.
     *
     * @param alunoId O ID do aluno a ser marcado como presente.
     * @param dataHora A data e hora específicas para o registro de presença.
     * @return O objeto Presenca salvo.
     * @throws NoSuchElementException se o aluno com o ID fornecido não for encontrado.
     */
    public Presenca marcarPresenca(Long alunoId, LocalDateTime dataHora) {
        // Busca o aluno pelo ID
        Alunos aluno = alunosRepository.findById(alunoId)
                .orElseThrow(() -> new NoSuchElementException("Aluno não encontrado com ID: " + alunoId));

        // Cria um novo registro de presença com a data/hora fornecida
        Presenca presenca = new Presenca(aluno, dataHora, true);
        return presencaRepository.save(presenca); // Salva o registro
    }

    /**
     * Busca todos os registros de presença para um aluno específico.
     *
     * @param alunoId O ID do aluno.
     * @return Uma lista de objetos Presenca para o aluno dado.
     * @throws NoSuchElementException se o aluno com o ID fornecido não for encontrado.
     */
    public List<Presenca> buscarPresencasPorAluno(Long alunoId) {
        // Busca o aluno pelo ID
        Alunos aluno = alunosRepository.findById(alunoId)
                .orElseThrow(() -> new NoSuchElementException("Aluno não encontrado com ID: " + alunoId));
        return presencaRepository.findByAluno(aluno); // Retorna as presenças encontradas
    }

    /**
     * Busca registros de presença para um aluno específico dentro de um intervalo de datas.
     *
     * @param alunoId O ID do aluno.
     * @param startDate A data de início do intervalo (inclusive).
     * @param endDate A data de fim do intervalo (inclusive).
     * @return Uma lista de objetos Presenca para o aluno no período especificado.
     * @throws NoSuchElementException se o aluno com o ID fornecido não for encontrado.
     */
    public List<Presenca> buscarPresencasPorAlunoEPeriodo(Long alunoId, LocalDate startDate, LocalDate endDate) {
        // Busca o aluno pelo ID
        Alunos aluno = alunosRepository.findById(alunoId)
                .orElseThrow(() -> new NoSuchElementException("Aluno não encontrado com ID: " + alunoId));

        // Converte as datas para LocalDateTime para usar no repositório
        LocalDateTime startDateTime = startDate.atStartOfDay(); // Início do dia
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX); // Fim do dia

        return presencaRepository.findByAlunoAndDataHoraBetween(aluno, startDateTime, endDateTime);
    }

    /**
     * Verifica se um aluno esteve presente em uma data específica.
     * Assume que "presente" significa que há pelo menos um registro de presença para aquele dia.
     *
     * @param alunoId O ID do aluno.
     * @param date A data a ser verificada.
     * @return True se o aluno esteve presente na data fornecida, false caso contrário.
     * @throws NoSuchElementException se o aluno com o ID fornecido não for encontrado.
     */
    public boolean verificarPresencaNoDia(Long alunoId, LocalDate date) {
        // Busca o aluno pelo ID
        Alunos aluno = alunosRepository.findById(alunoId)
                .orElseThrow(() -> new NoSuchElementException("Aluno não encontrado com ID: " + alunoId));

        // Define o intervalo de data e hora para o dia completo
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        // Busca presenças dentro do dia e verifica se a lista não está vazia
        List<Presenca> presencas = presencaRepository.findByAlunoAndDataHoraBetween(aluno, startOfDay, endOfDay);
        return !presencas.isEmpty();
    }

    /**
     * Busca todos os registros de presença existentes no sistema.
     *
     * @return Uma lista de todos os objetos Presenca.
     */
    public List<Presenca> buscarTodasPresencas() {
        return presencaRepository.findAll();
    }

    /**
     * Deleta um registro de presença pelo seu ID.
     *
     * @param presencaId O ID do registro de presença a ser deletado.
     * @throws NoSuchElementException se o registro de presença com o ID fornecido não for encontrado.
     */
    public void deletarPresenca(Long presencaId) {
        // Busca o registro de presença pelo ID, lançando uma exceção se não for encontrado
        Presenca presenca = presencaRepository.findById(presencaId)
                .orElseThrow(() -> new NoSuchElementException("Registro de presença não encontrado com ID: " + presencaId));
        presencaRepository.delete(presenca); // Deleta o registro
    }
}