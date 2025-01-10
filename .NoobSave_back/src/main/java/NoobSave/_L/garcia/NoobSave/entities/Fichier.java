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
 * <p>
 * Cette classe stocke les informations relatives aux fichiers, telles que
 * le nom, le type MIME, le chemin d'accès, les dates d'ajout et de modification,
 * ainsi que le contenu binaire du fichier.
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
@Document(collection = "fichiers") // Spécifie que cette classe correspond à une collection MongoDB nommée "fichiers"
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entité représentant un fichier enregistré dans le système.")
public class Fichier {

    /**
     * Identifiant unique du fichier (généré automatiquement par MongoDB).
     */
    @Id
    @Schema(
            description = "Identifiant unique du fichier.",
            example = "64a5f5f5f5f5f5f5f5f5f5f5",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String id; // MongoDB utilise des IDs sous forme de chaînes

    /**
     * Nom du fichier (avec extension).
     */
    @Schema(
            description = "Nom du fichier, incluant son extension.",
            example = "document.pdf",
            required = true
    )
    @NotBlank(message = "Le nom du fichier est requis.")
    private String nom;

    /**
     * Type MIME du fichier (exemple : application/pdf).
     */
    @Schema(
            description = "Type MIME du fichier, indiquant son format.",
            example = "application/pdf",
            required = true
    )
    @NotBlank(message = "Le type MIME du fichier est requis.")
    private String type;

    /**
     * Chemin absolu du fichier.
     */
    @Schema(
            description = "Chemin absolu où le fichier est stocké sur le serveur.",
            example = "/home/user/documents/document.pdf",
            required = true
    )
    @NotBlank(message = "Le chemin du fichier est requis.")
    private String chemin;

    /**
     * Date d'ajout du fichier dans la base de données.
     */
    @Schema(
            description = "Date et heure auxquelles le fichier a été ajouté à la base de données.",
            example = "2023-10-15T10:15:30",
            required = true
    )
    @NotNull(message = "La date d'ajout du fichier est requise.")
    private LocalDateTime dateAjout;

    /**
     * Date de la dernière modification du fichier.
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
     * Ce champ stocke le contenu réel du fichier sous forme binaire.
     * Il est recommandé de gérer ce contenu de manière sécurisée et d'éviter
     * de l'exposer directement via les API.
     * </p>
     */
    @Schema(
            description = "Contenu binaire du fichier. (Ne pas exposer directement via les API)",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private byte[] contenu;
}
