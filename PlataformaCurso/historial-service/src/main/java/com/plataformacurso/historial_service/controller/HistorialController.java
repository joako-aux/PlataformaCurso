package com.plataformacurso.historial_service.controller;

import com.plataformacurso.historial_service.model.Historial;
import com.plataformacurso.historial_service.service.HistorialService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/historial")
public class HistorialController {

    @Autowired
    private HistorialService historialService;

    // POST /historial (Validación activada)
    @PostMapping
    public ResponseEntity<Historial> crear(@Valid @RequestBody Historial historial) {
        // Spring validará las notas (0-10), la fecha (pasada/presente) y el estado antes de entrar aquí.
        Historial nuevoHistorial = historialService.registrarHistorial(historial);
        return new ResponseEntity<>(nuevoHistorial, HttpStatus.CREATED);
    }

    // GET /historial
    @GetMapping
    public ResponseEntity<List<Historial>> obtenerTodos() {
        return ResponseEntity.ok(historialService.obtenerTodos());
    }

    // GET /historial/usuario/{usuarioId}
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Historial>> buscarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(historialService.buscarPorUsuario(usuarioId));
    }

    // GET /historial/curso/{cursoId}
    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<List<Historial>> buscarPorCurso(@PathVariable Long cursoId) {
        return ResponseEntity.ok(historialService.buscarPorCurso(cursoId));
    }
}