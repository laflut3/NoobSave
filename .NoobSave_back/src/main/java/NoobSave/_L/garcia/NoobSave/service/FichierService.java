package NoobSave._L.garcia.NoobSave.service;

import NoobSave._L.garcia.NoobSave.entities.Fichier;
import NoobSave._L.garcia.NoobSave.repository.FichierRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

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
 * Service pour gérer les opérations sur les fichiers, y compris la synchronisation
 * avec le répertoire, l'ajout, la suppression, la sauvegarde programmée et la restauration.
 * <p>
 * <b>Note :</b> Ce service n'est pas exposé directement via HTTP, mais il est appelé
 * depuis des contrôleurs REST (ex. {@code FichierController}). L'ajout d'annotations
 * Swagger ici ne sera utile que si vous configurez explicitement SpringDoc (ou équivalent)
 * pour analyser et exposer la documentation de vos services internes.
 */
@Tag(name = "FichierService", description = "Service responsable de la gestion et de la synchronisation des fichiers.")
@Service
@RequiredArgsConstructor
public class FichierService {

    /**
     * Référence au dépôt de fichiers pour interagir avec la base de données.
     */
    private final FichierRepository fichierRepository;

    /**
     * Référence au service gérant les paramètres (chemins, extensions autorisées, etc.).
     */
    private final ParametreService parametreService;

    /**
     * Chemin par défaut vers le répertoire d’archive.
     */
    private final Path defaultPath = Paths.get("./../archive");

    /**
     * Date de la dernière synchronisation effectuée.
     */
    private LocalDateTime lastSyncTime = LocalDateTime.now().minusYears(10);

    /**
     * Retourne le chemin de sauvegarde à utiliser.
     * <p>
     * Si un chemin est défini dans {@link ParametreService#getParametre()},
     * celui-ci est utilisé. Sinon, le chemin par défaut est {@code ./../archive}.
     *
     * @return le chemin de sauvegarde actif.
     */
    @Operation(summary = "Obtenir le chemin de sauvegarde actif (interne)",
            description = "Renvoie le chemin où les fichiers doivent être archivés.")
    private Path savePath() {
        return Optional.ofNullable(parametreService.getParametre().getSavePath())
                .map(Paths::get)
                .orElse(defaultPath);
    }

    /**
     * Effectue une sauvegarde régulière (toutes les X millisecondes) si l’auto-save est activée.
     * <p>
     * Le délai entre deux sauvegardes est déterminé par
     * {@link ParametreService#getParametre()#getAutoSaveInterval()}.
     *
     * @throws IOException si un problème survient lors de la lecture/écriture des fichiers.
     */
    @Operation(summary = "Sauvegarde planifiée", description = "Déclenche la synchronisation du répertoire à intervalles réguliers.")
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
     * Déclenche manuellement la sauvegarde (synchronisation) des fichiers.
     *
     * @throws IOException si un problème survient lors de la lecture/écriture des fichiers.
     */
    @Operation(summary = "Déclencher manuellement la sauvegarde",
            description = "Appel direct pour lancer la synchronisation du répertoire (sans attendre la tâche planifiée).")
    public void saveDeclencher() throws IOException {
        Path savePath = savePath();
        synchroniserFichiersDuRepertoire(savePath);
    }

    /**
     * Synchronise tous les fichiers du répertoire (et sous-répertoires) avec la base de données.
     * <ul>
     *     <li>S’il s’agit d’un nouveau fichier, il est ajouté à la BDD.</li>
     *     <li>S’il existe déjà, son contenu est mis à jour si nécessaire.</li>
     *     <li>Le type MIME est détecté si possible.</li>
     * </ul>
     *
     * @param repertoire chemin du répertoire à synchroniser.
     * @throws IOException si un problème survient lors de la lecture/écriture des fichiers.
     */
    @Operation(summary = "Synchroniser un répertoire donné",
            description = "Analyse le répertoire (récursivement) et met à jour la base de données pour chaque fichier rencontré.")
    public void synchroniserFichiersDuRepertoire(
            @Parameter(description = "Chemin du répertoire à analyser pour la synchronisation.")
            Path repertoire) throws IOException {

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
     * Parcourt récursivement un répertoire et traite les fichiers.
     * <ul>
     *     <li>Si le fichier est valide, il est ajouté ou mis à jour en base de données.</li>
     *     <li>Les sous-répertoires sont visités de manière récursive.</li>
     * </ul>
     *
     * @param repertoireActuel répertoire à inspecter.
     * @return nombre de fichiers valides traités (ajoutés ou mis à jour).
     * @throws IOException si un problème survient lors de la lecture du répertoire.
     */
    @Operation(summary = "Traiter un répertoire (récursif, interne)",
            description = "Méthode interne qui ajoute ou met à jour chaque fichier détecté dans la BDD.")
    private int traiterRepertoire(
            @Parameter(description = "Chemin du répertoire actuel à analyser.")
            Path repertoireActuel) throws IOException {

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
    @Operation(summary = "Obtenir tous les fichiers (interne)",
            description = "Renvoie la liste de l'intégralité des fichiers présents en base de données.")
    public List<Fichier> obtenirTousLesFichiers() {
        return fichierRepository.findAll();
    }

    /**
     * Récupère un fichier par son identifiant unique.
     *
     * @param id identifiant du fichier en base.
     * @return un {@link Optional} contenant le fichier s’il existe, sinon vide.
     */
    @Operation(summary = "Obtenir un fichier par son ID (interne)",
            description = "Cherche un fichier dans la base de données à partir de son identifiant.")
    public Optional<Fichier> obtenirFichierParId(
            @Parameter(description = "Identifiant unique du fichier en base", example = "63c2f5e5ab12ef00123")
            String id) {
        return fichierRepository.findById(id);
    }

    /**
     * Vérifie si le fichier a une extension autorisée (exemple : .pdf, .txt, .docx, etc.).
     *
     * @param fichier fichier local à vérifier.
     * @return {@code true} si l’extension du fichier est dans la liste autorisée, {@code false} sinon.
     */
    @Operation(summary = "Vérifier la validité d’un fichier (interne)",
            description = "Vérifie si l’extension du fichier est bien dans la liste autorisée (définie dans ParametreService).")
    public boolean estUnFichierValide(
            @Parameter(description = "Fichier local à vérifier (extension, etc.)")
            File fichier) {

        String nom = fichier.getName().toLowerCase();
        List<String> allowedExtensions = parametreService.getAllowedFileExtensions();
        return allowedExtensions.stream().anyMatch(nom::endsWith);
    }

    /**
     * Supprime un fichier du disque et de la base de données.
     *
     * @param fichier instance {@link Fichier} à supprimer.
     */
    @Operation(summary = "Supprimer un fichier (interne)",
            description = "Supprime physiquement le fichier sur le disque puis le retire de la base de données.")
    public void supprimerFichier(
            @Parameter(description = "Entité représentant le fichier à supprimer (nom, chemin, etc.).")
            Fichier fichier) {

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
     * @param chemin répertoire ou fichier auquel appliquer les permissions.
     * @param perms  ensemble de permissions POSIX.
     */
    @Operation(summary = "Appliquer des permissions sur un chemin (interne)",
            description = "Remonte récursivement dans l’arborescence pour définir les permissions POSIX sur les répertoires.")
    private void appliquerPermissionsSurArborescence(
            @Parameter(description = "Chemin cible (fichier/répertoire) pour appliquer les permissions.")
            Path chemin,
            @Parameter(description = "Ensemble des permissions POSIX à appliquer.")
            Set<PosixFilePermission> perms) {

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
     * Extrait le sous-répertoire (par rapport à {@link #defaultPath}) depuis un chemin complet.
     *
     * @param cheminComplet chemin absolu vers un fichier.
     * @return nom du sous-répertoire ou chaîne vide si on est à la racine.
     */
    @Operation(summary = "Extraire le sous-répertoire (interne)",
            description = "Compare le chemin complet à la racine d'archive pour déterminer le sous-répertoire.")
    private String extraireSousRepertoire(
            @Parameter(description = "Chemin absolu depuis lequel extraire le sous-répertoire.")
            String cheminComplet) {

        Path pathNormalise = Paths.get(cheminComplet).normalize().toAbsolutePath();
        Path archiveRoot = defaultPath.normalize().toAbsolutePath();

        if (pathNormalise.startsWith(archiveRoot)) {
            Path relative = archiveRoot.relativize(pathNormalise);
            return relative.getParent() != null ? relative.getParent().toString() : "";
        }
        return "INCONNU";
    }

    /**
     * Liste les sous-répertoires (par rapport à {@link #defaultPath}) pour tous les fichiers en base.
     *
     * @return liste triée des sous-répertoires détectés.
     */
    @Operation(summary = "Lister les sous-répertoires présents (interne)",
            description = "Retourne la liste des sous-répertoires où se trouvent des fichiers en base de données.")
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
     * Restaure tous les fichiers manquants sur le disque en se basant sur la liste complète des fichiers en base.
     *
     * @return nombre de fichiers effectivement restaurés.
     */
    @Operation(summary = "Restaurer tous les fichiers manquants (interne)",
            description = "Pour chaque fichier en base, vérifie s'il est présent sur le disque, sinon le recrée.")
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
     * @param sousRepertoire sous-répertoire ciblé (si vide, la racine).
     * @return nombre de fichiers effectivement restaurés pour ce sous-répertoire.
     */
    @Operation(summary = "Restaurer les fichiers d’un sous-répertoire (interne)",
            description = "Ne restaure que les fichiers appartenant au sous-répertoire spécifié.")
    public int restaurerFichiersPourSousRepertoire(
            @Parameter(description = "Nom du sous-répertoire à cibler (vide pour la racine).")
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
     * Restaure les fichiers manquants satisfaisant une certaine condition.
     *
     * @param fichiers     liste de tous les fichiers connus en base.
     * @param condition    prédicat indiquant quels fichiers doivent être restaurés.
     * @param archiveRoot  racine où les fichiers sont (ou seront) stockés.
     * @param perms        permissions POSIX à appliquer sur les fichiers nouvellement créés.
     * @return nombre de fichiers effectivement restaurés.
     */
    @Operation(summary = "Méthode générique de restauration (interne)",
            description = "Pour chaque fichier répondant au prédicat, recrée physiquement le fichier s’il est absent sur le disque.")
    private int restoreFiles(
            @Parameter(description = "Liste de tous les fichiers en base.")
            List<Fichier> fichiers,

            @Parameter(description = "Condition/filter pour sélectionner les fichiers à restaurer.")
            Predicate<Fichier> condition,

            @Parameter(description = "Racine d’archive où déposer/restaurer les fichiers.")
            Path archiveRoot,

            @Parameter(description = "Ensemble de permissions POSIX à appliquer sur les fichiers créés.")
            Set<PosixFilePermission> perms) {

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
