package NoobSave._L.garcia.NoobSave.service;

import NoobSave._L.garcia.NoobSave.entities.Fichier;
import NoobSave._L.garcia.NoobSave.repository.FichierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FichierService {

    private final FichierRepository fichierRepository;
    private final Path repertoire = Paths.get("./archive");

    @Scheduled(fixedRate = 60000)
    public void synchroniserFichiersDuRepertoire() throws IOException {
        System.out.println("Début de la synchronisation à : " + LocalDateTime.now());

        // Vérifiez si le répertoire existe
        if (!Files.exists(repertoire)) {
            System.out.println("Le répertoire n'existe pas : " + repertoire.toAbsolutePath());
            Files.createDirectories(repertoire); // Création du répertoire s'il n'existe pas
            return;
        }

        // Liste des fichiers sur disque
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(repertoire)) {
            boolean fichierTrouve = false;

            System.out.println("====================================================================================");
            for (Path fichierPath : stream) {
                fichierTrouve = true;
                File fichier = fichierPath.toFile();

                // Affiche les fichiers détectés
                System.out.println("--------------------------------------------------------------------------------------");

                System.out.println("Fichier détecté : " + fichier.getName());

                // Validation du fichier
                if (fichier.isFile() && estUnFichierValide(fichier)) {
                    System.out.println("Fichier valide trouvé : " + fichier.getName());

                    // Lire le contenu du fichier
                    byte[] contenu = Files.readAllBytes(fichier.toPath());
                    if (contenu == null || contenu.length == 0) {
                        System.out.println("Contenu vide ou non lisible pour le fichier : " + fichier.getName());
                        continue;
                    }

                    // Déterminer le type MIME
                    String typeMime = Files.probeContentType(fichier.toPath());
                    System.out.println("Type MIME détecté : " + typeMime);

                    // Gestion des fichiers dans la base de données
                    Optional<Fichier> fichierExistant = fichierRepository.findByNom(fichier.getName());
                    LocalDateTime dateModification = LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(fichier.lastModified()), ZoneId.systemDefault());

                    if (fichierExistant.isPresent()) {
                        Fichier entiteFichier = fichierExistant.get();

                        // Mise à jour si le contenu a changé
                        if (!Arrays.equals(entiteFichier.getContenu(), contenu)) {
                            System.out.println("Mise à jour du fichier existant : " + fichier.getName());
                            entiteFichier.setContenu(contenu);
                            entiteFichier.setDateModification(dateModification);
                            fichierRepository.save(entiteFichier);
                        }
                    } else {
                        // Nouveau fichier
                        System.out.println("Ajout d'un nouveau fichier : " + fichier.getName());
                        Fichier nouvelFichier = new Fichier();
                        nouvelFichier.setNom(fichier.getName());
                        nouvelFichier.setType(typeMime);
                        nouvelFichier.setContenu(contenu);
                        nouvelFichier.setDateAjout(LocalDateTime.now());
                        nouvelFichier.setDateModification(dateModification);
                        fichierRepository.save(nouvelFichier);
                    }
                } else {
                    System.out.println("Fichier ignoré ou non valide : " + fichier.getName());
                }
            }

            if (!fichierTrouve) {
                System.out.println("Aucun fichier détecté dans le répertoire : " + repertoire.toAbsolutePath());
            }
        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture du répertoire : " + e.getMessage());
        }

        System.out.println("Fin de la synchronisation à : " + LocalDateTime.now());
    }



    public List<Fichier> obtenirTousLesFichiers() {
        return fichierRepository.findAll();
    }

    public Optional<Fichier> obtenirFichierParId(Long id) {
        return fichierRepository.findById(id);
    }

    public boolean estUnFichierValide(File fichier) {
        String nom = fichier.getName().toLowerCase();
        return nom.endsWith(".pdf") || nom.endsWith(".txt") || nom.endsWith(".docx");
    }
}
