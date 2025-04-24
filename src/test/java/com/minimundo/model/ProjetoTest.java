package com.minimundo.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;

class ProjetoTest {

    @Test
    void testCriarProjeto() {
        Projeto projeto = new Projeto();
        projeto.setId(1L);
        projeto.setNome("Projeto Teste");
        projeto.setDescricao("Descrição do projeto teste");
        projeto.setStatus(StatusProjeto.ATIVO);
        projeto.setOrcamento(new BigDecimal("10000.00"));

        assertEquals(1L, projeto.getId());
        assertEquals("Projeto Teste", projeto.getNome());
        assertEquals("Descrição do projeto teste", projeto.getDescricao());
        assertEquals(StatusProjeto.ATIVO, projeto.getStatus());
        assertEquals(new BigDecimal("10000.00"), projeto.getOrcamento());
    }

    @Test
    void testAdicionarTarefa() {
        Projeto projeto = new Projeto();
        Tarefa tarefa = new Tarefa();
        tarefa.setId(1L);
        tarefa.setDescricao("Tarefa Teste");

        projeto.getTarefas().add(tarefa);

        assertTrue(projeto.getTarefas().contains(tarefa));
        assertEquals(1, projeto.getTarefas().size());
    }

    @Test
    void testRemoverTarefa() {
        Projeto projeto = new Projeto();
        Tarefa tarefa = new Tarefa();
        tarefa.setId(1L);
        tarefa.setDescricao("Tarefa Teste");

        projeto.getTarefas().add(tarefa);
        projeto.getTarefas().remove(tarefa);

        assertFalse(projeto.getTarefas().contains(tarefa));
        assertEquals(0, projeto.getTarefas().size());
    }
} 