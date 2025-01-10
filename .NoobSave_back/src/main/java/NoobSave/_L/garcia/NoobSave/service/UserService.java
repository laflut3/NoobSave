package NoobSave._L.garcia.NoobSave.service;

import NoobSave._L.garcia.NoobSave.dto.LoginRequest;
import NoobSave._L.garcia.NoobSave.dto.RegisterRequest;
import NoobSave._L.garcia.NoobSave.entities.User;
import NoobSave._L.garcia.NoobSave.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Méthode pour créer un nouvel utilisateur (REGISTER)
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

    // Méthode pour authentifier l'utilisateur (LOGIN)
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

    // Trouver un user par username (utilisé pour la validation du token)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }


}
