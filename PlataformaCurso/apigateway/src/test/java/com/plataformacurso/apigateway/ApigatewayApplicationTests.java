package com.plataformacurso.apigateway;

import com.plataformacurso.apigateway.Filter.AuthenticationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class ApigatewayApplicationTests {

	private AuthenticationFilter authenticationFilter;
	private ServerWebExchange exchange;
	private GatewayFilterChain chain;
	private ServerHttpRequest request;
	private ServerHttpResponse response;
	private HttpHeaders headers;

	@BeforeEach
	void setUp() {
		authenticationFilter = new AuthenticationFilter();
		exchange = mock(ServerWebExchange.class);
		chain = mock(GatewayFilterChain.class);
		request = mock(ServerHttpRequest.class);
		response = mock(ServerHttpResponse.class);
		headers = new HttpHeaders();

		when(exchange.getRequest()).thenReturn(request);
		when(exchange.getResponse()).thenReturn(response);
		when(request.getHeaders()).thenReturn(headers);
		when(request.getMethod()).thenReturn(org.springframework.http.HttpMethod.GET);
		when(response.setComplete()).thenReturn(Mono.empty());
	}

	@Test
	void test1_RutaPublicaDeberiaPasarSinToken() {
		// Simula ruta pública de login
		when(request.getURI()).thenReturn(URI.create("http://localhost:8080/api/auth/login"));
		when(chain.filter(exchange)).thenReturn(Mono.empty());

		GatewayFilter filter = authenticationFilter.apply(new AuthenticationFilter.Config());
		Mono<Void> result = filter.filter(exchange, chain);

		assertNotNull(result);
		verify(chain, times(1)).filter(exchange);
	}

	@Test
	void test2_RutaProtegidaDeberiaRetornar401SiNoLlevaToken() {
		// Simula ruta protegida de cursos sin cabecera de Authorization
		when(request.getURI()).thenReturn(URI.create("http://localhost:8080/api/secured/cursos/listar"));

		GatewayFilter filter = authenticationFilter.apply(new AuthenticationFilter.Config());
		filter.filter(exchange, chain).block();

		verify(response, times(1)).setStatusCode(HttpStatus.UNAUTHORIZED);
	}

	@Test
	void test3_RutaProtegidaDeberiaRechazarTokenConFirmaInvalida() {
		// Simula ruta protegida mandando un token mal formado o inventado
		when(request.getURI()).thenReturn(URI.create("http://localhost:8080/api/secured/usuarios/perfil"));
		headers.add("Authorization", "Bearer token-falso-inventado-que-no-pasara-la-firma");

		GatewayFilter filter = authenticationFilter.apply(new AuthenticationFilter.Config());
		filter.filter(exchange, chain).block();

		verify(response, times(1)).setStatusCode(HttpStatus.UNAUTHORIZED);
	}
}