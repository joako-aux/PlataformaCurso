package com.plataformacurso.auth_service;

import com.plataformacurso.auth_service.Config.JwtUtil;
import com.plataformacurso.auth_service.Controller.AuthController;
import com.plataformacurso.auth_service.Model.AuthResponse;
import com.plataformacurso.auth_service.Model.LoginRequest;
import com.plataformacurso.auth_service.Model.RegisterRequest;
import com.plataformacurso.auth_service.Service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceApplicationTests {

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private JwtUtil jwtUtil;

	@Mock
	private AuthService authService;

	@InjectMocks
	private AuthController authController;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void login_DeberiaRetornarToken_CuandoCredencialesSeanCorrectas() {
		LoginRequest request = new LoginRequest("joaquin.perez", "Password123!");
		Authentication authentication = mock(Authentication.class);

		doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
				.when(authentication).getAuthorities();

		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
				.thenReturn(authentication);
		when(jwtUtil.generateToken("joaquin.perez", "ROLE_USER")).thenReturn("mocked-jwt-token-login");

		ResponseEntity<?> response = authController.login(request);

		assertEquals(200, response.getStatusCode().value());
		assertNotNull(response.getBody());
		assertTrue(response.getBody() instanceof AuthResponse);

		AuthResponse authResponse = (AuthResponse) response.getBody();
		assertEquals("mocked-jwt-token-login", authResponse.getAccessToken());
	}

	@Test
	void login_DeberiaRetornar401_CuandoCredencialesSeanIncorrectas() {
		LoginRequest request = new LoginRequest("usuario.incorrecto", "ClaveInvalida");

		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
				.thenThrow(new BadCredentialsException("Bad credentials"));

		ResponseEntity<?> response = authController.login(request);

		assertEquals(401, response.getStatusCode().value());
		assertNotNull(response.getBody());
		assertTrue(response.getBody().toString().contains("Credenciales incorrectas"));
	}

	@Test
	void register_DeberiaGuardarUsuarioYRetornarTokenAutomatico_CuandoRegistroSeaExitoso() {
		RegisterRequest request = new RegisterRequest("moise.fonseca", "SecurePass456!", "ROLE_USER");

		doNothing().when(authService).registrarUsuario(any(RegisterRequest.class));
		when(jwtUtil.generateToken("moise.fonseca", "ROLE_USER")).thenReturn("token-automatico-registro");

		ResponseEntity<AuthResponse> response = authController.register(request);

		assertEquals(200, response.getStatusCode().value());
		assertNotNull(response.getBody());
		assertEquals("token-automatico-registro", response.getBody().getAccessToken());

		verify(authService, times(1)).registrarUsuario(request);
	}
}