package NoobSave._L.garcia.NoobSave.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Configuration des CORS (Cross-Origin Resource Sharing).
 *
 * Cette classe configure les règles CORS pour autoriser ou restreindre les appels HTTP
 * provenant de domaines externes vers l'application Spring Boot.
 *
 */
@Configuration
public class CorsConfig {

    /**
     * Configure et enregistre un filtre CORS pour gérer les requêtes HTTP inter-origines.
     * <ul>
     *     <li><b>Origines autorisées :</b> Correspondent au motif <code>http://localhost:[*]</code> (tous les ports de localhost).</li>
     *     <li><b>Méthodes autorisées :</b> Toutes les méthodes HTTP (GET, POST, PUT, DELETE, etc.).</li>
     *     <li><b>En-têtes autorisés :</b> Tous les en-têtes HTTP.</li>
     *     <li><b>Envoi de cookies :</b> Autorisé (informations d'identification, comme les tokens, incluses).</li>
     * </ul>
     *
     * @return un {@link CorsFilter} appliqué à toutes les routes de l'application.
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
