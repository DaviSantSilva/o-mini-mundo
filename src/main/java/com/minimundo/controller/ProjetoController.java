package com.minimundo.controller;

import com.minimundo.dto.ProjetoDTO;
import com.minimundo.dto.ProjetoProgressoDTO;
import com.minimundo.model.Usuario;
import com.minimundo.service.ProjetoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projetos")
@RequiredArgsConstructor
public class ProjetoController {

    private final ProjetoService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ProjetoDTO> criar(
            @RequestBody ProjetoDTO dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(service.criar(dto, ((Usuario) userDetails).getId()));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProjetoDTO>> listar(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(service.listarPorUsuario(((Usuario) userDetails).getId()));
    }

    @GetMapping("/pesquisar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProjetoDTO>> pesquisarPorNome(
            @RequestParam String nome,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(service.pesquisarPorNome(nome, ((Usuario) userDetails).getId()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProjetoDTO> buscarPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(service.buscarPorId(id, ((Usuario) userDetails).getId()));
    }

    @GetMapping("/{id}/progresso")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProjetoProgressoDTO> calcularProgresso(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(service.calcularProgresso(id, ((Usuario) userDetails).getId()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ProjetoDTO> atualizar(
            @PathVariable Long id,
            @RequestBody ProjetoDTO dto,
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