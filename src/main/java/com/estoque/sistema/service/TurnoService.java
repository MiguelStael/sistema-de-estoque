package com.estoque.sistema.service;

import com.estoque.sistema.dto.TurnoResponseDTO;
import com.estoque.sistema.exception.ResourceNotFoundException;
import com.estoque.sistema.model.Turno;
import com.estoque.sistema.repository.TurnoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class TurnoService {

    private final TurnoRepository turnoRepository;

    public TurnoService(TurnoRepository turnoRepository) {
        this.turnoRepository = turnoRepository;
    }

    @Transactional
    public TurnoResponseDTO abrirTurno(String responsavel) {
        if (turnoRepository.existsByAbertoTrue()) {
            throw new RuntimeException("Ja existe um turno em aberto. Feche o turno atual antes de abrir um novo.");
        }

        Turno turno = new Turno();
        turno.setResponsavel(responsavel);
        turno.setAberto(true);

        return mapToResponse(turnoRepository.save(turno));
    }

    @Transactional
    public TurnoResponseDTO fecharTurno() {
        Turno turno = turnoRepository.findFirstByAbertoTrue()
                .orElseThrow(() -> new ResourceNotFoundException("Nenhum turno em aberto no momento."));

        LocalDateTime agora = LocalDateTime.now();
        turno.setDataFechamento(agora);
        turno.setAberto(false);
        turno.setDuracaoMinutos(ChronoUnit.MINUTES.between(turno.getDataAbertura(), agora));

        return mapToResponse(turnoRepository.save(turno));
    }

    public TurnoResponseDTO buscarTurnoAtual() {
        return turnoRepository.findFirstByAbertoTrue()
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Nenhum turno em aberto no momento."));
    }

    public boolean isTurnoAberto() {
        return turnoRepository.existsByAbertoTrue();
    }

    private TurnoResponseDTO mapToResponse(Turno turno) {
        return new TurnoResponseDTO(
                turno.getId(),
                turno.getResponsavel(),
                turno.getDataAbertura(),
                turno.getDataFechamento(),
                turno.getAberto(),
                turno.getDuracaoMinutos()
        );
    }
}
