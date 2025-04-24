package com.minimundo.service;

import com.minimundo.dto.ProjetoDTO;
import com.minimundo.dto.ProjetoProgressoDTO;
import com.minimundo.exception.BusinessException;
import com.minimundo.model.Projeto;
import com.minimundo.model.Role;
import com.minimundo.model.StatusTarefa;
import com.minimundo.model.Usuario;
import com.minimundo.repository.ProjetoRepository;
import com.minimundo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public ProjetoDTO criar(ProjetoDTO dto, Long usuarioId) {
        validarDuplicidadeNome(dto.getNome(), usuarioId);
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        if (usuario.getRole() == Role.USER) {
            throw new BusinessException("Apenas gerentes e administradores podem criar projetos");
        }

        Projeto projeto = Projeto.builder()
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .status(dto.getStatus())
                .orcamento(dto.getOrcamento())
                .dataFim(dto.getDataFim())
                .usuario(usuario)
                .build();

        projeto = projetoRepository.save(projeto);
        return converterParaDTO(projeto);
    }

    @Transactional(readOnly = true)
    public List<ProjetoDTO> listarPorUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        if (usuario.getRole() == Role.ADMIN) {
            return projetoRepository.findAll()
                    .stream()
                    .map(this::converterParaDTO)
                    .collect(Collectors.toList());
        } else if (usuario.getRole() == Role.MANAGER) {
            return projetoRepository.findAll()
                    .stream()
                    .map(this::converterParaDTO)
                    .collect(Collectors.toList());
        } else {
            return projetoRepository.findByUsuarioId(usuarioId)
                    .stream()
                    .map(this::converterParaDTO)
                    .collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    public List<ProjetoDTO> pesquisarPorNome(String nome, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        if (usuario.getRole() == Role.ADMIN || usuario.getRole() == Role.MANAGER) {
            return projetoRepository.findByNomeContainingIgnoreCaseAndUsuarioId(nome, usuarioId)
                    .stream()
                    .map(this::converterParaDTO)
                    .collect(Collectors.toList());
        } else {
            return projetoRepository.findByUsuarioIdAndNomeContainingIgnoreCase(usuarioId, nome)
                    .stream()
                    .map(this::converterParaDTO)
                    .collect(Collectors.toList());
        }
    }

    @Transactional(readOnly = true)
    public ProjetoDTO buscarPorId(Long id, Long usuarioId) {
        Projeto projeto = projetoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Projeto não encontrado"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        if (usuario.getRole() == Role.USER && !projeto.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("Acesso não autorizado");
        }

        return converterParaDTO(projeto);
    }

    @Transactional(readOnly = true)
    public ProjetoProgressoDTO calcularProgresso(Long id, Long usuarioId) {
        Projeto projeto = projetoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Projeto não encontrado"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        if (usuario.getRole() != Role.ADMIN && !projeto.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("Acesso não autorizado");
        }
        
        int totalTarefas = projeto.getTarefas().size();
        int tarefasConcluidas = (int) projeto.getTarefas().stream()
                .filter(tarefa -> tarefa.getStatus() == StatusTarefa.CONCLUIDA)
                .count();
        int tarefasNaoConcluidas = totalTarefas - tarefasConcluidas;
        double percentualConcluido = totalTarefas > 0 ? (double) tarefasConcluidas / totalTarefas * 100 : 0;
        
        return ProjetoProgressoDTO.builder()
                .projetoId(projeto.getId())
                .nomeProjeto(projeto.getNome())
                .totalTarefas(totalTarefas)
                .tarefasConcluidas(tarefasConcluidas)
                .tarefasNaoConcluidas(tarefasNaoConcluidas)
                .percentualConcluido(percentualConcluido)
                .build();
    }

    @Transactional
    public ProjetoDTO atualizar(Long id, ProjetoDTO dto, Long usuarioId) {
        Projeto projeto = projetoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Projeto não encontrado"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        if (usuario.getRole() == Role.USER) {
            throw new BusinessException("Apenas gerentes e administradores podem atualizar projetos");
        }

        if (usuario.getRole() == Role.MANAGER && !projeto.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("Gerentes só podem atualizar seus próprios projetos");
        }

        if (!projeto.getNome().equals(dto.getNome()) && projetoRepository.existsByNome(dto.getNome())) {
            throw new BusinessException("Já existe um projeto com este nome");
        }

        projeto.setNome(dto.getNome());
        projeto.setDescricao(dto.getDescricao());
        projeto.setStatus(dto.getStatus());
        projeto.setOrcamento(dto.getOrcamento());
        projeto.setDataFim(dto.getDataFim());

        projeto = projetoRepository.save(projeto);
        return converterParaDTO(projeto);
    }

    @Transactional
    public void excluir(Long id, Long usuarioId) {
        Projeto projeto = projetoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Projeto não encontrado"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        if (usuario.getRole() == Role.USER) {
            throw new BusinessException("Apenas gerentes e administradores podem excluir projetos");
        }

        if (usuario.getRole() == Role.MANAGER) {
            if (!projeto.getUsuario().getId().equals(usuarioId)) {
                throw new BusinessException("Gerentes só podem excluir seus próprios projetos");
            }
            if (!projeto.getTarefas().isEmpty()) {
                throw new BusinessException("Não é possível excluir um projeto com tarefas associadas");
            }
        }

        projetoRepository.delete(projeto);
    }

    private ProjetoDTO converterParaDTO(Projeto projeto) {
        return ProjetoDTO.builder()
                .id(projeto.getId())
                .nome(projeto.getNome())
                .descricao(projeto.getDescricao())
                .status(projeto.getStatus())
                .orcamento(projeto.getOrcamento())
                .dataFim(projeto.getDataFim())
                .usuarioId(projeto.getUsuario().getId())
                .build();
    }

    private void validarDuplicidadeNome(String nome, Long usuarioId) {
        if (projetoRepository.existsByNome(nome)) {
            throw new BusinessException("Já existe um projeto com este nome");
        }
    }
} 