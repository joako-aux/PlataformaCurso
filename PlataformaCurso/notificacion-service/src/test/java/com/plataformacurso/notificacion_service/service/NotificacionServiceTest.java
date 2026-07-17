package com.plataformacurso.notificacion_service.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.plataformacurso.notificacion_service.model.Notificacion;
import com.plataformacurso.notificacion_service.repository.NotificacionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class NotificacionServiceTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @InjectMocks
    private NotificacionService notificacionService;

    @Test
    @DisplayName("Debería asignar valores por defecto (fecha y estado) al registrar si vienen nulos")
    void registrarNotificacion_ValoresPorDefecto() {
        // Arrange
        Notificacion notificacionInput = new Notificacion();
        notificacionInput.setFechaEnvio(null);
        notificacionInput.setEstado(null);

        // Simulamos que el repositorio devuelve el mismo objeto que recibe al guardar
        when(notificacionRepository.save(any(Notificacion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Notificacion resultado = notificacionService.registrarNotificacion(notificacionInput);

        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.getFechaEnvio(), "La fecha de envío no debería ser nula");
        assertEquals("ENVIADO", resultado.getEstado(), "El estado por defecto debería ser ENVIADO");
        verify(notificacionRepository, times(1)).save(notificacionInput);
    }

    @Test
    @DisplayName("Debería respetar la fecha y estado si ya vienen informados al registrar")
    void registrarNotificacion_ConValoresExistentes() {
        // Arrange
        LocalDateTime fechaPrevia = LocalDateTime.of(2026, 1, 1, 10, 0);
        Notificacion notificacionInput = new Notificacion();
        notificacionInput.setFechaEnvio(fechaPrevia);
        notificacionInput.setEstado("PENDIENTE");

        when(notificacionRepository.save(any(Notificacion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Notificacion resultado = notificacionService.registrarNotificacion(notificacionInput);

        // Assert
        assertEquals(fechaPrevia, resultado.getFechaEnvio(), "Debería mantener la fecha original");
        assertEquals("PENDIENTE", resultado.getEstado(), "Debería mantener el estado original");
        verify(notificacionRepository, times(1)).save(notificacionInput);
    }

    @Test
    @DisplayName("Debería obtener todas las notificaciones")
    void obtenerTodas() {
        // Arrange
        List<Notificacion> listaSimulada = Arrays.asList(new Notificacion(), new Notificacion());
        when(notificacionRepository.findAll()).thenReturn(listaSimulada);

        // Act
        List<Notificacion> resultado = notificacionService.obtenerTodas();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(notificacionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería obtener las notificaciones asignadas a un usuario específico")
    void obtenerPorUsuario() {
        // Arrange
        Long usuarioId = 10L;
        List<Notificacion> listaSimulada = Arrays.asList(new Notificacion());
        when(notificacionRepository.findByUsuarioId(usuarioId)).thenReturn(listaSimulada);

        // Act
        List<Notificacion> resultado = notificacionService.obtenerPorUsuario(usuarioId);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(notificacionRepository, times(1)).findByUsuarioId(usuarioId);
    }
}