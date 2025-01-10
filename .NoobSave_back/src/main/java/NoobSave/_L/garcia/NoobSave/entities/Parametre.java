package NoobSave._L.garcia.NoobSave.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Entité représentant les paramètres de configuration de l'application.
 * <p>
 * Cette classe contient les réglages liés à la sauvegarde automatique, tels que :
 * <ul>
 *     <li>Activation ou désactivation de la sauvegarde automatique</li>
 *     <li>Intervalle entre deux sauvegardes automatiques</li>
 *     <li>Extensions de fichiers autorisées</li>
 *     <li>Chemin de sauvegarde</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Annotations utilisées :</strong>
 * <ul>
 *     <li>{@code @Data} : Génère automatiquement les méthodes getter, setter, {@code toString}, {@code equals} et {@code hashCode}.</li>
 *     <li>{@code @NoArgsConstructor} et {@code @AllArgsConstructor} : Fournissent des constructeurs par défaut et avec arguments.</li>
 *     <li>{@code @Document} : Spécifie que cette classe correspond à une collection MongoDB.</li>
 *     <li>{@code @Schema} : Fournit des métadonnées pour Swagger/OpenAPI.</li>
 * </ul>
 * </p>
 */
@Document(collection = "parametres")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entité représentant les paramètres de configuration de sauvegarde.")
public class Parametre {

    /**
     * Identifiant unique généré par MongoDB.
     */
    @Id
    @Schema(
            description = "Identifiant unique de la configuration dans MongoDB.",
            example = "64a5f5f5f5f5f5f5f5f5f5f5",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String id;

    /**
     * Indique si la sauvegarde automatique est activée ou non.
     */
    @Schema(
            description = "Indique si la sauvegarde automatique est activée.",
            example = "true",
            required = true
    )
    private boolean autoSaveEnabled;

    /**
     * Intervalle (en millisecondes) entre deux sauvegardes automatiques.
     */
    @Schema(
            description = "Intervalle en millisecondes entre deux sauvegardes automatiques (par exemple, 60000 pour 60 secondes).",
            example = "60000",
            required = true
    )
    private long autoSaveInterval;

    /**
     * Liste des extensions de fichiers autorisées, séparées par des virgules.
     */
    @Schema(
            description = "Liste des extensions de fichiers autorisées pour la sauvegarde, séparées par des virgules (exemple : \".pdf,.txt,.docx\").",
            example = ".pdf,.txt,.docx",
            required = true
    )
    private String allowedFileExtensions;

    /**
     * Chemin où les sauvegardes sont stockées.
     */
    @Schema(
            description = "Chemin absolu du répertoire ou fichier où les sauvegardes seront stockées.",
            example = "/home/user/sauvegardes",
            required = true
    )
    private String savePath;
}
