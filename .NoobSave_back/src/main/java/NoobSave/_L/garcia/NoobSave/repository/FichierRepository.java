package NoobSave._L.garcia.NoobSave.repository;

import NoobSave._L.garcia.NoobSave.entities.Fichier;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository pour gérer les opérations de persistance sur l'entité {@link Fichier}.
 * <p>
 * Cette interface étend {@link MongoRepository}, ce qui permet d'utiliser des méthodes CRUD standard et de définir
 * des méthodes de requête personnalisées pour interagir avec la collection MongoDB.
 * </p>
 *
 * <strong>Principales fonctionnalités :</strong>
 * <ul>
 *     <li>Héritage des méthodes CRUD de {@link MongoRepository} : {@code save}, {@code findById}, {@code delete}, etc.</li>
 *     <li>Ajout de méthodes personnalisées pour des besoins spécifiques, comme {@code findByNom}.</li>
 * </ul>
 *
 * <strong>Annotations utilisées :</strong>
 * <ul>
 *     <li>{@code @Repository} : Indique que cette interface est un composant Spring gérant la persistance.</li>
 * </ul>
 *
 * <strong>Utilisation dans les services :</strong>
 * <pre>{@code
 * @Service
 * public class FichierService {
 *
 *     private final FichierRepository fichierRepository;
 *
 *     @Autowired
 *     public FichierService(FichierRepository fichierRepository) {
 *         this.fichierRepository = fichierRepository;
 *     }
 *
 *     public Optional<Fichier> trouverFichierParNom(String nom) {
 *         return fichierRepository.findByNom(nom);
 *     }
 *
 *     // Autres méthodes de service...
 * }
 * }</pre>
 *
 * <strong>Bonnes pratiques :</strong>
 * <ul>
 *     <li>Utilisez {@code Optional} pour éviter les erreurs liées aux valeurs nulles.</li>
 *     <li>Définissez des méthodes spécifiques uniquement si nécessaire, pour éviter une surcharge inutile du repository.</li>
 *     <li>Gérez les exceptions liées aux opérations de persistance dans la couche service pour une meilleure abstraction.</li>
 *     <li>Utilisez des DTOs pour séparer les données de la couche de persistance et la présentation.</li>
 * </ul>
 *
 * @see Fichier
 * @see MongoRepository
 */

@Repository
public interface FichierRepository extends MongoRepository<Fichier, String> {

    /**
     * Recherche un fichier en fonction de son nom.
     * <p>
     * Cette méthode permet de retrouver un fichier ayant un nom spécifique dans la base de données.
     * </p>
     *
     * @param nom Le nom du fichier recherché.
     * @return Un {@link Optional} contenant l'entité {@link Fichier} si elle est trouvée, ou vide sinon.
     *
     * <strong>Exemple d'utilisation :</strong>
     * <pre>{@code
     * Optional<Fichier> fichier = fichierRepository.findByNom("document.pdf");
     * fichier.ifPresent(f -> {
     *     System.out.println("Fichier trouvé : " + f.getNom());
     * });
     * }</pre>
     */

    Optional<Fichier> findByNom(String nom);
}
