package NoobSave._L.garcia.NoobSave.controler;

import NoobSave._L.garcia.NoobSave.entities.Fichier;
import NoobSave._L.garcia.NoobSave.service.FichierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
 * <p>
 * Ce contrôleur expose différentes opérations pour :
 * <ul>
 *   <li>Consulter la liste de tous les fichiers sauvegardés</li>
 *   <li>Télécharger un fichier par son identifiant</li>
 *   <li>Supprimer un fichier</li>
 *   <li>Déclencher une sauvegarde manuelle</li>
 *   <li>Restaurer un ou plusieurs fichiers manquants</li>
 *   <li>Obtenir la liste des sous-répertoires contenant des fichiers</li>
 *   <li>Restaurer les fichiers d’un sous-répertoire spécifique</li>
 * </ul>
 */
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/fichiers")
@RequiredArgsConstructor
public class FichierController {

    /**
     * Service pour gérer les opérations sur les fichiers (CRUD, restauration, sauvegarde).
     */
    private final FichierService fichierService;

    /**
     * Récupère la liste de tous les fichiers enregistrés dans la base de données.
     *
     * @return ResponseEntity contenant une liste de tous les fichiers.
     */
    @Operation(
            summary = "Obtenir la liste de tous les fichiers",
            description = "Renvoie la liste complète des fichiers présents en base de données."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des fichiers récupérée avec succès"),
            @ApiResponse(responseCode = "500", description = "Erreur interne lors de la récupération des fichiers")
    })
    @GetMapping
    public ResponseEntity<List<Fichier>> obtenirTousLesFichiers() {
        return ResponseEntity.ok(fichierService.obtenirTousLesFichiers());
    }

    /**
     * Déclenche une sauvegarde manuelle des fichiers (copie sur le disque, etc.).
     *
     * @return Message indiquant que la sauvegarde s'est bien déroulée ou mentionnant une erreur.
     */
    @Operation(
            summary = "Déclencher une sauvegarde manuelle",
            description = "Lance le processus de sauvegarde : copie et enregistrement des fichiers sur le support configuré."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sauvegarde déclenchée avec succès"),
            @ApiResponse(responseCode = "500", description = "Erreur lors de la sauvegarde")
    })
    @GetMapping("/save")
    public ResponseEntity<String> declencherSauvegarde() {
        try {
            fichierService.saveDeclencher();
            return ResponseEntity.ok("Sauvegarde déclenchée avec succès !");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }

    /**
     * Télécharge un fichier spécifique en fonction de son identifiant.
     *
     * @param id identifiant du fichier à télécharger.
     * @return ResponseEntity contenant le fichier sous forme de ressource binaire ou un code 404 si le fichier est introuvable.
     */
    @Operation(
            summary = "Télécharger un fichier par son identifiant",
            description = "Permet de télécharger un fichier spécifique en se basant sur son identifiant unique."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Fichier téléchargé avec succès",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(type = "string", format = "binary")
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Fichier non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ByteArrayResource> telechargerFichier(
            @Parameter(
                    name = "id",
                    description = "Identifiant unique du fichier en base de données",
                    example = "63c2f5e5ab12ef00123",
                    required = true
            )
            @PathVariable String id) {
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
     * @return ResponseEntity avec un statut 200 si la suppression réussit, sinon 404.
     */
    @Operation(
            summary = "Supprimer un fichier",
            description = "Supprime un fichier de la base de données et du disque (si présent) en fonction de son identifiant."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fichier supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Fichier non trouvé")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerFichier(
            @Parameter(
                    name = "id",
                    description = "Identifiant unique du fichier à supprimer",
                    example = "63c2f5e5ab12ef00123",
                    required = true
            )
            @PathVariable String id) {
        Optional<Fichier> fichierOptional = fichierService.obtenirFichierParId(id);

        if (fichierOptional.isPresent()) {
            fichierService.supprimerFichier(fichierOptional.get());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Restaure tous les fichiers manquants sur le disque à partir de la base de données.
     *
     * @return ResponseEntity avec un message indiquant le nombre de fichiers restaurés.
     */
    @Operation(
            summary = "Restaurer tous les fichiers manquants",
            description = "Compare les fichiers présents en base de données avec ceux sur le disque, et restaure ceux qui manquent."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Nombre de fichiers restaurés renvoyé",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            examples = @ExampleObject(value = "5 fichier(s) restauré(s) avec succès !")
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Erreur lors de la restauration des fichiers")
    })
    @GetMapping("/restore")
    public ResponseEntity<String> restaurerFichiers() {
        int restoredCount = fichierService.restaurerFichiersManquants();
        return ResponseEntity.ok(restoredCount + " fichier(s) restauré(s) avec succès !");
    }

    /**
     * Renvoie la liste des sous-répertoires détectés qui contiennent au moins un fichier en base de données.
     *
     * @return ResponseEntity contenant la liste des sous-répertoires.
     */
    @Operation(
            summary = "Lister les sous-répertoires contenant des fichiers",
            description = "Récupère la liste des sous-dossiers connus où des fichiers sont enregistrés."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Liste des sous-répertoires récupérée avec succès")
    })
    @GetMapping("/subdirectories")
    public ResponseEntity<List<String>> getSubdirectories() {
        return ResponseEntity.ok(fichierService.listerSousRepertoires());
    }

    /**
     * Restaure tous les fichiers manquants pour un sous-répertoire donné.
     *
     * @param sousRepertoire Nom du sous-répertoire à restaurer (vide ou 'racine' si vous souhaitez restaurer le répertoire principal).
     * @return ResponseEntity contenant le nombre de fichiers restaurés pour ce sous-répertoire.
     */
    @Operation(
            summary = "Restaurer les fichiers d'un sous-répertoire",
            description = "Permet de restaurer uniquement les fichiers manquants d'un sous-répertoire spécifique."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Nombre de fichiers restaurés pour le sous-répertoire indiqué",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            examples = {
                                    @ExampleObject(name = "Sous-répertoire vide (racine)", value = "3 fichier(s) restauré(s) pour le sous-répertoire : (racine)"),
                                    @ExampleObject(name = "Sous-répertoire images", value = "2 fichier(s) restauré(s) pour le sous-répertoire : images")
                            }
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Erreur lors de la restauration du sous-répertoire")
    })
    @GetMapping("/restore-sous-repertoire")
    public ResponseEntity<String> restaurerSousRepertoire(
            @Parameter(
                    name = "sousRepertoire",
                    description = "Nom du sous-répertoire dans lequel restaurer les fichiers",
                    example = "images"
            )
            @RequestParam String sousRepertoire) {
        int restoredCount = fichierService.restaurerFichiersPourSousRepertoire(sousRepertoire);
        return ResponseEntity.ok(
                restoredCount + " fichier(s) restauré(s) pour le sous-répertoire : " +
                        (sousRepertoire.isEmpty() ? "(racine)" : sousRepertoire)
        );
    }
}
