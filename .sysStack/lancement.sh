#!/bin/bash

###############################################################################
#                           COULEURS ET STYLES                                #
###############################################################################
RED="\033[0;31m"
GREEN="\033[0;32m"
YELLOW="\033[0;33m"
BLUE="\033[0;34m"
MAGENTA="\033[0;35m"
CYAN="\033[0;36m"
BOLD="\033[1m"
RESET="\033[0m"

# Arrêt immédiat en cas d'erreur dans le script
set -e
clear

###############################################################################
#                           BARRE DE PROGRESSION                               #
###############################################################################
progress_bar() {
  local duration=$1  # Nombre d'étapes pour la progression
  local msg="$2"     # Message à afficher (optionnel)

  [ -n "$msg" ] && echo -e "$msg"

  echo -ne "${CYAN}En cours... [   0%]${RESET}"
  for ((i=1; i<=duration; i++)); do
    local percent=$(( i * 100 / duration ))
    echo -ne "\r${CYAN}En cours... [  ${percent}%]${RESET}"
    sleep 0.02  # Ajustez la vitesse de progression (en secondes)
  done
  echo
}

###############################################################################
#                           DÉBUT DU SCRIPT                                   #
###############################################################################

# Nettoyage de l’écran
clear

# (Optionnel) Petit ASCII art de présentation
echo -e "${BOLD}${MAGENTA}"
echo "     _   __           ____   ____                 "
echo "    / | / /___ ______/ __ \ / __ \___  ____  ___  "
echo "   /  |/ / __ \`/ ___/ / / // / / / _ \/ __ \/ _ \ "
echo "  / /|  / /_/ (__  ) /_/ // /_/ /  __/ /_/ /  __/ "
echo " /_/ |_/\__,_/____/____(_)____/\___/ .___/\___/  "
echo "                                 /_/              "
echo -e "          NOOB SAVE - Script de lancement${RESET}\n"

###############################################################################
#                  1. CRÉATION / VÉRIFICATION DU DOSSIER ARCHIVE             #
###############################################################################

ARCHIVE_FILE="archive"

echo -e "${YELLOW}• Vérification du dossier 'archive'...${RESET}"

if [ ! -e "$ARCHIVE_FILE" ]; then
    mkdir "$ARCHIVE_FILE"
    echo "Dossier 'archive' créé."
    # On peut rediriger la sortie standard de chmod si on ne veut rien afficher.
    sudo chmod 777 "$ARCHIVE_FILE" &>/dev/null
    echo "Droits 777 accordés au dossier 'archive'."
elif [ -f "$ARCHIVE_FILE" ]; then
    echo -e "${RED}Erreur : 'archive' existe déjà en tant que fichier.${RESET}"
    exit 1
else
    echo "Le dossier 'archive' existe déjà."
    sudo chmod 777 "$ARCHIVE_FILE" &>/dev/null
    echo "Droits 777 vérifiés/ajustés sur le dossier 'archive'."
fi
progress_bar 15 "Vérification terminée."

###############################################################################
#                       2. MISE À JOUR DE L'URI MONGODB                       #
###############################################################################
SPRING_PROPERTIES_FILE="./.NoobSave_back/src/main/resources/application.properties"

echo -e "${YELLOW}• Vérification/mise à jour de l'URI MongoDB...${RESET}"
if [ -f "$SPRING_PROPERTIES_FILE" ]; then
    
    # Lecture de l'URI existante
    EXISTING_URI=$(grep "^spring.data.mongodb.uri=" "$SPRING_PROPERTIES_FILE" \
        | cut -d'=' -f2-)

    if [ -z "$EXISTING_URI" ]; then
        echo "Aucune URI MongoDB configurée pour l'instant."
    else
        echo "URI actuelle : $EXISTING_URI"
    fi

    # Demande à l'utilisateur
    echo -e "${CYAN}Entrez l'URI MongoDB (laisser vide pour conserver l'actuelle) :${RESET}"
    read -r USER_INPUT_URI

    if [ -n "$USER_INPUT_URI" ]; then
        # Vérifie le format (exemple de validation basique)
        if [[ "$USER_INPUT_URI" =~ ^mongodb\+srv://.+ ]]; then
            echo "Mise à jour de l'URI MongoDB..."
            sed -i "/^spring.data.mongodb.uri=/d" "$SPRING_PROPERTIES_FILE"
            sed -i "/# MongoDB configuration/a spring.data.mongodb.uri=$USER_INPUT_URI" \
                "$SPRING_PROPERTIES_FILE"
            echo "URI MongoDB mise à jour avec succès."
        else
            echo -e "${RED}Erreur : L'URI MongoDB fournie est invalide.${RESET}"
            exit 1
        fi
    else
        echo "URI MongoDB conservée."
    fi
else
    echo -e "${RED}Erreur : Le fichier application.properties est introuvable dans :${RESET}"
    echo "$SPRING_PROPERTIES_FILE"
    exit 1
fi
progress_bar 15 "Mise à jour de l'URI terminée."

###############################################################################
#                3. DÉMARRAGE DE L'APPLICATION SPRING BOOT                    #
###############################################################################
SPRING_BOOT_DIR="./.NoobSave_back/"

echo -e "${YELLOW}• Démarrage de l'application Spring Boot...${RESET}"
if [ -d "$SPRING_BOOT_DIR" ]; then
    cd "$SPRING_BOOT_DIR" || exit 1

    if [ -f "./mvnw" ]; then
        # Pour n'afficher que les erreurs (stderr), on redirige stdout
        # (vous pouvez enlever '&>/dev/null' si vous souhaitez voir les logs).
        ./mvnw spring-boot:run 1>/dev/null 2>&1 &
        SPRING_BOOT_PID=$!

        progress_bar 20 "Spring Boot en cours de démarrage..."
        echo -e "${GREEN}✓ Application Spring Boot démarrée (PID: $SPRING_BOOT_PID).${RESET}"
    else
        echo -e "${RED}Erreur : Le fichier 'mvnw' est introuvable dans $SPRING_BOOT_DIR.${RESET}"
        exit 1
    fi

    # On revient au répertoire initial
    cd - &>/dev/null
else
    echo -e "${RED}Erreur : Le répertoire Spring Boot ($SPRING_BOOT_DIR) n'existe pas.${RESET}"
    exit 1
fi

###############################################################################
#                 4. DÉMARRAGE DE L'APPLICATION REACT                         #
###############################################################################
REACT_APP_DIR="./.NoobSave_front"

echo -e "${YELLOW}• Démarrage de l'application React...${RESET}"
if [ -d "$REACT_APP_DIR" ]; then
    cd "$REACT_APP_DIR" || exit 1

    # Installation des dépendances (on redirige la sortie std)
    echo "Installation des dépendances NPM..."
    npm install 1>/dev/null
    progress_bar 25 "Installation NPM..."

    # Lancement de l'appli React (on redirige la sortie si on veut cacher tout sauf les erreurs)
    npm start 1>/dev/null 2>&1 &
    REACT_APP_PID=$!

    progress_bar 10 "Démarrage React..."
    echo -e "${GREEN}✓ Application React démarrée (PID: $REACT_APP_PID).${RESET}"

    # Retour au répertoire initial
    cd - &>/dev/null
else
    echo -e "${RED}Erreur : Le répertoire React ($REACT_APP_DIR) n'existe pas.${RESET}"
    exit 1
fi

###############################################################################
#                     5. MESSAGE FINAL ET GESTION DU SCRIPT                   #
###############################################################################
echo -e "${BOLD}${BLUE}Toutes les applications ont été démarrées avec succès.${RESET}"

# Si le script reçoit CTRL+C ou un signal d’arrêt, on tue les processus lancés
trap "kill $SPRING_BOOT_PID $REACT_APP_PID 2>/dev/null || true" SIGINT SIGTERM

# On attend (wait) pour que le script continue à “vivre” tant que nos applis tournent
wait
