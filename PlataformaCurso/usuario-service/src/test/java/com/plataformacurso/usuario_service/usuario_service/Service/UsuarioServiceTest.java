package com.plataformacurso.usuario_service.usuario_service.Service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.plataformacurso.usuario_service.Service.UsuarioService;
import com.plataformacurso.usuario_service.model.Usuario;
import com.plataformacurso.usuario_service.repository.UsuarioRepository;
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
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    @DisplayName("Debería guardar un usuario exitosamente")
    void guardarUsuario() {
        Usuario usuarioParaGuardar = new Usuario();
        Usuario usuarioGuardado = new Usuario();

        when(usuarioRepository.save(usuarioParaGuardar)).thenReturn(usuarioGuardado);

        Usuario resultado = usuarioService.guardarUsuario(usuarioParaGuardar);

        assertNotNull(resultado, "El usuario retornado no debería ser nulo");
        verify(usuarioRepository, times(1)).save(usuarioParaGuardar);
    }

    @Test
    @DisplayName("Debería retornar la lista de todos los usuarios")
    void obtenerTodos() {
        Usuario u1 = new Usuario();
        Usuario u2 = new Usuario();
        List<Usuario> listaSimulada = Arrays.asList(u1, u2);

        when(usuarioRepository.findAll()).thenReturn(listaSimulada);

        List<Usuario> resultado = usuarioService.obtenerTodos();

        assertNotNull(resultado);
        assertEquals(2, resultado.size(), "La lista debería contener exactamente 2 usuarios");
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería retornar un Optional con el usuario si el ID existe")
    void obtenerPorId_CuandoExiste() {
        Long idBuscado = 1L;
        Usuario usuarioSimulado = new Usuario();

        when(usuarioRepository.findById(idBuscado)).thenReturn(Optional.of(usuarioSimulado));

        Optional<Usuario> resultado = usuarioService.obtenerPorId(idBuscado);

        assertTrue(resultado.isPresent(), "El Optional debería contener un usuario");
        verify(usuarioRepository, times(1)).findById(idBuscado);
    }

    @Test
    @DisplayName("Debería retornar un Optional vacío si el ID no existe")
    void obtenerPorId_CuandoNoExiste() {
        Long idInexistente = 99L;

        when(usuarioRepository.findById(idInexistente)).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioService.obtenerPorId(idInexistente);

        assertTrue(resultado.isEmpty(), "El Optional debería estar vacío para un ID inexistente");
        verify(usuarioRepository, times(1)).findById(idInexistente);
    }
}