package NoobSave._L.garcia.NoobSave.controler;

import NoobSave._L.garcia.NoobSave.entities.Parametre;
import NoobSave._L.garcia.NoobSave.entities.User;
import NoobSave._L.garcia.NoobSave.service.ParametreService;
// --> Swagger/OpenAPI imports (assurez-vous de les avoir dans votre classpath)
// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.media.Content;
// import io.swagger.v3.oas.annotations.media.Schema;
// import io.swagger.v3.oas.annotations.responses.ApiResponse;
// import io.swagger.v3.oas.annotations.responses.ApiResponses;
// import io.swagger.v3.oas.annotations.tags.Tag;

import NoobSave._L.garcia.NoobSave.service.UtilsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Contrôleur REST pour gérer les paramètres de l'application (sauvegarde automatique,
 * extensions autorisées, chemin de sauvegarde, etc.).
 */
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/parametres")
@RequiredArgsConstructor
@Tag(name = "Paramètres", description = "Gère la configuration de l'application (auto-save, extensions autorisées, chemin, etc.)")
public class ParametreController {

    private final ParametreService parametreService;

    private final UtilsService utils;

    /**
     * Récupère la configuration actuelle.
     *
     * @return l'entité {@link Parametre} contenant la configuration de l'application.
     */
    @Operation(summary = "Obtenir la configuration actuelle",
            description = "Renvoie l'objet Parametre qui contient les réglages actuels de l'application.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuration récupérée avec succès")
    })
    @GetMapping
    public Parametre getParametres(Authentication authentication) {
        if (!utils.isAdmin(authentication)) {
            System.out.println("Permission refusée pour user: " + authentication.getName());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès interdit");
        }
        return parametreService.getParametre();
    }

    /**
     * Active ou désactive la sauvegarde automatique.
     *
     * @param enabled valeur booléenne indiquant si l'auto-save doit être activé (true) ou non (false).
     */
    @Operation(summary = "Activer/Désactiver la sauvegarde automatique",
            description = "Permet de changer l'état de la sauvegarde automatique de l'application (true pour activer, false pour désactiver).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "État de la sauvegarde automatique mis à jour avec succès")
    })
    @PostMapping("/toggle-auto-save")
    public void toggleAutoSave(
            @RequestParam
            @Schema(description = "Indique si l'auto-save doit être activé ou non", example = "true")
            boolean enabled
    ) {
        parametreService.updateAutoSaveEnabled(enabled);
    }

    /**
     * Met à jour l'intervalle (en millisecondes) pour la sauvegarde automatique.
     *
     * @param intervalMs nouvelle valeur d'intervalle pour l'auto-save en millisecondes.
     */
    @Operation(summary = "Mettre à jour l'intervalle de sauvegarde automatique",
            description = "Définit le délai (en millisecondes) entre deux sauvegardes automatiques.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Intervalle de sauvegarde mis à jour avec succès")
    })
    @PostMapping("/interval")
    public void setAutoSaveInterval(
            @RequestParam
            @Schema(description = "Nouveau délai entre deux sauvegardes auto, en millisecondes", example = "60000")
            long intervalMs
    ) {
        parametreService.updateAutoSaveInterval(intervalMs);
    }

    /**
     * Met à jour la liste des extensions autorisées pour les fichiers.
     * Par exemple, via body/form-data => filetypes=.pdf&filetypes=.docx&filetypes=.txt
     *
     * @param filetypes liste des extensions autorisées (ex: [.pdf, .docx, .txt]).
     */
    @Operation(summary = "Mettre à jour la liste d'extensions de fichiers autorisées",
            description = "Définit ou modifie les extensions (avec le point) que l'application accepte pour la sauvegarde.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des extensions autorisées mise à jour avec succès")
    })
    @PostMapping("/filetypes")
    public void setAllowedFileExtensions(
            @RequestParam
            @Schema(description = "Liste des extensions autorisées (par ex. .pdf, .txt)", example = "[\".pdf\",\".docx\",\".txt\"]")
            List<String> filetypes
    ) {
        parametreService.updateAllowedFileExtensions(filetypes);
    }

    /**
     * Met à jour le chemin de sauvegarde.
     *
     * @param path nouveau chemin de sauvegarde pour l'archive.
     */
    @Operation(summary = "Définir le chemin de sauvegarde",
            description = "Met à jour le chemin (absolu ou relatif) où les fichiers seront sauvegardés.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chemin de sauvegarde mis à jour avec succès")
    })
    @PostMapping("/save-path")
    public void setSavePath(
            @RequestParam
            @Schema(description = "Nouveau chemin de sauvegarde", example = "/home/user/noobsave/archive")
            String path
    ) {
        parametreService.updateSavePath(path);
    }

    /**
     * Récupère le chemin de sauvegarde actuel.
     *
     * @return chaîne de caractères représentant le chemin de sauvegarde.
     */
    @Operation(summary = "Obtenir le chemin de sauvegarde",
            description = "Renvoie le chemin actuellement utilisé pour la sauvegarde.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chemin de sauvegarde récupéré avec succès")
    })
    @GetMapping("/save-path")
    public String getSavePath() {
        return parametreService.getParametre().getSavePath();
    }

    /**
     * Supprime le chemin de sauvegarde actuel (le réinitialise à null).
     */
    @Operation(summary = "Supprimer le chemin de sauvegarde",
            description = "Réinitialise le chemin de sauvegarde en le mettant à null.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chemin de sauvegarde supprimé (réinitialisé) avec succès")
    })
    @DeleteMapping("/save-path")
    public void deleteSavePath() {
        parametreService.deleteSavePath();
    }
}
