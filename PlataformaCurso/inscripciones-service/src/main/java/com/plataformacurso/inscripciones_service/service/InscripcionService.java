package com.plataformacurso.inscripciones_service.service;

import com.plataformacurso.inscripciones_service.model.Inscripcion;
import com.plataformacurso.inscripciones_service.repository.InscripcionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class InscripcionService {

    @Autowired
    private InscripcionRepository inscripcionRepository;

    public Inscripcion registrar(Inscripcion inscripcion) {
        if (inscripcion.getFechaInscripcion() == null) {
            inscripcion.setFechaInscripcion(LocalDate.now());
        }
        if (inscripcion.getEstado() == null) {
            inscripcion.setEstado("ACTIVA");
        }
        return inscripcionRepository.save(inscripcion);
    }

    public List<Inscripcion> buscarPorUsuario(Long usuarioId) {
        return inscripcionRepository.findByUsuarioId(usuarioId);
    }

    public List<Inscripcion> buscarPorCurso(Long cursoId) {
        return inscripcionRepository.findByCursoId(cursoId);
    }

    public void eliminar(Long id) {
        Inscripcion inscripcion = inscripcionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inscripción no encontrada con id: " + id));
        inscripcionRepository.delete(inscripcion);
    }
}