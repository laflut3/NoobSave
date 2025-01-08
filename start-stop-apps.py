#!/usr/bin/env python3

import os
import subprocess
import tkinter as tk
from tkinter import ttk, messagebox, simpledialog

###############################################################################
#                      CONFIGURATION DES SCRIPTS ET LOGO                      #
###############################################################################
SYS_STACK_DIR = ".sysStack"
LANCEMENT_SCRIPT = os.path.join(SYS_STACK_DIR, "lancement.sh")
ARRET_SCRIPT = os.path.join(SYS_STACK_DIR, "arret.sh")
LOGO_PATH = os.path.join(SYS_STACK_DIR, "logo.png")  # <-- Chemin vers le logo

# Variable pour stocker le mot de passe sudo
sudo_password = None

###############################################################################
#                          FONCTIONS UTILITAIRES                              #
###############################################################################
def ask_sudo_password():
    """Demande le mot de passe sudo via une boîte de dialogue."""
    global sudo_password
    sudo_password = simpledialog.askstring(
        "Mot de passe Sudo",
        "Veuillez entrer votre mot de passe sudo :",
        show="*"
    )
    if not sudo_password:
        messagebox.showerror("Erreur", "Mot de passe sudo requis.")
        exit(1)

def run_with_sudo(command):
    """Exécute une commande avec sudo, en utilisant le mot de passe fourni."""
    global sudo_password
    command_with_sudo = f"echo {sudo_password} | sudo -S {command}"
    result = subprocess.run(command_with_sudo, shell=True, stderr=subprocess.PIPE)
    if result.returncode != 0:
        messagebox.showerror("Erreur", f"Échec de l'exécution de la commande : {result.stderr.decode()}")
        exit(1)

def set_executable_permissions():
    """Donne les droits d'exécution aux scripts de lancement/arrêt."""
    try:
        run_with_sudo(f"chmod +x {LANCEMENT_SCRIPT}")
        run_with_sudo(f"chmod +x {ARRET_SCRIPT}")
    except FileNotFoundError:
        messagebox.showerror("Erreur", f"Les fichiers {LANCEMENT_SCRIPT} ou {ARRET_SCRIPT} sont introuvables.")
        exit(1)

###############################################################################
#                           FONCTIONS D'ACTIONS                               #
###############################################################################
def start_application():
    """Lance les applications via le script de lancement."""
    try:
        subprocess.Popen([f"./{LANCEMENT_SCRIPT}"], shell=True)
        messagebox.showinfo("Succès", "Applications démarrées avec succès.")
        status_var.set("Applications en cours d'exécution")
    except Exception as e:
        messagebox.showerror("Erreur", f"Échec du lancement : {e}")
        status_var.set("Échec du lancement")

def stop_application():
    """Arrête les applications via le script d'arrêt."""
    try:
        subprocess.Popen([f"./{ARRET_SCRIPT}"], shell=True)
        messagebox.showinfo("Succès", "Applications arrêtées avec succès.")
        status_var.set("Applications arrêtées")
    except Exception as e:
        messagebox.showerror("Erreur", f"Échec de l'arrêt : {e}")
        status_var.set("Échec de l'arrêt")

###############################################################################
#                        DEMANDE DU MOT DE PASSE SUDO                         #
###############################################################################
ask_sudo_password()
set_executable_permissions()

###############################################################################
#                        CRÉATION DE L'INTERFACE TKINTER                      #
###############################################################################
root = tk.Tk()
root.title("Gestion des Applications - NoobSave")

# Dimensions plus généreuses et autorisation du redimensionnement
root.geometry("600x700")   
root.resizable(True, True)

# Couleur de fond
root.configure(bg="#ECECEC")

###############################################################################
#                          STYLES PERSONNALISÉS                               #
###############################################################################
style = ttk.Style()
# style.theme_use("clam")  # <- active ce thème si tu veux changer l'apparence

style.configure("TLabel", font=("Helvetica", 12), background="#ECECEC")
style.configure("Title.TLabel", font=("Helvetica", 18, "bold"), foreground="#333", background="#ECECEC")
style.configure("Status.TLabel", font=("Helvetica", 11, "italic"), foreground="#555", background="#ECECEC")
style.configure("TButton", font=("Helvetica", 12, "bold"), padding=8)

###############################################################################
#               WIDGET PRINCIPAL : UN FRAME QUI VA S'ÉTIRER                   #
###############################################################################
main_frame = ttk.Frame(root, padding=20)
main_frame.pack(expand=True, fill="both")

# On autorise le redimensionnement en "poussant" ce frame
main_frame.columnconfigure(0, weight=1)
main_frame.rowconfigure(0, weight=0)  # Logo
main_frame.rowconfigure(1, weight=0)  # Titre
main_frame.rowconfigure(2, weight=0)  # Boutons
main_frame.rowconfigure(3, weight=1)  # Espace (statut)
main_frame.rowconfigure(4, weight=0)  # Footer

###############################################################################
#                          LOGO (IMAGE)                                       #
###############################################################################
try:
    logo_image = tk.PhotoImage(file=LOGO_PATH)
    logo_label = ttk.Label(main_frame, image=logo_image, background="#ECECEC")
    logo_label.grid(row=0, column=0, pady=(0, 15))
except tk.TclError:
    fallback_label = ttk.Label(main_frame, text="(Logo non disponible)", style="TLabel")
    fallback_label.grid(row=0, column=0, pady=(0, 15))

###############################################################################
#                         TITRE DE L'APPLICATION                               #
###############################################################################
title_label = ttk.Label(main_frame, text="NoobSave Management Tool", style="Title.TLabel")
title_label.grid(row=1, column=0, pady=(0, 30))

###############################################################################
#                       BOUTONS D'ACTION (Start / Stop)                        #
###############################################################################
buttons_frame = ttk.Frame(main_frame)
buttons_frame.grid(row=2, column=0, pady=10)

btn_start = ttk.Button(buttons_frame, text="Lancer les applications", command=start_application)
btn_start.pack(pady=5, fill="x")

btn_stop = ttk.Button(buttons_frame, text="Arrêter les applications", command=stop_application)
btn_stop.pack(pady=5, fill="x")

###############################################################################
#                      ÉTIQUETTE D'ÉTAT (status_var)                           #
###############################################################################
status_var = tk.StringVar(value="En attente...")
status_label = ttk.Label(main_frame, textvariable=status_var, style="Status.TLabel")
status_label.grid(row=3, column=0, pady=(20, 0), sticky="n")

###############################################################################
#                             PIED DE PAGE                                    #
###############################################################################
footer_label = ttk.Label(main_frame, text="© 2025 - NoobSave Management Tool")
footer_label.grid(row=4, column=0, pady=10, sticky="s")

###############################################################################
#                   LANCEMENT DE LA BOUCLE PRINCIPALE                         #
###############################################################################
root.mainloop()
