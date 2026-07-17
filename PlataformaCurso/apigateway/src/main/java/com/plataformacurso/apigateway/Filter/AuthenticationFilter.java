package com.plataformacurso.apigateway.Filter;

import com.plataformacurso.apigateway.Config.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private JwtUtils jwtUtils;

    public AuthenticationFilter() {
        super(Config.class);
    }

    public static class Config {
        // Añadir propiedades de configuración si es necesario
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // 1. Validar si el Header de Autorización existe
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "Falta el encabezado de autorización", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().getOrEmpty(HttpHeaders.AUTHORIZATION).get(0);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Estructura de token inválida", HttpStatus.UNAUTHORIZED);
            }

            // 2. Extraer el string del Token
            String token = authHeader.substring(7);

            try {
                // 3. Validar con JwtUtils empleando la clave secreta compartida
                jwtUtils.validateToken(token);

                // Opcional: Pasar el usuario verificado hacia los microservicios de destino vía Header interno
                String username = jwtUtils.extractUsername(token);
                request = exchange.getRequest().mutate()
                        .header("X-User-Username", username)
                        .build();

            } catch (Exception e) {
                return onError(exchange, "Token inválido o expirado", HttpStatus.UNAUTHORIZED);
            }

            return chain.filter(exchange.mutate().request(request).build());
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }
}