package NoobSave._L.garcia.NoobSave;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@OpenAPIDefinition(info = @Info(
        title = "Noob Save",
        version = "1.0.0",
        description = "projet de sauvegarde automatique",
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

    public static void main(String[] args) {
        SpringApplication.run(NoobSaveApplication.class, args);
    }

}
