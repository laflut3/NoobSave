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

/**
 * Service pour gérer les opérations sur les fichiers, y compris la synchronisation avec le répertoire,
 * l'ajout, la suppression et la récupération des fichiers depuis la base de données.
 */
@Service
@RequiredArgsConstructor
public class FichierService {

    /**
     * Référence au dépôt de fichiers pour interagir avec la base de données.
     */
    private final FichierRepository fichierRepository;

    /**
     * Chemin du répertoire local pour archiver les fichiers.
     */
    private final Path repertoire = Paths.get("./../archive");

    /**
     * declenche une sauvegarde toute les minute
     *
     * @throws IOException en cas d'erreur d'accès au répertoire ou de lecture des fichiers.
     */
    @Scheduled(fixedRate = 60000)
    public void regularSave() throws IOException {
        synchroniserFichiersDuRepertoire();
    }

    /**
     * declenche la sauvegarde
     *
     * @throws IOException en cas d'erreur d'accès au répertoire ou de lecture des fichiers.
     */
    public void saveDeclencher() throws IOException {
        synchroniserFichiersDuRepertoire();
    }

    /**
     * Synchronise les fichiers dans le répertoire local avec la base de données.
     * Si le fichier existe déjà, son contenu est mis à jour si modifié.
     * Si le fichier est nouveau, il est ajouté à la base de données.
     *
     * @throws IOException en cas d'erreur d'accès au répertoire ou de lecture des fichiers.
     */
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
                        Fichier nouveauFichier = new Fichier();
                        nouveauFichier.setNom(fichier.getName());
                        nouveauFichier.setType(typeMime);
                        nouveauFichier.setChemin(fichierPath.toString());
                        nouveauFichier.setContenu(contenu);
                        nouveauFichier.setDateAjout(LocalDateTime.now());
                        nouveauFichier.setDateModification(dateModification);
                        fichierRepository.save(nouveauFichier);
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


    /**
     * Récupère tous les fichiers enregistrés dans la base de données.
     *
     * @return une liste contenant tous les fichiers.
     */
    public List<Fichier> obtenirTousLesFichiers() {
        return fichierRepository.findAll();
    }

    /**
     * Récupère un fichier à partir de son identifiant unique.
     *
     * @param id identifiant du fichier.
     * @return un objet `Optional` contenant le fichier s'il existe, sinon vide.
     */
    public Optional<Fichier> obtenirFichierParId(Long id) {
        return fichierRepository.findById(id);
    }

    /**
     * Vérifie si un fichier est valide pour être enregistré.
     * Seuls les fichiers avec les extensions .pdf, .txt ou .docx sont considérés comme valides.
     *
     * @param fichier fichier à vérifier.
     * @return true si le fichier est valide, sinon false.
     */
    public boolean estUnFichierValide(File fichier) {
        String nom = fichier.getName().toLowerCase();
        return nom.endsWith(".pdf") || nom.endsWith(".txt") || nom.endsWith(".docx");
    }

    /**
     * Supprime un fichier du répertoire local et de la base de données.
     *
     * @param fichier instance de l'entité Fichier à supprimer.
     */
    public void supprimerFichier(Fichier fichier) {
        Path fichierPath = repertoire.resolve(fichier.getNom());

        try {
            Files.deleteIfExists(fichierPath);
            System.out.println("Fichier supprimé du disque : " + fichierPath.toAbsolutePath());
        } catch (IOException e) {
            System.out.println("Erreur lors de la suppression du fichier : " + e.getMessage());
        }

        fichierRepository.delete(fichier);
        System.out.println("Fichier supprimé de la base de données : " + fichier.getNom());
    }

}
