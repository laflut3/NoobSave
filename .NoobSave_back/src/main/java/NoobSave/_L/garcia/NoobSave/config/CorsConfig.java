package NoobSave._L.garcia.NoobSave.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:3000"); // Autorise le frontend
        config.addAllowedMethod("*"); // Autorise toutes les méthodes (GET, POST, PUT, DELETE, etc.)
        config.addAllowedHeader("*"); // Autorise tous les en-têtes
        config.setAllowCredentials(true); // Autorise les cookies, si nécessaire

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Autoriser toutes les routes
                .allowedOrigins("http://localhost:3000") // Autoriser localhost:3000
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Autoriser les méthodes nécessaires
                .allowedHeaders("*")
                .allowCredentials(true); // Autoriser les cookies (si nécessaire)
    }
}
