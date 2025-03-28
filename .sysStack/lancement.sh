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

# Arrêt immédiat en cas d'erreur
set -e
clear

###############################################################################
#                           BARRE DE PROGRESSION                               #
###############################################################################
progress_bar() {
  local duration=$1
  local msg="$2"

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
clear

# Petit ASCII art
echo -e "${BOLD}${MAGENTA}"
echo "     _   __           ____   ____                 "
echo "    / | / /___ ______/ __ \ / __ \___  ____  ___  "
echo "   /  |/ / __ \`/ ___/ / / // / / / _ \/ __ \/ _ \ "
echo "  / /|  / /_/ (__  ) /_/ // /_/ /  __/ /_/ /  __/ "
echo " /_/ |_/\__,_/____/____(_)____/\___/ .___/\___/  "
echo "                                 /_/              "
echo -e "          NOOB SAVE - Script de lancement${RESET}\n"

###############################################################################
# 1. Vérification du dossier 'archive'
###############################################################################
ARCHIVE_FILE="archive"

echo -e "${YELLOW}• Vérification du dossier 'archive'...${RESET}"

if [ ! -e "$ARCHIVE_FILE" ]; then
    mkdir "$ARCHIVE_FILE"
    echo "Dossier 'archive' créé."
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
# 2. Démarrage de l'application Spring Boot
###############################################################################
SPRING_BOOT_DIR="./.NoobSave_back/"

echo -e "${YELLOW}• Démarrage de l'application Spring Boot...${RESET}"
if [ -d "$SPRING_BOOT_DIR" ]; then
    cd "$SPRING_BOOT_DIR" || exit 1

    if [ -f "./mvnw" ]; then
        # Redirige la stdout pour ne rien voir sauf en cas d'erreur
        ./mvnw spring-boot:run 1>/dev/null &
        SPRING_BOOT_PID=$!

        progress_bar 20 "Spring Boot en cours de démarrage..."
        echo -e "${GREEN}✓ Application Spring Boot démarrée (PID: $SPRING_BOOT_PID).${RESET}"
    else
        echo -e "${RED}Erreur : Le fichier 'mvnw' est introuvable dans $SPRING_BOOT_DIR.${RESET}"
        exit 1
    fi

    cd - &>/dev/null
else
    echo -e "${RED}Erreur : Le répertoire Spring Boot ($SPRING_BOOT_DIR) n'existe pas.${RESET}"
    exit 1
fi

###############################################################################
# 3. Démarrage de l'application React
###############################################################################
REACT_APP_DIR="./.NoobSave_front"

echo -e "${YELLOW}• Démarrage de l'application React...${RESET}"
if [ -d "$REACT_APP_DIR" ]; then
    cd "$REACT_APP_DIR" || exit 1

    echo "Installation des dépendances NPM..."
    npm install --loglevel=error 1>/dev/null
    progress_bar 25 "Installation NPM..."

    # Lancement de l'appli (stdout masquée, stderr visible)
    CI=true npm start 1>/dev/null 2>&1 &
    REACT_APP_PID=$!

    progress_bar 10 "Démarrage React..."
    echo -e "${GREEN}✓ Application React démarrée (PID: $REACT_APP_PID).${RESET}"

    cd - &>/dev/null
else
    echo -e "${RED}Erreur : Le répertoire React ($REACT_APP_DIR) n'existe pas.${RESET}"
    exit 1
fi

###############################################################################
# 4. Message final & gestion signaux
###############################################################################
echo -e "${BOLD}${BLUE}Toutes les applications ont été démarrées avec succès.${RESET}"

trap "kill $SPRING_BOOT_PID $REACT_APP_PID 2>/dev/null || true" SIGINT SIGTERM
wait
