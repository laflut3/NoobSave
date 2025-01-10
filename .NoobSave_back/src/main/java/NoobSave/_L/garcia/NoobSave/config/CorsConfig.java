package NoobSave._L.garcia.NoobSave.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Configuration des CORS (Cross-Origin Resource Sharing).
 * <p>
 * Cette classe permet d'autoriser ou restreindre les appels HTTP
 * provenant de différents domaines (origines) vers l'application Spring Boot.
 * </p>
 */
@Configuration
public class CorsConfig {

    /**
     * Configure et enregistre un {@link CorsFilter} pour gérer les requêtes CORS.
     * <p>
     * - <strong>Allowed Origin Pattern :</strong> Permet d’autoriser toutes les origines
     *   qui correspondent à <code>http://localhost:[*]</code> (tous les ports de localhost).
     * <br>
     * - <strong>Allowed Methods :</strong> Autorise toutes les méthodes HTTP (GET, POST, PUT, DELETE, etc.).
     * <br>
     * - <strong>Allowed Headers :</strong> Autorise tous les en-têtes HTTP (headers).
     * <br>
     * - <strong>Allow Credentials :</strong> Permet l’envoi de cookies et autres informations d’identification
     *   dans les requêtes (utile, par exemple, pour des tokens dans les cookies).
     * </p>
     *
     * @return {@link CorsFilter} : Le filtre CORS appliqué à l'ensemble des endpoints de l'application.
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("http://localhost:[*]"); // Autorise localhost sur tous les ports
        config.addAllowedMethod("*"); // Autorise toutes les méthodes HTTP
        config.addAllowedHeader("*"); // Autorise tous les en-têtes HTTP
        config.setAllowCredentials(true); // Autorise l'envoi de cookies et d'identifiants

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Applique cette configuration à toutes les routes

        return new CorsFilter(source);
    }
}
