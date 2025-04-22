package com.minimundo.service;

import com.minimundo.dto.ProjetoDTO;
import com.minimundo.dto.ProjetoProgressoDTO;
import com.minimundo.model.Projeto;
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
        if (projetoRepository.existsByNome(dto.getNome())) {
            throw new RuntimeException("Já existe um projeto com este nome");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Projeto projeto = Projeto.builder()
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .status(dto.getStatus())
                .orcamento(dto.getOrcamento())
                .usuario(usuario)
                .build();

        projeto = projetoRepository.save(projeto);
        return converterParaDTO(projeto);
    }

    @Transactional(readOnly = true)
    public List<ProjetoDTO> listarPorUsuario(Long usuarioId) {
        return projetoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProjetoDTO> pesquisarPorNome(String nome, Long usuarioId) {
        return projetoRepository.findByUsuarioIdAndNomeContainingIgnoreCase(usuarioId, nome)
                .stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProjetoDTO buscarPorId(Long id, Long usuarioId) {
        Projeto projeto = projetoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        if (!projeto.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Acesso não autorizado");
        }

        return converterParaDTO(projeto);
    }

    @Transactional(readOnly = true)
    public ProjetoProgressoDTO calcularProgresso(Long id, Long usuarioId) {
        Projeto projeto = projetoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        if (!projeto.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Acesso não autorizado");
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
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        if (!projeto.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Acesso não autorizado");
        }

        if (!projeto.getNome().equals(dto.getNome()) && projetoRepository.existsByNome(dto.getNome())) {
            throw new RuntimeException("Já existe um projeto com este nome");
        }

        projeto.setNome(dto.getNome());
        projeto.setDescricao(dto.getDescricao());
        projeto.setStatus(dto.getStatus());
        projeto.setOrcamento(dto.getOrcamento());

        projeto = projetoRepository.save(projeto);
        return converterParaDTO(projeto);
    }

    @Transactional
    public void excluir(Long id, Long usuarioId) {
        Projeto projeto = projetoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Projeto não encontrado"));

        if (!projeto.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Acesso não autorizado");
        }

        if (!projeto.getTarefas().isEmpty()) {
            throw new RuntimeException("Não é possível excluir um projeto com tarefas associadas");
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
                .usuarioId(projeto.getUsuario().getId())
                .build();
    }
} 