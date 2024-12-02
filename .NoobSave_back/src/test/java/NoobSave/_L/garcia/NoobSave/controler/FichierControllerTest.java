package NoobSave._L.garcia.NoobSave.controler;

import static org.junit.jupiter.api.Assertions.*;

import NoobSave._L.garcia.NoobSave.entities.Fichier;
import NoobSave._L.garcia.NoobSave.service.FichierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FichierControllerTest {

    @Mock
    private FichierService fichierService;

    @InjectMocks
    private FichierController fichierController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testObtenirTousLesFichiers() {
        // Préparer des données factices
        Fichier fichier1 = new Fichier(1L, "test1.txt", "text/plain", null, null, null);
        Fichier fichier2 = new Fichier(2L, "test2.txt", "text/plain", null, null, null);
        List<Fichier> fichiers = Arrays.asList(fichier1, fichier2);

        when(fichierService.obtenirTousLesFichiers()).thenReturn(fichiers);

        ResponseEntity<List<Fichier>> response = fichierController.obtenirTousLesFichiers();

        assertNotNull(response);
        assertEquals(2, response.getBody().size());
        assertEquals("test1.txt", response.getBody().get(0).getNom());
    }

    @Test
    public void testTelechargerFichier_FichierTrouve() {
        // Préparer un fichier factice
        Fichier fichier = new Fichier(1L, "test.txt", "text/plain", null, null, "Contenu".getBytes());
        when(fichierService.obtenirFichierParId(1L)).thenReturn(Optional.of(fichier));

        ResponseEntity<?> response = fichierController.telechargerFichier(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("attachment;filename=test.txt", response.getHeaders().get("Content-Disposition").get(0));
    }

    @Test
    public void testTelechargerFichier_FichierNonTrouve() {
        when(fichierService.obtenirFichierParId(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = fichierController.telechargerFichier(1L);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
