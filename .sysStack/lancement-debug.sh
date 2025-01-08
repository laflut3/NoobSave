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

# Nettoyage de l’écran
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
    sleep 0.02
  done
  echo
}

###############################################################################
#                           DÉBUT DU SCRIPT                                   #
###############################################################################

# (Optionnel) Petit ASCII art pour le visuel
echo -e "${BOLD}${MAGENTA}"
echo "    _   __           ____   ____                "
echo "   / | / /___ ______/ __ \ / __ \___  ____  ___ "
echo "  /  |/ / __ \`/ ___/ / / // / / / _ \/ __ \/ _ \\"
echo " / /|  / /_/ (__  ) /_/ // /_/ /  __/ /_/ /  __/ "
echo "/_/ |_/\__,_/____/____(_)____/\___/ .___/\___/  "
echo "                                /_/             "
echo -e "     NOOB SAVE - Script de lancement (DEBUG)${RESET}\n"

###############################################################################
#                  1. CRÉATION / VÉRIFICATION DU DOSSIER ARCHIVE             #
###############################################################################
ARCHIVE_FILE="archive"

echo -e "${YELLOW}• Vérification du dossier 'archive'...${RESET}"

if [ ! -e "$ARCHIVE_FILE" ]; then
    mkdir "$ARCHIVE_FILE"
    echo "Dossier 'archive' créé."
    sudo chmod 777 "$ARCHIVE_FILE"
    echo "Droits 777 accordés au dossier 'archive'."
elif [ -f "$ARCHIVE_FILE" ]; then
    echo -e "${RED}Erreur : 'archive' existe déjà en tant que fichier.${RESET}"
    exit 1
else
    echo "Le dossier 'archive' existe déjà."
    sudo chmod 777 "$ARCHIVE_FILE"
    echo "Droits 777 vérifiés/ajustés sur le dossier 'archive'."
fi

progress_bar 15 "Vérification terminée."

###############################################################################
#                2. DÉMARRAGE DE L'APPLICATION SPRING BOOT                    #
###############################################################################
SPRING_BOOT_DIR="./.NoobSave_back/"

echo -e "${YELLOW}• Démarrage de l'application Spring Boot...${RESET}"
if [ -d "$SPRING_BOOT_DIR" ]; then
    cd "$SPRING_BOOT_DIR" || exit 1

    if [ -f "./mvnw" ]; then
        # Ici, AUCUNE redirection => on voit TOUT
        ./mvnw spring-boot:run &
        SPRING_BOOT_PID=$!

        progress_bar 20 "Spring Boot en cours de démarrage..."
        echo -e "${GREEN}✓ Application Spring Boot démarrée (PID: $SPRING_BOOT_PID).${RESET}"
    else
        echo -e "${RED}Erreur : Le fichier 'mvnw' est introuvable dans $SPRING_BOOT_DIR.${RESET}"
        exit 1
    fi

    # Retour au répertoire initial
    cd - || exit 1
else
    echo -e "${RED}Erreur : Le répertoire Spring Boot ($SPRING_BOOT_DIR) n'existe pas.${RESET}"
    exit 1
fi

###############################################################################
#                 3. DÉMARRAGE DE L'APPLICATION REACT                         #
###############################################################################
REACT_APP_DIR="./.NoobSave_front"

echo -e "${YELLOW}• Démarrage de l'application React...${RESET}"
if [ -d "$REACT_APP_DIR" ]; then
    cd "$REACT_APP_DIR" || exit 1

    echo "Installation des dépendances NPM..."
    # Pas de redirection => on voit TOUT
    npm install
    progress_bar 25 "Installation NPM..."

    npm start &
    REACT_APP_PID=$!

    progress_bar 10 "Démarrage React..."
    echo -e "${GREEN}✓ Application React démarrée (PID: $REACT_APP_PID).${RESET}"

    # Retour au répertoire initial
    cd - || exit 1
else
    echo -e "${RED}Erreur : Le répertoire React ($REACT_APP_DIR) n'existe pas.${RESET}"
    exit 1
fi

###############################################################################
#                     4. MESSAGE FINAL ET GESTION DU SCRIPT                   #
###############################################################################
echo -e "${BOLD}${BLUE}Toutes les applications (mode DEBUG) ont été démarrées.${RESET}"

# Si le script reçoit CTRL+C ou un signal d’arrêt, on tue les processus lancés
trap "kill $SPRING_BOOT_PID $REACT_APP_PID 2>/dev/null || true" SIGINT SIGTERM

# On attend (wait) pour que le script continue à “vivre” tant que nos applis tournent
wait
