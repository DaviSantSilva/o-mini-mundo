package com.minimundo.controller;

import com.minimundo.dto.TarefaDTO;
import com.minimundo.model.StatusTarefa;
import com.minimundo.model.Usuario;
import com.minimundo.service.TarefaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tarefas")
@RequiredArgsConstructor
public class TarefaController {

    private final TarefaService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<TarefaDTO> criar(
            @RequestBody TarefaDTO dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(service.criar(dto, ((Usuario) userDetails).getId()));
    }

    @GetMapping("/projeto/{projetoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TarefaDTO>> listarPorProjeto(
            @PathVariable Long projetoId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(service.listarPorProjeto(projetoId, ((Usuario) userDetails).getId()));
    }
    
    @GetMapping("/pesquisar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TarefaDTO>> pesquisarPorDescricao(
            @RequestParam String descricao,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(service.pesquisarPorDescricao(descricao, ((Usuario) userDetails).getId()));
    }
    
    @GetMapping("/projeto/{projetoId}/status/{status}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TarefaDTO>> listarPorProjetoEStatus(
            @PathVariable Long projetoId,
            @PathVariable StatusTarefa status,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(service.listarPorProjetoEStatus(projetoId, status, ((Usuario) userDetails).getId()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TarefaDTO> buscarPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(service.buscarPorId(id, ((Usuario) userDetails).getId()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<TarefaDTO> atualizar(
            @PathVariable Long id,
            @RequestBody TarefaDTO dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(service.atualizar(id, dto, ((Usuario) userDetails).getId()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        service.excluir(id, ((Usuario) userDetails).getId());
        return ResponseEntity.noContent().build();
    }
} 