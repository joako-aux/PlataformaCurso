package com.plataformacurso.auth_service.service;

import com.plataformacurso.auth_service.entity.UserAuth;
import com.plataformacurso.auth_service.repository.UserAuthRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserAuthRepository userAuthRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private String email;
    private String rawPassword;
    private String encodedPassword;
    private UserAuth usuarioEjemplo;

    @BeforeEach
    void setUp() {
        email = "test@correo.com";
        rawPassword = "password123";
        encodedPassword = "$2a$10$encodedPasswordHash";
        usuarioEjemplo = new UserAuth(email, encodedPassword);
    }

    @Nested
    @DisplayName("Pruebas para registrarUsuario")
    class RegistrarUsuarioTests {

        @Test
        @DisplayName("Debe registrar exitosamente cuando el email no existe")
        void registrarUsuarioExito() {
            // Arrange (Configurar comportamiento simulado)
            when(userAuthRepository.findByEmail(email)).thenReturn(Optional.empty());
            when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
            when(userAuthRepository.save(any(UserAuth.class))).thenReturn(usuarioEjemplo);

            // Act (Ejecutar el método)
            String resultado = authService.registrarUsuario(email, rawPassword);

            // Assert (Verificar resultados)
            assertEquals("Usuario registrado exitosamente", resultado);
            verify(userAuthRepository, times(1)).findByEmail(email);
            verify(passwordEncoder, times(1)).encode(rawPassword);
            verify(userAuthRepository, times(1)).save(any(UserAuth.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción si el email ya se encuentra registrado")
        void registrarUsuarioEmailYaExiste() {
            // Arrange
            when(userAuthRepository.findByEmail(email)).thenReturn(Optional.of(usuarioEjemplo));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                authService.registrarUsuario(email, rawPassword);
            });

            assertEquals("El correo ya está registrado", exception.getMessage());
            // Verificamos que el flujo se cortó y nunca guardó ni encriptó nada
            verify(passwordEncoder, never()).encode(anyString());
            verify(userAuthRepository, never()).save(any(UserAuth.class));
        }
    }

    @Nested
    @DisplayName("Pruebas para loginYGenerarToken")
    class LoginYGenerarTokenTests {

        @Test
        @DisplayName("Debe retornar un JWT válido cuando las credenciales son correctas")
        void loginExito() {
            String tokenEsperado = "jwt.token.valido";

            // Arrange
            when(userAuthRepository.findByEmail(email)).thenReturn(Optional.of(usuarioEjemplo));
            when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
            when(jwtService.generateToken(email)).thenReturn(tokenEsperado);

            // Act
            String tokenObtenido = authService.loginYGenerarToken(email, rawPassword);

            // Assert
            assertEquals(tokenEsperado, tokenObtenido);
            verify(jwtService, times(1)).generateToken(email);
        }

        @Test
        @DisplayName("Debe lanzar excepción si el usuario no existe en el repositorio")
        void loginUsuarioNoEncontrado() {
            // Arrange
            when(userAuthRepository.findByEmail(email)).thenReturn(Optional.empty());

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                authService.loginYGenerarToken(email, rawPassword);
            });

            assertEquals("Usuario no encontrado", exception.getMessage());
            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(jwtService, never()).generateToken(anyString());
        }

        @Test
        @DisplayName("Debe lanzar excepción si la contraseña es incorrecta")
        void loginContrasenaIncorrecta() {
            // Arrange
            when(userAuthRepository.findByEmail(email)).thenReturn(Optional.of(usuarioEjemplo));
            when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                authService.loginYGenerarToken(email, rawPassword);
            });

            assertEquals("Contraseña incorrecta", exception.getMessage());
            verify(jwtService, never()).generateToken(anyString());
        }
    }
}