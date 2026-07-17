package com.plataformacurso.inscripciones_service.repository;

import com.plataformacurso.inscripciones_service.model.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {
    List<Inscripcion> findByUsuarioId(Long usuarioId);
    List<Inscripcion> findByCursoId(Long cursoId);
}