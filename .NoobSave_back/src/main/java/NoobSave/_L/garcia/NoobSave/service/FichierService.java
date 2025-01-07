package NoobSave._L.garcia.NoobSave.service;

import NoobSave._L.garcia.NoobSave.entities.Fichier;
import NoobSave._L.garcia.NoobSave.repository.FichierRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermission;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

    private final ParametreService parametreService;

    /**
     * Chemin du répertoire local pour archiver les fichiers.
     */
    private final Path defaultPath = Paths.get("./../archive");

    private LocalDateTime lastSyncTime = LocalDateTime.now().minusYears(10);

    private Path savePath(){
        return Optional.ofNullable(parametreService.getParametre().getSavePath())
                .map(Paths::get)
                .orElse(defaultPath);
    }

    /**
     * declenche une sauvegarde toute les minute
     *
     * @throws IOException en cas d'erreur d'accès au répertoire ou de lecture des fichiers.
     */
    @Scheduled(fixedRate = 5000)
    public void regularSave() throws IOException {
        if (!parametreService.getParametre().isAutoSaveEnabled()) {
            return;
        }

        long intervalMs = parametreService.getParametre().getAutoSaveInterval();
        long sinceLastMs = Duration.between(lastSyncTime, LocalDateTime.now()).toMillis();

        Path savePath = savePath();

        if (sinceLastMs >= intervalMs) {
            synchroniserFichiersDuRepertoire(savePath); // Utilisation du chemin correct
            lastSyncTime = LocalDateTime.now(); // mise à jour
        }
    }

    /**
     * declenche la sauvegarde
     *
     * @throws IOException en cas d'erreur d'accès au répertoire ou de lecture des fichiers.
     */
    public void saveDeclencher() throws IOException {
        Path savePath = savePath();
        synchroniserFichiersDuRepertoire(savePath);
    }

    /**
     * Synchronise les fichiers dans le répertoire local avec la base de données.
     * Si le fichier existe déjà, son contenu est mis à jour si modifié.
     * Si le fichier est nouveau, il est ajouté à la base de données.
     *
     * @throws IOException en cas d'erreur d'accès au répertoire ou de lecture des fichiers.
     */
    public void synchroniserFichiersDuRepertoire(Path repertoire) throws IOException {
        System.out.println("Début de la synchronisation à : " + LocalDateTime.now());

        // Vérifie si le répertoire existe
        if (!Files.exists(repertoire)) {
            System.out.println("Le répertoire n'existe pas : " + repertoire.toAbsolutePath());
            Files.createDirectories(repertoire); // Création du répertoire s'il n'existe pas
            return;
        }

        // Parcours récursif du répertoire
        int nbFichiersTraites = traiterRepertoire(repertoire);

        if (nbFichiersTraites == 0) {
            System.out.println("Aucun fichier détecté dans le répertoire (y compris les sous-répertoires) : " + repertoire.toAbsolutePath());
        }

        System.out.println("Fin de la synchronisation à : " + LocalDateTime.now());
    }


    /**
     * Parcourt récursivement un répertoire, traite les fichiers valides et ajoute ou met à jour
     * leur contenu dans la base de données.
     *
     * @param repertoireActuel le répertoire à traiter
     * @return le nombre de fichiers traités (ajoutés ou mis à jour)
     * @throws IOException en cas d'erreur de lecture du répertoire
     */
    private int traiterRepertoire(Path repertoireActuel) throws IOException {
        int count = 0;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(repertoireActuel)) {
            for (Path chemin : stream) {
                File fichier = chemin.toFile();

                if (fichier.isDirectory()) {
                    // Appel récursif pour les répertoires
                    count += traiterRepertoire(chemin);
                } else if (fichier.isFile() && estUnFichierValide(fichier)) {
                    System.out.println("--------------------------------------------------------------------------------------");
                    System.out.println("Fichier détecté : " + fichier.getName());

                    byte[] contenu = Files.readAllBytes(chemin);
                    if (contenu == null || contenu.length == 0) {
                        System.out.println("Contenu vide ou non lisible pour le fichier : " + fichier.getName());
                        continue;
                    }

                    // Déterminer le type MIME
                    String typeMime = Files.probeContentType(chemin);
                    if (typeMime == null) {
                        typeMime = "application/octet-stream"; // type par défaut si inconnu
                    }
                    System.out.println("Type MIME détecté : " + typeMime);

                    // Récupérer le fichier existant si présent
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
                        } else {
                            System.out.println("Aucune modification détectée pour le fichier : " + fichier.getName());
                        }
                    } else {
                        // Nouveau fichier
                        System.out.println("Ajout d'un nouveau fichier : " + fichier.getName());
                        Fichier nouveauFichier = new Fichier();
                        nouveauFichier.setNom(fichier.getName());
                        nouveauFichier.setType(typeMime);
                        nouveauFichier.setChemin(chemin.toString());
                        nouveauFichier.setContenu(contenu);
                        nouveauFichier.setDateAjout(LocalDateTime.now());
                        nouveauFichier.setDateModification(dateModification);
                        fichierRepository.save(nouveauFichier);
                    }

                    count++;
                } else if (fichier.isFile()) {
                    System.out.println("Fichier ignoré ou non valide : " + fichier.getName());
                }
            }
        } catch (IOException e) {
            System.out.println("Erreur lors de la lecture du répertoire : " + repertoireActuel.toAbsolutePath() + " - " + e.getMessage());
        }

        return count;
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
    public Optional<Fichier> obtenirFichierParId(String id) {
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
        List<String> allowedExtensions = parametreService.getAllowedFileExtensions();

        return allowedExtensions.stream().anyMatch(nom::endsWith);
    }

    /**
     * Supprime un fichier du répertoire local et de la base de données.
     *
     * @param fichier instance de l'entité Fichier à supprimer.
     */
    public void supprimerFichier(Fichier fichier) {
        Path fichierPath = defaultPath.resolve(fichier.getNom());

        try {
            Files.deleteIfExists(fichierPath);
            System.out.println("Fichier supprimé du disque : " + fichierPath.toAbsolutePath());
        } catch (IOException e) {
            System.out.println("Erreur lors de la suppression du fichier : " + e.getMessage());
        }

        fichierRepository.delete(fichier);
        System.out.println("Fichier supprimé de la base de données : " + fichier.getNom());
    }

    /*=============================== Restauration ===============================*/
    /**
     * Applique de manière récursive les permissions spécifiées sur le répertoire donné,
     * ainsi que sur tous ses répertoires parents, jusqu'à la racine spécifiée.
     */
    private void appliquerPermissionsSurArborescence(Path chemin, Set<PosixFilePermission> perms) {
        // Parcours en remontant les répertoires parents
        Path current = chemin;
        while (current != null && Files.exists(current)) {
            try {
                // On vérifie que c'est bien un répertoire avant d'appliquer les perms
                if (Files.isDirectory(current)) {
                    Files.setPosixFilePermissions(current, perms);
                }
            } catch (IOException e) {
                System.out.println("Impossible d'appliquer les permissions sur : " + current + " - " + e.getMessage());
            }
            current = current.getParent();
        }
    }

    private String extraireSousRepertoire(String cheminComplet) {
        Path pathNormalise = Paths.get(cheminComplet).normalize().toAbsolutePath();
        Path archiveRoot = defaultPath.normalize().toAbsolutePath();

        if (pathNormalise.startsWith(archiveRoot)) {
            Path relative = archiveRoot.relativize(pathNormalise);
            return relative.getParent() != null ? relative.getParent().toString() : "";
        }
        return "INCONNU";
    }


    public List<String> listerSousRepertoires() {
        List<Fichier> fichiers = fichierRepository.findAll();

        Set<String> sousRepertoires = new HashSet<>();
        for (Fichier fichier : fichiers) {
            String sousRep = extraireSousRepertoire(fichier.getChemin());
            if (!"INCONNU".equals(sousRep)) {
                sousRepertoires.add(sousRep);
            }
        }

        return sousRepertoires.stream().sorted().collect(Collectors.toList());
    }


    public int restaurerFichiersManquants() {
        List<Fichier> fichiers = fichierRepository.findAll();

        // Ensemble de permissions (rwx pour tout le monde)
        Set<PosixFilePermission> perms = EnumSet.of(
                PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE,
                PosixFilePermission.GROUP_READ, PosixFilePermission.GROUP_WRITE, PosixFilePermission.GROUP_EXECUTE,
                PosixFilePermission.OTHERS_READ, PosixFilePermission.OTHERS_WRITE, PosixFilePermission.OTHERS_EXECUTE
        );

        // Condition : on restaure TOUTES les entrées => (f -> true)
        return restoreFiles(fichiers, f -> true, defaultPath, perms);
    }

    public int restaurerFichiersPourSousRepertoire(String sousRepertoire) {
        List<Fichier> fichiers = fichierRepository.findAll();

        Set<PosixFilePermission> perms = EnumSet.of(
                PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE,
                PosixFilePermission.GROUP_READ, PosixFilePermission.GROUP_WRITE, PosixFilePermission.GROUP_EXECUTE,
                PosixFilePermission.OTHERS_READ, PosixFilePermission.OTHERS_WRITE, PosixFilePermission.OTHERS_EXECUTE
        );

        return restoreFiles(
                fichiers,
                f -> {
                    String rep = extraireSousRepertoire(f.getChemin());
                    // Si sousRepertoire est vide, c'est la racine
                    return sousRepertoire.isEmpty() ? rep.isEmpty() : rep.equals(sousRepertoire);
                },
                defaultPath,
                perms
        );
    }

    private int restoreFiles(List<Fichier> fichiers,
                             Predicate<Fichier> condition,
                             Path archiveRoot,
                             Set<PosixFilePermission> perms) {
        int restoredCount = 0;

        for (Fichier fichier : fichiers) {
            // On applique le filtre : si false, on skip
            if (!condition.test(fichier)) {
                continue;
            }

            Path cheminFichier = Paths.get(fichier.getChemin()).normalize();
            // Si chemin pas absolu => on le résout dans archiveRoot
            if (!cheminFichier.isAbsolute()) {
                cheminFichier = archiveRoot.resolve(cheminFichier).normalize();
            }

            // Vérifie si le fichier est absent sur le disque => on restaure
            if (!Files.exists(cheminFichier)) {
                try {
                    // Créer les répertoires parents si nécessaire
                    if (cheminFichier.getParent() != null) {
                        Files.createDirectories(cheminFichier.getParent());
                        appliquerPermissionsSurArborescence(cheminFichier.getParent(), perms);
                    }

                    // Écrire le contenu
                    Files.write(cheminFichier, fichier.getContenu(), StandardOpenOption.CREATE_NEW);
                    // Appliquer les permissions
                    Files.setPosixFilePermissions(cheminFichier, perms);

                    restoredCount++;
                    System.out.println("Fichier restauré : "
                            + fichier.getNom()
                            + " -> "
                            + cheminFichier.toAbsolutePath());
                } catch (IOException e) {
                    System.out.println("Erreur de restauration pour "
                            + fichier.getNom() + " : " + e.getMessage());
                }
            }
        }
        return restoredCount;
    }


}
