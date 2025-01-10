package NoobSave._L.garcia.NoobSave.security;

import NoobSave._L.garcia.NoobSave.entities.User;
import NoobSave._L.garcia.NoobSave.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Récupération du header Authorization
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // retirer "Bearer "

            if (jwtProvider.validateToken(token)) {
                // Récupérer le username depuis le token
                String username = jwtProvider.getUsernameFromToken(token);
                User user = userService.findByUsername(username);

                // Créer l'objet d'authentification
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                user, // principal
                                null, // credentials
                                null  // authorities/roles => à adapter si vous avez des rôles
                        );

                // Injecter dans le contexte de sécurité
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        // Continuer la chaîne
        filterChain.doFilter(request, response);
    }
}
