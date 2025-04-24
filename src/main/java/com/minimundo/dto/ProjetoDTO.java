package com.minimundo.dto;

import com.minimundo.model.StatusProjeto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjetoDTO {
    private Long id;
    private String nome;
    private String descricao;
    private StatusProjeto status;
    private BigDecimal orcamento;
    private LocalDateTime dataFim;
    private List<TarefaDTO> tarefas;
    private Long usuarioId;
} 