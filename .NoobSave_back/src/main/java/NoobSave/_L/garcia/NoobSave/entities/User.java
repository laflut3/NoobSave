package NoobSave._L.garcia.NoobSave.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Représente un utilisateur dans le système.
 * <p>
 * Cette classe est une entité MongoDB qui stocke les informations relatives aux utilisateurs,
 * telles que le nom d'utilisateur, l'adresse e-mail, le mot de passe et le rôle administrateur.
 * </p>
 *
 * <p>
 * <strong>Annotations :</strong>
 * <ul>
 *   <li>{@code @Data} (Lombok) : Génère automatiquement les getters, setters, {@code toString}, {@code equals} et {@code hashCode}.</li>
 *   <li>{@code @Document} : Indique que cette classe est une entité MongoDB et définit la collection associée.</li>
 *   <li>{@code @Schema} (Swagger/OpenAPI) : Fournit des métadonnées pour la documentation OpenAPI/Swagger.</li>
 * </ul>
 * </p>
 */
@Data
@Document(collection = "utilisateurs")
@Schema(description = "Entité représentant un utilisateur du système.")
public class User {

    /**
     * Identifiant unique de l'utilisateur.
     * <p>
     * Généré automatiquement par MongoDB.
     * </p>
     */
    @Id
    @Schema(description = "Identifiant unique de l'utilisateur.", example = "64a5f5f5f5f5f5f5f5f5f5f5")
    private String id;

    /**
     * Nom d'utilisateur choisi par l'utilisateur.
     * <p>
     * Doit être unique dans le système.
     * </p>
     */
    @Schema(description = "Nom d'utilisateur choisi par l'utilisateur.", example = "JohnDoe", required = true)
    private String username;

    /**
     * Adresse e-mail de l'utilisateur.
     * <p>
     * Doit être unique et valide.
     * </p>
     */
    @Schema(description = "Adresse e-mail de l'utilisateur.", example = "john.doe@example.com", required = true)
    private String email;

    /**
     * Mot de passe de l'utilisateur.
     * <p>
     * Doit être stocké de manière sécurisée (haché).
     * </p>
     * <strong>Remarque :</strong> Ce champ ne devrait jamais être exposé dans les réponses API.
     * Utilisez des DTOs pour contrôler les informations exposées.
     */
    @Schema(description = "Mot de passe de l'utilisateur. (Ne pas exposer dans les réponses API)", example = "Secret123", accessMode = Schema.AccessMode.READ_ONLY)
    private String password;

    /**
     * Indique si l'utilisateur a des privilèges d'administrateur.
     * <p>
     * Par défaut, cette valeur est `false`.
     * </p>
     */
    @Schema(description = "Indique si l'utilisateur est administrateur.", example = "false", required = true)
    private boolean administrateur = false;

}
