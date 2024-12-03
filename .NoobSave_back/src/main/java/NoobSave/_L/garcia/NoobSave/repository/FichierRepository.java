package NoobSave._L.garcia.NoobSave.repository;

import NoobSave._L.garcia.NoobSave.entities.Fichier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Interface JpaRepository pour effectuer des opérations CRUD sur les entités Fichier.
 */
public interface FichierRepository extends JpaRepository<Fichier, Long> {

    /**
     * Recherche un fichier dans la base de données en fonction de son nom.
     *
     * @param nom nom du fichier.
     * @return un objet `Optional` contenant le fichier s'il existe, sinon vide.
     */
    Optional<Fichier> findByNom(String nom);
}

