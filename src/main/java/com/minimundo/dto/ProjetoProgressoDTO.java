package com.minimundo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjetoProgressoDTO {
    private Long projetoId;
    private String nomeProjeto;
    private int totalTarefas;
    private int tarefasConcluidas;
    private int tarefasNaoConcluidas;
    private double percentualConcluido;
} 