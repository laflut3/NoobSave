package NoobSave._L.garcia.NoobSave.controler;

import NoobSave._L.garcia.NoobSave.entities.Fichier;
import NoobSave._L.garcia.NoobSave.service.FichierService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fichiers")
@RequiredArgsConstructor
public class FichierController {

    private final FichierService fichierService;

    @GetMapping
    public ResponseEntity<List<Fichier>> obtenirTousLesFichiers() {
        return ResponseEntity.ok(fichierService.obtenirTousLesFichiers());
    }

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
}

