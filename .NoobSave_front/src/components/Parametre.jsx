import React, { useEffect, useState } from "react";
import { FaRegClock, FaFileAlt, FaPlay, FaPause } from "react-icons/fa";

/**
 * Types de fichiers possibles à cocher.
 */
const possibleFileTypes = [".pdf", ".txt", ".docx", ".jpg", ".png"];

export default function Parametre() {
    const [param, setParam] = useState(null);

    const [enabled, setEnabled] = useState(false);
    const [intervalMs, setIntervalMs] = useState(60000);

    // Liste des extensions cochées
    const [checkedExtensions, setCheckedExtensions] = useState([]);

    useEffect(() => {
        fetch("http://localhost:8080/api/parametres")
            .then((res) => res.json())
            .then((data) => {
                setParam(data);
                setEnabled(data.autoSaveEnabled);
                setIntervalMs(data.autoSaveInterval);

                // Conversion string -> tableau (si besoin)
                const existing = data.allowedFileExtensions
                    ?.split(",")
                    .map((ext) => ext.trim()) || [];
                setCheckedExtensions(existing);
            })
            .catch(console.error);
    }, []);

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

    if (!param) {
        return (
            <section className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-100 to-pink-100">
                <div className="text-gray-700 text-xl">Chargement...</div>
            </section>
        );
    }

    return (
        <section className="min-h-screen bg-gradient-to-br from-blue-100 to-pink-100 flex items-center justify-center py-12 px-4">
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
                            {enabled ? <FaPlay size={20} /> : <FaPause size={20} />}
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
                            <FaRegClock size={20} />
                        </div>
                        <h3 className="font-semibold text-gray-700 flex-1">
                            Intervalle de sauvegarde (ms)
                        </h3>
                    </div>
                    <p className="text-sm text-gray-500 mb-3">
                        Définissez la fréquence de la sauvegarde automatique.
                        <br />
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
                            <FaFileAlt size={20} />
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
            </div>
        </section>
    );
}
