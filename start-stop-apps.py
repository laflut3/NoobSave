#!/usr/bin/env python3

import os
import re
import subprocess
import tkinter as tk
from tkinter import ttk, messagebox, simpledialog

###############################################################################
#                      CONFIGURATION DES SCRIPTS ET LOGO                      #
###############################################################################
SYS_STACK_DIR = ".sysStack"
LANCEMENT_SCRIPT = os.path.join(SYS_STACK_DIR, "lancement.sh")
ARRET_SCRIPT = os.path.join(SYS_STACK_DIR, "arret.sh")
LANCEMENT_DEBUG_SCRIPT = os.path.join(SYS_STACK_DIR, "lancement-debug.sh")
LOGO_PATH = os.path.join(SYS_STACK_DIR, "logo.png")

SPRING_PROPERTIES_FILE = "./.NoobSave_back/src/main/resources/application.properties"

# Variable pour stocker le mot de passe sudo
sudo_password = None

###############################################################################
#                          FONCTIONS UTILITAIRES                              #
###############################################################################
def ask_sudo_password():
    """
    Demande le mot de passe sudo via une boîte de dialogue.
    Renvoie None si l'utilisateur annule ou ferme la fenêtre.
    """
    return simpledialog.askstring(
        "Mot de passe Sudo",
        "Veuillez entrer votre mot de passe sudo :",
        show="*"
    )

def run_with_sudo(command):
    """
    Exécute une commande avec sudo, en utilisant le mot de passe fourni.
    Si le mot de passe est invalide, on redemande jusqu'à ce que l'utilisateur
    fournisse un mot de passe correct (ou ferme la boîte de dialogue).
    """
    global sudo_password

    while True:
        # Si on n'a pas encore de mot de passe, ou si on vient de le réinitialiser
        if not sudo_password:
            sudo_password = ask_sudo_password()
            if sudo_password is None:
                # L'utilisateur a annulé
                messagebox.showinfo("Info", "Opération annulée (sudo requis).")
                return False  # On retourne False pour indiquer l'échec

        # Tente d'exécuter la commande avec le mot de passe actuel
        command_with_sudo = f"echo {sudo_password} | sudo -S {command}"
        result = subprocess.run(command_with_sudo, shell=True, stderr=subprocess.PIPE)

        if result.returncode == 0:
            # Succès !
            return True
        else:
            # Mot de passe invalide ou autre erreur
            # On peut afficher le stderr pour diagnostiquer
            err_msg = result.stderr.decode().strip()
            messagebox.showerror(
                "Erreur",
                f"Échec de l'exécution de la commande (sudo invalide ?) :\n{err_msg}"
            )
            # On réinitialise le password pour forcer une nouvelle demande
            sudo_password = None
            # La boucle continue, ce qui redemande le mot de passe

def set_executable_permissions():
    """Donne les droits d'exécution aux scripts de lancement/arrêt."""
    # Ici, si run_with_sudo échoue (retourne False), on peut s’arrêter
    if not run_with_sudo(f"chmod +x {LANCEMENT_SCRIPT}"):
        return False
    if not run_with_sudo(f"chmod +x {ARRET_SCRIPT}"):
        return False
    if not run_with_sudo(f"chmod +x {LANCEMENT_DEBUG_SCRIPT}"):
        return False
    return True

###############################################################################
#                           FONCTIONS D'ACTIONS                               #
###############################################################################
def get_current_mongo_uri():
    """Lit l'URI MongoDB dans le fichier application.properties."""
    if not os.path.isfile(SPRING_PROPERTIES_FILE):
        return None

    with open(SPRING_PROPERTIES_FILE, 'r', encoding='utf-8') as f:
        for line in f:
            if line.startswith("spring.data.mongodb.uri="):
                return line.split("=", 1)[1].strip()
    return None

def update_mongo_uri(new_uri):
    """
    Met à jour l'URI MongoDB dans application.properties,
    en supprimant l'ancienne ligne 'spring.data.mongodb.uri=' si nécessaire,
    puis en insérant la nouvelle.
    """
    if not os.path.isfile(SPRING_PROPERTIES_FILE):
        messagebox.showerror("Erreur", "Le fichier application.properties est introuvable.")
        return

    with open(SPRING_PROPERTIES_FILE, 'r', encoding='utf-8') as f:
        lines = f.readlines()

    # On supprime toute ligne commençant par spring.data.mongodb.uri=
    lines = [l for l in lines if not l.startswith("spring.data.mongodb.uri=")]

    # Recherche du commentaire "# MongoDB configuration"
    insert_index = None
    for i, line in enumerate(lines):
        if line.strip() == "# MongoDB configuration":
            insert_index = i + 1
            break

    new_line = f"spring.data.mongodb.uri={new_uri}\n"

    if insert_index is not None:
        # On insère juste après '# MongoDB configuration'
        lines.insert(insert_index, new_line)
    else:
        # Sinon on l'ajoute à la fin
        lines.append("# MongoDB configuration\n")
        lines.append(new_line)

    # Écriture du fichier
    with open(SPRING_PROPERTIES_FILE, 'w', encoding='utf-8') as f:
        f.writelines(lines)

def start():
    """Lance les applications via le script de lancement (avec mise à jour URI si besoin)."""
    current_uri = get_current_mongo_uri() or ""
    prompt_text = f"Entrez la nouvelle URI MongoDB (laissez vide pour conserver l'actuelle) :\nActuelle : {current_uri}"
    new_uri = simpledialog.askstring("URI MongoDB", prompt_text)
    if new_uri is None:
        # L'utilisateur a annulé la saisie
        messagebox.showinfo("Info", "Aucune mise à jour de l'URI, opération annulée.")
        return

    new_uri = new_uri.strip()
    if new_uri:
        pattern = r"^mongodb\+srv://"
        if not re.match(pattern, new_uri):
            messagebox.showerror("Erreur", "L'URI MongoDB doit commencer par mongodb+srv://")
            return
        update_mongo_uri(new_uri)
        messagebox.showinfo("Info", "URI MongoDB mise à jour avec succès.")


def start_application():
    start()
    
    # Lance le script bash
    try:
        subprocess.Popen([f"./{LANCEMENT_SCRIPT}"], shell=True)
        messagebox.showinfo("Succès", "Applications démarrées avec succès.")
        status_var.set("Applications en cours d'exécution")
    except Exception as e:
        messagebox.showerror("Erreur", f"Échec du lancement : {e}")
        status_var.set("Échec du lancement")

def start_debug_application():
    start()

    # Lance le script bash
    try:
        subprocess.Popen([f"./{LANCEMENT_DEBUG_SCRIPT}"], shell=True)
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
#                   CRÉATION DE L'INTERFACE GRAPHIQUE TKINTER                 #
###############################################################################
root = tk.Tk()
root.title("Gestion des Applications - NoobSave")
root.geometry("600x700")
root.resizable(True, True)
root.configure(bg="#ECECEC")

style = ttk.Style()
style.configure("TLabel", font=("Helvetica", 12), background="#ECECEC")
style.configure("Title.TLabel", font=("Helvetica", 18, "bold"), foreground="#333", background="#ECECEC")
style.configure("Status.TLabel", font=("Helvetica", 11, "italic"), foreground="#555", background="#ECECEC")
style.configure("TButton", font=("Helvetica", 12, "bold"), padding=8)

main_frame = ttk.Frame(root, padding=20)
main_frame.pack(expand=True, fill="both")

main_frame.columnconfigure(0, weight=1)
for i in range(5):
    main_frame.rowconfigure(i, weight=0)
main_frame.rowconfigure(3, weight=1)  # Pour étirer un peu la zone du statut

# Logo
try:
    logo_image = tk.PhotoImage(file=LOGO_PATH)
    logo_label = ttk.Label(main_frame, image=logo_image, background="#ECECEC")
    logo_label.grid(row=0, column=0, pady=(0, 15))
except tk.TclError:
    fallback_label = ttk.Label(main_frame, text="(Logo non disponible)", style="TLabel")
    fallback_label.grid(row=0, column=0, pady=(0, 15))

title_label = ttk.Label(main_frame, text="NoobSave Management Tool", style="Title.TLabel")
title_label.grid(row=1, column=0, pady=(0, 30))

buttons_frame = ttk.Frame(main_frame)
buttons_frame.grid(row=2, column=0, pady=10)

btn_start = ttk.Button(buttons_frame, text="Lancer les applications", command=start_application)
btn_start.pack(pady=5, fill="x")

btn_stop = ttk.Button(buttons_frame, text="Arrêter les applications", command=stop_application)
btn_stop.pack(pady=5, fill="x")

btn_debug = ttk.Button(buttons_frame, text="Lancer en mode debug", command=start_debug_application)
btn_debug.pack(pady=5, fill="x")

status_var = tk.StringVar(value="En attente...")
status_label = ttk.Label(main_frame, textvariable=status_var, style="Status.TLabel")
status_label.grid(row=3, column=0, pady=(20, 0), sticky="n")

footer_label = ttk.Label(main_frame, text="© 2025 - NoobSave Management Tool")
footer_label.grid(row=4, column=0, pady=10, sticky="s")

###############################################################################
#            DEMANDE DU MOT DE PASSE SUDO + MISE EN PLACE DES DROITS          #
###############################################################################
def init_permissions():
    if not set_executable_permissions():
        # Si on n’arrive pas à mettre les droits (l’utilisateur refuse ou échoue),
        # on peut désactiver les boutons de l’application ou afficher un message
        btn_start.configure(state="disabled")
        btn_debug.configure(state="disabled")
        btn_stop.configure(state="disabled")
        messagebox.showwarning(
            "Attention",
            "Impossible de configurer les scripts (sudo refusé). L'application est désactivée."
        )

# On appelle cette fonction à la fin de la construction
root.after(100, init_permissions)

root.mainloop()
