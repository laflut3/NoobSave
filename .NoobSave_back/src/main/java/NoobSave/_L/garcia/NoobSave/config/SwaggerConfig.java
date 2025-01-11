package NoobSave._L.garcia.NoobSave.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration Swagger/OpenAPI pour la documentation de l'API.
 *
 * Cette classe configure les informations principales de l'API, notamment :
 * <ul>
 *     <li>Le titre</li>
 *     <li>La version</li>
 *     <li>La description</li>
 * </ul>
 * Ces informations apparaîtront dans l'interface Swagger UI, permettant d'interagir avec les endpoints de l'API.
 *
 * <strong>Annotation :</strong>
 * <ul>
 *     <li>{@code @Configuration} : Indique que cette classe définit des beans Spring.</li>
 * </ul>
 */


@Configuration
public class SwaggerConfig {

    /**
     * Définit et retourne une instance personnalisée d'{@link OpenAPI}.
     *
     * Cette méthode configure les informations générales de l'API, telles que :
     * <ul>
     *     <li>Le titre : "API de gestion des fichiers".</li>
     *     <li>La version : "1.0".</li>
     *     <li>La description : "API pour gérer les fichiers avec des fonctionnalités de sauvegarde, téléchargement et restauration".</li>
     * </ul>
     * Ces informations sont affichées dans Swagger UI.
     *
     * @return une instance configurée de {@link OpenAPI}
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
