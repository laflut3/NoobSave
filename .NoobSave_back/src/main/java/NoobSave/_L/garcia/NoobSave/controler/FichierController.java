package NoobSave._L.garcia.NoobSave.controler;

import NoobSave._L.garcia.NoobSave.entities.Fichier;
import NoobSave._L.garcia.NoobSave.service.FichierService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Contrôleur REST pour gérer les fichiers via des requêtes HTTP.
 * Permet la récupération, le téléchargement et la suppression de fichiers.
 */
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/fichiers")
@RequiredArgsConstructor
public class FichierController {

    /**
     * Service pour gérer les opérations sur les fichiers.
     */
    private final FichierService fichierService;

    /**
     * Récupère la liste de tous les fichiers enregistrés.
     *
     * @return ResponseEntity contenant une liste de tous les fichiers.
     */
    @GetMapping
    public ResponseEntity<List<Fichier>> obtenirTousLesFichiers() {
        return ResponseEntity.ok(fichierService.obtenirTousLesFichiers());
    }

    /**
     * Récupère la liste de tous les fichiers enregistrés.
     *
     * ResponseEntity contenant une liste de tous les fichiers.
     */
    @GetMapping(path = "/save")
    public ResponseEntity<String> declencherSauvegarde() throws IOException {
        fichierService.saveDeclencher();
        return ResponseEntity.ok("Sauvegarde déclenchée avec succès !");
    }

    /**
     * Télécharge un fichier spécifique en fonction de son identifiant.
     *
     * @param id identifiant du fichier à télécharger.
     * @return ResponseEntity contenant le fichier sous forme de ressource binaire, ou un code 404 si le fichier n'existe pas.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ByteArrayResource> telechargerFichier(@PathVariable Long id) {
        return fichierService.obtenirFichierParId(id)
                .map(fichier -> {
                    ByteArrayResource resource = new ByteArrayResource(fichier.getContenu());
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fichier.getNom())
                            .contentType(MediaType.parseMediaType(fichier.getType()))
                            .body(resource);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Supprime un fichier spécifique en fonction de son identifiant.
     *
     * @param id identifiant du fichier à supprimer.
     * @return ResponseEntity avec un statut 200 si la suppression réussit, sinon un statut 404.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerFichier(@PathVariable Long id) {
        Optional<Fichier> fichierOptional = fichierService.obtenirFichierParId(id);

        if (fichierOptional.isPresent()) {
            Fichier fichier = fichierOptional.get();
            fichierService.supprimerFichier(fichier);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

