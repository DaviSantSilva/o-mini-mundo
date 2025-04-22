package com.minimundo.repository;

import com.minimundo.model.Tarefa;
import com.minimundo.model.StatusTarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
    List<Tarefa> findByProjetoId(Long projetoId);
    List<Tarefa> findByProjetoIdAndStatus(Long projetoId, StatusTarefa status);
    List<Tarefa> findByTarefaPredecessoraId(Long tarefaPredecessoraId);
    boolean existsByTarefaPredecessoraId(Long tarefaPredecessoraId);
    
    @Query("SELECT t FROM Tarefa t WHERE t.usuario.id = :usuarioId AND LOWER(t.descricao) LIKE LOWER(CONCAT('%', :descricao, '%'))")
    List<Tarefa> findByUsuarioIdAndDescricaoContainingIgnoreCase(@Param("usuarioId") Long usuarioId, @Param("descricao") String descricao);
    
    @Query("SELECT t FROM Tarefa t WHERE t.usuario.id = :usuarioId AND t.projeto.id = :projetoId AND t.status = :status")
    List<Tarefa> findByUsuarioIdAndProjetoIdAndStatus(@Param("usuarioId") Long usuarioId, @Param("projetoId") Long projetoId, @Param("status") StatusTarefa status);
} 