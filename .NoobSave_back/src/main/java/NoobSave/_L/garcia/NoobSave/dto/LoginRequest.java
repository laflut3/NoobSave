package NoobSave._L.garcia.NoobSave.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Représente une requête de connexion pour authentifier un utilisateur.
 * <p>
 * Cette classe contient les informations nécessaires pour qu'un utilisateur puisse se connecter,
 * telles que le nom d'utilisateur et le mot de passe.
 * </p>
 *
 * <p>
 * <strong>Annotations :</strong>
 * <ul>
 *   <li>{@code @Data} : Génère automatiquement les getters, setters, {@code toString}, {@code equals} et {@code hashCode}.</li>
 *   <li>{@code @Schema} : Fournit des métadonnées pour la documentation OpenAPI/Swagger.</li>
 *   <li>{@code @NotBlank} : Valide que les champs ne sont pas vides ou composés uniquement d'espaces blancs.</li>
 * </ul>
 * </p>
 */
@Data
@Schema(description = "Requête de connexion pour authentifier un utilisateur.")
public class LoginRequest {

    /**
     * Le nom d'utilisateur de l'utilisateur.
     * <p>
     * Doit être fourni et ne doit pas être vide.
     * </p>
     */
    @Schema(
            description = "Nom d'utilisateur de l'utilisateur.",
            example = "JohnDoe",
            required = true
    )
    @NotBlank(message = "Le nom d'utilisateur est requis.")
    private String username;

    /**
     * Le mot de passe de l'utilisateur.
     * <p>
     * Doit être fourni et ne doit pas être vide.
     * </p>
     */
    @Schema(
            description = "Mot de passe de l'utilisateur.",
            example = "Secret123",
            required = true
    )
    @NotBlank(message = "Le mot de passe est requis.")
    private String password;
}
