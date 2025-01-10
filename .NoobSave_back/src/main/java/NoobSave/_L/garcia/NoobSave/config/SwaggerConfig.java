package NoobSave._L.garcia.NoobSave.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Configuration de Swagger/OpenAPI pour l'application.
 * <p>
 * Cette classe configure les informations de base de la documentation Swagger,
 * telles que le titre, la version et la description de l'API. Elle permet d'accéder
 * à une interface utilisateur Swagger UI pour interagir avec les endpoints de l'API.
 * </p>
 *
 * <p>
 * <strong>Annotations :</strong>
 * <ul>
 *   <li>{@code @Configuration} : Indique que cette classe contient des définitions de beans Spring.</li>
 * </ul>
 * </p>
 */
@Configuration
public class SwaggerConfig {

    /**
     * Configure et retourne une instance personnalisée d'OpenAPI.
     * <p>
     * Cette configuration définit les informations principales de l'API, telles que le titre,
     * la version et la description. Ces informations apparaîtront dans l'interface Swagger UI.
     * </p>
     *
     * @return une instance configurée d'{@link OpenAPI}
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de gestion des fichiers")
                        .version("1.0")
                        .description("API pour gérer les fichiers avec des fonctionnalités de sauvegarde, téléchargement et restauration.")
                );
    }
}
