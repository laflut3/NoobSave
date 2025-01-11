package NoobSave._L.garcia.NoobSave.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Représente une requête de connexion pour authentifier un utilisateur.
 * <p>
 * Cette classe contient les informations nécessaires pour qu'un utilisateur puisse se connecter,
 * notamment le nom d'utilisateur et le mot de passe.
 * </p>
 *
 * <strong>Annotations :</strong>
 * <ul>
 *   <li>{@code @Data} : Génère automatiquement les méthodes getter, setter, {@code toString}, {@code equals} et {@code hashCode}.</li>
 *   <li>{@code @Schema} : Fournit des métadonnées pour la documentation OpenAPI/Swagger.</li>
 *   <li>{@code @NotBlank} : Assure que les champs ne sont pas nuls, vides ou composés uniquement d'espaces blancs.</li>
 * </ul>
 */
@Data
@Schema(description = "Requête de connexion contenant le nom d'utilisateur et le mot de passe nécessaires pour l'authentification.")
public class LoginRequest {

    /**
     * Le nom d'utilisateur de l'utilisateur.
     * <p>
     * Ce champ est obligatoire et doit être renseigné.
     * </p>
     */
    @Schema(
            description = "Nom d'utilisateur pour l'authentification.",
            example = "JohnDoe",
            required = true
    )
    @NotBlank(message = "Le nom d'utilisateur est requis.")
    private String username;

    /**
     * Le mot de passe de l'utilisateur.
     * <p>
     * Ce champ est obligatoire et doit être renseigné.
     * </p>
     */
    @Schema(
            description = "Mot de passe pour l'authentification.",
            example = "Secret123",
            required = true
    )
    @NotBlank(message = "Le mot de passe est requis.")
    private String password;
}
