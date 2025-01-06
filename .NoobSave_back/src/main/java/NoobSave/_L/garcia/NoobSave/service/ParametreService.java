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

@Service
@RequiredArgsConstructor
public class ParametreService {

    private final ParametreRepository parametreRepository;

    /**
     * Récupère l'unique configuration (ou la crée par défaut s'il n'y en a pas).
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
     * Active / désactive la sauvegarde automatique.
     */
    public void updateAutoSaveEnabled(boolean enabled) {
        Parametre p = getParametre();
        p.setAutoSaveEnabled(enabled);
        parametreRepository.save(p);
    }

    /**
     * Met à jour l'intervalle de sauvegarde (en ms).
     */
    public void updateAutoSaveInterval(long interval) {
        Parametre p = getParametre();
        p.setAutoSaveInterval(interval);
        parametreRepository.save(p);
    }

    /**
     * Met à jour la liste des extensions autorisées.
     * On reçoit par ex. [".pdf", ".docx", ".txt"]
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
     * Retourne les extensions autorisées sous forme de liste.
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
     * Met à jour le chemin de sauvegarde.
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
     * Supprime le chemin de sauvegarde en le réinitialisant à null.
     */
    public void deleteSavePath() {
        Parametre p = getParametre();
        p.setSavePath(null); // Réinitialise le chemin
        parametreRepository.save(p); // Sauvegarde les modifications
    }



}
