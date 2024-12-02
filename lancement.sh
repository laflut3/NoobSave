#!/bin/bash

# Arrêt immédiat en cas d'erreur
set -e

# Chemin pour le fichier archive
ARCHIVE_FILE="archive"

# Vérification et création du fichier archive
if [ ! -e "$ARCHIVE_FILE" ]; then
    mkdir "$ARCHIVE_FILE"
    echo "Dossier archive créé."
elif [ -f "$ARCHIVE_FILE" ]; then
    echo "Erreur : 'archive' existe déjà en tant que fichier."
    exit 1
else
    echo "Le dossier archive existe déjà."
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
COMPOSE_DIR="./.NoobSave_back/src/main/resources/"
if [ -d "$COMPOSE_DIR" ]; then
    echo "Démarrage des conteneurs avec docker-compose..."
    cd "$COMPOSE_DIR"
    docker compose up -d
    cd - > /dev/null
    echo "Conteneurs démarrés."
else
    echo "Erreur : Le répertoire Docker Compose ($COMPOSE_DIR) n'existe pas."
    exit 1
fi

# Démarrage de l'application Spring Boot
SPRING_BOOT_DIR="./.NoobSave_back/"
if [ -d "$SPRING_BOOT_DIR" ]; then
    echo "Démarrage de l'application Spring Boot..."
    cd "$SPRING_BOOT_DIR"
    if [ -f "./mvnw" ]; then
        ./mvnw spring-boot:run & SPRING_BOOT_PID=$!
        echo "Application Spring Boot démarrée (PID: $SPRING_BOOT_PID)."
    else
        echo "Erreur : Le fichier 'mvnw' est introuvable dans $SPRING_BOOT_DIR."
        exit 1
    fi
    cd - > /dev/null
else
    echo "Erreur : Le répertoire Spring Boot ($SPRING_BOOT_DIR) n'existe pas."
    exit 1
fi

# Démarrage de l'application React
REACT_APP_DIR="./.NoobSave_front"
if [ -d "$REACT_APP_DIR" ]; then
    echo "Démarrage de l'application React..."
    cd "$REACT_APP_DIR"
    npm install
    npm start & REACT_APP_PID=$!
    echo "Application React démarrée (PID: $REACT_APP_PID)."
    cd - > /dev/null
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
