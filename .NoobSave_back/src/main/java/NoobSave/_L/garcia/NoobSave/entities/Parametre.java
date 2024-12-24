package NoobSave._L.garcia.NoobSave.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Parametre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Indique si la sauvegarde automatique est active ou non.
     */
    private boolean autoSaveEnabled;

    /**
     * Intervalle (en millisecondes) entre deux sauvegardes automatiques.
     * Ex : 60000 = 60 secondes
     */
    // Stocké en base sous forme de BIGINT (ou INT, etc.)
    @Getter
    @Setter
    private long autoSaveInterval;

    /**
     * Liste d'extensions autorisées, stockées en base séparées par des virgules.
     * Ex : ".pdf,.txt,.docx"
     */
    private String allowedFileExtensions;
}
