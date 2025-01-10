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
 * Ce contrôleur expose diverses fonctionnalités comme :
 * <ul>
 *   <li>Liste des fichiers sauvegardés</li>
 *   <li>Téléchargement, suppression et restauration des fichiers</li>
 *   <li>Détection et restauration des fichiers manquants</li>
 * </ul>
 * </p>
 */
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/fichiers")
@RequiredArgsConstructor
public class FichierController {

    private final FichierService fichierService;

    /**
     * Récupère la liste de tous les fichiers enregistrés dans la base de données.
     *
     * @return ResponseEntity contenant une liste de tous les fichiers.
     */
    @Operation(
            summary = "Obtenir tous les fichiers",
            description = "Renvoie la liste complète des fichiers enregistrés en base de données avec leurs détails."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste des fichiers récupérée avec succès",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Fichier.class),
                            examples = @ExampleObject(
                                    name = "Exemple de réponse",
                                    value = """
                                    [
                                      {
                                        "id": "63c2f5e5ab12ef00123",
                                        "nom": "document.pdf",
                                        "type": "application/pdf",
                                        "taille": 2048,
                                        "contenu": "Base64EncodedData"
                                      }
                                    ]
                                    """
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Erreur interne lors de la récupération des fichiers")
    })
    @GetMapping
    public ResponseEntity<List<Fichier>> obtenirTousLesFichiers() {
        return ResponseEntity.ok(fichierService.obtenirTousLesFichiers());
    }

    /**
     * Déclenche une sauvegarde manuelle des fichiers.
     *
     * @return Message de confirmation ou erreur.
     */
    @Operation(
            summary = "Déclencher une sauvegarde manuelle",
            description = "Lance le processus de sauvegarde de tous les fichiers enregistrés."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Sauvegarde déclenchée avec succès",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Sauvegarde déclenchée avec succès !")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erreur lors de la sauvegarde",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "Erreur lors de la sauvegarde : Détails de l'erreur.")
                    )
            )
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
     * Télécharge un fichier spécifique par son identifiant.
     *
     * @param id Identifiant unique du fichier.
     * @return Le fichier en tant que ressource binaire ou 404 si introuvable.
     */
    @Operation(
            summary = "Télécharger un fichier",
            description = "Télécharge un fichier à partir de son identifiant unique dans la base de données."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Fichier téléchargé avec succès",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            schema = @Schema(type = "string", format = "binary"),
                            examples = @ExampleObject(value = "Contenu binaire du fichier")
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Fichier non trouvé")
    })
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
     * Supprime un fichier spécifique.
     *
     * @param id Identifiant du fichier à supprimer.
     * @return Code 200 si succès, 404 sinon.
     */
    @Operation(
            summary = "Supprimer un fichier",
            description = "Supprime un fichier à partir de son identifiant unique."
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
     * Restaure tous les fichiers manquants.
     *
     * @return Nombre de fichiers restaurés.
     */
    @Operation(
            summary = "Restaurer tous les fichiers manquants",
            description = "Compare les fichiers en base de données avec ceux sur le disque et restaure les fichiers manquants."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Nombre de fichiers restaurés",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(value = "5 fichier(s) restauré(s) avec succès !")
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Erreur lors de la restauration")
    })
    @GetMapping("/restore")
    public ResponseEntity<String> restaurerFichiers() {
        int restoredCount = fichierService.restaurerFichiersManquants();
        return ResponseEntity.ok(restoredCount + " fichier(s) restauré(s) avec succès !");
    }
}
