package NoobSave._L.garcia.NoobSave.repository;

import NoobSave._L.garcia.NoobSave.entities.Fichier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FichierRepository extends JpaRepository<Fichier, Long> {
    Optional<Fichier> findByNom(String nom);
}

