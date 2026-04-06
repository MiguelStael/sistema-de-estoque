package com.estoque.sistema.dto;

import java.time.LocalDateTime;

public record TurnoResponseDTO(
        Long id,
        String responsavel,
        LocalDateTime dataAbertura,
        LocalDateTime dataFechamento,
        Boolean aberto,
        Long duracaoMinutos
) {}
