package com.minimundo.service;

import com.minimundo.dto.TarefaDTO;
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

    private final TarefaRepository tarefaRepository;
    private final ProjetoRepository projetoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public TarefaDTO criar(TarefaDTO dto, Long usuarioId) {
        Projeto projeto = projetoRepository.findById(dto.getProjetoId())
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        if (!projeto.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Acesso não autorizado");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (dto.getDataFim() != null && dto.getDataInicio() != null && dto.getDataFim().isBefore(dto.getDataInicio())) {
            throw new RuntimeException("A data de fim não pode ser anterior à data de início");
        }

        Tarefa tarefa = Tarefa.builder()
                .descricao(dto.getDescricao())
                .projeto(projeto)
                .dataInicio(dto.getDataInicio())
                .dataFim(dto.getDataFim())
                .status(dto.getStatus())
                .usuario(usuario)
                .build();

        if (dto.getTarefaPredecessoraId() != null) {
            Tarefa predecessora = tarefaRepository.findById(dto.getTarefaPredecessoraId())
                    .orElseThrow(() -> new RuntimeException("Tarefa predecessora não encontrada"));
            tarefa.setTarefaPredecessora(predecessora);
        }

        tarefa = tarefaRepository.save(tarefa);
        return converterParaDTO(tarefa);
    }

    @Transactional(readOnly = true)
    public List<TarefaDTO> listarPorProjeto(Long projetoId, Long usuarioId) {
        Projeto projeto = projetoRepository.findById(projetoId)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        if (!projeto.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Acesso não autorizado");
        }

        return tarefaRepository.findByProjetoId(projetoId)
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<TarefaDTO> pesquisarPorDescricao(String descricao, Long usuarioId) {
        return tarefaRepository.findByUsuarioIdAndDescricaoContainingIgnoreCase(usuarioId, descricao)
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<TarefaDTO> listarPorProjetoEStatus(Long projetoId, StatusTarefa status, Long usuarioId) {
        Projeto projeto = projetoRepository.findById(projetoId)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        if (!projeto.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Acesso não autorizado");
        }

        return tarefaRepository.findByUsuarioIdAndProjetoIdAndStatus(usuarioId, projetoId, status)
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TarefaDTO buscarPorId(Long id, Long usuarioId) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));

        if (!tarefa.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Acesso não autorizado");
        }

        return converterParaDTO(tarefa);
    }

    @Transactional
    public TarefaDTO atualizar(Long id, TarefaDTO dto, Long usuarioId) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));

        if (!tarefa.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Acesso não autorizado");
        }

        if (dto.getDataFim() != null && dto.getDataInicio() != null && dto.getDataFim().isBefore(dto.getDataInicio())) {
            throw new RuntimeException("A data de fim não pode ser anterior à data de início");
        }

        tarefa.setDescricao(dto.getDescricao());
        tarefa.setDataInicio(dto.getDataInicio());
        tarefa.setDataFim(dto.getDataFim());
        tarefa.setStatus(dto.getStatus());

        if (dto.getTarefaPredecessoraId() != null) {
            Tarefa predecessora = tarefaRepository.findById(dto.getTarefaPredecessoraId())
                    .orElseThrow(() -> new RuntimeException("Tarefa predecessora não encontrada"));
            tarefa.setTarefaPredecessora(predecessora);
        }

        tarefa = tarefaRepository.save(tarefa);
        return converterParaDTO(tarefa);
    }

    @Transactional
    public void excluir(Long id, Long usuarioId) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada"));

        if (!tarefa.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Acesso não autorizado");
        }

        if (tarefaRepository.existsByTarefaPredecessoraId(id)) {
            throw new RuntimeException("Não é possível excluir uma tarefa que é predecessora de outra");
        }

        tarefaRepository.delete(tarefa);
    }

    private TarefaDTO converterParaDTO(Tarefa tarefa) {
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