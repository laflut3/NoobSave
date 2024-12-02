package NoobSave._L.garcia.NoobSave.service;

import static org.junit.jupiter.api.Assertions.*;

import NoobSave._L.garcia.NoobSave.entities.Fichier;
import NoobSave._L.garcia.NoobSave.repository.FichierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FichierServiceTest {

    @Mock
    private FichierRepository fichierRepository;

    @InjectMocks
    private FichierService fichierService;

    private Path testDirectory;

    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        // Créer un répertoire temporaire pour les tests
        testDirectory = Files.createTempDirectory("test_directory");
    }

    @Test
    public void testEstUnFichierValide() {
        File validFile = new File("test.pdf");
        File invalidFile = new File("test.exe");

        assertTrue(fichierService.estUnFichierValide(validFile));
        assertFalse(fichierService.estUnFichierValide(invalidFile));
    }

    @Test
    public void testSynchroniserFichiersDuRepertoire_AjoutNouveauFichier() throws IOException {
        // Créer un fichier temporaire dans le répertoire de test
        Path testFile = Files.createFile(testDirectory.resolve("test.txt"));
        Files.writeString(testFile, "Contenu du fichier de test");

        when(fichierRepository.findByNom("test.txt")).thenReturn(Optional.empty());

        fichierService.synchroniserFichiersDuRepertoire();

        verify(fichierRepository, times(1)).save(any(Fichier.class));
    }

    @Test
    public void testSynchroniserFichiersDuRepertoire_FichierExistantMisAJour() throws IOException {
        // Créer un fichier temporaire dans le répertoire de test
        Path testFile = Files.createFile(testDirectory.resolve("test.txt"));
        Files.writeString(testFile, "Contenu du fichier de test");

        // Simuler un fichier existant dans la base de données
        Fichier fichierExistant = new Fichier();
        fichierExistant.setNom("test.txt");
        fichierExistant.setContenu("Ancien contenu".getBytes());
        fichierExistant.setDateModification(LocalDateTime.now().minusDays(1));

        when(fichierRepository.findByNom("test.txt")).thenReturn(Optional.of(fichierExistant));

        fichierService.synchroniserFichiersDuRepertoire();

        verify(fichierRepository, times(1)).save(any(Fichier.class));
    }
}
