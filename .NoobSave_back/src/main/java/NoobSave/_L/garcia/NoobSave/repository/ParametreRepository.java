package NoobSave._L.garcia.NoobSave.repository;

import NoobSave._L.garcia.NoobSave.entities.Parametre;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository pour gérer les opérations de persistance liées à l'entité {@link Parametre}.
 * <p>
 * Cette interface étend {@link MongoRepository}, offrant des méthodes standard pour les opérations CRUD (Create, Read, Update, Delete)
 * ainsi que la possibilité de définir des requêtes personnalisées.
 * </p>
 *
 * <p>
 * <strong>Fonctionnalités principales :</strong>
 * <ul>
 *     <li>Héritage des méthodes CRUD de {@link MongoRepository}, telles que {@code save}, {@code findById}, {@code deleteById}, etc.</li>
 *     <li>Possibilité d'ajouter des méthodes personnalisées en suivant les conventions de Spring Data.</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Annotations utilisées :</strong>
 * <ul>
 *     <li>{@code @Repository} : Indique que cette interface est un composant Spring responsable des opérations de persistance.</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Utilisation dans les services :</strong>
 * <pre>{@code
 * @Service
 * public class ParametreService {
 *
 *     private final ParametreRepository parametreRepository;
 *
 *     @Autowired
 *     public ParametreService(ParametreRepository parametreRepository) {
 *         this.parametreRepository = parametreRepository;
 *     }
 *
 *     public Parametre getParametres() {
 *         return parametreRepository.findAll().stream().findFirst().orElse(null);
 *     }
 *
 *     public void updateParametre(Parametre parametre) {
 *         parametreRepository.save(parametre);
 *     }
 * }
 * }</pre>
 *
 * <p>
 * <strong>Bonnes pratiques :</strong>
 * <ul>
 *     <li>Utilisez les méthodes héritées pour gérer les opérations standard sans implémentation supplémentaire.</li>
 *     <li>Créez des requêtes personnalisées uniquement si nécessaire, pour éviter de surcharger le repository.</li>
 *     <li>Utilisez {@code Optional} pour gérer les résultats pouvant être absents, évitant ainsi les erreurs.</li>
 *     <li>Manipulez les exceptions liées aux opérations de persistance dans la couche service pour une meilleure abstraction.</li>
 *     <li>Utilisez des DTOs (Data Transfer Objects) pour séparer la persistance des besoins de la couche de présentation.</li>
 * </ul>
 * </p>
 *
 * @see Parametre
 * @see MongoRepository
 */
@Repository
public interface ParametreRepository extends MongoRepository<Parametre, String> {

}
