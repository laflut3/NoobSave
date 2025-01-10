package NoobSave._L.garcia.NoobSave.config;

import NoobSave._L.garcia.NoobSave.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

/**
 * Configuration de la sécurité de l'application en utilisant Spring Security.
 * <p>
 * Cette classe configure les filtres de sécurité, les règles d'autorisation,
 * la gestion des CORS (Cross-Origin Resource Sharing) et l'intégration du filtre JWT.
 * </p>
 *
 * <p><strong>Annotations :</strong></p>
 * <ul>
 *   <li>{@code @Configuration} : Indique que cette classe est une configuration Spring.</li>
 *   <li>{@code @EnableWebSecurity} : Active les fonctionnalités de Spring Security.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Définit un bean de type {@link PasswordEncoder} utilisant l'algorithme BCrypt.
     * <p>
     * BCrypt est un algorithme de hachage sécurisé, recommandé pour le stockage des mots de passe.
     * </p>
     *
     * @return une instance de {@link BCryptPasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configure la chaîne de filtres de sécurité pour l'application.
     *
     * Cette méthode définit :
     * <ul>
     *     <li>Les règles d'autorisation des requêtes (par exemple, accès public ou authentifié).</li>
     *     <li>La gestion des CORS, permettant des appels inter-origines sécurisés.</li>
     *     <li>La désactivation de la protection CSRF (non nécessaire pour les API REST).</li>
     *     <li>L'ajout d'un filtre personnalisé pour l'authentification via JWT.</li>
     * </ul>
     *
     * @param http L'objet {@link HttpSecurity} utilisé pour configurer les filtres de sécurité.
     * @param jwtAuthenticationFilter Le filtre personnalisé pour l'authentification JWT.
     * @return Une instance configurée de {@link SecurityFilterChain}.
     * @throws Exception En cas d'erreur lors de la configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter // injecté automatiquement
    ) throws Exception {

        http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.addAllowedOriginPattern("http://localhost:[*]"); // Autorise localhost sur tous les ports
                    config.addAllowedMethod("*"); // Autorise toutes les méthodes HTTP
                    config.addAllowedHeader("*"); // Autorise tous les en-têtes HTTP
                    config.setAllowCredentials(true); // Permet l'envoi de cookies
                    return config;
                }))
                .csrf(AbstractHttpConfigurer::disable) // Désactive CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/index.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll() // Autorise les requêtes publiques pour Swagger
                        .requestMatchers("/api/**").permitAll() // Autorise toutes les requêtes API
                        .requestMatchers("/api/users/me").authenticated() // Nécessite une authentification pour cet endpoint
                );

        // Ajoute le filtre JWT avant le filtre d'authentification classique
        http.addFilterBefore(jwtAuthenticationFilter,
                org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
