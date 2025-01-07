package NoobSave._L.garcia.NoobSave.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

/**
 * Entité représentant les paramètres de configuration enregistrés dans MongoDB.
 */
@Document(collection = "parametres") // Correspond à une collection MongoDB nommée "parametres"
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Parametre {

    /**
     * Identifiant unique, généré automatiquement par MongoDB.
     */
    @Id
    private String id; // MongoDB utilise des IDs sous forme de chaînes

    /**
     * Indique si la sauvegarde automatique est active ou non.
     */
    private boolean autoSaveEnabled;

    /**
     * Intervalle (en millisecondes) entre deux sauvegardes automatiques.
     * Ex : 60000 = 60 secondes
     */
    private long autoSaveInterval;

    /**
     * Liste d'extensions autorisées, stockées en base sous forme de chaîne.
     * Ex : ".pdf,.txt,.docx"
     */
    private String allowedFileExtensions;

    /**
     * Chemin du répertoire ou fichier pour la sauvegarde.
     * Ex : "/home/user/sauvegardes"
     */
    private String savePath;
}
