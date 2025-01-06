import React, { useState, useEffect } from "react";
import axios from "axios";
import { FaDownload, FaTrash } from 'react-icons/fa';

const Documents = () => {
    const [fichiers, setFichiers] = useState([]);
    const [subdirectories, setSubdirectories] = useState([]);

    /** ============================================ CHARGEMENT DES FICHIERS ET DES CHEMINS ============================================ */

    useEffect(() => {
        chargerFichiers();
        chargerSubdirectories();
    }, []);

    const chargerFichiers = () => {
        axios
            .get("http://localhost:8080/api/fichiers")
            .then((response) => setFichiers(response.data))
            .catch((error) => console.error(error));
    };

    const chargerSubdirectories = () => {
        axios
            .get("http://localhost:8080/api/fichiers/subdirectories")
            .then((response) => setSubdirectories(response.data))
            .catch((error) => console.error(error));
    };

    /** ============================================ SAUVEGARDE ============================================ */

    const save = () => {
        axios
            .get("http://localhost:8080/api/fichiers/save")
            .then((response) => console.log(response))
            .catch((error) => console.error(error));
    };

    /** ============================================ RESTAURATION ============================================ */

    const telechargerFichier = (id, nom) => {
        axios
            .get(`http://localhost:8080/api/fichiers/${id}`, { responseType: "blob" })
            .then((response) => {
                const url = window.URL.createObjectURL(new Blob([response.data]));
                const link = document.createElement("a");
                link.href = url;
                link.setAttribute("download", nom);
                document.body.appendChild(link);
                link.click();
            })
            .catch((error) => console.error(error));
    };

    const restore = () => {
        axios
            .get("http://localhost:8080/api/fichiers/restore")
            .then((response) => console.log(response))
            .catch((error) => console.error(error));
    };

    const restoreSubdirectory = (sousRep) => {
        axios
            .get(`http://localhost:8080/api/fichiers/restore-sous-repertoire?sousRepertoire=${encodeURIComponent(sousRep)}`)
            .then((response) => {
                console.log(response.data);
                alert(response.data); // Afficher le rés     ultat
            })
            .catch((error) => {
                console.error("Erreur lors de la restauration :", error.response || error);
                alert("Erreur : " + (error.response?.data || error.message));
            });
    };



    /** ============================================ SUPPRESSION DE TOUT ============================================ */

    const supprimerFichier = (id) => {
        if (window.confirm("Êtes-vous sûr de vouloir supprimer ce fichier ?")) {
            axios
                .delete(`http://localhost:8080/api/fichiers/${id}`, { withCredentials: true })
                .then(() => {
                    console.log("Fichier supprimé !");
                    // Met à jour l'état en supprimant le fichier supprimé
                    setFichiers((prevFichiers) =>
                        prevFichiers.filter((fichier) => fichier.id !== id)
                    );
                })
                .catch((error) => {
                    console.error("Erreur lors de la suppression du fichier :", error);
                });
        }
    };

    return (
        <section className="min-h-screen w-full flex flex-col justify-center bg-gray-50 py-12 pt-32">
            {/* Titre Principal */}
            <div className="max-w-5xl mx-auto text-center mb-10">
                <h1 className="text-5xl font-extrabold text-transparent bg-clip-text bg-gradient-to-r from-blue-500 via-purple-500 to-pink-500 mb-3">
                    Mes Fichiers
                </h1>
                <p className="text-gray-600">
                    Gérez, téléchargez et supprimez vos documents en toute simplicité.
                </p>
            </div>

            {/* Liste des Fichiers */}
            <div className="max-w-5xl mx-auto grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {fichiers.length > 0 ? (
                    fichiers.map((fichier) => (
                        <div
                            key={fichier.id}
                            className="
                bg-white rounded-lg shadow-lg p-6
                hover:shadow-2xl transition-shadow duration-300
              "
                        >
                            {/* Nom du Fichier */}
                            <h2 className="text-xl font-semibold text-gray-800 mb-1 break-words">
                                {fichier.nom}
                            </h2>

                            {/* Date de Dernière Modification */}
                            <p className="text-gray-500 text-sm mb-4">
                                Dernière modification :{" "}
                                {new Date(fichier.dateModification).toLocaleString()}
                            </p>

                            {/* Boutons d'action */}
                            <div className="flex items-center space-x-2">
                                {/* Télécharger */}
                                <button
                                    className="
                    flex items-center px-4 py-2 bg-blue-500 text-white
                    rounded-md shadow-sm hover:bg-blue-600
                    focus:outline-none focus:ring-2 focus:ring-blue-300
                    transition duration-200
                  "
                                    onClick={() => telechargerFichier(fichier.id, fichier.nom)}
                                >
                                    <FaDownload className="mr-2"/>
                                    Télécharger
                                </button>

                                {/* Supprimer */}
                                <button
                                    className="
                    flex items-center px-4 py-2 bg-red-500 text-white
                    rounded-md shadow-sm hover:bg-red-600
                    focus:outline-none focus:ring-2 focus:ring-red-300
                    transition duration-200
                  "
                                    onClick={() => supprimerFichier(fichier.id)}
                                >
                                    <FaTrash className="mr-2"/>
                                    Supprimer
                                </button>
                            </div>
                        </div>
                    ))
                ) : (
                    <div className="col-span-full text-center">
                        <p className="text-gray-600 text-lg font-medium">
                            Aucun fichier disponible
                        </p>
                        <p className="text-sm text-gray-400">
                            Ajoutez des fichiers pour les voir ici.
                        </p>
                    </div>
                )}
            </div>

            {/* Boutons Sauvegarde / Restauration */}
            <div className="mt-12 flex flex-col items-center gap-4 md:flex-row md:justify-center">
                <button
                    onClick={save}
                    className="
            flex items-center justify-center px-6 py-3
            text-white font-bold text-lg
            bg-blue-500 rounded-full shadow-lg
            hover:shadow-xl hover:bg-blue-600
            transition-transform transform hover:-translate-y-0.5
          "
                >
                    Déclencher la sauvegarde
                </button>

                <button
                    onClick={restore}
                    className="
            flex items-center justify-center px-6 py-3
            text-white font-bold text-lg
            bg-red-500 rounded-full shadow-lg
            hover:shadow-xl hover:bg-red-600
            transition-transform transform hover:-translate-y-0.5
          "
                >
                    Restaurer les fichiers manquants
                </button>
            </div>
            <div className="max-w-5xl mx-auto mb-10 p-10">
                <h2 className="text-2xl font-semibold text-gray-800 mb-4">Sous-répertoires</h2>
                <div className="flex flex-wrap gap-4">
                    {subdirectories.map((rep) => (
                        <button
                            key={rep}
                            onClick={() => restoreSubdirectory(rep)}
                            className="px-4 py-2 bg-green-500 hover:bg-green-600 text-white rounded shadow"
                        >
                            Restaurer « {rep || "(racine)"} »
                        </button>
                    ))}
                </div>
            </div>
        </section>
    );
};

export default Documents;
