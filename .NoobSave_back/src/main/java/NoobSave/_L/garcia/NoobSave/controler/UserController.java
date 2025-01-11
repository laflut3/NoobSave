package NoobSave._L.garcia.NoobSave.controler;

import NoobSave._L.garcia.NoobSave.entities.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Contrôleur REST pour gérer les utilisateurs.
 *
 * Ce contrôleur permet de :
 * <ul>
 *     <li>Récupérer les informations de l'utilisateur authentifié.</li>
 *     <li>Vérifier si l'utilisateur est administrateur.</li>
 * </ul>
 */
@Tag(
        name = "User Management",
        description = "Endpoints pour gérer les utilisateurs connectés, vérifier leurs informations et leurs privilèges."
)
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    /**
     * Récupère les informations de l'utilisateur actuellement connecté.
     *
     * @param authentication Jeton d'authentification géré par Spring Security.
     * @return Objet JSON contenant les informations de l'utilisateur ou une erreur HTTP 401.
     */
    @Operation(
            summary = "Obtenir les informations de l'utilisateur connecté",
            description = "Renvoie les informations de l'utilisateur actuellement authentifié si le jeton est valide.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Utilisateur récupéré avec succès.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = User.class),
                                    examples = @ExampleObject(
                                            name = "Exemple de réponse",
                                            value = """
                                            {
                                              "id": "123456789",
                                              "username": "JohnDoe",
                                              "email": "john.doe@example.com",
                                              "administrateur": false
                                            }
                                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Non autorisé : utilisateur non authentifié ou token invalide.",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = "Non autorisé")
                            )
                    )
            }
    )
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Non autorisé");
        }

        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(user);
    }

    /**
     * Vérifie si l'utilisateur actuellement connecté possède des privilèges administrateurs.
     *
     * @param authentication Jeton d'authentification géré par Spring Security.
     * @return Objet JSON avec un champ `admin` indiquant si l'utilisateur est administrateur.
     */
    @Operation(
            summary = "Vérifier si l'utilisateur est administrateur",
            description = "Renvoie un objet JSON contenant un booléen `admin` pour indiquer si l'utilisateur a des privilèges administrateurs.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Statut de l'administrateur récupéré avec succès.",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            name = "Exemple de réponse",
                                            value = """
                                            {
                                              "admin": true
                                            }
                                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Non autorisé : utilisateur non authentifié ou token invalide.",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = "Non autorisé")
                            )
                    )
            }
    )
    @GetMapping("/isAdmin")
    public ResponseEntity<?> isAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Non autorisé");
        }

        User user = (User) authentication.getPrincipal();
        boolean isAdmin = user.isAdministrateur();
        return ResponseEntity.ok(Map.of("admin", isAdmin));
    }
}
