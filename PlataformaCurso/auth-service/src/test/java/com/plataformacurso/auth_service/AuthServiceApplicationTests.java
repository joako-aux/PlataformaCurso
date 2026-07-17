package com.plataformacurso.auth_service;

import com.plataformacurso.auth_service.Model.AuthResponse;
import com.plataformacurso.auth_service.Model.LoginRequest;
import com.plataformacurso.auth_service.Model.Usuario;
import com.plataformacurso.auth_service.Repository.UsuarioRepository;
import com.plataformacurso.auth_service.Service.AuthService;
import com.plataformacurso.auth_service.Service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class AuthServiceApplicationTests {

	@Mock
	private UsuarioRepository usuarioRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtService jwtService;

	@InjectMocks
	private AuthService authService;

	private LoginRequest loginRequest;
	private Usuario usuarioMock;

	@BeforeEach
	void setUp() {
		// Inicializamos los datos compartidos de prueba
		loginRequest = new LoginRequest();
		loginRequest.setEmail("moises@correo.com");
		loginRequest.setPassword("Password123");

		usuarioMock = Usuario.builder()
				.email("moises@correo.com")
				.password("passwordEncriptado123") // Simula la clave ya cifrada en la BD
				.build();
	}

	@Test
	@DisplayName("Debería iniciar sesión exitosamente cuando las credenciales son correctas")
	void loginExitoso() {
		log.info("Ejecutando test: loginExitoso");

		// GIVEN
		when(usuarioRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(usuarioMock));
		when(passwordEncoder.matches(loginRequest.getPassword(), usuarioMock.getPassword())).thenReturn(true);
		// Usamos any() genérico para evitar conflictos con la firma de java.util.Map en Mockito
		when(jwtService.generateToken(anyString(), any())).thenReturn("jwt.token.valido");

		// WHEN
		AuthResponse response = authService.login(loginRequest);

		// THEN
		assertNotNull(response);
		assertEquals("jwt.token.valido", response.getToken());

		// Verificar que los mocks se llamaron el número correcto de veces
		verify(usuarioRepository, times(1)).findByEmail(loginRequest.getEmail());
		verify(passwordEncoder, times(1)).matches(loginRequest.getPassword(), usuarioMock.getPassword());
		verify(jwtService, times(1)).generateToken(anyString(), any());
	}

	@Test
	@DisplayName("Debería lanzar excepción cuando el email no existe en la base de datos")
	void loginEmailNoEncontrado() {
		log.info("Ejecutando test: loginEmailNoEncontrado");

		// GIVEN
		when(usuarioRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

		// WHEN & THEN
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			authService.login(loginRequest);
		});

		assertEquals("Email no encontrado", exception.getMessage());

		// Verificaciones de flujo
		verify(usuarioRepository, times(1)).findByEmail(loginRequest.getEmail());
		verifyNoInteractions(passwordEncoder);
		verifyNoInteractions(jwtService);
	}
}