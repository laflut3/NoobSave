#!/bin/bash

# Arrêt immédiat des conteneurs Docker
echo "Arrêt des conteneurs Docker..."
COMPOSE_DIR="./.NoobSave_back/src/main/resources/"
if [ -d "$COMPOSE_DIR" ]; then
    cd "$COMPOSE_DIR"
    docker compose down
    cd - > /dev/null
    echo "Conteneurs Docker arrêtés."
else
    echo "Erreur : Le répertoire Docker Compose ($COMPOSE_DIR) n'existe pas."
fi

# Arrêt des processus React
echo "Arrêt du serveur React..."
REACT_PID=$(pgrep -f "npm start")
if [ -n "$REACT_PID" ]; then
    kill "$REACT_PID"
    echo "Serveur React arrêté."
else
    echo "Aucun serveur React en cours d'exécution."
fi

# Arrêt des processus Spring Boot
echo "Arrêt de l'application Spring Boot..."
SPRING_PID=$(pgrep -f "spring-boot:run")
if [ -n "$SPRING_PID" ]; then
    kill "$SPRING_PID"
    echo "Application Spring Boot arrêtée."
else
    echo "Aucune application Spring Boot en cours d'exécution."
fi

echo "Toutes les applications ont été arrêtées avec succès."
