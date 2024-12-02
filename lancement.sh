#!/bin/bash

# Arrêt immédiat en cas d'erreur
set -e

# Chemin pour le fichier archive
ARCHIVE_FILE="archive"

# Vérification et création du fichier archive
if [ -f "$ARCHIVE_FILE" ]; then
    echo "Le fichier archive existe déjà."
else
    echo "Création du fichier archive..."
    touch "$ARCHIVE_FILE"
    echo "Fichier archive créé."
fi

# Vérification des images Docker
function check_and_pull_image() {
    IMAGE=$1
    if docker image inspect "$IMAGE" > /dev/null 2>&1; then
        echo "L'image Docker $IMAGE est déjà téléchargée."
    else
        echo "Téléchargement de l'image Docker $IMAGE..."
        docker pull "$IMAGE"
        echo "Image $IMAGE téléchargée avec succès."
    fi
}

check_and_pull_image "adminer:latest"
check_and_pull_image "mariadb:latest"

# Démarrage des conteneurs avec Docker Compose
echo "Démarrage des conteneurs avec docker-compose..."
docker-compose up -d
echo "Conteneurs démarrés."

# Démarrage de l'application Spring Boot
SPRING_BOOT_DIR="./path_to_spring_boot_app" # Changez ce chemin
if [ -d "$SPRING_BOOT_DIR" ]; then
    echo "Démarrage de l'application Spring Boot..."
    cd "$SPRING_BOOT_DIR"
    ./mvnw spring-boot:run &
    SPRING_BOOT_PID=$!
    cd - > /dev/null
    echo "Application Spring Boot démarrée (PID: $SPRING_BOOT_PID)."
else
    echo "Erreur : Le répertoire Spring Boot ($SPRING_BOOT_DIR) n'existe pas."
    exit 1
fi

# Démarrage de l'application React
REACT_APP_DIR="./path_to_react_app" # Changez ce chemin
if [ -d "$REACT_APP_DIR" ]; then
    echo "Démarrage de l'application React..."
    cd "$REACT_APP_DIR"
    npm install
    npm start &
    REACT_APP_PID=$!
    cd - > /dev/null
    echo "Application React démarrée (PID: $REACT_APP_PID)."
else
    echo "Erreur : Le répertoire React ($REACT_APP_DIR) n'existe pas."
    exit 1
fi

# Confirmation de l'exécution
echo "Toutes les applications ont été démarrées avec succès."

# Terminer les processus lorsque le script est interrompu
trap "kill $SPRING_BOOT_PID $REACT_APP_PID" SIGINT SIGTERM

# Attente pour garder les processus en vie
wait
