package NoobSave._L.garcia.NoobSave.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entité représentant un fichier enregistré dans la base de données MongoDB.
 */
@Document(collection = "fichiers") // Spécifie que cette classe correspond à une collection MongoDB
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fichier {

    /**
     * Identifiant unique du fichier (généré automatiquement par MongoDB).
     */
    @Id
    private String id; // MongoDB utilise des IDs sous forme de chaînes

    /**
     * Nom du fichier (avec extension).
     */
    private String nom;

    /**
     * Type MIME du fichier (exemple : application/pdf).
     */
    private String type;

    /**
     * Chemin absolu du fichier.
     */
    private String chemin;

    /**
     * Date d'ajout du fichier dans la base de données.
     */
    private LocalDateTime dateAjout;

    /**
     * Date de la dernière modification du fichier.
     */
    private LocalDateTime dateModification;

    /**
     * Contenu binaire du fichier (stocké sous forme de tableau d'octets).
     */
    private byte[] contenu;
}
