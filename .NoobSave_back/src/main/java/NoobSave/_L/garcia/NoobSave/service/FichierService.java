package NoobSave._L.garcia.NoobSave.service;

import NoobSave._L.garcia.NoobSave.entities.Fichier;
import NoobSave._L.garcia.NoobSave.repository.FichierRepository;

import lombok.RequiredArgsConstructor;
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
 * Service pour gérer les opérations sur les fichiers.
 *
 * Les fonctionnalités incluent :
 * <ul>
 *     <li>Synchronisation avec un répertoire local et la base de données.</li>
 *     <li>Ajout, suppression et restauration de fichiers.</li>
 *     <li>Gestion des permissions POSIX sur les fichiers/répertoires.</li>
 *     <li>Exécution de sauvegardes automatiques ou manuelles.</li>
 * </ul>

 * <b>Note :</b> Ce service est conçu pour être utilisé par des contrôleurs REST.
 *
 * @author torres léo
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class FichierService {

    /**
     * Référentiel pour gérer les entités {@link Fichier}.
     */
    private final FichierRepository fichierRepository;

    /**
     * Service pour accéder aux paramètres globaux.
     */
    private final ParametreService parametreService;

    /**
     * Chemin par défaut utilisé pour les fichiers d'archive.
     */
    private final Path defaultPath = Paths.get("./../archive");

    /**
     * Date de la dernière synchronisation effectuée.
     */
    private LocalDateTime lastSyncTime = LocalDateTime.now().minusYears(10);

    /**
     * Retourne le chemin de sauvegarde actif.
     *
     * <p>Si un chemin est configuré dans les paramètres, celui-ci est utilisé. Sinon, le chemin par défaut est
     * {@code ./../archive}.</p>
     *
     * @return Le chemin de sauvegarde actif.
     */

    private Path savePath() {
        return Optional.ofNullable(parametreService.getParametre().getSavePath())
                .map(Paths::get)
                .orElse(defaultPath);
    }

    /**
     * Sauvegarde régulière des fichiers à un intervalle fixe si l'auto-save est activée.
     *
     * @throws IOException Si une erreur survient lors de la lecture/écriture des fichiers.
     */
    @Scheduled(fixedRate = 5000) // équivaut ici à 5 secondes en exemple
    public void regularSave() throws IOException {
        if (!parametreService.getParametre().isAutoSaveEnabled()) {
            return;
        }

        long intervalMs = parametreService.getParametre().getAutoSaveInterval();
        long sinceLastMs = Duration.between(lastSyncTime, LocalDateTime.now()).toMillis();

        Path savePath = savePath();

        if (sinceLastMs >= intervalMs) {
            synchroniserFichiersDuRepertoire(savePath);
            lastSyncTime = LocalDateTime.now();
        }
    }

    /**
     * Déclenche une sauvegarde manuelle (sans attendre la tâche planifiée).
     *
     * @throws IOException Si une erreur survient lors de la lecture/écriture des fichiers.
     */
    public void saveDeclencher() throws IOException {
        Path savePath = savePath();
        synchroniserFichiersDuRepertoire(savePath);
    }

    /**
     * Synchronise un répertoire local (et ses sous-répertoires) avec la base de données.
     *
     * <strong>Fonctionnalités : </strong>
     * <ul>
     *     <li>Ajout de nouveaux fichiers dans la base de données.</li>
     *     <li>Mise à jour des fichiers existants si leur contenu a changé.</li>
     *     <li>Détection automatique du type MIME.</li>
     * </ul>
     *
     * @param repertoire Chemin du répertoire à synchroniser.
     * @throws IOException Si une erreur survient lors de la lecture ou de l'écriture.
     */
    public void synchroniserFichiersDuRepertoire(Path repertoire) throws IOException {

        System.out.println("Début de la synchronisation à : " + LocalDateTime.now());

        if (!Files.exists(repertoire)) {
            System.out.println("Le répertoire n'existe pas : " + repertoire.toAbsolutePath());
            Files.createDirectories(repertoire);
            return;
        }

        int nbFichiersTraites = traiterRepertoire(repertoire);

        if (nbFichiersTraites == 0) {
            System.out.println("Aucun fichier détecté dans le répertoire (y compris les sous-répertoires) : "
                    + repertoire.toAbsolutePath());
        }
        System.out.println("Fin de la synchronisation à : " + LocalDateTime.now());
    }

    /**
     * Traite un répertoire en ajoutant ou mettant à jour les fichiers dans la base de données.
     *
     * <p>Parcourt récursivement le répertoire spécifié et gère les fichiers valides :</p>
     * <ul>
     *     <li>Ajoute les nouveaux fichiers dans la base de données.</li>
     *     <li>Met à jour les fichiers existants si leur contenu a changé.</li>
     * </ul>
     *
     * @param repertoireActuel Chemin du répertoire à analyser.
     * @return Nombre de fichiers valides traités.
     * @throws IOException Si une erreur survient lors de la lecture des fichiers.
     */
    private int traiterRepertoire(Path repertoireActuel) throws IOException {

        int count = 0;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(repertoireActuel)) {
            for (Path chemin : stream) {
                File fichier = chemin.toFile();
                if (fichier.isDirectory()) {
                    count += traiterRepertoire(chemin);
                } else if (fichier.isFile() && estUnFichierValide(fichier)) {
                    System.out.println("--------------------------------------------------------------------------------------");
                    System.out.println("Fichier détecté : " + fichier.getName());

                    byte[] contenu = Files.readAllBytes(chemin);
                    if (contenu == null || contenu.length == 0) {
                        System.out.println("Contenu vide ou non lisible pour le fichier : " + fichier.getName());
                        continue;
                    }

                    String typeMime = Files.probeContentType(chemin);
                    if (typeMime == null) {
                        typeMime = "application/octet-stream";
                    }
                    System.out.println("Type MIME détecté : " + typeMime);

                    Optional<Fichier> fichierExistant = fichierRepository.findByNom(fichier.getName());
                    LocalDateTime dateModification = LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(fichier.lastModified()), ZoneId.systemDefault()
                    );

                    if (fichierExistant.isPresent()) {
                        // Mise à jour si le contenu a changé
                        Fichier entiteFichier = fichierExistant.get();
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
            System.out.println("Erreur lors de la lecture du répertoire : "
                    + repertoireActuel.toAbsolutePath()
                    + " - " + e.getMessage());
        }
        return count;
    }

    /**
     * Récupère la liste de tous les fichiers enregistrés en base de données.
     *
     * @return liste de fichiers.
     */
    public List<Fichier> obtenirTousLesFichiers() {
        return fichierRepository.findAll();
    }

    /**
     * Récupère un fichier par son identifiant unique.
     *
     * @param id identifiant du fichier en base.
     * @return un {@link Optional} contenant le fichier s’il existe, sinon vide.
     */
     public Optional<Fichier> obtenirFichierParId(String id) {
        return fichierRepository.findById(id);
    }

    /**
     * Vérifie si le fichier a une extension autorisée.
     *
     * @param fichier Fichier local à vérifier.
     * @return {@code true} si l'extension est autorisée, sinon {@code false}.
     */
   public boolean estUnFichierValide(File fichier) {

        String nom = fichier.getName().toLowerCase();
        List<String> allowedExtensions = parametreService.getAllowedFileExtensions();
        return allowedExtensions.stream().anyMatch(nom::endsWith);
    }

    /**
     * Supprime un fichier du disque et de la base de données.
     *
     * @param fichier Instance {@link Fichier} à supprimer.
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
     * Applique de manière récursive les permissions POSIX spécifiées sur un chemin (et ses parents).
     *
     * <p>Cette méthode traverse chaque parent du chemin fourni, en appliquant les permissions POSIX
     * spécifiées aux répertoires rencontrés. Si un chemin n'existe pas, l'opération est arrêtée.</p>
     *
     * @param chemin Répertoire ou fichier auquel appliquer les permissions POSIX.
     * @param perms  Ensemble de permissions POSIX à appliquer sur le chemin et ses parents.
     */
    private void appliquerPermissionsSurArborescence(Path chemin,Set<PosixFilePermission> perms) {

        Path current = chemin;
        while (current != null && Files.exists(current)) {
            try {
                if (Files.isDirectory(current)) {
                    Files.setPosixFilePermissions(current, perms);
                }
            } catch (IOException e) {
                System.out.println("Impossible d'appliquer les permissions sur : "
                        + current + " - " + e.getMessage());
            }
            current = current.getParent();
        }
    }

    /**
     * Extrait le sous-répertoire relatif au chemin par défaut ({@link #defaultPath}).
     *
     * <p>Cette méthode analyse le chemin absolu fourni et retourne le sous-répertoire relatif au chemin
     * par défaut (racine des archives). Si le chemin fourni n'est pas dans l'arborescence du chemin
     * par défaut, la valeur "INCONNU" est retournée.</p>
     *
     * @param cheminComplet Chemin absolu vers un fichier ou répertoire.
     * @return Nom du sous-répertoire ou chaîne vide si le chemin est à la racine.
     */
   private String extraireSousRepertoire(String cheminComplet) {

        Path pathNormalise = Paths.get(cheminComplet).normalize().toAbsolutePath();
        Path archiveRoot = defaultPath.normalize().toAbsolutePath();

        if (pathNormalise.startsWith(archiveRoot)) {
            Path relative = archiveRoot.relativize(pathNormalise);
            return relative.getParent() != null ? relative.getParent().toString() : "";
        }
        return "INCONNU";
    }

    /**
     * Liste les sous-répertoires présents dans la base de données en se basant sur leurs chemins.
     *
     * <p>Chaque fichier enregistré en base est analysé pour extraire son sous-répertoire relatif au
     * chemin par défaut. Les résultats sont triés et retournés sous forme de liste unique.</p>
     *
     * @return Liste triée des sous-répertoires détectés.
     */
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

    /**
     * Restaure tous les fichiers manquants sur le disque en fonction des enregistrements en base de données.
     *
     * <p>Pour chaque fichier enregistré en base, cette méthode vérifie s'il est présent sur le disque.
     * Si le fichier est manquant, il est restauré dans le chemin correspondant en appliquant des
     * permissions POSIX prédéfinies.</p>
     *
     * @return Nombre de fichiers effectivement restaurés.
     */
    public int restaurerFichiersManquants() {
        List<Fichier> fichiers = fichierRepository.findAll();

        Set<PosixFilePermission> perms = EnumSet.of(
                PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE,
                PosixFilePermission.GROUP_READ, PosixFilePermission.GROUP_WRITE, PosixFilePermission.GROUP_EXECUTE,
                PosixFilePermission.OTHERS_READ, PosixFilePermission.OTHERS_WRITE, PosixFilePermission.OTHERS_EXECUTE
        );

        return restoreFiles(fichiers, f -> true, defaultPath, perms);
    }

    /**
     * Restaure uniquement les fichiers manquants pour un sous-répertoire donné.
     *
     * <p>Cette méthode filtre les fichiers enregistrés en base de données pour ne restaurer que ceux
     * correspondant au sous-répertoire spécifié. Si le sous-répertoire est vide, elle cible la racine.</p>
     *
     * @param sousRepertoire Sous-répertoire ciblé pour la restauration (vide pour la racine).
     * @return Nombre de fichiers effectivement restaurés pour ce sous-répertoire.
     */
    public int restaurerFichiersPourSousRepertoire(
            String sousRepertoire) {

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
                    return sousRepertoire.isEmpty() ? rep.isEmpty() : rep.equals(sousRepertoire);
                },
                defaultPath,
                perms
        );
    }

    /**
     * Restaure les fichiers manquants répondant à une certaine condition.
     *
     * <p>Cette méthode parcourt la liste des fichiers en base et applique un filtre pour déterminer
     * lesquels doivent être restaurés. Les fichiers manquants sont écrits sur le disque et les
     * permissions POSIX spécifiées sont appliquées.</p>
     *
     * @param fichiers    Liste de tous les fichiers enregistrés en base.
     * @param condition   Prédicat indiquant les fichiers à restaurer.
     * @param archiveRoot Chemin racine où les fichiers doivent être restaurés.
     * @param perms       Ensemble de permissions POSIX à appliquer sur les fichiers restaurés.
     * @return Nombre de fichiers effectivement restaurés.
     */
    private int restoreFiles(List<Fichier> fichiers, Predicate<Fichier> condition, Path archiveRoot, Set<PosixFilePermission> perms) {

        int restoredCount = 0;

        for (Fichier fichier : fichiers) {
            // On applique le filtre
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
                    // Écrit le contenu
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
