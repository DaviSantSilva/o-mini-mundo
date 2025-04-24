package com.minimundo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimundo.config.SecurityTestConfig;
import com.minimundo.dto.ProjetoDTO;
import com.minimundo.dto.ProjetoProgressoDTO;
import com.minimundo.model.Projeto;
import com.minimundo.model.StatusProjeto;
import com.minimundo.model.Usuario;
import com.minimundo.security.JwtAuthenticationFilter;
import com.minimundo.security.JwtService;
import com.minimundo.security.CustomUserDetailsService;
import com.minimundo.service.ProjetoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjetoController.class)
@Import({SecurityTestConfig.class, JwtService.class, JwtAuthenticationFilter.class, CustomUserDetailsService.class})
@ActiveProfiles("test")
class ProjetoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProjetoService projetoService;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private Projeto projeto;
    private ProjetoDTO projetoDTO;
    private Usuario usuario;
    private ProjetoProgressoDTO progressoDTO;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Usuário Teste");
        usuario.setEmail("teste@email.com");

        projeto = new Projeto();
        projeto.setId(1L);
        projeto.setNome("Projeto Teste");
        projeto.setDescricao("Descrição do projeto teste");
        projeto.setStatus(StatusProjeto.ATIVO);
        projeto.setOrcamento(new BigDecimal("10000.00"));
        projeto.setUsuario(usuario);

        projetoDTO = new ProjetoDTO();
        projetoDTO.setNome("Projeto Teste");
        projetoDTO.setDescricao("Descrição do projeto teste");
        projetoDTO.setStatus(StatusProjeto.ATIVO);
        projetoDTO.setOrcamento(new BigDecimal("10000.00"));

        progressoDTO = ProjetoProgressoDTO.builder()
                .projetoId(1L)
                .nomeProjeto("Projeto Teste")
                .totalTarefas(10)
                .tarefasConcluidas(5)
                .tarefasNaoConcluidas(5)
                .percentualConcluido(50.0)
                .build();
    }

    @Test
    @WithMockUser
    void testCriarProjeto() throws Exception {
        when(projetoService.criar(any(ProjetoDTO.class), anyLong()))
            .thenReturn(projetoDTO);

        mockMvc.perform(post("/projetos")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(projetoDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(projeto.getId()))
                .andExpect(jsonPath("$.nome").value(projeto.getNome()));
    }

    @Test
    @WithMockUser
    void testListarProjetos() throws Exception {
        when(projetoService.listarPorUsuario(anyLong()))
            .thenReturn(Arrays.asList(projetoDTO));

        mockMvc.perform(get("/projetos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(projeto.getId()))
                .andExpect(jsonPath("$[0].nome").value(projeto.getNome()));
    }

    @Test
    @WithMockUser
    void testBuscarProjetoPorId() throws Exception {
        when(projetoService.buscarPorId(anyLong(), anyLong()))
            .thenReturn(projetoDTO);

        mockMvc.perform(get("/projetos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projeto.getId()))
                .andExpect(jsonPath("$.nome").value(projeto.getNome()));
    }

    @Test
    @WithMockUser
    void testAtualizarProjeto() throws Exception {
        when(projetoService.atualizar(anyLong(), any(ProjetoDTO.class), anyLong()))
            .thenReturn(projetoDTO);

        mockMvc.perform(put("/projetos/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(projetoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projeto.getId()))
                .andExpect(jsonPath("$.nome").value(projeto.getNome()));
    }

    @Test
    @WithMockUser
    void testExcluirProjeto() throws Exception {
        mockMvc.perform(delete("/projetos/1")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void testCalcularProgresso() throws Exception {
        when(projetoService.calcularProgresso(anyLong(), anyLong()))
            .thenReturn(progressoDTO);

        mockMvc.perform(get("/projetos/1/progresso"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projetoId").value(progressoDTO.getProjetoId()))
                .andExpect(jsonPath("$.percentualConcluido").value(progressoDTO.getPercentualConcluido()));
    }
} 