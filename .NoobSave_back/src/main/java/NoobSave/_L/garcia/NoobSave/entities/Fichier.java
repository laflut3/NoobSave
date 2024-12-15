package NoobSave._L.garcia.NoobSave.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Lob;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Entité représentant un fichier enregistré dans la base de données.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fichier {

    /**
     * Identifiant unique du fichier, généré automatiquement.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nom du fichier (avec extension).
     */
    private String nom;

    /**
     * Type MIME du fichier (exemple : application/pdf).
     */
    private String type;

    /**
     * Chemin absolu du fichier
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
     * Contenu binaire du fichier (stocké sous forme de blob).
     */
    @Lob
    private byte[] contenu;
}


