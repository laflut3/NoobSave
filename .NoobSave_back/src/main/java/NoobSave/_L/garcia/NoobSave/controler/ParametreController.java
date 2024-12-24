package NoobSave._L.garcia.NoobSave.controler;

import NoobSave._L.garcia.NoobSave.entities.Parametre;
import NoobSave._L.garcia.NoobSave.service.ParametreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parametres")
@RequiredArgsConstructor
public class ParametreController {

    private final ParametreService parametreService;

    /**
     * Récupère la configuration actuelle.
     */
    @GetMapping
    public Parametre getParametres() {
        return parametreService.getParametre();
    }

    /**
     * Active ou désactive la sauvegarde automatique.
     */
    @PostMapping("/toggle-auto-save")
    public void toggleAutoSave(@RequestParam boolean enabled) {
        parametreService.updateAutoSaveEnabled(enabled);
    }

    /**
     * Met à jour l'intervalle (en millisecondes) pour la sauvegarde.
     */
    @PostMapping("/interval")
    public void setAutoSaveInterval(@RequestParam long intervalMs) {
        parametreService.updateAutoSaveInterval(intervalMs);
    }

    /**
     * Met à jour la liste des extensions autorisées.
     * Ex : body/form-data => filetypes=.pdf&filetypes=.docx&filetypes=.txt
     */
    @PostMapping("/filetypes")
    public void setAllowedFileExtensions(@RequestParam List<String> filetypes) {
        parametreService.updateAllowedFileExtensions(filetypes);
    }
}
