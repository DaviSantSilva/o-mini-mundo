package com.minimundo.dto;

import com.minimundo.model.StatusTarefa;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TarefaDTO {
    private Long id;
    private String descricao;
    private Long projetoId;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private Long tarefaPredecessoraId;
    private StatusTarefa status;
    private Long usuarioId;
} 