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
 * <p>
 * <strong>Annotations :</strong>
 * <ul>
 *   <li>{@code @Configuration} : Indique que cette classe contient des beans Spring.</li>
 *   <li>{@code @EnableWebSecurity} : Active les fonctionnalités de Spring Security.</li>
 * </ul>
 * </p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Bean de type {@link PasswordEncoder} utilisant l'algorithme BCrypt.
     * <p>
     * BCrypt est un algorithme de hachage sécurisé recommandé pour le stockage des mots de passe.
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
     * <p>
     * Cette méthode définit les règles de sécurité, la gestion des CORS,
     * la désactivation de CSRF, et l'ajout du filtre d'authentification JWT.
     * </p>
     *
     * @param http                l'objet {@link HttpSecurity} à configurer
     * @param jwtAuthenticationFilter le filtre personnalisé pour l'authentification JWT
     * @return une instance configurée de {@link SecurityFilterChain}
     * @throws Exception en cas d'erreur de configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter // injecté via @RequiredArgsConstructor
    ) throws Exception {

        http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.addAllowedOriginPattern("http://localhost:[*]");
                    config.addAllowedMethod("*");
                    config.addAllowedHeader("*");
                    config.setAllowCredentials(true);
                    return config;
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/index.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).authenticated()
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/api/users/me").authenticated()
                        .requestMatchers("/api/fichiers/{id}").authenticated()

                );

        http.addFilterBefore(jwtAuthenticationFilter,
                org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
