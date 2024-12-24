import React, { useEffect, useState } from "react";

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
                const existing = data.allowedFileExtensions?.split(",").map((ext) => ext.trim()) || [];
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
            <section className="min-h-screen flex items-center justify-center">
                <div className="text-gray-600">Chargement...</div>
            </section>
        );
    }

    return (
        <section className="min-h-screen bg-gray-100 p-8">
            <div className="max-w-3xl mx-auto bg-white rounded-lg shadow-md p-6">
                <h2 className="text-2xl font-bold mb-4 text-center">Paramètres</h2>

                {/* Bloc : Sauvegarde automatique */}
                <div className="bg-gray-50 p-4 rounded-md mb-4 flex items-center justify-between">
                    <div>
                        <label className="font-semibold">Sauvegarde automatique</label>
                        <p className="text-sm text-gray-500">
                            Activez ou désactivez la sauvegarde automatique.
                        </p>
                    </div>
                    <div className="flex items-center space-x-2">
                        <input
                            type="checkbox"
                            className="h-5 w-5"
                            checked={enabled}
                            onChange={(e) => setEnabled(e.target.checked)}
                        />
                        <button
                            onClick={updateAutoSave}
                            className="px-3 py-1 bg-blue-500 text-white rounded-md hover:bg-blue-600"
                        >
                            Appliquer
                        </button>
                    </div>
                </div>

                {/* Bloc : Intervalle de sauvegarde */}
                <div className="bg-gray-50 p-4 rounded-md mb-4 flex items-center justify-between">
                    <div>
                        <label className="font-semibold">Intervalle (ms)</label>
                        <p className="text-sm text-gray-500">
                            Définissez la fréquence de la sauvegarde automatique. Minimum: 5000 ms.
                        </p>
                    </div>
                    <div className="flex items-center space-x-2">
                        <input
                            type="number"
                            min="5000" // empêche côté UI de descendre sous 5000
                            step="1000"
                            className="border border-gray-300 rounded-md p-1 w-24 text-right"
                            value={intervalMs}
                            onChange={handleIntervalChange}
                        />
                        <button
                            onClick={updateInterval}
                            className="px-3 py-1 bg-blue-500 text-white rounded-md hover:bg-blue-600"
                        >
                            Appliquer
                        </button>
                    </div>
                </div>

                {/* Bloc : Types de fichiers autorisés */}
                <div className="bg-gray-50 p-4 rounded-md mb-4">
                    <label className="font-semibold block mb-2">Types de fichiers autorisés</label>
                    <p className="text-sm text-gray-500 mb-3">
                        Cochez les extensions de fichiers à enregistrer dans la base.
                    </p>

                    <div className="flex flex-wrap gap-4">
                        {possibleFileTypes.map((ext) => (
                            <label key={ext} className="flex items-center space-x-2">
                                <input
                                    type="checkbox"
                                    className="h-5 w-5"
                                    checked={checkedExtensions.includes(ext)}
                                    onChange={() => handleCheckboxChange(ext)}
                                />
                                <span>{ext}</span>
                            </label>
                        ))}
                    </div>

                    <div className="mt-4 flex justify-end">
                        <button
                            onClick={updateFileTypes}
                            className="px-3 py-1 bg-blue-500 text-white rounded-md hover:bg-blue-600"
                        >
                            Appliquer
                        </button>
                    </div>
                </div>
            </div>
        </section>
    );
}
