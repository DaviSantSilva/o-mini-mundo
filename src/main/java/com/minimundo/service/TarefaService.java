package com.minimundo.service;

import com.minimundo.dto.TarefaDTO;
import com.minimundo.exception.BusinessException;
import com.minimundo.model.Projeto;
import com.minimundo.model.Tarefa;
import com.minimundo.model.StatusTarefa;
import com.minimundo.model.Usuario;
import com.minimundo.repository.ProjetoRepository;
import com.minimundo.repository.TarefaRepository;
import com.minimundo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TarefaService {

    private final TarefaRepository repository;
    private final ProjetoRepository projetoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public TarefaDTO criar(TarefaDTO dto, Long usuarioId) {
        validarDatas(dto.getDataInicio(), dto.getDataFim());
        
        var projeto = projetoRepository.findByIdAndUsuarioId(dto.getProjetoId(), usuarioId)
                .orElseThrow(() -> new BusinessException("Projeto não encontrado"));
        
        var tarefa = Tarefa.builder()
                .descricao(dto.getDescricao())
                .projeto(projeto)
                .dataInicio(dto.getDataInicio())
                .dataFim(dto.getDataFim())
                .tarefaPredecessora(dto.getTarefaPredecessoraId() != null ? 
                        repository.findById(dto.getTarefaPredecessoraId())
                                .orElseThrow(() -> new BusinessException("Tarefa predecessora não encontrada"))
                        : null)
                .status(dto.getStatus())
                .usuario(Usuario.builder().id(usuarioId).build())
                .build();
        
        return TarefaDTO.fromEntity(repository.save(tarefa));
    }

    public List<TarefaDTO> listarPorProjeto(Long projetoId, Long usuarioId) {
        return repository.findByProjetoIdAndUsuarioId(projetoId, usuarioId)
                .stream()
                .map(TarefaDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TarefaDTO> pesquisarPorDescricao(String descricao, Long usuarioId) {
        return repository.findByDescricaoContainingIgnoreCaseAndUsuarioId(descricao, usuarioId)
                .stream()
                .map(TarefaDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TarefaDTO> listarPorProjetoEStatus(Long projetoId, StatusTarefa status, Long usuarioId) {
        return repository.findByProjetoIdAndStatusAndUsuarioId(projetoId, status, usuarioId)
                .stream()
                .map(TarefaDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public TarefaDTO buscarPorId(Long id, Long usuarioId) {
        return repository.findByIdAndUsuarioId(id, usuarioId)
                .map(TarefaDTO::fromEntity)
                .orElseThrow(() -> new BusinessException("Tarefa não encontrada"));
    }

    @Transactional
    public TarefaDTO atualizar(Long id, TarefaDTO dto, Long usuarioId) {
        validarDatas(dto.getDataInicio(), dto.getDataFim());
        
        var tarefa = repository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new BusinessException("Tarefa não encontrada"));
        
        tarefa.setDescricao(dto.getDescricao());
        tarefa.setDataInicio(dto.getDataInicio());
        tarefa.setDataFim(dto.getDataFim());
        tarefa.setStatus(dto.getStatus());
        
        if (dto.getTarefaPredecessoraId() != null) {
            tarefa.setTarefaPredecessora(repository.findById(dto.getTarefaPredecessoraId())
                    .orElseThrow(() -> new BusinessException("Tarefa predecessora não encontrada")));
        } else {
            tarefa.setTarefaPredecessora(null);
        }
        
        return TarefaDTO.fromEntity(repository.save(tarefa));
    }

    @Transactional
    public void excluir(Long id, Long usuarioId) {
        var tarefa = repository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new BusinessException("Tarefa não encontrada"));
        
        if (!tarefa.getTarefasDependentes().isEmpty()) {
            throw new BusinessException("Não é possível excluir a tarefa pois existem tarefas dependentes");
        }
        
        repository.delete(tarefa);
    }

    private void validarDatas(LocalDateTime dataInicio, LocalDateTime dataFim) {
        if (dataInicio != null && dataFim != null && dataFim.isBefore(dataInicio)) {
            throw new BusinessException("A data de fim não pode ser anterior à data de início");
        }
    }
} 