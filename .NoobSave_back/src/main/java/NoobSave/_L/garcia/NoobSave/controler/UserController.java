package NoobSave._L.garcia.NoobSave.controler;

import NoobSave._L.garcia.NoobSave.entities.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(
        name = "User Management",
        description = "Gère les opérations liées à l'utilisateur, comme récupérer ses informations ou vérifier son rôle."
)
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    /**
     * Récupère les informations de l'utilisateur actuellement connecté.
     *
     * @param authentication Jeton d'authentification géré par Spring Security.
     * @return Renvoie l'objet User si l'authentification est valide, sinon un statut 401.
     */
    @Operation(
            summary = "Obtenir l'utilisateur courant",
            description = "Renvoie les informations de l'utilisateur authentifié (principal) si la session est valide."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Informations de l'utilisateur renvoyées avec succès"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Non autorisé : l'utilisateur n'est pas connecté ou le token est invalide"
            )
    })
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Non autorisé");
        }

        // Dans le filtre, on a mis un `User` comme principal
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(user);
    }

    /**
     * Vérifie si l'utilisateur actuellement connecté possède le rôle administrateur.
     *
     * @param authentication Jeton d'authentification géré par Spring Security.
     * @return Renvoie un JSON indiquant `admin: true` ou `admin: false`.
     */
    @Operation(
            summary = "Vérifier si l'utilisateur est administrateur",
            description = "Renvoie un booléen dans un champ `admin` pour indiquer si l'utilisateur est administrateur."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "La vérification a réussi, retour d'un objet JSON {\"admin\": true/false}"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Non autorisé : l'utilisateur n'est pas connecté ou le token est invalide"
            )
    })
    @GetMapping("/isAdmin")
    public ResponseEntity<?> isAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Non autorisé");
        }

        // Récupérer l'objet User depuis le principal
        User user = (User) authentication.getPrincipal();

        boolean isAdmin = user.isAdministrateur();

        // Ex : { "admin": true/false }
        Map<String, Boolean> response = Map.of("admin", isAdmin);
        return ResponseEntity.ok(response);
    }
}
