#!/bin/bash

# Arrêt immédiat en cas d'erreur
set -e

# Chemin pour le fichier archive
ARCHIVE_FILE="archive"

# Vérification et création du fichier archive
if [ ! -e "$ARCHIVE_FILE" ]; then
    mkdir "$ARCHIVE_FILE"
    echo "Dossier archive créé."
    # Accorder les droits nécessaires sur le répertoire
    sudo chmod 777 "$ARCHIVE_FILE"
    echo "Droits 777 accordés au dossier archive."
elif [ -f "$ARCHIVE_FILE" ]; then
    echo "Erreur : 'archive' existe déjà en tant que fichier."
    exit 1
else
    echo "Le dossier archive existe déjà."
    # Vérifier et corriger les permissions si nécessaire
    sudo chmod 777 "$ARCHIVE_FILE"
    echo "Droits 777 vérifiés/ajustés sur le dossier archive."
fi

# Mise à jour de l'URI MongoDB
SPRING_PROPERTIES_FILE="./.NoobSave_back/src/main/resources/application.properties"

if [ -f "$SPRING_PROPERTIES_FILE" ]; then
    echo "Vérification de l'URI MongoDB..."

    # Lecture de l'URI existante dans application.properties
    EXISTING_URI=$(grep "^spring.data.mongodb.uri=" "$SPRING_PROPERTIES_FILE" | cut -d'=' -f2-)

    if [ -z "$EXISTING_URI" ]; then
        echo "Aucune URI MongoDB configurée."
    else
        echo "URI actuelle : $EXISTING_URI"
    fi

    # Demander à l'utilisateur une nouvelle URI ou conserver l'existante
    read -p "Entrez l'URI MongoDB (laisser vide pour conserver l'actuelle) : " USER_INPUT_URI
    if [ -n "$USER_INPUT_URI" ]; then
        # Valider le format de l'URI MongoDB
        if [[ "$USER_INPUT_URI" =~ ^mongodb\+srv://.+ ]]; then
            echo "Mise à jour de l'URI MongoDB..."

            # Supprimer l'ancienne ligne si elle existe
            sed -i "/^spring.data.mongodb.uri=/d" "$SPRING_PROPERTIES_FILE"

            # Insérer la nouvelle URI juste après la ligne `# MongoDB configuration`
            sed -i "/# MongoDB configuration/a spring.data.mongodb.uri=$USER_INPUT_URI" "$SPRING_PROPERTIES_FILE"

            echo "URI MongoDB mise à jour avec succès."
        else
            echo "Erreur : L'URI MongoDB fournie est invalide."
            exit 1
        fi
    else
        echo "URI MongoDB conservée."
    fi
else
    echo "Erreur : Le fichier application.properties est introuvable dans $SPRING_PROPERTIES_FILE."
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
