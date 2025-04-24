package com.minimundo.service;

import com.minimundo.dto.TarefaDTO;
import com.minimundo.exception.BusinessException;
import com.minimundo.model.*;
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
                .orElseThrow(() -> new BusinessException("Projeto não encontrado"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        if (usuario.getRole() == Role.USER && !projeto.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("Acesso não autorizado");
        }

        if (dto.getTarefaPredecessoraId() != null) {
            Tarefa tarefaPredecessora = tarefaRepository.findById(dto.getTarefaPredecessoraId())
                    .orElseThrow(() -> new BusinessException("Tarefa predecessora não encontrada"));
            
            if (!tarefaPredecessora.getProjeto().getId().equals(dto.getProjetoId())) {
                throw new BusinessException("A tarefa predecessora deve pertencer ao mesmo projeto");
            }
        }

        validarDatas(dto.getDataInicio(), dto.getDataFim(), projeto);

        Tarefa tarefa = Tarefa.builder()
                .descricao(dto.getDescricao())
                .projeto(projeto)
                .dataInicio(dto.getDataInicio())
                .dataFim(dto.getDataFim())
                .tarefaPredecessora(dto.getTarefaPredecessoraId() != null ? 
                    tarefaRepository.findById(dto.getTarefaPredecessoraId()).orElse(null) : null)
                .status(dto.getStatus())
                .usuario(usuario)
                .build();

        tarefa = tarefaRepository.save(tarefa);
        return converterParaDTO(tarefa);
    }

    @Transactional(readOnly = true)
    public List<TarefaDTO> listarPorProjeto(Long projetoId, Long usuarioId) {
        Projeto projeto = projetoRepository.findById(projetoId)
                .orElseThrow(() -> new BusinessException("Projeto não encontrado"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        if (usuario.getRole() == Role.USER && !projeto.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("Acesso não autorizado");
        }

        return tarefaRepository.findByProjetoId(projetoId)
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TarefaDTO> pesquisarPorDescricao(String descricao, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        if (usuario.getRole() == Role.ADMIN) {
            return tarefaRepository.findByDescricaoContainingIgnoreCase(descricao)
                    .stream()
                    .map(this::converterParaDTO)
                    .collect(Collectors.toList());
        } else {
            return tarefaRepository.findByDescricaoContainingIgnoreCaseAndUsuarioId(descricao, usuarioId)
                    .stream()
                    .map(this::converterParaDTO)
                    .collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    public List<TarefaDTO> listarPorProjetoEStatus(Long projetoId, StatusTarefa status, Long usuarioId) {
        Projeto projeto = projetoRepository.findById(projetoId)
                .orElseThrow(() -> new BusinessException("Projeto não encontrado"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        if (usuario.getRole() == Role.USER && !projeto.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("Acesso não autorizado");
        }

        return tarefaRepository.findByProjetoIdAndStatus(projetoId, status)
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TarefaDTO buscarPorId(Long id, Long usuarioId) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Tarefa não encontrada"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        if (usuario.getRole() == Role.USER && !tarefa.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("Acesso não autorizado");
        }

        return converterParaDTO(tarefa);
    }

    @Transactional
    public TarefaDTO atualizar(Long id, TarefaDTO dto, Long usuarioId) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Tarefa não encontrada"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        if (usuario.getRole() == Role.USER && !tarefa.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("Acesso não autorizado");
        }

        if (dto.getTarefaPredecessoraId() != null) {
            Tarefa tarefaPredecessora = tarefaRepository.findById(dto.getTarefaPredecessoraId())
                    .orElseThrow(() -> new BusinessException("Tarefa predecessora não encontrada"));
            
            if (!tarefaPredecessora.getProjeto().getId().equals(tarefa.getProjeto().getId())) {
                throw new BusinessException("A tarefa predecessora deve pertencer ao mesmo projeto");
            }
        }

        validarDatas(dto.getDataInicio(), dto.getDataFim(), tarefa.getProjeto());

        tarefa.setDescricao(dto.getDescricao());
        tarefa.setDataInicio(dto.getDataInicio());
        tarefa.setDataFim(dto.getDataFim());
        tarefa.setTarefaPredecessora(dto.getTarefaPredecessoraId() != null ? 
            tarefaRepository.findById(dto.getTarefaPredecessoraId()).orElse(null) : null);
        tarefa.setStatus(dto.getStatus());

        tarefa = tarefaRepository.save(tarefa);
        return converterParaDTO(tarefa);
    }

    @Transactional
    public void excluir(Long id, Long usuarioId) {
        Tarefa tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Tarefa não encontrada"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        if (usuario.getRole() == Role.USER && !tarefa.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("Acesso não autorizado");
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
                .status(tarefa.getStatus())
                .tarefaPredecessoraId(tarefa.getTarefaPredecessora() != null ? tarefa.getTarefaPredecessora().getId() : null)
                .build();
    }

    private void validarDatas(LocalDateTime dataInicio, LocalDateTime dataFim, Projeto projeto) {
        if (dataInicio != null && dataFim != null && dataFim.isBefore(dataInicio)) {
            throw new BusinessException("A data de fim não pode ser anterior à data de início");
        }

        if (projeto.getDataFim() != null && dataInicio != null && dataFim != null 
            && (dataInicio.isAfter(projeto.getDataFim()) || dataFim.isAfter(projeto.getDataFim()))) {
            throw new BusinessException("A data de início e de fim devem estar dentro do período do projeto");
        }
    }
} 