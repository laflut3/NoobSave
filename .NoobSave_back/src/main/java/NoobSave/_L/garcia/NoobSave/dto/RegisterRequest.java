package NoobSave._L.garcia.NoobSave.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Représente une requête d'inscription pour créer un nouvel utilisateur.
 * <p>
 * Cette classe contient les informations nécessaires pour qu'un utilisateur puisse s'inscrire,
 * telles que le nom d'utilisateur, l'adresse e-mail et le mot de passe.
 * </p>
 *
 * <p>
 * <strong>Annotations :</strong>
 * <ul>
 *   <li>{@code @Data} : Génère automatiquement les getters, setters, {@code toString}, {@code equals} et {@code hashCode}.</li>
 *   <li>{@code @Schema} : Fournit des métadonnées pour la documentation OpenAPI/Swagger.</li>
 *   <li>{@code @NotBlank} : Valide que les champs ne sont pas vides ou composés uniquement d'espaces blancs.</li>
 *   <li>{@code @Email} : Valide que le champ contient une adresse e-mail valide.</li>
 * </ul>
 * </p>
 */
@Data
@Schema(description = "Requête d'inscription pour créer un nouvel utilisateur.")
public class RegisterRequest {

    /**
     * Le nom d'utilisateur choisi par l'utilisateur.
     * <p>
     * Doit être fourni et ne doit pas être vide.
     * </p>
     */
    @Schema(
            description = "Nom d'utilisateur choisi par l'utilisateur.",
            example = "JohnDoe",
            required = true
    )
    @NotBlank(message = "Le nom d'utilisateur est requis.")
    private String username;

    /**
     * L'adresse e-mail de l'utilisateur.
     * <p>
     * Doit être fournie, ne doit pas être vide et doit être une adresse e-mail valide.
     * </p>
     */
    @Schema(
            description = "Adresse e-mail de l'utilisateur.",
            example = "john.doe@example.com",
            required = true
    )
    @NotBlank(message = "L'adresse e-mail est requise.")
    @Email(message = "L'adresse e-mail doit être valide.")
    private String email;

    /**
     * Le mot de passe choisi par l'utilisateur.
     * <p>
     * Doit être fourni et ne doit pas être vide.
     * </p>
     */
    @Schema(
            description = "Mot de passe choisi par l'utilisateur.",
            example = "Secret123",
            required = true
    )
    @NotBlank(message = "Le mot de passe est requis.")
    private String password;
}
