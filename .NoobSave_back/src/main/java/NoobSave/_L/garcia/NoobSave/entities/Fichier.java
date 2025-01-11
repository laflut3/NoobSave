package NoobSave._L.garcia.NoobSave.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Entité représentant un fichier enregistré dans la base de données MongoDB.
 *
 * Cette classe contient les informations essentielles sur un fichier :
 * <ul>
 *     <li>Identifiant unique généré par MongoDB</li>
 *     <li>Nom et type MIME</li>
 *     <li>Chemin absolu du fichier</li>
 *     <li>Dates d'ajout et de dernière modification</li>
 *     <li>Contenu binaire (stocké sous forme de tableau d'octets)</li>
 * </ul>
 *
 * <strong>Annotations utilisées :</strong>
 * <ul>
 *     <li>{@code @Data} : Génère automatiquement les méthodes getter, setter, {@code toString}, {@code equals}, et {@code hashCode}.</li>
 *     <li>{@code @NoArgsConstructor} et {@code @AllArgsConstructor} : Fournissent des constructeurs par défaut et avec tous les arguments.</li>
 *     <li>{@code @Document} : Indique que cette classe est une entité MongoDB et spécifie la collection.</li>
 *     <li>{@code @Schema} : Fournit des métadonnées pour Swagger/OpenAPI.</li>
 *     <li>{@code @NotBlank} et {@code @NotNull} : Assurent la validation des champs.</li>
 * </ul>
 */
@Document(collection = "fichiers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entité représentant un fichier enregistré dans la base de données MongoDB.")
public class Fichier {

    /**
     * Identifiant unique du fichier (généré automatiquement par MongoDB).
     */
    @Id
    @Schema(
            description = "Identifiant unique du fichier dans MongoDB.",
            example = "64a5f5f5f5f5f5f5f5f5f5f5",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String id;

    /**
     * Nom du fichier (avec extension).
     */
    @Schema(
            description = "Nom du fichier incluant son extension.",
            example = "document.pdf",
            required = true
    )
    @NotBlank(message = "Le nom du fichier est requis.")
    private String nom;

    /**
     * Type MIME du fichier (exemple : application/pdf).
     */
    @Schema(
            description = "Type MIME du fichier, indiquant son format (ex. application/pdf).",
            example = "application/pdf",
            required = true
    )
    @NotBlank(message = "Le type MIME du fichier est requis.")
    private String type;

    /**
     * Chemin absolu où le fichier est stocké.
     */
    @Schema(
            description = "Chemin absolu du fichier sur le serveur.",
            example = "/home/user/documents/document.pdf",
            required = true
    )
    @NotBlank(message = "Le chemin du fichier est requis.")
    private String chemin;

    /**
     * Date et heure auxquelles le fichier a été ajouté à la base de données.
     */
    @Schema(
            description = "Date et heure d'ajout du fichier dans le système.",
            example = "2023-10-15T10:15:30",
            required = true
    )
    @NotNull(message = "La date d'ajout du fichier est requise.")
    private LocalDateTime dateAjout;

    /**
     * Date et heure de la dernière modification du fichier.
     */
    @Schema(
            description = "Date et heure de la dernière modification du fichier.",
            example = "2023-10-20T12:00:00",
            required = true
    )
    @NotNull(message = "La date de modification du fichier est requise.")
    private LocalDateTime dateModification;

    /**
     * Contenu binaire du fichier (stocké sous forme de tableau d'octets).
     * <p>
     * Ce champ contient le contenu réel du fichier sous forme binaire.
     * Il est recommandé de ne pas exposer ce champ directement via les API.
     * </p>
     */
    @Schema(
            description = "Contenu binaire du fichier, généralement sous forme de tableau d'octets. Ne pas exposer directement via les API.",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private byte[] contenu;
}
