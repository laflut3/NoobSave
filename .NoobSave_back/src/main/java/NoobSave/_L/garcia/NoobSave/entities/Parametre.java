package NoobSave._L.garcia.NoobSave.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Entité représentant les paramètres de configuration enregistrés dans MongoDB.
 * <p>
 * Cette classe stocke les paramètres liés à la sauvegarde automatique des fichiers, tels que
 * l'activation de la sauvegarde automatique, l'intervalle entre les sauvegardes,
 * les extensions de fichiers autorisées, et le chemin de sauvegarde.
 * </p>
 *
 * <p>
 * <strong>Annotations :</strong>
 * <ul>
 *   <li>{@code @Data} (Lombok) : Génère automatiquement les getters, setters, {@code toString}, {@code equals} et {@code hashCode}.</li>
 *   <li>{@code @NoArgsConstructor} (Lombok) : Génère un constructeur sans arguments.</li>
 *   <li>{@code @AllArgsConstructor} (Lombok) : Génère un constructeur avec tous les arguments.</li>
 *   <li>{@code @Document} : Indique que cette classe est une entité MongoDB et définit la collection associée.</li>
 *   <li>{@code @Schema} (Swagger/OpenAPI) : Fournit des métadonnées pour la documentation OpenAPI/Swagger.</li>
 * </ul>
 * </p>
 */
@Document(collection = "parametres") // Correspond à une collection MongoDB nommée "parametres"
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entité représentant les paramètres de configuration de sauvegarde.")
public class Parametre {

    /**
     * Identifiant unique, généré automatiquement par MongoDB.
     */
    @Id
    @Schema(
            description = "Identifiant unique de la configuration.",
            example = "64a5f5f5f5f5f5f5f5f5f5f5",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String id; // MongoDB utilise des IDs sous forme de chaînes

    /**
     * Indique si la sauvegarde automatique est active ou non.
     */
    @Schema(
            description = "Indique si la sauvegarde automatique est activée.",
            example = "true",
            required = true
    )
    private boolean autoSaveEnabled;

    /**
     * Intervalle (en millisecondes) entre deux sauvegardes automatiques.
     * Ex : 60000 = 60 secondes
     */
    @Schema(
            description = "Intervalle en millisecondes entre deux sauvegardes automatiques.",
            example = "60000",
            required = true
    )
    private long autoSaveInterval;

    /**
     * Liste d'extensions autorisées, stockées en base sous forme de chaîne.
     * Ex : ".pdf,.txt,.docx"
     */
    @Schema(
            description = "Liste des extensions de fichiers autorisées pour la sauvegarde, séparées par des virgules.",
            example = ".pdf,.txt,.docx",
            required = true
    )
    private String allowedFileExtensions;

    /**
     * Chemin du répertoire ou fichier pour la sauvegarde.
     * Ex : "/home/user/sauvegardes"
     */
    @Schema(
            description = "Chemin du répertoire ou fichier où les sauvegardes seront stockées.",
            example = "/home/user/sauvegardes",
            required = true
    )
    private String savePath;
}
