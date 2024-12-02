CREATE DATABASE NSave

CREATE TABLE NSave (
                       id INT AUTO_INCREMENT PRIMARY KEY,  -- Clé primaire pour identifier chaque enregistrement
                       titre VARCHAR(255) NOT NULL,        -- Attribut de type string pour le titre
                       file LONGBLOB NOT NULL,             -- Attribut de type buffer pour stocker des fichiers
                       date_ajout DATETIME DEFAULT CURRENT_TIMESTAMP,           -- Date d'ajout avec valeur par défaut à l'insertion
                       date_modification DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- Date de modification mise à jour automatiquement
);
