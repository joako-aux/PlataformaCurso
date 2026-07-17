package com.plataformacurso.historial_service.controller;

import com.plataformacurso.historial_service.model.Historial;
import com.plataformacurso.historial_service.service.HistorialService;
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

    @PostMapping
    public ResponseEntity<Historial> crear(@RequestBody Historial historial) {
        Historial nuevoHistorial = historialService.registrarHistorial(historial);
        return new ResponseEntity<>(nuevoHistorial, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Historial>> obtenerTodos() {
        return ResponseEntity.ok(historialService.obtenerTodos());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Historial>> buscarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(historialService.buscarPorUsuario(usuarioId));
    }

    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<List<Historial>> buscarPorCurso(@PathVariable Long cursoId) {
        return ResponseEntity.ok(historialService.buscarPorCurso(cursoId));
    }
}