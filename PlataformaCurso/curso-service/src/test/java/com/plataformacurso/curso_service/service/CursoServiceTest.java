package com.plataformacurso.curso_service.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.plataformacurso.curso_service.model.Curso;
import com.plataformacurso.curso_service.repository.CursoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CursoServiceTest {

    @Mock
    private CursoRepository cursoRepository;

    @InjectMocks
    private CursoService cursoService;

    @Test
    @DisplayName("Debería retornar todos los cursos disponibles")
    void obtenerTodos() {
        // Arrange
        List<Curso> cursosSimulados = Arrays.asList(new Curso(), new Curso());
        when(cursoRepository.findAll()).thenReturn(cursosSimulados);

        // Act
        List<Curso> resultado = cursoService.obtenerTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(cursoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería retornar un Optional con el curso si el ID existe")
    void obtenerPorId_CuandoExiste() {
        // Arrange
        Long idBuscado = 1L;
        Curso cursoSimulado = new Curso();
        when(cursoRepository.findById(idBuscado)).thenReturn(Optional.of(cursoSimulado));

        // Act
        Optional<Curso> resultado = cursoService.obtenerPorId(idBuscado);

        // Assert
        assertTrue(resultado.isPresent());
        verify(cursoRepository, times(1)).findById(idBuscado);
    }

    @Test
    @DisplayName("Debería inicializar los cupos disponibles con el cupo máximo al crear un curso nuevo")
    void guardar_NuevoCurso() {
        // Arrange
        Curso cursoNuevo = new Curso();
        cursoNuevo.setId(null); // Es nuevo
        cursoNuevo.setCupoMaximo(30);
        cursoNuevo.setCuposDisponibles(null);

        when(cursoRepository.save(any(Curso.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Curso resultado = cursoService.guardar(cursoNuevo);

        // Assert
        assertNotNull(resultado);
        assertEquals(30, resultado.getCuposDisponibles(), "Los cupos disponibles deberían igualar al cupo máximo");
        verify(cursoRepository, times(1)).save(cursoNuevo);
    }

    @Test
    @DisplayName("Debería actualizar todas las propiedades de un curso existente")
    void actualizar_CuandoExiste() {
        // Arrange
        Long idExistente = 1L;

        Curso cursoEnBD = new Curso();
        cursoEnBD.setId(idExistente);
        cursoEnBD.setTitulo("Java Viejo");

        Curso detallesNuevos = new Curso();
        detallesNuevos.setTitulo("Java Moderno");
        detallesNuevos.setDescripcion("Curso actualizado");
        detallesNuevos.setCupoMaximo(40);
        detallesNuevos.setCuposDisponibles(40);

        when(cursoRepository.findById(idExistente)).thenReturn(Optional.of(cursoEnBD));
        when(cursoRepository.save(any(Curso.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Curso resultado = cursoService.actualizar(idExistente, detallesNuevos);

        // Assert
        assertNotNull(resultado);
        assertEquals("Java Moderno", resultado.getTitulo());
        assertEquals("Curso actualizado", resultado.getDescripcion());
        assertEquals(40, resultado.getCupoMaximo());
        verify(cursoRepository, times(1)).findById(idExistente);
        verify(cursoRepository, times(1)).save(cursoEnBD);
    }

    @Test
    @DisplayName("Debería lanzar RuntimeException al intentar actualizar un curso inexistente")
    void actualizar_CuandoNoExiste_LanzaExcepcion() {
        // Arrange
        Long idInexistente = 99L;
        Curso detalles = new Curso();
        when(cursoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            cursoService.actualizar(idInexistente, detalles);
        });

        assertEquals("Curso no encontrado con el id: " + idInexistente, excepcion.getMessage());
        verify(cursoRepository, never()).save(any(Curso.class));
    }

    @Test
    @DisplayName("Debería eliminar un curso correctamente si el ID existe")
    void eliminar_CuandoExiste() {
        // Arrange
        Long idExistente = 1L;
        Curso cursoSimulado = new Curso();
        when(cursoRepository.findById(idExistente)).thenReturn(Optional.of(cursoSimulado));
        doNothing().when(cursoRepository).delete(cursoSimulado);

        // Act
        assertDoesNotThrow(() -> cursoService.eliminar(idExistente));

        // Assert
        verify(cursoRepository, times(1)).findById(idExistente);
        verify(cursoRepository, times(1)).delete(cursoSimulado);
    }

    @Test
    @DisplayName("Debería lanzar RuntimeException al intentar eliminar un curso que no existe")
    void eliminar_CuandoNoExiste_LanzaExcepcion() {
        // Arrange
        Long idInexistente = 99L;
        when(cursoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            cursoService.eliminar(idInexistente);
        });

        assertEquals("Curso no encontrado con el id: " + idInexistente, excepcion.getMessage());
        verify(cursoRepository, never()).delete(any(Curso.class));
    }
}