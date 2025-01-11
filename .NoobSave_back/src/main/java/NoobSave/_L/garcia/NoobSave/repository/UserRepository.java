package NoobSave._L.garcia.NoobSave.repository;

import NoobSave._L.garcia.NoobSave.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository pour gérer les opérations de persistance liées à l'entité {@link User}.
 * <p>
 * Cette interface étend {@link MongoRepository}, offrant des méthodes standard pour les opérations CRUD (Create, Read, Update, Delete)
 * ainsi que des méthodes de requête personnalisées pour répondre à des besoins spécifiques.
 * </p>
 *
 * <strong>Principales fonctionnalités :</strong>
 * <ul>
 *     <li>Héritage des méthodes CRUD de {@link MongoRepository}, telles que {@code save}, {@code findById}, {@code deleteById}, etc.</li>
 *     <li>Ajout de méthodes personnalisées comme {@code findByUsername}, {@code existsByUsername} et {@code existsByEmail}.</li>
 * </ul>
 *
 * <strong>Annotations utilisées :</strong>
 * <ul>
 *     <li>{@code @Repository} : Indique que cette interface est un composant Spring responsable des opérations de persistance.</li>
 * </ul>
 *
 * <strong>Exemple d'utilisation dans les services :</strong>
 * <pre>{@code
 * @Service
 * public class UserService {
 *
 *     private final UserRepository userRepository;
 *
 *     @Autowired
 *     public UserService(UserRepository userRepository) {
 *         this.userRepository = userRepository;
 *     }
 *
 *     public Optional<User> findUserByUsername(String username) {
 *         return userRepository.findByUsername(username);
 *     }
 *
 *     public boolean isUsernameTaken(String username) {
 *         return userRepository.existsByUsername(username);
 *     }
 *
 *     public boolean isEmailTaken(String email) {
 *         return userRepository.existsByEmail(email);
 *     }
 * }
 * }</pre>
 *
 *
 * <strong>Bonnes pratiques :</strong>
 * <ul>
 *     <li>Utilisez les méthodes dérivées comme {@code findByUsername} pour éviter d'écrire des requêtes personnalisées complexes.</li>
 *     <li>Préférez {@code Optional} pour gérer les résultats pouvant être absents, évitant ainsi les erreurs de type {@code NullPointerException}.</li>
 *     <li>Validez les entrées dans la couche service avant d'appeler le repository.</li>
 *     <li>Utilisez des DTOs pour éviter d'exposer directement les entités dans les réponses API.</li>
 * </ul>
 *
 *
 * @see User
 * @see MongoRepository
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Recherche un utilisateur par son nom d'utilisateur.
     * <p>
     * Permet de retrouver un utilisateur en fonction de son nom unique.
     * </p>
     *
     * @param username Le nom d'utilisateur recherché.
     * @return Un {@link Optional} contenant l'utilisateur trouvé, ou vide si aucun utilisateur n'est trouvé.
     *
     * <strong>Exemple d'utilisation :</strong>
     * <pre>{@code
     * Optional<User> user = userRepository.findByUsername("JohnDoe");
     * user.ifPresent(u -> System.out.println("Utilisateur trouvé : " + u.getUsername()));
     * }</pre>
     */
    Optional<User> findByUsername(String username);

    /**
     * Vérifie l'existence d'un utilisateur par son nom d'utilisateur.
     * <p>
     * Détermine si un utilisateur avec le nom spécifié existe déjà dans la base de données.
     * </p>
     *
     * @param username Le nom d'utilisateur à vérifier.
     * @return {@code true} si un utilisateur avec ce nom existe, {@code false} sinon.
     */
    boolean existsByUsername(String username);

    /**
     * Vérifie l'existence d'un utilisateur par son adresse e-mail.
     * <p>
     * Détermine si un utilisateur avec l'adresse e-mail spécifiée existe déjà dans la base de données.
     * </p>
     *
     * @param email L'adresse e-mail à vérifier.
     * @return {@code true} si un utilisateur avec cette adresse existe, {@code false} sinon.
     */
    boolean existsByEmail(String email);
}
