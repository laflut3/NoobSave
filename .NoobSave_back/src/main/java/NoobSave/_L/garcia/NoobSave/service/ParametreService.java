package NoobSave._L.garcia.NoobSave.service;

import NoobSave._L.garcia.NoobSave.entities.Parametre;
import NoobSave._L.garcia.NoobSave.repository.ParametreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des paramètres de configuration de l'application.
 *
 * Ce service permet de :
 * <ul>
 *     <li>Récupérer ou créer la configuration par défaut.</li>
 *     <li>Mettre à jour les paramètres de sauvegarde automatique.</li>
 *     <li>Gérer les extensions de fichiers autorisées.</li>
 *     <li>Configurer et valider le chemin de sauvegarde des fichiers.</li>
 * </ul>
 *
 * <b>Note :</b> Les paramètres sont stockés dans une entité unique {@link Parametre}.
 *
 * @author Votre Nom
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class ParametreService {

    private final ParametreRepository parametreRepository;

    /**
     * Récupère l'unique configuration (ou la crée par défaut si inexistante).
     *
     * <p>Cette méthode vérifie si une configuration existe en base de données. Si aucune configuration
     * n'est trouvée, une nouvelle instance avec des paramètres par défaut est créée et sauvegardée.</p>
     *
     * @return L'instance unique de {@link Parametre}.
     */
    public Parametre getParametre() {
        Optional<Parametre> opt = parametreRepository.findAll().stream().findFirst();
        if (opt.isPresent()) {
            return opt.get();
        } else {
            // Création d'un paramètre par défaut si rien en base
            Parametre defaut = new Parametre();
            defaut.setAutoSaveEnabled(true);          // par défaut on active la sauvegarde
            defaut.setAutoSaveInterval(60000L);       // 60s par défaut
            defaut.setAllowedFileExtensions(".pdf,.txt,.docx");
            return parametreRepository.save(defaut);
        }
    }


    /**
     * Active ou désactive la sauvegarde automatique.
     *
     * @param enabled Indique si la sauvegarde automatique doit être activée.
     */
    public void updateAutoSaveEnabled(boolean enabled) {
        Parametre p = getParametre();
        p.setAutoSaveEnabled(enabled);
        parametreRepository.save(p);
    }

    /**
     * Met à jour l'intervalle de sauvegarde automatique.
     *
     * @param interval Intervalle de temps en millisecondes entre deux sauvegardes.
     */
    public void updateAutoSaveInterval(long interval) {
        Parametre p = getParametre();
        p.setAutoSaveInterval(interval);
        parametreRepository.save(p);
    }

    /**
     * Met à jour la liste des extensions de fichiers autorisées.
     *
     * <p>Les extensions sont reçues sous forme de liste et concaténées en une chaîne séparée par des virgules.</p>
     *
     * @param extensions Liste des extensions autorisées (par exemple : [".pdf", ".docx", ".txt"]).
     */
    public void updateAllowedFileExtensions(List<String> extensions) {
        Parametre p = getParametre();
        // On concatène tout dans une seule string séparée par des virgules
        String joined = extensions.stream()
                .map(String::trim)
                .collect(Collectors.joining(","));
        p.setAllowedFileExtensions(joined);
        parametreRepository.save(p);
    }

    /**
     * Retourne la liste des extensions de fichiers autorisées.
     *
     * <p>Cette méthode convertit la chaîne stockée en base (séparée par des virgules) en une liste d'extensions.</p>
     *
     * @return Liste des extensions autorisées.
     */
    public List<String> getAllowedFileExtensions() {
        Parametre p = getParametre();
        String all = p.getAllowedFileExtensions();
        if (all == null || all.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(all.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    /**
     * Met à jour le chemin de sauvegarde des fichiers.
     *
     * <p>Valide que le chemin spécifié est un répertoire existant avant de le sauvegarder en base.</p>
     *
     * @param path Chemin de sauvegarde à définir.
     * @throws IllegalArgumentException Si le chemin est invalide ou inexistant.
     */
    public void updateSavePath(String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Le chemin de sauvegarde ne peut pas être vide.");
        }
        File file = new File(path);
        if (!file.exists() || !file.isDirectory()) {
            throw new IllegalArgumentException("Le chemin spécifié n'est pas un répertoire valide : " + path);
        }
        Parametre p = getParametre();
        System.out.println("Chemin reçu pour sauvegarde : " + path); // Journal
        p.setSavePath(path);
        parametreRepository.save(p);
    }


    /**
     * Réinitialise le chemin de sauvegarde à une valeur nulle.
     *
     * <p>Cette méthode supprime le chemin de sauvegarde défini dans la configuration actuelle.</p>
     */
    public void deleteSavePath() {
        Parametre p = getParametre();
        p.setSavePath(null); // Réinitialise le chemin
        parametreRepository.save(p); // Sauvegarde les modifications
    }



}
