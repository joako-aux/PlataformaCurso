package com.plataformacurso.auth_service.Config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService; // Spring inyectará automáticamente tu AuthService

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 1. Validar si el encabezado Authorization viene y tiene el prefijo Bearer
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extraer el token de la cadena (después de "Bearer ")
        jwt = authHeader.substring(7);

        try {
            username = jwtUtil.extractUsername(jwt);

            // 3. Si hay un usuario válido y no está autenticado aún en el contexto de Spring
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // 4. Validar si el token sigue vigente y pertenece al usuario
                if (jwtUtil.isTokenValid(jwt, userDetails.getUsername())) {
                    log.info("[AUTH SERVICE - FILTER] Token válido detectado para el usuario: {}", username);

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Almacenamos la autenticación en el contexto para que Spring le permita pasar
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            log.error("[AUTH SERVICE - FILTER] Error al procesar o validar el token JWT: {}", e.getMessage());
        }

        // 5. Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}