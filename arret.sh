#!/bin/bash

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
