package NoobSave._L.garcia.NoobSave.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Représente une requête d'inscription pour créer un nouvel utilisateur.
 *
 * Cette classe contient les informations nécessaires pour qu'un utilisateur puisse s'inscrire :
 * <ul>
 *     <li>Nom d'utilisateur</li>
 *     <li>Adresse e-mail</li>
 *     <li>Mot de passe</li>
 * </ul>
 *
 * <strong>Annotations utilisées :</strong>
 * <ul>
 *     <li>{@code @Data} : Génère automatiquement les méthodes getters, setters, {@code toString}, {@code equals} et {@code hashCode}.</li>
 *     <li>{@code @Schema} : Fournit des métadonnées pour la documentation Swagger/OpenAPI.</li>
 *     <li>{@code @NotBlank} : Valide que le champ n'est pas vide ni composé uniquement d'espaces blancs.</li>
 *     <li>{@code @Email} : Valide que le champ contient une adresse e-mail valide.</li>
 * </ul>
 */
@Data
@Schema(description = "Requête pour inscrire un nouvel utilisateur. Contient les informations nécessaires pour créer un compte utilisateur.")
public class RegisterRequest {

    /**
     * Le nom d'utilisateur choisi par l'utilisateur.
     * <p>
     * Ce champ est obligatoire et ne doit pas être vide.
     * </p>
     */
    @Schema(
            description = "Nom d'utilisateur choisi par l'utilisateur. Doit être unique.",
            example = "JohnDoe",
            required = true
    )
    @NotBlank(message = "Le nom d'utilisateur est requis.")
    private String username;

    /**
     * L'adresse e-mail de l'utilisateur.
     * <p>
     * Ce champ est obligatoire, doit être valide et unique.
     * </p>
     */
    @Schema(
            description = "Adresse e-mail de l'utilisateur. Doit être valide et unique.",
            example = "john.doe@example.com",
            required = true
    )
    @NotBlank(message = "L'adresse e-mail est requise.")
    @Email(message = "L'adresse e-mail doit être valide.")
    private String email;

    /**
     * Le mot de passe choisi par l'utilisateur.
     * <p>
     * Ce champ est obligatoire et doit respecter les règles de sécurité définies (ex. : longueur minimale, complexité).
     * </p>
     */
    @Schema(
            description = "Mot de passe choisi par l'utilisateur. Doit respecter les exigences de sécurité.",
            example = "Secret123",
            required = true
    )
    @NotBlank(message = "Le mot de passe est requis.")
    private String password;
}
