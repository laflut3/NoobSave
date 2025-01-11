package NoobSave._L.garcia.NoobSave;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Classe principale de l'application Noob Save.
 *
 * <p>Cette application est un projet de sauvegarde automatique avec des fonctionnalités de
 * gestion et de synchronisation des fichiers. Elle utilise Spring Boot pour sa configuration et
 * son exécution.</p>
 *
 * <p>Les annotations Swagger permettent de générer automatiquement la documentation de l'API REST
 * pour une meilleure lisibilité et intégration.</p>
 *
 *Fonctionnalités principales :
 * <ul>
 *     <li>Sauvegarde automatique planifiée (grâce à {@link EnableScheduling}).</li>
 *     <li>API documentée avec Swagger.</li>
 * </ul>
 *
 * <b>Configuration :</b>
 * <p>Les informations de licence, de contact et de conditions d'utilisation sont renseignées via
 * l'annotation {@link OpenAPIDefinition}.</p>
 *
 * @author Léo Torres
 * @version 1.0.0
 */
@SpringBootApplication
@EnableScheduling
@OpenAPIDefinition(info = @Info(
        title = "Noob Save",
        version = "1.0.0",
        description = "Projet de sauvegarde automatique",
        termsOfService = "https://github.com/laflut3/NoobSave/blob/main/README.md",
        contact = @Contact(
                name = "Mr Léo Torres & Mr Léo Constantin",
                email = "leo.torres@etu.umontpellier.fr"
        ),
        license = @License(
                name = "license",
                url = "https://github.com/laflut3/NoobSave/blob/main/LICENSE"
        )
)
)
public class NoobSaveApplication {

    /**
     * Point d'entrée principal de l'application.
     *
     * <p>Cette méthode exécute l'application en utilisant Spring Boot. Elle initialise toutes
     * les configurations, démarre le contexte Spring et lance l'exécution des composants.</p>
     *
     * @param args Arguments de ligne de commande passés à l'application.
     */
    public static void main(String[] args) {
        SpringApplication.run(NoobSaveApplication.class, args);
    }

}

