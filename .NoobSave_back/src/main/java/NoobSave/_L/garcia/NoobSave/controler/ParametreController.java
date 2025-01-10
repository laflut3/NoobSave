package NoobSave._L.garcia.NoobSave.controler;

import NoobSave._L.garcia.NoobSave.entities.Parametre;
import NoobSave._L.garcia.NoobSave.service.ParametreService;
import NoobSave._L.garcia.NoobSave.service.UtilsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Contrôleur REST pour gérer les paramètres de l'application.
 * <p>
 * Ce contrôleur permet de configurer des paramètres tels que :
 * <ul>
 *     <li>Sauvegarde automatique</li>
 *     <li>Intervalle de sauvegarde</li>
 *     <li>Extensions de fichiers autorisées</li>
 *     <li>Chemin de sauvegarde</li>
 * </ul>
 * </p>
 */
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/parametres")
@RequiredArgsConstructor
@Tag(name = "Paramètres", description = "Gère la configuration de l'application : auto-save, extensions autorisées, chemin de sauvegarde, etc.")
public class ParametreController {

    private final ParametreService parametreService;
    private final UtilsService utils;

    /**
     * Récupère la configuration actuelle de l'application.
     *
     * @param authentication Objet représentant l'utilisateur authentifié.
     * @return L'entité {@link Parametre} contenant la configuration actuelle.
     */
    @Operation(
            summary = "Obtenir la configuration actuelle",
            description = "Renvoie les paramètres actuels de l'application.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Configuration récupérée avec succès"),
                    @ApiResponse(responseCode = "403", description = "Accès interdit pour les utilisateurs non administrateurs")
            }
    )
    @GetMapping
    public Parametre getParametres(Authentication authentication) {
        if (!utils.isAdmin(authentication)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès interdit");
        }
        return parametreService.getParametre();
    }

    /**
     * Active ou désactive la sauvegarde automatique.
     *
     * @param enabled Valeur booléenne indiquant si l'auto-save doit être activé (true) ou non (false).
     */
    @Operation(
            summary = "Activer/Désactiver la sauvegarde automatique",
            description = "Modifie l'état de la sauvegarde automatique.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "État souhaité pour la sauvegarde automatique.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(description = "État de l'auto-save", example = "true")
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "État de la sauvegarde automatique mis à jour avec succès")
            }
    )
    @PostMapping("/toggle-auto-save")
    public void toggleAutoSave(@RequestParam boolean enabled) {
        parametreService.updateAutoSaveEnabled(enabled);
    }

    /**
     * Met à jour l'intervalle de sauvegarde automatique.
     *
     * @param intervalMs Intervalle en millisecondes entre deux sauvegardes.
     */
    @Operation(
            summary = "Mettre à jour l'intervalle de sauvegarde automatique",
            description = "Définit le délai en millisecondes entre deux sauvegardes automatiques.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Intervalle en millisecondes.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(description = "Intervalle en millisecondes", example = "60000")
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Intervalle mis à jour avec succès")
            }
    )
    @PostMapping("/interval")
    public void setAutoSaveInterval(@RequestParam long intervalMs) {
        parametreService.updateAutoSaveInterval(intervalMs);
    }

    /**
     * Met à jour les extensions de fichiers autorisées.
     *
     * @param filetypes Liste des extensions autorisées (ex. : [.pdf, .docx, .txt]).
     */
    @Operation(
            summary = "Mettre à jour les extensions de fichiers autorisées",
            description = "Définit les extensions de fichiers acceptées pour la sauvegarde.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Liste des extensions autorisées.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(description = "Extensions autorisées", example = "[\".pdf\", \".txt\", \".docx\"]")
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Extensions mises à jour avec succès")
            }
    )
    @PostMapping("/filetypes")
    public void setAllowedFileExtensions(@RequestParam List<String> filetypes) {
        parametreService.updateAllowedFileExtensions(filetypes);
    }

    /**
     * Met à jour le chemin de sauvegarde.
     *
     * @param path Nouveau chemin de sauvegarde.
     */
    @Operation(
            summary = "Définir le chemin de sauvegarde",
            description = "Modifie le chemin utilisé pour sauvegarder les fichiers.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nouveau chemin de sauvegarde.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(description = "Chemin de sauvegarde", example = "/home/user/noobsave")
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Chemin de sauvegarde mis à jour avec succès")
            }
    )
    @PostMapping("/save-path")
    public void setSavePath(@RequestParam String path) {
        parametreService.updateSavePath(path);
    }

    /**
     * Supprime le chemin de sauvegarde actuel (le réinitialise à null).
     */
    @Operation(
            summary = "Réinitialiser le chemin de sauvegarde",
            description = "Supprime le chemin actuellement défini pour la sauvegarde.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Chemin réinitialisé avec succès")
            }
    )
    @DeleteMapping("/save-path")
    public void deleteSavePath() {
        parametreService.deleteSavePath();
    }
}
