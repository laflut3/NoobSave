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
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Contrôleur pour la gestion des opérations d'authentification (inscription et connexion).
 */
@Tag(
        name = "Authentication",
        description = "Gère l'inscription et la connexion des utilisateurs avec des fonctionnalités sécurisées."
)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtProvider jwtProvider;

    /**
     * Inscrit un nouvel utilisateur dans le système.
     *
     * @param request Objet contenant les informations nécessaires à l'inscription de l'utilisateur.
     * @return Une réponse HTTP avec un message de succès ou un statut d'erreur.
     */
    @Operation(
            summary = "Inscrire un nouvel utilisateur",
            description = "Permet de créer un compte utilisateur en enregistrant ses informations dans la base de données.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Données nécessaires pour l'inscription d'un utilisateur.",
                    content = @Content(
                            schema = @Schema(implementation = RegisterRequest.class),
                            examples = @ExampleObject(
                                    name = "Exemple d'inscription",
                                    value = """
                                    {
                                      "username": "JaneDoe",
                                      "password": "Password123",
                                      "email": "jane@example.com"
                                    }
                                    """
                            )
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
                              "userId": "64abcdef1234567890abcdef"
                            }
                            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Données invalides ou manquantes",
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
        if (request.getUsername() == null || request.getPassword() == null || request.getEmail() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", "Les champs 'username', 'password' et 'email' sont obligatoires."
            ));
        }

        User user = userService.register(request);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Utilisateur créé avec succès",
                "userId", user.getId()
        ));
    }

    /**
     * Authentifie un utilisateur en validant ses identifiants de connexion.
     *
     * @param request Objet contenant le nom d'utilisateur et le mot de passe.
     * @return Une réponse HTTP avec un token JWT ou un statut d'erreur.
     */
    @Operation(
            summary = "Authentifier un utilisateur",
            description = "Vérifie les identifiants fournis et génère un token JWT si la connexion est réussie.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Identifiants de connexion de l'utilisateur.",
                    content = @Content(
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = @ExampleObject(
                                    name = "Exemple de connexion",
                                    value = """
                                    {
                                      "username": "JaneDoe",
                                      "password": "Password123"
                                    }
                                    """
                            )
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
                                "id": "64abcdef1234567890abcdef",
                                "username": "JaneDoe",
                                "email": "jane@example.com",
                                "administrateur": false
                              }
                            }
                            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Identifiants invalides",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(example = """
                            {
                              "error": "Identifiants invalides"
                            }
                            """)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erreur interne du serveur",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (request.getUsername() == null || request.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", "Les champs 'username' et 'password' sont obligatoires."
            ));
        }

        User user = userService.authenticate(request);
        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Identifiants invalides"));
        }

        String token = jwtProvider.generateToken(user.getUsername());
        return ResponseEntity.ok(Map.of(
                "token", token,
                "user", user
        ));
    }
}
