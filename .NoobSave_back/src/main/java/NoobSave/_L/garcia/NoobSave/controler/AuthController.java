package NoobSave._L.garcia.NoobSave.controler;

import NoobSave._L.garcia.NoobSave.dto.LoginRequest;
import NoobSave._L.garcia.NoobSave.dto.RegisterRequest;
import NoobSave._L.garcia.NoobSave.entities.User;
import NoobSave._L.garcia.NoobSave.security.JwtProvider;
import NoobSave._L.garcia.NoobSave.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(
        name = "Authentication",
        description = "Gère l'inscription et la connexion des utilisateurs."
)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtProvider jwtProvider;

    /**
     * Inscrit un nouvel utilisateur.
     *
     * @param request Objet contenant les informations de l'utilisateur à créer.
     * @return Une réponse HTTP avec un message de succès et l'ID de l'utilisateur créé.
     */
    @Operation(
            summary = "Créer un nouvel utilisateur",
            description = "Enregistre un nouvel utilisateur dans la base de données à partir des informations fournies.",
            requestBody = @RequestBody(
                    required = true,
                    description = "Données nécessaires à la création d'un compte utilisateur.",
                    content = @Content(
                            schema = @Schema(implementation = RegisterRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Exemple de création d'utilisateur",
                                            value = """
                        {
                          "username": "JohnDoe",
                          "password": "Secret123",
                          "email": "john@example.com"
                        }
                        """
                                    )
                            }
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Utilisateur créé avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                {
                  "success": true,
                  "message": "Utilisateur créé avec succès",
                  "userId": 1
                }
                """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requête invalide (données manquantes ou incorrectes)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erreur interne du serveur",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        User user = userService.register(request);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Utilisateur créé avec succès",
                "userId", user.getId()
        ));
    }

    /**
     * Authentifie un utilisateur via ses identifiants de connexion.
     *
     * @param request Objet contenant le nom d'utilisateur et le mot de passe.
     * @return Une réponse HTTP avec un token JWT en cas de succès, ou un statut 401 en cas d'échec.
     */
    @Operation(
            summary = "Authentifier un utilisateur",
            description = "Vérifie les identifiants de l'utilisateur et renvoie un token JWT si la connexion est validée.",
            requestBody = @RequestBody(
                    required = true,
                    description = "Identifiants de connexion de l'utilisateur.",
                    content = @Content(
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Exemple de connexion",
                                            value = """
                        {
                          "username": "JohnDoe",
                          "password": "Secret123"
                        }
                        """
                                    )
                            }
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Connexion réussie",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                {
                  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                  "user": {
                    "id": 1,
                    "username": "JohnDoe",
                    "email": "john@example.com",
                    ...
                  }
                }
                """)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Identifiants invalides",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erreur interne du serveur",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = userService.authenticate(request);
        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Identifiants invalides");
        }

        String token = jwtProvider.generateToken(user.getUsername());
        return ResponseEntity.ok(Map.of(
                "token", token,
                "user", user
        ));
    }
}
