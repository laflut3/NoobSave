#!/bin/bash

###################
#    COULEURS     #
###################
RED="\033[0;31m"
GREEN="\033[0;32m"
YELLOW="\033[0;33m"
BLUE="\033[0;34m"
MAGENTA="\033[0;35m"
CYAN="\033[0;36m"
BOLD="\033[1m"
RESET="\033[0m"

###############################
#  FONCTION : Barre de chargement simulée
###############################
progress_bar() {
  local duration=$1  # Nombre d'étapes

  echo -ne "${CYAN}Arrêt en cours... [   0%]${RESET}"
  for ((i=1; i<=duration; i++)); do
    # Calcul du pourcentage (i/duration)*100
    local percent=$(( i * 100 / duration ))
    # Retour au début de la ligne (\r) et on réécrit
    echo -ne "\r${CYAN}Arrêt en cours... [  ${percent}%]${RESET}"
    sleep 0.05  # Ajustez la vitesse si besoin
  done
  # Saut de ligne à la fin
  echo
}

###############################
#        DEBUT DU SCRIPT      #
###############################

# 1. Nettoie l’écran
clear

# (Optionnel) Petit ASCII art pour la “jolie présentation” :
echo -e "${BOLD}${MAGENTA}"
echo "     ___         _      __       "
echo "    / _ | ___   (_)__  / /__ ____"
echo "   / __ |/ _ \ / / _ \/ / -_) __/"
echo "  /_/ |_/_//_//_/_//_/_/\__/_/   "
echo -e "      Noob Save Stop      ${RESET}\n"

#################################
#    2. Arrêt du serveur React  #
#################################
echo -e "${YELLOW}• Arrêt du serveur React...${RESET}"
pkill -f "react-scripts/scripts/start.js"
progress_bar 20  # Barre de progression simulée
echo -e "${GREEN}✓ Serveur React arrêté.${RESET}\n"

#############################################
#  3. Arrêt de l'application Spring Boot    #
#############################################
echo -e "${YELLOW}• Arrêt de l'application Spring Boot...${RESET}"
SPRING_PID=$(pgrep -f "spring-boot:run")
if [ -n "$SPRING_PID" ]; then
    kill "$SPRING_PID"
    progress_bar 20  # Barre de progression simulée
    echo -e "${GREEN}✓ Application Spring Boot arrêtée.${RESET}\n"
else
    echo -e "${RED}Aucune application Spring Boot en cours d'exécution.${RESET}\n"
fi

#################################################
#    4. Message final - tout est arrêté         #
#################################################
echo -e "${BOLD}${BLUE}Toutes les applications ont été arrêtées avec succès.${RESET}\n"
