package com.plataformacurso.historial_service.repository;

import com.plataformacurso.historial_service.model.Historial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HistorialRepository extends JpaRepository<Historial, Long> {

    // Método para buscar el historial de un usuario específico
    List<Historial> findByUsuarioId(Long usuarioId);

    // Método para ver quiénes han finalizado un curso específico
    List<Historial> findByCursoId(Long cursoId);
}