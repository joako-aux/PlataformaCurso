package com.plataformacurso.inscripciones_service.controller;

import com.plataformacurso.inscripciones_service.model.Inscripcion;
import com.plataformacurso.inscripciones_service.service.InscripcionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inscripciones")
public class InscripcionController {

    @Autowired
    private InscripcionService inscripcionService;

    // POST /inscripciones
    @PostMapping
    public ResponseEntity<Inscripcion> crearInscripcion(@RequestBody Inscripcion inscripcion) {
        Inscripcion nuevaInscripcion = inscripcionService.registrar(inscripcion);
        return new ResponseEntity<>(nuevaInscripcion, HttpStatus.CREATED);
    }

    // GET /inscripciones/usuario/{id}
    @GetMapping("/usuario/{id}")
    public ResponseEntity<List<Inscripcion>> listarPorUsuario(@PathVariable("id") Long usuarioId) {
        return ResponseEntity.ok(inscripcionService.buscarPorUsuario(usuarioId));
    }

    // GET /inscripciones/curso/{id}
    @GetMapping("/curso/{id}")
    public ResponseEntity<List<Inscripcion>> listarPorCurso(@PathVariable("id") Long cursoId) {
        return ResponseEntity.ok(inscripcionService.buscarPorCurso(cursoId));
    }

    // DELETE /inscripciones/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarInscripcion(@PathVariable Long id) {
        try {
            inscripcionService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
