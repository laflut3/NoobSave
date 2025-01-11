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

/**
 * Filtre JWT pour authentifier les utilisateurs à partir de leurs tokens.
 * <p>
 * Cette classe intercepte chaque requête HTTP pour vérifier si elle contient
 * un token JWT valide dans le header "Authorization". Si un token est présent
 * et valide, l'utilisateur correspondant est authentifié et injecté dans le
 * contexte de sécurité de Spring Security.
 * </p>
 *
 *
 * <strong>Annotations :</strong>
 * <ul>
 *   <li>{@code @Component} : Enregistre cette classe comme composant Spring.</li>
 *   <li>{@code @RequiredArgsConstructor} : Génère un constructeur avec les dépendances nécessaires.</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider; // Fournisseur de tokens JWT
    private final UserService userService; // Service pour accéder aux utilisateurs

    /**
     * Intercepte chaque requête pour valider le token JWT.
     *
     * @param request     Requête HTTP en cours.
     * @param response    Réponse HTTP associée.
     * @param filterChain Chaîne de filtres à poursuivre.
     * @throws ServletException En cas de problème de traitement de la requête.
     * @throws IOException      En cas d'erreur d'entrée/sortie.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Récupérer le header Authorization
        String authHeader = request.getHeader("Authorization");

        // Vérifier la présence et le format du header
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Supprimer le préfixe "Bearer "

            // Valider le token
            if (jwtProvider.validateToken(token)) {
                // Extraire le nom d'utilisateur du token
                String username = jwtProvider.getUsernameFromToken(token);
                User user = userService.findByUsername(username);

                // Vérifier si l'utilisateur est valide
                if (user != null) {
                    // Créer un objet d'authentification
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    user, // principal : l'utilisateur
                                    null, // credentials : aucun mot de passe n'est nécessaire ici
                                    null  // authorities : ajouter les rôles ici si nécessaires
                            );

                    // Injecter dans le contexte de sécurité
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        // Continuer la chaîne de filtres
        filterChain.doFilter(request, response);
    }
}
