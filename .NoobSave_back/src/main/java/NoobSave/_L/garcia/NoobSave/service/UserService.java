package NoobSave._L.garcia.NoobSave.service;

import NoobSave._L.garcia.NoobSave.dto.LoginRequest;
import NoobSave._L.garcia.NoobSave.dto.RegisterRequest;
import NoobSave._L.garcia.NoobSave.entities.User;
import NoobSave._L.garcia.NoobSave.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service pour la gestion des utilisateurs.
 *
 * <p>Ce service fournit des méthodes pour l'enregistrement, l'authentification et la récupération
 * des utilisateurs. Il utilise un encodeur de mots de passe pour sécuriser les données sensibles
 * et vérifie la disponibilité des noms d'utilisateur et des emails lors de l'inscription.</p>
 *
 * <b>Note :</b> Ce service est conçu pour fonctionner avec un système de sécurité basé sur Spring Security.
 *
 * @author Votre Nom
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Inscrit un nouvel utilisateur.
     *
     * <p>Cette méthode crée un nouvel utilisateur après avoir vérifié que le nom d'utilisateur et l'email
     * ne sont pas déjà utilisés. Le mot de passe est haché avant d'être stocké en base.</p>
     *
     * @param request Objet contenant les informations d'inscription, comme le nom d'utilisateur,
     *                l'email et le mot de passe.
     * @return L'utilisateur nouvellement créé.
     * @throws RuntimeException Si le nom d'utilisateur ou l'email est déjà utilisé.
     */
    public User register(RegisterRequest request) {
        // Vérifier si l’utilisateur existe déjà
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username déjà utilisé");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }

        // On hache le mot de passe
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(hashedPassword);
        newUser.setAdministrateur(false);

        return userRepository.save(newUser);
    }

    /**
     * Authentifie un utilisateur.
     *
     * <p>Cette méthode vérifie que le nom d'utilisateur existe et que le mot de passe fourni correspond
     * au mot de passe haché stocké en base de données. Si les informations sont correctes, l'utilisateur
     * est retourné, sinon {@code null} est retourné.</p>
     *
     * @param request Objet contenant le nom d'utilisateur et le mot de passe.
     * @return L'utilisateur authentifié ou {@code null} si les informations sont incorrectes.
     */
    public User authenticate(LoginRequest request) {
        Optional<User> optUser = userRepository.findByUsername(request.getUsername());
        if (optUser.isEmpty()) {
            return null;
        }

        User user = optUser.get();
        // On compare le password haché
        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        return matches ? user : null;
    }

    /**
     * Trouve un utilisateur par son nom d'utilisateur.
     *
     * <p>Cette méthode est principalement utilisée pour valider les tokens d'authentification.
     * Elle lève une exception si aucun utilisateur correspondant n'est trouvé.</p>
     *
     * @param username Nom d'utilisateur à rechercher.
     * @return L'utilisateur correspondant.
     * @throws RuntimeException Si aucun utilisateur avec ce nom d'utilisateur n'est trouvé.
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }


}
