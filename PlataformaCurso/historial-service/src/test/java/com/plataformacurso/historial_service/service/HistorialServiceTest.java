package com.plataformacurso.historial_service.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.plataformacurso.historial_service.model.Historial;
import com.plataformacurso.historial_service.repository.HistorialRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class HistorialServiceTest {

    @Mock
    private HistorialRepository historialRepository;

    @InjectMocks
    private HistorialService historialService;

    @Test
    @DisplayName("Debería asignar la fecha actual si la fecha de finalización viene nula al registrar")
    void registrarHistorial_FechaPorDefecto() {
        // Arrange
        Historial historialInput = new Historial();
        historialInput.setFechaFinalizacion(null);

        // Usamos thenAnswer para devolver el objeto modificado internamente por el servicio
        when(historialRepository.save(any(Historial.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Historial resultado = historialService.registrarHistorial(historialInput);

        // Assert
        assertNotNull(resultado);
        assertEquals(LocalDate.now(), resultado.getFechaFinalizacion(), "La fecha de finalización debería ser la de hoy");
        verify(historialRepository, times(1)).save(historialInput);
    }

    @Test
    @DisplayName("Debería mantener la fecha de finalización si ya viene informada al registrar")
    void registrarHistorial_ConFechaExistente() {
        // Arrange
        LocalDate fechaPrevia = LocalDate.of(2026, 5, 20);
        Historial historialInput = new Historial();
        historialInput.setFechaFinalizacion(fechaPrevia);

        when(historialRepository.save(any(Historial.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Historial resultado = historialService.registrarHistorial(historialInput);

        // Assert
        assertNotNull(resultado);
        assertEquals(fechaPrevia, resultado.getFechaFinalizacion(), "Debería respetar la fecha original proporcionada");
        verify(historialRepository, times(1)).save(historialInput);
    }

    @Test
    @DisplayName("Debería retornar todo el historial completo")
    void obtenerTodos() {
        // Arrange
        List<Historial> listaSimulada = Arrays.asList(new Historial(), new Historial());
        when(historialRepository.findAll()).thenReturn(listaSimulada);

        // Act
        List<Historial> resultado = historialService.obtenerTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size(), "Debería retornar exactamente 2 registros de historial");
        verify(historialRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería buscar registros de historial pertenecientes a un usuario específico")
    void buscarPorUsuario() {
        // Arrange
        Long usuarioId = 1L;
        List<Historial> listaSimulada = Arrays.asList(new Historial());
        when(historialRepository.findByUsuarioId(usuarioId)).thenReturn(listaSimulada);

        // Act
        List<Historial> resultado = historialService.buscarPorUsuario(usuarioId);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(historialRepository, times(1)).findByUsuarioId(usuarioId);
    }

    @Test
    @DisplayName("Debería buscar registros de historial pertenecientes a un curso específico")
    void buscarPorCurso() {
        // Arrange
        Long cursoId = 5L;
        List<Historial> listaSimulada = Arrays.asList(new Historial(), new Historial());
        when(historialRepository.findByCursoId(cursoId)).thenReturn(listaSimulada);

        // Act
        List<Historial> resultado = historialService.buscarPorCurso(cursoId);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(historialRepository, times(1)).findByCursoId(cursoId);
    }
}