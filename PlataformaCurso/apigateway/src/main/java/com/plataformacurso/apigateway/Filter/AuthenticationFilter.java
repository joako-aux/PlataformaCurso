package com.plataformacurso.apigateway.Filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Predicate;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    public AuthenticationFilter() {
        super(Config.class);
    }

    public static class Config {
        // Configuraciones dinámicas si las requieres en el futuro
    }

    // 1. Definimos la LISTA BLANCA (Rutas que NO necesitan Token)
    private static final List<String> openApiEndpoints = List.of(
            "/api/auth/register",
            "/api/auth/login",
            "/v3/api-docs",
            "/swagger-ui"
    );

    // Predicado para verificar si la ruta actual requiere seguridad
    private final Predicate<ServerHttpRequest> isSecured = request ->
            openApiEndpoints.stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 2. Si la ruta está protegida, validamos el Token
            if (isSecured.test(request)) {
                // Obtenemos directamente el primer valor de la cabecera "Authorization"
                String authHeader = request.getHeaders().getFirst("Authorization");

                // Si es nulo o está vacío, bloqueamos la petición de inmediato
                if (authHeader == null || authHeader.trim().isEmpty()) {
                    return onError(exchange, "Cabecera Authorization ausente", HttpStatus.UNAUTHORIZED);
                }

                // Si viene el prefijo "Bearer ", se lo quitamos para quedarnos solo con el token
                if (authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }

                try {
                    // Aquí puedes añadir tu lógica para verificar la firma del JWT con tu JwtUtil en el futuro
                } catch (Exception e) {
                    return onError(exchange, "Token JWT incorrecto o inválido", HttpStatus.UNAUTHORIZED);
                }
            }

            // 3. Si la ruta es pública o el token es válido, continúa el flujo normal hacia el microservicio
            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }
}