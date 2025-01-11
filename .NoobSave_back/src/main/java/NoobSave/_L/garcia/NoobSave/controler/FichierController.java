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
import io.swagger.v3.oas.annotations.tags.Tag;
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
 *
 * Ce contrôleur expose plusieurs endpoints pour :
 * <ul>
 *   <li>Consulter et gérer les fichiers sauvegardés.</li>
 *   <li>Effectuer des sauvegardes et restaurations.</li>
 *   <li>Télécharger ou supprimer des fichiers spécifiques.</li>
 * </ul>
 *
 * <strong>Annotations Swagger :</strong>
 * <ul>
 *   <li>{@code @Tag} : Ajoute un groupe pour regrouper les endpoints dans Swagger UI.</li>
 *   <li>{@code @Operation} : Décrit le but de chaque endpoint.</li>
 *   <li>{@code @ApiResponses} : Documente les codes de réponse attendus.</li>
 *   <li>{@code @Parameter} et {@code @Schema} : Décrit les paramètres et le format attendu des entrées/sorties.</li>
 * </ul>
 */
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/fichiers")
@RequiredArgsConstructor
@Tag(name = "Fichiers", description = "API pour gérer les opérations liées aux fichiers.")
public class FichierController {

    private final FichierService fichierService;

    /**
     * Récupère la liste de tous les fichiers enregistrés dans la base de données.
     *
     * @return Une {@link ResponseEntity} contenant une liste de tous les fichiers.
     */
    @Operation(
            summary = "Obtenir la liste de tous les fichiers",
            description = "Renvoie la liste complète des fichiers sauvegardés dans la base de données.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Liste des fichiers récupérée avec succès",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Fichier.class),
                                    examples = @ExampleObject(
                                            value = "[{\"id\": \"123\", \"nom\": \"document.txt\", \"type\": \"text/plain\", \"chemin\": \"/archive/document.txt\"}]"
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Erreur interne lors de la récupération des fichiers")
            }
    )
    @GetMapping
    public ResponseEntity<List<Fichier>> obtenirTousLesFichiers() {
        return ResponseEntity.ok(fichierService.obtenirTousLesFichiers());
    }

    /**
     * Déclenche une sauvegarde manuelle des fichiers.
     *
     * @return Une {@link ResponseEntity} contenant un message de succès ou d'erreur.
     */
    @Operation(
            summary = "Déclencher une sauvegarde manuelle",
            description = "Lance une sauvegarde manuelle en synchronisant les fichiers avec le support de stockage configuré.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Sauvegarde déclenchée avec succès",
                            content = @Content(
                                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                                    examples = @ExampleObject(value = "Sauvegarde déclenchée avec succès !")
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Erreur lors de la sauvegarde")
            }
    )
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
     * @param id L'identifiant unique du fichier à télécharger.
     * @return Une {@link ResponseEntity} contenant le fichier en tant que ressource binaire ou un code 404 si introuvable.
     */
    @Operation(
            summary = "Télécharger un fichier",
            description = "Télécharge un fichier spécifique identifié par son ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Fichier téléchargé avec succès",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                                    schema = @Schema(type = "string", format = "binary")
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Fichier non trouvé")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ByteArrayResource> telechargerFichier(
            @Parameter(
                    name = "id",
                    description = "Identifiant unique du fichier",
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
     * @param id L'identifiant unique du fichier à supprimer.
     * @return Une {@link ResponseEntity} indiquant le succès ou l'échec de l'opération.
     */
    @Operation(
            summary = "Supprimer un fichier",
            description = "Supprime un fichier de la base de données et du disque.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Fichier supprimé avec succès"),
                    @ApiResponse(responseCode = "404", description = "Fichier non trouvé")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerFichier(
            @Parameter(
                    name = "id",
                    description = "Identifiant unique du fichier",
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
     * Restaure tous les fichiers manquants sur le disque.
     *
     * @return Une {@link ResponseEntity} contenant un message indiquant le nombre de fichiers restaurés.
     */
    @Operation(
            summary = "Restaurer les fichiers manquants",
            description = "Restaure les fichiers absents du disque en se basant sur les données de la base.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Nombre de fichiers restaurés renvoyé",
                            content = @Content(
                                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                                    examples = @ExampleObject(value = "5 fichier(s) restauré(s) avec succès !")
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "Erreur lors de la restauration des fichiers")
            }
    )
    @GetMapping("/restore")
    public ResponseEntity<String> restaurerFichiers() {
        int restoredCount = fichierService.restaurerFichiersManquants();
        return ResponseEntity.ok(restoredCount + " fichier(s) restauré(s) avec succès !");
    }

    /**
     * Renvoie la liste des sous-répertoires contenant des fichiers.
     *
     * @return Une {@link ResponseEntity} contenant la liste des sous-répertoires.
     */
    @Operation(
            summary = "Lister les sous-répertoires",
            description = "Récupère la liste des sous-dossiers où des fichiers sont enregistrés.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Liste des sous-répertoires renvoyée avec succès",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    examples = @ExampleObject(value = "[\"documents\", \"images\", \"archives\"]")
                            )
                    )
            }
    )
    @GetMapping("/subdirectories")
    public ResponseEntity<List<String>> getSubdirectories() {
        return ResponseEntity.ok(fichierService.listerSousRepertoires());
    }

    /**
     * Restaure les fichiers manquants pour un sous-répertoire spécifique.
     *
     * @param sousRepertoire Nom du sous-répertoire cible pour la restauration (vide pour la racine).
     * @return Une {@link ResponseEntity} contenant le nombre de fichiers restaurés pour ce sous-répertoire.
     */
    @Operation(
            summary = "Restaurer les fichiers d'un sous-répertoire",
            description = "Restaure les fichiers absents dans un sous-répertoire spécifique en se basant sur les données de la base de données.",
            responses = {
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
                    @ApiResponse(
                            responseCode = "500",
                            description = "Erreur lors de la restauration du sous-répertoire",
                            content = @Content(
                                    mediaType = MediaType.TEXT_PLAIN_VALUE,
                                    examples = @ExampleObject(value = "Erreur lors de la restauration du sous-répertoire.")
                            )
                    )
            }
    )
    @GetMapping("/restore-sous-repertoire")
    public ResponseEntity<String> restaurerSousRepertoire(
            @Parameter(
                    name = "sousRepertoire",
                    description = "Nom du sous-répertoire dans lequel les fichiers doivent être restaurés.",
                    example = "images",
                    required = true
            )
            @RequestParam String sousRepertoire) {
        try {
            int restoredCount = fichierService.restaurerFichiersPourSousRepertoire(sousRepertoire);
            String message = restoredCount + " fichier(s) restauré(s) pour le sous-répertoire : " +
                    (sousRepertoire.isEmpty() ? "(racine)" : sousRepertoire);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de la restauration du sous-répertoire : " + e.getMessage());
        }
    }

}
