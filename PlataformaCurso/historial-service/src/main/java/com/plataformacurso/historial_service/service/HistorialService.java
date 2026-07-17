package com.plataformacurso.historial_service.service;

import com.plataformacurso.historial_service.model.Historial;
import com.plataformacurso.historial_service.repository.HistorialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class HistorialService {

    @Autowired
    private HistorialRepository historialRepository;

    public Historial registrarHistorial(Historial historial) {
        if (historial.getFechaFinalizacion() == null) {
            historial.setFechaFinalizacion(LocalDate.now());
        }
        return historialRepository.save(historial);
    }

    public List<Historial> obtenerTodos() {
        return historialRepository.findAll();
    }

    public List<Historial> buscarPorUsuario(Long usuarioId) {
        return historialRepository.findByUsuarioId(usuarioId);
    }

    public List<Historial> buscarPorCurso(Long cursoId) {
        return historialRepository.findByCursoId(cursoId);
    }
}