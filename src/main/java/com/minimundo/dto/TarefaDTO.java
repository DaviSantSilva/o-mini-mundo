package com.minimundo.dto;

import com.minimundo.model.Tarefa;
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

    public static TarefaDTO fromEntity(Tarefa tarefa) {
        return TarefaDTO.builder()
                .id(tarefa.getId())
                .descricao(tarefa.getDescricao())
                .projetoId(tarefa.getProjeto().getId())
                .dataInicio(tarefa.getDataInicio())
                .dataFim(tarefa.getDataFim())
                .tarefaPredecessoraId(tarefa.getTarefaPredecessora() != null ? tarefa.getTarefaPredecessora().getId() : null)
                .status(tarefa.getStatus())
                .usuarioId(tarefa.getUsuario().getId())
                .build();
    }
} 