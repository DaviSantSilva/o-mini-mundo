package com.minimundo.service;

import com.minimundo.dto.ProjetoDTO;
import com.minimundo.exception.BusinessException;
import com.minimundo.model.Projeto;
import com.minimundo.model.StatusProjeto;
import com.minimundo.model.Usuario;
import com.minimundo.repository.ProjetoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjetoServiceTest {

    @Mock
    private ProjetoRepository projetoRepository;

    @InjectMocks
    private ProjetoService projetoService;

    private Usuario usuario;
    private Projeto projeto;
    private ProjetoDTO projetoDTO;

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
    }

    @Test
    void testCriarProjeto() {
        when(projetoRepository.existsByNomeAndUsuarioId(anyString(), anyLong())).thenReturn(false);
        when(projetoRepository.save(any(Projeto.class))).thenReturn(projeto);

        Projeto resultado = projetoService.criarProjeto(projetoDTO, usuario);

        assertNotNull(resultado);
        assertEquals(projetoDTO.getNome(), resultado.getNome());
        assertEquals(usuario, resultado.getUsuario());
        verify(projetoRepository).save(any(Projeto.class));
    }

    @Test
    void testCriarProjetoComNomeDuplicado() {
        when(projetoRepository.existsByNomeAndUsuarioId(anyString(), anyLong())).thenReturn(true);

        assertThrows(BusinessException.class, () -> {
            projetoService.criarProjeto(projetoDTO, usuario);
        });
    }

    @Test
    void testListarProjetos() {
        when(projetoRepository.findByUsuarioId(anyLong())).thenReturn(Arrays.asList(projeto));

        List<Projeto> resultados = projetoService.listarProjetos(usuario.getId());

        assertNotNull(resultados);
        assertEquals(1, resultados.size());
        assertEquals(projeto.getNome(), resultados.get(0).getNome());
    }

    @Test
    void testBuscarProjetoPorId() {
        when(projetoRepository.findById(anyLong())).thenReturn(Optional.of(projeto));

        Projeto resultado = projetoService.buscarProjetoPorId(1L, usuario.getId());

        assertNotNull(resultado);
        assertEquals(projeto.getId(), resultado.getId());
        assertEquals(projeto.getNome(), resultado.getNome());
    }

    @Test
    void testBuscarProjetoNaoEncontrado() {
        when(projetoRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> {
            projetoService.buscarProjetoPorId(1L, usuario.getId());
        });
    }

    @Test
    void testAtualizarProjeto() {
        when(projetoRepository.findById(anyLong())).thenReturn(Optional.of(projeto));
        when(projetoRepository.save(any(Projeto.class))).thenReturn(projeto);

        Projeto resultado = projetoService.atualizarProjeto(1L, projetoDTO, usuario.getId());

        assertNotNull(resultado);
        assertEquals(projetoDTO.getNome(), resultado.getNome());
        assertEquals(projetoDTO.getDescricao(), resultado.getDescricao());
    }

    @Test
    void testExcluirProjeto() {
        when(projetoRepository.findById(anyLong())).thenReturn(Optional.of(projeto));
        when(projetoRepository.existsById(anyLong())).thenReturn(true);

        projetoService.excluirProjeto(1L, usuario.getId());

        verify(projetoRepository).deleteById(1L);
    }

    @Test
    void testExcluirProjetoComTarefas() {
        projeto.getTarefas().add(new Tarefa());
        when(projetoRepository.findById(anyLong())).thenReturn(Optional.of(projeto));

        assertThrows(BusinessException.class, () -> {
            projetoService.excluirProjeto(1L, usuario.getId());
        });
    }
} 