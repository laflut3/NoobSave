package NoobSave._L.garcia.NoobSave.service;

import NoobSave._L.garcia.NoobSave.entities.User;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Service utilitaire pour les vérifications liées aux utilisateurs.
 *
 * <p>Ce service fournit des méthodes pour effectuer des vérifications courantes, telles que la
 * vérification des privilèges administratifs d'un utilisateur authentifié.</p>
 *
 * @author Votre Nom
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class UtilsService {

    /**
     * Vérifie si l'utilisateur authentifié est un administrateur.
     *
     * <p>Cette méthode détermine si l'utilisateur actuellement authentifié dispose des privilèges
     * administratifs en vérifiant son rôle.</p>
     *
     * @param authentication Objet d'authentification contenant les informations de l'utilisateur.
     * @return {@code true} si l'utilisateur est un administrateur, {@code false} sinon.
     */
    public boolean isAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        if (authentication.getPrincipal() instanceof User user) {
            return user.isAdministrateur();
        }

        return false;
    }
}
