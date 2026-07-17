package com.plataformacurso.curso_service.service;

import com.plataformacurso.curso_service.model.Curso;
import com.plataformacurso.curso_service.repository.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CursoService {

    @Autowired
    private CursoRepository cursoRepository;

    public List<Curso> obtenerTodos() {
        return cursoRepository.findAll();
    }

    public Optional<Curso> obtenerPorId(Long id) {
        return cursoRepository.findById(id);
    }

    public Curso guardar(Curso curso) {
        // Al crear un curso por primera vez, los cupos disponibles son iguales al cupo máximo
        if (curso.getId() == null) {
            curso.setCuposDisponibles(curso.getCupoMaximo());
        }
        return cursoRepository.save(curso);
    }

    public Curso actualizar(Long id, Curso cursoDetalles) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado con el id: " + id));

        curso.setTitulo(cursoDetalles.getTitulo());
        curso.setDescripcion(cursoDetalles.getDescripcion());
        curso.setCategoria(cursoDetalles.getCategoria());
        curso.setInstructor(cursoDetalles.getInstructor());
        curso.setFechaInicio(cursoDetalles.getFechaInicio());
        curso.setFechaFin(cursoDetalles.getFechaFin());
        curso.setCupoMaximo(cursoDetalles.getCupoMaximo());
        curso.setCuposDisponibles(cursoDetalles.getCuposDisponibles());
        curso.setModalidad(cursoDetalles.getModalidad());
        curso.setEstado(cursoDetalles.getEstado());

        return cursoRepository.save(curso);
    }

    public void eliminar(Long id) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado con el id: " + id));
        cursoRepository.delete(curso);
    }
}