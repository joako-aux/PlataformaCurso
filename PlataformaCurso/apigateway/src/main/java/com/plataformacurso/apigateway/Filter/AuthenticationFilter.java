package com.plataformacurso.apigateway.Filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Predicate;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);

    // Debe ser exactamente la misma clave de tu auth-service
    private final String SECRET_KEY = "TuClaveSecretaSujetaAUnMinimoDeTreintaYDosCaracteres!";

    public AuthenticationFilter() {
        super(Config.class);
    }

    public static class Config {
    }

    private static final List<String> openApiEndpoints = List.of(
            "/api/auth/register",
            "/api/auth/login",
            "/v3/api-docs",
            "/swagger-ui"
    );

    private final Predicate<ServerHttpRequest> isSecured = request ->
            openApiEndpoints.stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();
            String method = request.getMethod().name();

            log.info("[GATEWAY-LOG] Petición recibida en API Gateway: {} {}", method, path);

            if (isSecured.test(request)) {
                log.info("[GATEWAY-LOG] Protegido! Validando token de acceso en cabecera para: {}", path);
                String authHeader = request.getHeaders().getFirst("Authorization");

                if (authHeader == null || authHeader.trim().isEmpty()) {
                    log.warn("[GATEWAY-WARN] Error de Autenticación: Cabecera 'Authorization' ausente en {}", path);
                    return onError(exchange, "Cabecera Authorization ausente", HttpStatus.UNAUTHORIZED);
                }

                if (authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                }

                try {
                    SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

                    Claims claims = Jwts.parserBuilder()
                            .setSigningKey(key)
                            .build()
                            .parseClaimsJws(authHeader)
                            .getBody();

                    String username = claims.getSubject();
                    String role = claims.get("role", String.class);

                    log.info("[GATEWAY-SUCCESS] Token verificado correctamente. Usuario: '{}' | Rol: '{}'", username, role);

                    request = exchange.getRequest().mutate()
                            .header("X-User-Username", username)
                            .header("X-User-Role", role != null ? role : "")
                            .build();

                    return chain.filter(exchange.mutate().request(request).build());

                } catch (Exception e) {
                    log.error("[GATEWAY-ERROR] El token enviado es inválido, expiró o fue manipulado para la ruta: {}", path);
                    return onError(exchange, "Token JWT incorrecto o inválido", HttpStatus.UNAUTHORIZED);
                }
            }

            log.info("[GATEWAY-LOG] Ruta identificada como pública. Derivando petición libremente.");
            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }
}