import React, {useEffect, useState} from "react";
import {FaRegClock, FaFileAlt, FaPlay, FaPause} from "react-icons/fa";
import {PiPath} from "react-icons/pi";

/**
 * Types de fichiers possibles à cocher.
 */
const possibleFileTypes = [".pdf", ".txt", ".docx", ".jpg", ".png"];

export default function Parametre() {
    const [param, setParam] = useState(null);
    const [isForbidden, setIsForbidden] = useState(false);
    const [isNotConnected, setIsNotConnected] = useState(false);


    const [enabled, setEnabled] = useState(false);
    const [intervalMs, setIntervalMs] = useState(60000);

    // Liste des extensions cochées
    const [checkedExtensions, setCheckedExtensions] = useState([]);

    useEffect(() => {
        const token = localStorage.getItem("token");
        if (!token) {
            setIsNotConnected(true);
            return;
        }

        fetch("http://localhost:8080/api/parametres", {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        })
            .then((res) => {
                if (res.status === 403) {
                    // L’utilisateur n’a pas les droits admin
                    setIsForbidden(true);
                    return null; // On arrête là (pas de res.json())
                }
                if (!res.ok) {
                    throw new Error(`Erreur HTTP : ${res.status}`);
                }
                return res.json();
            })
            .then((data) => {
                if (data) {
                    setParam(data);
                    setEnabled(data.autoSaveEnabled);
                    setIntervalMs(data.autoSaveInterval);

                    const existing = data.allowedFileExtensions
                        ?.split(",")
                        .map((ext) => ext.trim()) || [];
                    setCheckedExtensions(existing);
                }
            })
            .catch((err) => {
                console.error("Erreur lors du chargement des param:", err);
            });
    }, []);


    const [savePath, setSavePath] = useState("");

    useEffect(() => {
        fetch("http://localhost:8080/api/parametres/save-path")
            .then((res) => res.text())
            .then(setSavePath)
            .catch(console.error);
    }, []);

    const updateSavePath = (folderPath) => {
        console.log("Chemin envoyé au backend :", folderPath); // Ajout de log

        if (!folderPath || folderPath.trim() === "") {
            console.error("Le chemin est vide ou invalide.");
            return;
        }

        fetch(`http://localhost:8080/api/parametres/save-path?path=${encodeURIComponent(folderPath)}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
        })
            .then((res) => {
                if (!res.ok) {
                    throw new Error(`Erreur serveur : ${res.status}`);
                }
                console.log("Chemin sauvegardé :", folderPath);
            })
            .catch((err) => {
                console.error("Erreur lors de la mise à jour du chemin :", err.message);
            });
    };


    const deleteSavePath = () => {
        fetch("http://localhost:8080/api/parametres/save-path", {
            method: "DELETE",
        })
            .then(() => {
                setSavePath(""); // Réinitialise le champ côté frontend
            })
            .catch(console.error);
    };

    const updateAutoSave = () => {
        fetch(`http://localhost:8080/api/parametres/toggle-auto-save?enabled=${enabled}`, {
            method: "POST",
        }).catch(console.error);
    };

    const updateInterval = () => {
        // On envoie la valeur d'intervalMs au backend
        fetch(`http://localhost:8080/api/parametres/interval?intervalMs=${intervalMs}`, {
            method: "POST",
        }).catch(console.error);
    };

    const updateFileTypes = () => {
        const body = new URLSearchParams();
        checkedExtensions.forEach((ft) => {
            body.append("filetypes", ft);
        });

        fetch("http://localhost:8080/api/parametres/filetypes", {
            method: "POST",
            body: body,
        }).catch(console.error);
    };

    // Gérer un clic sur une checkbox
    const handleCheckboxChange = (ext) => {
        if (checkedExtensions.includes(ext)) {
            setCheckedExtensions(checkedExtensions.filter((e) => e !== ext));
        } else {
            setCheckedExtensions([...checkedExtensions, ext]);
        }
    };

    // Forcer la valeur minimum à 5000
    const handleIntervalChange = (e) => {
        let val = parseInt(e.target.value, 10);
        if (isNaN(val) || val < 5000) {
            val = 5000; // on force à 5000 si la saisie < 5000
        }
        setIntervalMs(val);
    };

    if (isNotConnected) {
        return (
            <section className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-100 to-pink-100">
                <div className="text-gray-700 text-xl">
                    Vous n'êtes pas connecté ...
                </div>
            </section>
        );
    }

    if (isForbidden) {
        return (
            <section className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-100 to-pink-100">
                <div className="text-gray-700 text-xl">
                    Vous n'avez pas les permissions admin ...
                </div>
            </section>
        );
    }

    if (!param) {
        return (
            <section
                className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-100 to-pink-100">
                <div className="text-gray-700 text-xl">Chargement...</div>
            </section>
        );
    }

    return (
        <section
            className="min-h-screen bg-gradient-to-br from-blue-100 to-pink-100 flex items-center justify-center py-32 px-4">
            <div
                className="
          max-w-xl w-full bg-white rounded-lg shadow-lg p-6
          animate-fadeIn
        "
            >
                <h2 className="text-3xl font-bold mb-6 text-center text-gray-800">
                    Paramètres de <span className="text-blue-500">NoobSave</span>
                </h2>

                {/* Bloc : Sauvegarde automatique */}
                <div className="mb-6 p-4 rounded-md border border-gray-200">
                    <div className="flex items-center mb-2">
                        {/* Icône */}
                        <div className="text-blue-500 mr-2">
                            {enabled ? <FaPlay size={20}/> : <FaPause size={20}/>}
                        </div>
                        <h3 className="font-semibold text-gray-700 flex-1">
                            Sauvegarde automatique
                        </h3>
                    </div>
                    <p className="text-sm text-gray-500 mb-3">
                        Activez ou désactivez la sauvegarde automatique.
                    </p>

                    <div className="flex items-center justify-between">
                        <label className="flex items-center space-x-2">
                            <input
                                type="checkbox"
                                className="h-5 w-5 text-blue-600"
                                checked={enabled}
                                onChange={(e) => setEnabled(e.target.checked)}
                            />
                            <span className="text-gray-700">
                {enabled ? "Activé" : "Désactivé"}
              </span>
                        </label>
                        <button
                            onClick={updateAutoSave}
                            className="
                px-3 py-1 bg-blue-500 text-white rounded-md
                hover:bg-blue-600 transition-colors
              "
                        >
                            Appliquer
                        </button>
                    </div>
                </div>

                {/* Bloc : Intervalle de sauvegarde */}
                <div className="mb-6 p-4 rounded-md border border-gray-200">
                    <div className="flex items-center mb-2">
                        <div className="text-green-500 mr-2">
                            <FaRegClock size={20}/>
                        </div>
                        <h3 className="font-semibold text-gray-700 flex-1">
                            Intervalle de sauvegarde (ms)
                        </h3>
                    </div>
                    <p className="text-sm text-gray-500 mb-3">
                        Définissez la fréquence de la sauvegarde automatique.
                        <br/>
                        Minimum : 5000 ms.
                    </p>
                    <div className="flex items-center justify-between space-x-2">
                        <input
                            type="number"
                            min="5000"
                            step="1000"
                            className="
                border border-gray-300 rounded-md p-1 w-24 text-center
                focus:outline-none focus:border-blue-400
              "
                            value={intervalMs}
                            onChange={handleIntervalChange}
                        />
                        <button
                            onClick={updateInterval}
                            className="
                px-3 py-1 bg-green-500 text-white rounded-md
                hover:bg-green-600 transition-colors
              "
                        >
                            Appliquer
                        </button>
                    </div>
                </div>

                {/* Bloc : Types de fichiers autorisés */}
                <div className="mb-2 p-4 rounded-md border border-gray-200">
                    <div className="flex items-center mb-2">
                        <div className="text-purple-500 mr-2">
                            <FaFileAlt size={20}/>
                        </div>
                        <h3 className="font-semibold text-gray-700 flex-1">
                            Types de fichiers autorisés
                        </h3>
                    </div>
                    <p className="text-sm text-gray-500 mb-3">
                        Cochez les extensions de fichiers à enregistrer.
                    </p>

                    <div className="flex flex-wrap gap-4 mb-4">
                        {possibleFileTypes.map((ext) => (
                            <label
                                key={ext}
                                className="
                  flex items-center space-x-2
                  text-gray-700
                "
                            >
                                <input
                                    type="checkbox"
                                    className="h-5 w-5 text-purple-600"
                                    checked={checkedExtensions.includes(ext)}
                                    onChange={() => handleCheckboxChange(ext)}
                                />
                                <span>{ext}</span>
                            </label>
                        ))}
                    </div>

                    <div className="flex justify-end">
                        <button
                            onClick={updateFileTypes}
                            className="
                px-3 py-1 bg-purple-500 text-white rounded-md
                hover:bg-purple-600 transition-colors
              "
                        >
                            Appliquer
                        </button>
                    </div>
                </div>

                {/* Bloc : Chemin de sauvegarde */}
                <div className="mb-2 p-4 rounded-md border border-gray-200">
                    <div className="flex items-center mb-2">
                        <div className="text-red-500 mr-2">
                            <PiPath size={20}/>
                        </div>
                        <h3 className="font-semibold text-gray-700 flex-1">
                            Sélectionnez un dossier pour la sauvegarde
                        </h3>
                    </div>
                    <p className="text-sm text-gray-500 mb-3">
                        Sélectionnez un dossier pour la sauvegarde ou entrez un chemin manuellement.
                    </p>
                    <div className="flex flex-col space-y-4 pb-5">
                        <div
                            className="flex items-center space-x-2 border border-gray-300 rounded-md p-2 bg-white shadow-sm">
                            {/* Icône du chemin */}
                            <svg
                                xmlns="http://www.w3.org/2000/svg"
                                className="h-6 w-6 text-blue-500"
                                fill="none"
                                viewBox="0 0 24 24"
                                stroke="currentColor"
                            >
                                <path
                                    strokeLinecap="round"
                                    strokeLinejoin="round"
                                    strokeWidth={2}
                                    d="M3 7a2 2 0 012-2h14a2 2 0 012 2v10a2 2 0 01-2 2H5a2 2 0 01-2-2V7z"
                                />
                            </svg>

                            {/* Champ texte pour entrer le chemin */}
                            <input
                                type="text"
                                placeholder="Chemin du dossier (ex: /home/user/docs)"
                                value={savePath}
                                onChange={(e) => setSavePath(e.target.value)}
                                className="flex-1 outline-none bg-transparent text-gray-700 placeholder-gray-400"
                            />
                        </div>
                    </div>

                    {/* Suppression du chemin */}
                    <div className="flex text-end justify-end w-full space-x-2">
                        <button
                            onClick={() => updateSavePath(savePath)}
                            className={"px-3 py-2 bg-orange-500 text-white rounded-md hover:bg-orange-600 transition-colors"}
                        >
                            Enregistrer
                        </button>
                        <button
                            onClick={deleteSavePath}
                            className="px-3 py-2 bg-red-500 text-white rounded-md hover:bg-red-600 transition-colors"
                        >
                            Supprimer le chemin
                        </button>
                    </div>
                </div>
            </div>
        </section>
    );
}
