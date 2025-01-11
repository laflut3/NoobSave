package NoobSave._L.garcia.NoobSave.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Représente un utilisateur dans le système.
 *
 * Cette entité MongoDB stocke les informations essentielles des utilisateurs, telles que :
 * <ul>
 *     <li>Nom d'utilisateur</li>
 *     <li>Adresse e-mail</li>
 *     <li>Mot de passe</li>
 *     <li>Statut d'administrateur</li>
 * </ul>
 *
 * <strong>Annotations utilisées :</strong>
 * <ul>
 *     <li>{@code @Data} : Génère automatiquement les méthodes getter, setter, {@code toString}, {@code equals} et {@code hashCode}.</li>
 *     <li>{@code @Document} : Spécifie que cette classe correspond à une collection MongoDB nommée "utilisateurs".</li>
 *     <li>{@code @Schema} : Fournit des métadonnées Swagger pour chaque champ.</li>
 * </ul>
 */
@Data
@Document(collection = "utilisateurs")
@Schema(description = "Entité représentant un utilisateur dans le système.")
public class User {

    /**
     * Identifiant unique de l'utilisateur, généré automatiquement par MongoDB.
     */
    @Id
    @Schema(
            description = "Identifiant unique de l'utilisateur généré par MongoDB.",
            example = "64a5f5f5f5f5f5f5f5f5f5f5",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String id;

    /**
     * Nom d'utilisateur unique choisi par l'utilisateur.
     */
    @Schema(
            description = "Nom d'utilisateur unique choisi par l'utilisateur.",
            example = "JohnDoe",
            required = true
    )
    private String username;

    /**
     * Adresse e-mail unique et valide associée à l'utilisateur.
     */
    @Schema(
            description = "Adresse e-mail unique et valide de l'utilisateur.",
            example = "john.doe@example.com",
            required = true
    )
    private String email;

    /**
     * Mot de passe sécurisé (haché).
     * <p>
     * Ce champ ne doit jamais être exposé directement dans les réponses API.
     * Utilisez des DTOs pour masquer les informations sensibles.
     * </p>
     */
    @Schema(
            description = "Mot de passe de l'utilisateur. (Stocké de manière sécurisée, non exposé dans les réponses API.)",
            example = "hashed_password",
            accessMode = Schema.AccessMode.WRITE_ONLY
    )
    private String password;

    /**
     * Indique si l'utilisateur possède des privilèges d'administrateur.
     * <p>
     * Par défaut, cette valeur est `false`.
     * </p>
     */
    @Schema(
            description = "Indique si l'utilisateur possède des privilèges d'administrateur.",
            example = "false",
            required = true
    )
    private boolean administrateur = false;

}
