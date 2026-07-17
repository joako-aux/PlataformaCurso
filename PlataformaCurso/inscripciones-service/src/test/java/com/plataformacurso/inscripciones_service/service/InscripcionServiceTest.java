package com.plataformacurso.inscripciones_service.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.plataformacurso.inscripciones_service.model.Inscripcion;
import com.plataformacurso.inscripciones_service.repository.InscripcionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class InscripcionServiceTest {

    @Mock
    private InscripcionRepository inscripcionRepository;

    @InjectMocks
    private InscripcionService inscripcionService;

    @Test
    @DisplayName("Debería asignar fecha actual y estado ACTIVA si vienen nulos al registrar")
    void registrar_ValoresPorDefecto() {
        // Arrange
        Inscripcion inscripcionInput = new Inscripcion();
        inscripcionInput.setFechaInscripcion(null);
        inscripcionInput.setEstado(null);

        when(inscripcionRepository.save(any(Inscripcion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Inscripcion resultado = inscripcionService.registrar(inscripcionInput);

        // Assert
        assertNotNull(resultado);
        assertEquals(LocalDate.now(), resultado.getFechaInscripcion(), "La fecha debería ser la de hoy");
        assertEquals("ACTIVA", resultado.getEstado(), "El estado por defecto debería ser ACTIVA");
        verify(inscripcionRepository, times(1)).save(inscripcionInput);
    }

    @Test
    @DisplayName("Debería buscar inscripciones por el ID del usuario")
    void buscarPorUsuario() {
        // Arrange
        Long usuarioId = 1L;
        List<Inscripcion> listaSimulada = Arrays.asList(new Inscripcion(), new Inscripcion());
        when(inscripcionRepository.findByUsuarioId(usuarioId)).thenReturn(listaSimulada);

        // Act
        List<Inscripcion> resultado = inscripcionService.buscarPorUsuario(usuarioId);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(inscripcionRepository, times(1)).findByUsuarioId(usuarioId);
    }

    @Test
    @DisplayName("Debería buscar inscripciones por el ID del curso")
    void buscarPorCurso() {
        // Arrange
        Long cursoId = 5L;
        List<Inscripcion> listaSimulada = Arrays.asList(new Inscripcion());
        when(inscripcionRepository.findByCursoId(cursoId)).thenReturn(listaSimulada);

        // Act
        List<Inscripcion> resultado = inscripcionService.buscarPorCurso(cursoId);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(inscripcionRepository, times(1)).findByCursoId(cursoId);
    }

    @Test
    @DisplayName("Debería eliminar la inscripción exitosamente si el ID existe")
    void eliminar_CuandoExiste() {
        // Arrange
        Long idExistente = 10L;
        Inscripcion inscripcionSimulada = new Inscripcion();

        when(inscripcionRepository.findById(idExistente)).thenReturn(Optional.of(inscripcionSimulada));
        doNothing().when(inscripcionRepository).delete(inscripcionSimulada); // DoNothing se usa para métodos 'void'

        // Act
        assertDoesNotThrow(() -> inscripcionService.eliminar(idExistente));

        // Assert
        verify(inscripcionRepository, times(1)).findById(idExistente);
        verify(inscripcionRepository, times(1)).delete(inscripcionSimulada);
    }

    @Test
    @DisplayName("Debería lanzar RuntimeException al intentar eliminar un ID que no existe")
    void eliminar_CuandoNoExiste_LanzaExcepcion() {
        // Arrange
        Long idInexistente = 99L;
        when(inscripcionRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            inscripcionService.eliminar(idInexistente);
        });

        assertEquals("Inscripción no encontrada con id: " + idInexistente, excepcion.getMessage());

        // Verificamos que buscó el id pero NUNCA llamó al método delete porque se interrumpió antes
        verify(inscripcionRepository, times(1)).findById(idInexistente);
        verify(inscripcionRepository, never()).delete(any(Inscripcion.class));
    }
}