package com.plataformacurso.curso_service.controller;

import com.plataformacurso.curso_service.model.Curso;
import com.plataformacurso.curso_service.service.CursoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cursos")
public class CursoController {

    @Autowired
    private CursoService cursoService;

    // GET /cursos
    @GetMapping
    public ResponseEntity<List<Curso>> listarCursos() {
        return ResponseEntity.ok(cursoService.obtenerTodos());
    }

    // GET /cursos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Curso> obtenerCurso(@PathVariable Long id) {
        return cursoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /cursos (Validación activada para creación)
    @PostMapping
    public ResponseEntity<Curso> crearCurso(@Valid @RequestBody Curso curso) {
        // Ejecuta todas las validaciones del modelo, incluyendo el método custom isFechaFinValida()
        Curso nuevoCurso = cursoService.guardar(curso);
        return new ResponseEntity<>(nuevoCurso, HttpStatus.CREATED);
    }

    // PUT /cursos/{id} (Validación activada para actualización)
    @PutMapping("/{id}")
    public ResponseEntity<Curso> actualizarCurso(@PathVariable Long id, @Valid @RequestBody Curso cursoDetalles) {
        // Al agregar @Valid aquí, evitas que en una actualización dañen la coherencia de los datos
        Curso cursoActualizado = cursoService.actualizar(id, cursoDetalles);
        return ResponseEntity.ok(cursoActualizado);
    }

    // DELETE /cursos/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCurso(@PathVariable Long id) {
        cursoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}