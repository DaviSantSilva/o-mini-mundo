package com.minimundo.repository;

import com.minimundo.model.Projeto;
import com.minimundo.model.StatusProjeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjetoRepository extends JpaRepository<Projeto, Long> {
    List<Projeto> findByUsuarioId(Long usuarioId);
    List<Projeto> findByStatus(StatusProjeto status);
    boolean existsByNome(String nome);
    
    @Query("SELECT p FROM Projeto p WHERE p.usuario.id = :usuarioId AND LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Projeto> findByUsuarioIdAndNomeContainingIgnoreCase(@Param("usuarioId") Long usuarioId, @Param("nome") String nome);

    List<Projeto> findByNomeContainingIgnoreCaseAndUsuarioId(String nome, Long usuarioId);
    Optional<Projeto> findByIdAndUsuarioId(Long id, Long usuarioId);
    boolean existsByNomeAndUsuarioId(String nome, Long usuarioId);
} 